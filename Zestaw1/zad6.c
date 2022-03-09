#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
    // Otworzenie pliku wejściowego
    int wejscie = open(argv[1], O_RDONLY);

    // Obsługa błędu otwarcia pliku wejściowego
    if (wejscie == -1)
    {
        perror("opening file error");
        exit(1);
    }

    // Otworzenie pliku wyjściowego
    int wyjscie = open(argv[2], O_RDWR | O_TRUNC | O_CREAT, 0666);
    

    // Obsługa błędu otwarcia pliku wyjściowego
    if (wyjscie == -1)
    {
        printf("tu");
        perror("opening file error");
        exit(1);
    }

    // Zmienne pomocnicze
    int dataProd;
    char inBuff[3];

    // Wczytywnie danych z pliku
    while (1)
    {
        // Pobranie kilku bajtów danych
        dataProd = read(wejscie, inBuff, sizeof(inBuff));

        // Obsługa błędu funkcji read
        if (dataProd == -1)
        {
            perror("read error");
            exit(3);
        }

        // Zapis do pliku wyjściowego
        if (dataProd > 0)
        {
            // Zapisanie tekstu do pliku i obsługa błędu funkcji write
            if (write(wyjscie, inBuff, dataProd) == -1)
            {
                perror("write error");
                exit(4);
            }
        }

        // Zakończenie pobierania gdy koniec pliku
        if (dataProd == 0)
        {
            break;
        }
    }

    // Zamknięcie pliku wejściowego
    close(wejscie);

    // Zamknięcie pliku wejściowego
    close(wyjscie);

    return 0;
}