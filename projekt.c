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

#define MAX_CLIENTS 10

// Definicja struktury pary klucz-wartość
struct ClientsPair {
    char* key;
    char* value;
};

// Funkcja tworząca nową parę klucz-wartość
struct ClientsPair createClientsPair(char* key, char* value) {
    struct ClientsPair pair;
    pair.key = key;
    pair.value = value;
    return pair;
}

// Funkcja usuwająca parę klucz-wartość
void destroyClientsPair(struct ClientsPair pair) {
    free(pair.key);
    free(pair.value);
}

struct Message {
    struct ClientsPair pair;
    char *content;
};

struct Message createMessage(struct ClientsPair pair, char* content) {
    struct Message message;
    message.pair = pair;
    message.content = strdup(content);
    return message;
}

void destroyMessage(struct Message message) {
    destroyClientsPair(message.pair);
    free(message.content);
}


int check_if_array_contains_element(char* array[], int array_len, char* element) {
    for (int i = 0; i < array_len/8; i++) {
        if (strcmp(array[i], element) == 0) {
            return 1;
        }
    }
    return 0;
}

void fill_array_with_zeros(char* array[], int array_len) {
    for (int i = 0; i < array_len/8; i++) {
        array[i] = "0";
    }
}

int _write(int fd, char* message, int message_len) {
    int bytes_sent = 0;
    while (bytes_sent < message_len) {
        int bytes_remaining = message_len - bytes_sent;
        int bytes_sent_now = write(fd, message + bytes_sent, bytes_remaining);
        if (bytes_sent_now < 0) {
            printf("Failed to write to socket");
            return -1;
        }
        bytes_sent += bytes_sent_now;
    }
    write(fd, "\n\n", 2); // End the stream with a newline character
    return 1;
}

int _read(int fd, char* buffer, int buffer_len) {
    int bytes_received = 0;
    while (bytes_received < buffer_len) {
        int bytes_remaining = buffer_len - bytes_received;
        int bytes_received_now = read(fd, buffer + bytes_received, bytes_remaining);
        if (bytes_received_now < 0) {
            printf("Failed to read from socket");
            return -1;
        }
        bytes_received += bytes_received_now;
        if (buffer[bytes_received - 1] == '\n' && buffer[bytes_received - 2] == '\n') {
            break;
        }
    }


    return bytes_received;
}

char* concat(char* s1, char* s2) {
    char* result = malloc(strlen(s1) + strlen(s2) + 1); // +1 for the null-terminator
    strcpy(result, s1);
    strcat(result, s2);
    return result;
}

char* generateNumber() {
    int number;
    srand(time(NULL)); // Seed the random number generator with current time
    int randomNumber = rand() % 8999 + 1001; // Generate a random number between 1000 and 9999
    char* resultString = (char*)malloc(4); // Allocate memory for the result string
    sprintf(resultString, "%d", randomNumber); // Convert the random number to a string
    return resultString;
}

