#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/socket.h>
#include <string.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdbool.h>

int main(int argc, char *argv[])
{
    // Tworzenie gniazdka
    int klient = socket(AF_INET, SOCK_DGRAM, 0);

    // Sprawdzenie czy gniazdko zostało stworzone poprawnie
    if (klient == -1)
    {
        perror("socket error");
        exit(1);
    }

    // Tworzenie adresu
    struct sockaddr_in adres;
    memset(&adres, 0, sizeof(adres));
    adres.sin_family = AF_INET;
    adres.sin_addr.s_addr = inet_addr("127.0.0.1");
    adres.sin_port = htons(2020);
    char test[65507];

    int suma = 0;
    for (int i = 0; i < 65507; i++)
    {
        if (i % 2 == 0)
        {
            test[i] = '1';
            suma++;
        }
        else
        {
            test[i] = ' ';
        }
    }

    // Wysłanie wiadomości do gniazdka i sprawdzenie błędów funkcji sendto
    if (sendto(klient, test, 65507, 0, (struct sockaddr *)&adres, sizeof(adres)) == -1)
    {
        perror("sendto error");
        exit(1);
    }

    char dane[20];
    unsigned int len = sizeof(adres);

    // Odebranie wiadomości z gniazdka
    int odczyt = recvfrom(klient, dane, sizeof(dane), 0, (struct sockaddr *)&adres, &len);

    // Sprawdzenie błędów funkcji recvfrom
    if (odczyt == -1)
    {
        perror("recivefrom error");
        exit(1);
    }

    printf("%.*s", odczyt, dane);

    // Zamknięcie połączenia
    if (close(klient) == -1)
    {
        perror("close error");
        exit(1);
    }

    return 0;
}

