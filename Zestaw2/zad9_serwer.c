#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/socket.h>
#include <string.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <signal.h>

int serwer;

// Funkcja własnej obsługi sygnału
void koniec(void)
{
    // Zamknięcie połączenia
    if (close(serwer) == -1)
    {
        perror("close error");
        exit(1);
    }
}

int main(int argc, char *argv[])
{
    // Sprawdzenie czy podano argumenty
    if (argc != 2)
    {
        printf("Zla ilosc argumentow wywolania programu \n");
        exit(1);
    }

    // Tworzenie gniazdka
    serwer = socket(AF_INET, SOCK_DGRAM, 0);

    // Sprawdzenie czy gniazdko zostało stworzone poprawnie
    if (serwer == -1)
    {
        perror("socket error");
        exit(1);
    }

    // Rejestracja funkcji wywołanych przez exit()
    if (atexit(koniec) != 0)
    {
        perror("atexit error");
        exit(EXIT_FAILURE);
    }

    // Tworzenie adresu
    struct sockaddr_in adres;
    memset(&adres, 0, sizeof(adres));
    adres.sin_family = AF_INET;
    adres.sin_addr.s_addr = htonl(INADDR_ANY);
    adres.sin_port = htons(atoi(argv[1]));

    // Ustalenie ardesu lokalnego końca gniazdka
    if (bind(serwer, (struct sockaddr *)&adres, sizeof(adres)) == -1)
    {
        perror("bind error");
        exit(1);
    }

    char buffer[1024];
    char message[] = "Hello, world!\r\n";
    struct sockaddr_in klient;

    // Pętla czekająca na połączenia
    while (1)
    {
        unsigned int len = sizeof(klient);

        // Odebranie wiadomości z gniazdka
        int odczyt = recvfrom(serwer, (char *)buffer, 1024, 0, (struct sockaddr *)&klient, &len);

        // Sprawdzenie błędów funkcji recvfrom
        if (odczyt == -1)
        {
            perror("recivefrom error");
            exit(1);
        }

        // Wysłanie wiadomości do gniazdka
        int zapis = sendto(serwer, message, strlen(message), 0, (struct sockaddr *)&klient, len);

        // Sprawdzenie błędów funkcji sendto
        if (zapis != strlen(message))
        {
            perror("sendto error");
            exit(1);
        }
    }

    return 0;
}