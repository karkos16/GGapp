#include <iostream>
#include <vector>
#include <cstring>
#include <cstdlib>
#include <ctime>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/types.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <climits>
#include <algorithm>
#include <map>

#define MAX_CLIENTS 10

// Definicja struktury pary klucz-wartość
struct ClientsPair {
    std::string key;
    std::string value;
};

// Funkcja tworząca nową parę klucz-wartość
struct ClientsPair createClientsPair(const std::string& key, const std::string& value) {
    return {key, value};
}

struct Message {
    ClientsPair pair;
    std::string content;
};

struct Message createMessage(const ClientsPair& pair, const std::string& content) {
    return {pair, content};
}

int check_if_vector_contains_element(const std::vector<std::string>& vec, const std::string& element) {
    return std::find(vec.begin(), vec.end(), element) != vec.end();
}

void fill_vector_with_zeros(std::vector<std::string>& vec, int size) {
    vec.assign(size, "0");
}

int _write(int fd, const std::string& message) {
    int bytes_sent = 0;
    while (bytes_sent < message.length()) {
        int bytes_sent_now = write(fd, message.c_str() + bytes_sent, message.length() - bytes_sent);
        if (bytes_sent_now < 0) {
            std::cerr << "Failed to write to socket" << std::endl;
            return -1;
        }
        bytes_sent += bytes_sent_now;
    }
    write(fd, "\n\n", 2);
    return 1;
}

int _read(int fd, std::string& buffer) {
    int bytes_received = 0;
    while (true) {
        int bytes_received_now = read(fd, &buffer[bytes_received], buffer.length() - bytes_received);
        if (bytes_received_now < 0) {
            std::cerr << "Failed to read from socket" << std::endl;
            return -1;
        }
        bytes_received += bytes_received_now;
        if (buffer[bytes_received - 1] == '\n' && buffer[bytes_received - 2] == '\n') {
            buffer.erase(bytes_received - 2, 2);
            break;
        }
    }
    return bytes_received;
}

std::string concat(const std::string& s1, const std::string& s2) {
    return s1 + s2;
}

std::string remove_zeros(const std::string& s) {
    std::string result = s;
    // result.erase(std::remove(result.begin(), result.end(), '\n'), result.end());
    result.erase(std::remove(result.begin(), result.end(), '\0'), result.end());
    return result;
}

std::string generateNumber() {
    srand(time(NULL));
    int randomNumber = rand() % 8999 + 1001;
    return std::to_string(randomNumber);
}

