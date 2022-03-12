#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/socket.h>
#include <string.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdbool.h>

bool drukowalne_byte(const char *buf)
{
    const char *buf_in = buf;

    while (true)
    {
        if (*buf_in == 0)
            return true;
        if ((*buf_in < 32 || *buf_in > 126) && *buf_in != 13 && *buf_in != 10)
            return false;

        buf_in += 1;
    }
}

int main(int argc, char *argv[])
{
    // Sprawdzenie czy podano argumenty
    if (argc != 3)
    {
        printf("Zla ilosc argumentow wywolania programu \n");
        exit(1);
    }

    // Tworzenie gniazdka
    int klient = socket(AF_INET, SOCK_STREAM, 0);

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
    adres.sin_addr.s_addr = inet_addr(argv[1]);
    adres.sin_port = htons(atoi(argv[2]));

    // Ustalenie ardesu lokalnego końca gniazdka
    if (connect(klient, (struct sockaddr *)&adres, sizeof(adres)) == -1)
    {
        perror("connect error");
        exit(1);
    }

    char dane[20];

    // Odczytanie danych
    int odczyt = read(klient, dane, sizeof(dane));

    // Sprawdzenie czy odczytano dane
    if (odczyt == -1)
    {
        perror("read error");
        exit(1);
    }

    // Sprawdzenie czy odebane bajty są drukowalne
    if (drukowalne_byte(dane) == false)
    {
        printf("Bajty nie składają się ze znaków drukowalnych\n");
        exit(1);
    }

    // Wypisanie wiadomości na konsolę
    int zapis = write(0, dane, odczyt);

    // Obsługa błędu funkcji write
    if (zapis != odczyt)
    {
        perror("write error");
        exit(1);
    }

    // Zamknięcie połączenia
    if (close(klient) == -1)
    {
        perror("close error");
        exit(1);
    }

    return 0;
}
