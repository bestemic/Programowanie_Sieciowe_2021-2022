#include <stdio.h>

int main(int argc, char **argv)
{
    int n = 50;
    int liczby[n];

    int i = 0;
    int liczba;
    while (1)
    {
        printf("Podaj liczbę: ");
        scanf("%d", &liczba);
        if (liczba == 0)
        {
            break;
        }

        liczby[i] = liczba;
        i++;

        if (i == n)
        {
            break;
        }
    }

    printf("Liczby większe od 10 i mniejsze od 100\n");
    for (int j = 0; j < n; j++)
    {
        if (liczby[j] > 10 && liczby[j] < 100)
        {
            printf("%d\n", liczby[j]);
        }
    }

    return 0;
}