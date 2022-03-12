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
    serwer = socket(AF_INET, SOCK_STREAM, 0);

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

    // Oznaczenia gniazdka jako nasłuchujące
    if (listen(serwer, 5) == -1)
    {
        perror("listen error");
        exit(1);
    }

    char message[] = "Hello, world!\r\n";

    // Pętla czekająca na połączenia
    while (1)
    {
        // Apceptacja połączenia
        int klient = accept(serwer, NULL, 0);

        // Sprawdzenia czy udało się zaakceptowac połączenie
        if (klient == -1)
        {
            perror("accept error");
            exit(1);
        }

        // Wysłanie wiadomości
        if (write(klient, message, sizeof(message)) == -1)
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
    }

    return 0;
}