int main() {
    char* clients[1024]; // utworzenie tablicy dla identyfikatorów klientów
    fill_array_with_zeros(clients, sizeof(clients)); // wypełnienie tablicy zerami
    struct Message* messages[6442]; // utworzenie tablicy dla wiadomości
    struct ClientsPair* clientPairs[6442]; // utworzenie tablicy dla par klucz-wartość
    int clientPairIndex = 0; // utworzenie zmiennej pomocniczej dla tablicy par klucz-wartość
    int clientIndex = 0; // utworzenie zmiennej pomocniczej dla tablicy identyfikatorów klientów
    int activeClientIndex = 0; // utworzenie zmiennej pomocniczej dla tablicy aktywnych identyfikatorów klientów
    socklen_t slt; // utworzenie zmiennej dla rozmiaru struktury adresowej
    int on = 1; // zmienna pomocnicza
    int sfd, cfd; // utworzenie deskryptorów dla gniazd serwera i klienta
    int fdmax; // zmienna pomocnicza
    int fda; // zmienna pomocnicza, która przechowuje liczbę zdarzeń
    int rc; // zmienna pomocnicza dla funkcji close
    int i; // zmienna pomocnicza, która przechowuje numer deskryptora
    struct sockaddr_in saddr, caddr; // utworzenie struktur adresowych dla serwera i klienta
    static struct timeval timeout; // utworzenie zmiennej dla timeoutu
    fd_set mask, rmask, wmask, clients_waiting_for_id, clients_waiting_for_adding_contact, clients_failure; // utworzenie zbiorów deskryptorów dla wszystkich deskryptorów, deskryptorów do odczytu, deskryptorów do zapisu i deskryptorów klientów oczekujących na numer Gadu-Gadu, deskryptorów klientów oczekujących na dodanie kontaktu, deskryptorów klientów, którzy nie podali poprawnego numeru Gadu-Gadu
    char message[32]; // utworzenie zmiennej dla wiadomości w Gadu-Gadu

    memset(&saddr, 0, sizeof(saddr)); // wypełnienie struktury zerami

    saddr.sin_family = AF_INET; // rodzina adresów IPv4
    saddr.sin_addr.s_addr = INADDR_ANY; // ustawienie adresu IP serwera
    saddr.sin_port = htons(1234); // ustawianie portu 1234 w kolejności bitów sieciowych

    sfd = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP); // utworzenie gniazda TCP
    if(sfd == -1) {
        perror("Creating socket failed");
        exit(1);
    }
    setsockopt(sfd, SOL_SOCKET, SO_REUSEADDR, (char*)&on, sizeof(on)); // ustawienie opcji gniazda: SO_REUSEADDR - ponowne użycie adresu, on - włączenie opcji

    if(bind(sfd, (struct sockaddr*) &saddr, sizeof(saddr)) == -1) { // powiązanie gniazda z adresem i portem
        printf("Bind failed\n");
    }

    if (listen(sfd, MAX_CLIENTS) == -1) { // nasłuchiwanie na porcie 1234 z maksymalnie 10 klientami
        printf("Listen failed\n");
        exit(1);
    }

    FD_ZERO(&mask); // wyczyszczenie zbioru deskryptorów
    FD_ZERO(&rmask); // wyczyszczenie zbioru deskryptorów do odczytu
    FD_ZERO(&wmask); // wyczyszczenie zbioru deskryptorów do zapisu
    FD_ZERO(&clients_waiting_for_id); // wyczyszczenie zbioru deskryptorów klientów oczekujących na numer Gadu-Gadu
    FD_ZERO(&clients_waiting_for_adding_contact); // wyczyszczenie zbioru deskryptorów klientów oczekujących na dodanie kontaktu
    FD_ZERO(&clients_failure); // wyczyszczenie zbioru deskryptorów klientów, którzy nie podali poprawnego numeru Gadu-Gadu
    fdmax = sfd; // ustawienie maksymalnego deskryptora na deskryptor serwera

    while(1) {
        FD_SET(sfd, &mask); // dodanie deskryptora serwera do zbioru deskryptorów
        rmask = mask; // przypisanie zbioru deskryptorów do zbioru deskryptorów do odczytu
        timeout.tv_sec = 5; // ustawienie timeoutu na 5 sekund
        timeout.tv_usec = 0; // ustawienie timeoutu na 0 mikrosekund (czyli w sumie i tak 5 sekund)
        rc = select(fdmax+1, &rmask, &wmask, (fd_set*)0, &timeout); // wywołanie selecta, który czeka na zdarzenie na jednym z deskryptorów. Select służy do obsługi wielu połączeń na raz
        if (rc == 0)
            continue; // jeśli nie ma żadnego zdarzenia, to kontynuuj
        fda = rc; // przypisanie liczby zdarzeń do zmiennej pomocniczej
        if (FD_ISSET(sfd, &rmask)) { // jeśli zdarzenie jest na deskryptorze serwera
            fda -= 1; // zmniejsz liczbę zdarzeń o 1
            slt = sizeof(caddr); // ustaw rozmiar struktury adresowej
            cfd = accept(sfd, (struct sockaddr*)&caddr, &slt); // odebranie połączenia od klienta
            FD_SET(cfd, &mask); // dodanie deskryptora klienta do zbioru deskryptorów
            if (cfd > fdmax) fdmax = cfd; // jeśli deskryptor klienta jest większy od maksymalnego deskryptora, to ustaw maksymalny deskryptor na deskryptor klienta
        }

        for (i = sfd+1; i <= fdmax && fda > 0; i++) { // dla każdego deskryptora od serwera do maksymalnego deskryptora i jeśli liczba zdarzeń jest większa od 0
            if (FD_ISSET(i, &wmask)) { // jeśli zdarzenie jest na deskryptorze do zapisu
                fda -= 1;
                if (FD_ISSET(i, &clients_waiting_for_id)) { // jeśli deskryptor klienta jest w zbiorze deskryptorów klientów oczekujących na numer Gadu-Gadu
                    char *number;
                    do
                    {
                        number = generateNumber(); // wygeneruj numer Gadu-Gadu
                        printf("breakpoint");
                    } while (check_if_array_contains_element(clients, sizeof(clients), number) == 1); // sprawdź, czy numer Gadu-Gadu nie jest już zajęty

                    clients[clientIndex++] = number; // dodaj numer Gadu-Gadu do tablicy identyfikatorów klientów
                    _write(i, number, 4); // wyślij numer Gadu-Gadu do klienta
                    FD_CLR(i, &clients_waiting_for_id); // usuń deskryptor klienta z zbioru deskryptorów klientów oczekujących na numer Gadu-Gadu
                    FD_SET(i, &rmask); // dodaj deskryptor klienta do zbioru deskryptorów do odczytu
                } else if (FD_ISSET(i, &clients_waiting_for_adding_contact)) { // jeśli deskryptor klienta jest w zbiorze deskryptorów klientów oczekujących na dodanie kontaktu
                    _write(i, "1", 1); // wyślij potwierdzenie dodania kontaktu do klienta
                } else if (FD_ISSET(i, &clients_failure)) { // jeśli deskryptor klienta jest w zbiorze deskryptorów klientów, którzy nie podali poprawnego numeru Gadu-Gadu
                    _write(i, "0", 1); // wyślij informację o niepoprawnym numerze Gadu-Gadu do klienta
                }
                close(i); // zamknięcie połączenia z klientem
                FD_CLR(i, &wmask); // usunięcie deskryptora z zbioru deskryptorów do zapisu
                FD_CLR(i, &mask); // usunięcie deskryptora z zbioru deskryptorów

                if (i == fdmax) { // jeśli deskryptor jest maksymalnym deskryptorem
                    while(fdmax > sfd && !FD_ISSET(fdmax, &mask)) { // dopóki maksymalny deskryptor jest większy od deskryptora serwera i nie jest w zbiorze deskryptorów
                        fdmax -= 1;
                    }
                }
            }

            if(FD_ISSET(i, &rmask)) { // jeśli zdarzenie jest na deskryptorze do odczytu
                _read(i, message, sizeof(message)); // odczytaj numer indeksu
                if (strncmp(message, "0000", 4) == 0) {
                    FD_SET(i, &clients_waiting_for_id); // dodaj deskryptor klienta do zbioru deskryptorów klientów oczekujących na numer Gadu-Gadu
                    FD_SET(i, &wmask); // dodaj deskryptor klienta do zbioru deskryptorów do zapisu

                } else if (strncmp(message, "0001", 4) == 0) {
                    FD_SET(i, &wmask); // dodaj deskryptor klienta do zbioru deskryptorów do zapisu
                    if (check_if_array_contains_element(clients, sizeof(clients), message+7) == 1) { // sprawdź, czy numer Gadu-Gadu istnieje
                        FD_SET(i, &clients_waiting_for_adding_contact); // dodaj deskryptor klienta do zbioru deskryptorów klientów oczekujących na dodanie kontaktu
                        struct ClientsPair pair = createClientsPair(message+3, message+7); // utwórz parę klucz-wartość z numerem Gadu-Gadu nadawcy i numerem Gadu-Gadu odbiorcy
                        clientPairs[clientPairIndex++] = &pair; // dodaj parę klucz-wartość do tablicy par klucz-wartość
                    } else if (strncmp(message, "0002", 4) == 0) {
                        FD_SET(i, &wmask);
                        int flag = 0;
                        for (int j = 0; j < __INT_MAX__; j++)
                        {
                            if (messages[j]->pair.key == message+3) {
                                if (messages[j]->pair.value == message+7) {
                                    messages[j]->content = concat(messages[j]->content, message+11);
                                    flag = 1;
                                    break;
                                }
                            }
                        }
                        if (flag == 0) {
                            struct Message newMessage = createMessage(createClientsPair(message+3, message+7), message+11);
                            messages[activeClientIndex++] = &newMessage;
                        }
                    } else {
                        FD_SET(i, &clients_failure); // dodaj deskryptor klienta do zbioru deskryptorów klientów, którzy nie podali poprawnego numeru Gadu-Gadu
                    }
                }
                FD_CLR(i, &mask); // usunięcie deskryptora z zbioru deskryptorów
                FD_SET(i, &wmask); // dodanie deskryptora do zbioru deskryptorów do zapisu
            }
        }
    }
}