int main() {
    std::vector<std::string> clients(1024, "0");
    std::vector<Message*> messages(6442, 0);
    std::vector<ClientsPair*> clientPairs(6442);
    std::map<int, std::string> descriptorsToMessageMap;
    std::map<int, std::string> descrtiptorsToContactsMap;
    int clientPairIndex = 0;
    int clientIndex = 0;
    int messageIndex = 0;
    socklen_t slt;
    int on = 1;
    int sfd, cfd;
    int fdmax;
    int fda;
    int rc;
    int i;
    sockaddr_in saddr, caddr;
    timeval timeout;
    fd_set mask, rmask, wmask, clients_waiting_for_id, clients_waiting_for_adding_contact, clients_failure, client_success, client_wants_messages, client_wants_contacts;
    std::string message(128, '\0');

    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons(1234);
    sfd = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
    if(sfd == -1) {
        perror("Creating socket failed");
        exit(1);
    }
    setsockopt(sfd, SOL_SOCKET, SO_REUSEADDR, (char*)&on, sizeof(on));
    if(bind(sfd, (sockaddr*) &saddr, sizeof(saddr)) == -1) {
        std::cerr << "Bind failed" << std::endl;
        exit(1);
    }
    if (listen(sfd, MAX_CLIENTS) == -1) {
        std::cerr << "Listen failed" << std::endl;
        exit(1);
    }

    FD_ZERO(&mask);
    FD_ZERO(&rmask);
    FD_ZERO(&wmask);
    FD_ZERO(&clients_waiting_for_id);
    FD_ZERO(&clients_waiting_for_adding_contact);
    FD_ZERO(&clients_failure);
    FD_ZERO(&client_success);
    FD_ZERO(&client_wants_messages);
    FD_ZERO(&client_wants_contacts);

    fdmax = sfd;
    while(1) {
        FD_SET(sfd, &mask);
        rmask = mask;
        timeout.tv_sec = 5;
        timeout.tv_usec = 0;
        rc = select(fdmax+1, &rmask, &wmask, (fd_set*)0, &timeout);
        if (rc == 0)
            continue;
        fda = rc;
        if (FD_ISSET(sfd, &rmask)) {
            fda -= 1;
            slt = sizeof(caddr);
            cfd = accept(sfd, (sockaddr*)&caddr, &slt);
            FD_SET(cfd, &mask);
            if (cfd > fdmax) fdmax = cfd;
        }
        for (i = sfd+1; i <= fdmax && fda > 0; i++) {

            // Obsługa deskryptora do zapisu
            if (FD_ISSET(i, &wmask)) {
                fda -= 1;
                if (FD_ISSET(i, &clients_waiting_for_id)) { 
                    std::string number;
                    do {
                        number = generateNumber();
                    } while (check_if_vector_contains_element(clients, number) == 1);
                    // sprawdzamy czy wektor clients nie jest pełny
                    if (clientIndex == clients.size() - 1) {
                        FD_SET(i, &clients_failure);
                        FD_CLR(i, &clients_waiting_for_id);
                    } else {
                        clients[clientIndex++] = number;
                        _write(i, number);
                        FD_CLR(i, &clients_waiting_for_id);
                    }
                } else if (FD_ISSET(i, &clients_waiting_for_adding_contact)) {
                    _write(i, "1");
                    FD_CLR(i, &clients_waiting_for_adding_contact);
                } else if (FD_ISSET(i, &clients_failure)) {
                    _write(i, "0");
                    FD_CLR(i, &clients_failure);
                } else if (FD_ISSET(i, &client_wants_messages)) {
                    _write(i, descriptorsToMessageMap[i]);
                    FD_CLR(i, &client_wants_messages);
                    descriptorsToMessageMap.erase(i);
                } else if (FD_ISSET(i, &client_wants_contacts)) {
                    _write(i, descrtiptorsToContactsMap[i]);
                    FD_CLR(i, &client_wants_contacts);
                    descrtiptorsToContactsMap.erase(i);
                } else if (FD_ISSET(i, &client_success)) {
                    _write(i, "1");
                    FD_CLR(i, &client_success);
                } else {
                    _write(i, "0");
                }

                close(i);
                FD_CLR(i, &wmask);
                FD_CLR(i, &mask);
                if (i == fdmax) {
                    while(fdmax > sfd && !FD_ISSET(fdmax, &mask)) {
                        fdmax -= 1;
                    }
                }
            }

            // Obsługa deskryptora do odczytu
            if(FD_ISSET(i, &rmask)) {
                message = std::string(128, '\0');
                _read(i, message);
                if (strncmp(message.c_str(), "0000", 4) == 0) {
                    FD_SET(i, &clients_waiting_for_id);
                } else if (strncmp(message.c_str(), "0001", 4) == 0) {
                    // sprawdzamy teraz czy czterocyfrowy numer (po 0001xxxx) istnieje w wektorze clients
                    if (check_if_vector_contains_element(clients, message.substr(8, 4))) {
                        int flag = 0;
                        FD_SET(i, &clients_waiting_for_adding_contact);
                        for (int i = 0; i < clientPairIndex; i++) {
                            if ((clientPairs[i]->key == message.substr(4, 4) || clientPairs[i]->value == message.substr(4, 4)) && (clientPairs[i]->key == message.substr(8, 4) || clientPairs[i]->value == message.substr(8, 4))) {
                                flag = 1;
                                break;
                            }
                        }
        
                        if (flag == 0) {
                            ClientsPair* pair = new ClientsPair(createClientsPair(message.substr(4,4), message.substr(8,4)));
                            clientPairs[clientPairIndex++] = pair;
                            Message* newMessage = new Message(createMessage(*pair, ""));
                            messages[messageIndex++] = newMessage;
                        }
                    } else { // gdy nie istnieje taki czat
                        FD_SET(i, &clients_failure);
                    }
                } else if (strncmp(message.c_str(), "0002", 4) == 0) {
                    int flag = 0;
                    for (int j = 0; j < messageIndex; j++) {
                        if (messages[j]->pair.key == message.substr(4, 4) || messages[j]->pair.key == message.substr(8, 4)) {
                            if (messages[j]->pair.value == message.substr(4, 4) || messages[j]->pair.value == message.substr(8,4)) {
                                std::string messageCopyWithoutZeros = remove_zeros(message).substr(12);
                                std::cout << "Wiadomosc do wyslania: " << messageCopyWithoutZeros << std::endl;
                                messages[j]->content += message.substr(4, 4) + "klfjaklfsjalkfsjafklsaj\n" + messageCopyWithoutZeros + "kjasdflksajklafjkll\n";
                                FD_SET(i, &client_success);
                                flag = 1;
                                break;
                            }
                        }
                    }
                    if (flag == 0) {
                        FD_SET(i, &clients_failure);
                    }

                // szukanie wszystkich wiadomości numer-numer
                } else if (strncmp(message.c_str(), "0003", 4) == 0) {
                    std::string sender = message.substr(4, 4);
                    std::string receiver = message.substr(8, 4);
                    std::string allMessages = "";

                    for (int j = 0; j < messageIndex; j++) {
                        if (messages[j]->pair.key == sender || messages[j]->pair.key == receiver) {
                            if (messages[j]->pair.value == sender || messages[j]->pair.value == receiver) {
                                allMessages += messages[j]->content;
                                if (allMessages.length() == 0) {
                                    allMessages = "NONE";
                                }
                                std::cout<< allMessages << std::endl;
                                descriptorsToMessageMap.insert(std::pair<int, std::string>(i, allMessages));
                                FD_SET(i, &client_wants_messages);
                                break;
                            }
                        }
                    }
                } else if (strncmp(message.c_str(), "0004", 4) == 0) {
                    std::string sender = message.substr(4, 4);
                    std::string allContacts = "";

                    for (int j = 0; j < clientPairIndex; j++) {
                        if (clientPairs[j]->key == sender) {
                            allContacts += clientPairs[j]->value + "oiaiudusj\n";
                        } else if (clientPairs[j]->value == sender) {
                            allContacts += clientPairs[j]->key + "oiaiudusj\n";
                        }
                    }
                    if (allContacts.length() == 0) {
                        allContacts = "NONE";
                    }
                    descrtiptorsToContactsMap.insert(std::pair<int, std::string>(i, allContacts));
                    FD_SET(i, &client_wants_contacts);
                } else {
                    FD_SET(i, &clients_failure);
                }
            }
            FD_CLR(i, &mask);
            FD_SET(i, &wmask);
        }
    }
    return 0;
}
