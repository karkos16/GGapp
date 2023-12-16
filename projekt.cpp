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

// Funkcja usuwająca parę klucz-wartość
void destroyClientsPair(const ClientsPair& pair) {
    // No need to free strings as std::string handles memory automatically
}

struct Message {
    ClientsPair pair;
    std::string content;
};

struct Message createMessage(const ClientsPair& pair, const std::string& content) {
    return {pair, content};
}

void destroyMessage(const Message& message) {
    // No need to free strings as std::string handles memory automatically
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
    write(fd, "\n\n", 2); // End the stream with a newline character
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
    fd_set mask, rmask, wmask, clients_waiting_for_id, clients_waiting_for_adding_contact, clients_failure, client_success;
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

            if(FD_ISSET(i, &rmask)) {
                _read(i, message);

                if (strncmp(message.c_str(), "0000", 4) == 0) {
                    FD_SET(i, &clients_waiting_for_id);
                    // FD_SET(i, &wmask); duplikat?
                } else if (strncmp(message.c_str(), "0001", 4) == 0) {
                    // sprawdzamy teraz czy czterocyfrowy numer (po 0001xxxx) istnieje w wektorze clients
                    std::cout<<"Sprawdzam id" << message.substr(8, 4) << std::endl;
                    if (check_if_vector_contains_element(clients, message.substr(8, 4))) {
                        FD_SET(i, &clients_waiting_for_adding_contact);
                        ClientsPair* pair = new ClientsPair(createClientsPair(message.substr(4,4), message.substr(8,4)));
                        clientPairs[clientPairIndex++] = pair;
                        Message* newMessage = new Message(createMessage(*pair, ""));
                        messages[messageIndex++] = newMessage;
                    } else { // gdy nie istnieje taki czat
                        FD_SET(i, &clients_failure);
                    }
                } else if (strncmp(message.c_str(), "0002", 4) == 0) {
                    int flag = 0;
                    for (int j = 0; j < messages.size(); j++) {
                        if (messages[j]->pair.key == message.substr(4, 4) || messages[j]->pair.key == message.substr(8, 4)) {
                            if (messages[j]->pair.value == message.substr(4, 4) || messages[j]->pair.value == message.substr(8,4)) {
                                std::string messageCopyWithoutZeros = remove_zeros(message).substr(12);
                                messages[j]->content += message.substr(4, 4) + "klfjaklfsjalkfsjafklsaj\n" + messageCopyWithoutZeros + "kjasdflksajklafjkll\n";
                                // messages[j]->content = concat(messages[j]->content, concat(message.substr(4, 4), "klfjaklfsjalkfsjafklsaj\n"));
                                // messages[j]->content = concat(messages[j]->content, message.substr(12, 3) + "kjasdflksajklafjkll\n");
                                // messages[j]->content += "kjasdflksajklafjkll\n"; // TODO uwaga na niedodające się taby jasny gwint
                                FD_SET(i, &client_success);
                                flag = 1;
                                break;
                            }
                        }
                    }
                    if (flag == 0) {
                        FD_SET(i, &clients_failure);
                    }
                } else if (strncmp(message.c_str(), "0003", 4)) {
                    std::string number = message.substr(4, 4);
                    for (size_t i = 0; i < count; i++)
                    {
                        /* code */
                    }
                    
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
