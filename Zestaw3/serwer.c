#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <limits.h>
#include <string.h>
#include <math.h>

#define UDP_SIZE 65535
#define PORT 2020

int soc;
int isPresent = 0;

// Funkcja obsługi wyjścia
void koniec(void)
{
    // Sprawdzenie czy istnieje socket
    if (isPresent == 1)
    {
        // Zamknięcie połączenia
        if (close(soc) == -1)
        {
            perror("close error");
            exit(1);
        }
    }
}

int main(int argc, char *argv[])
{

    // Tworzenie gniazdka
    soc = socket(AF_INET, SOCK_DGRAM, 0);

    // Sprawdzenie czy gniazdko zostało stworzone poprawnie
    if (soc == -1)
    {
        perror("socket error");
        exit(1);
    }

    // Ustawienie informacji o istniejącym gniazdku
    isPresent = 1;

    // Rejestracja funkcji wywołanych przez exit()
    if (atexit(koniec) != 0)
    {
        perror("atexit error");
        exit(1);
    }

    // Tworzenie adresu
    struct sockaddr_in adres;
    memset(&adres, 0, sizeof(adres));
    adres.sin_family = AF_INET;
    adres.sin_addr.s_addr = htonl(INADDR_ANY);
    adres.sin_port = htons(PORT);

    // Ustalenie ardesu lokalnego końca gniazdka
    if (bind(soc, (struct sockaddr *)&adres, sizeof(adres)) == -1)
    {
        perror("bind error");
        exit(1);
    }

    // Zmienne pomocnicze
    struct sockaddr_in socIn;
    char buffer[UDP_SIZE];

    // Obliczanie długości maksymalnej liczby
    int maxLength = floor(log10(ULONG_MAX)) + 1;
    char maxNumber[maxLength];
    unsigned long int num = ULONG_MAX;

    // Konwersja liczby na tablicę znaków
    for (int j = maxLength - 1; j >= 0; --j, num /= 10)
    {
        maxNumber[j] = (num % 10) + '0';
    }

    // Pętla czekająca na połączenia
    while (1)
    {
        unsigned int len = sizeof(socIn);

        // Odebranie wiadomości z gniazdka
        int odczyt = recvfrom(soc, (char *)buffer, UDP_SIZE, 0, (struct sockaddr *)&socIn, &len);

        // Sprawdzenie błędów funkcji recvfrom
        if (odczyt == -1)
        {
            perror("recivefrom error");
            exit(1);
        }

        // Zmienne pomocnicze
        unsigned long int sum = 0;
        unsigned long int leftSpace = ULONG_MAX;
        char number[maxLength];
        int x = 0;
        int error = 0;
        int i = 0;

        // Przejście przez bufor i odczytanie jego zawartości
        while (i < odczyt)
        {
            // Sprawdzenie czy dozwolony znak
            if (buffer[i] == ' ' || buffer[i] == 10 || (buffer[i] == 13 && buffer[i + 1] == 10))
            {
                // Jeśli przed znakim były liczba
                if (x != 0)
                {
                    // Jeśli odczytana liczba ma długość maksymalnej długości liczby
                    if (x == maxLength)
                    {
                        // Sprawdzenie czy liczba nie jest za duża
                        for (int p = 0, q = maxLength - x; p < x && q < maxLength; p++, q++)
                        {
                            if (number[p] < maxNumber[q])
                            {
                                break;
                            }
                            else if (number[p] > maxNumber[q])
                            {
                                error = 1;
                                printf("ERROR: podana liczba jest za duza\n");
                                break;
                            }
                        }
                    }

                    // Jeśli nie ma błędów
                    if (!error)
                    {
                        unsigned long int tmp = 0;

                        // Konwersja charów na liczbę
                        for (int p = 0; p < x; p++)
                        {
                            tmp = tmp * 10 + (number[p] - '0');
                        }

                        // Sprawdzenie czy po dodaniu nie wystąpi przepełnienie
                        if (tmp <= leftSpace)
                        {
                            sum += tmp;
                            leftSpace -= tmp;
                        }
                        else
                        {
                            error = 1;
                            printf("ERROR: wystapi przepelnienie\n");
                            break;
                        }

                        x = 0;
                    }
                }

                i++;
            }
            else if (buffer[i] < 48 || buffer[i] > 57) // Sprawdzenie czy odczytany znak to cyfra
            {
                error = 1;
                printf("ERROR: odczytany znak nie jest cyfra\n");
                break;
            }
            else
            {
                // Sprawdzenie czy liczba nie jest za duża i powiększenie liczby o kolejną cyfrę
                if (x < maxLength)
                {
                    number[x] = buffer[i];
                    x++;
                    i++;
                }
                else
                {
                    error = 1;
                    printf("ERROR: podana liczba jest za duza\n");
                    break;
                }
            }
        }

        int sumLen = 1;

        // Obliczanie długości sumy
        if (sum != 0)
        {
            sumLen = floor(log10(sum)) + 1;
        }

        char sumChar[sumLen + 1];

        // Konwersja liczby na tablicę znaków
        for (int j = sumLen - 1; j >= 0; --j, sum /= 10)
        {
            sumChar[j] = (sum % 10) + '0';
        }

        // Dodanie znaku końca linii

        sumChar[sumLen] = '\n';

        int zapis;

        // Wysyłanie sumy gdy nie ma błędów
        if (error)
        {
            zapis = sendto(soc, "ERROR\n", 7, 0, (struct sockaddr *)&socIn, len);

            // Sprawdzenie błędów funkcji sendto
            if (zapis != 7)
            {
                perror("sendto error");
                exit(1);
            }
        }
        else
        {
            zapis = sendto(soc, sumChar, sumLen + 1, 0, (struct sockaddr *)&socIn, len);

            // Sprawdzenie błędów funkcji sendto
            if (zapis != sumLen + 1)
            {
                perror("sendto error");
                exit(1);
            }
        }
    }

    return 0;
}