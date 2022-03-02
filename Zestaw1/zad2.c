#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv)
{
    int n = 5;
    int *liczby = (int *)malloc(sizeof(int) * n);
    int *p = liczby;

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

        *p = liczba;
        p += 1;
        i++;

        if (i == n)
        {
            break;
        }
    }

    p = liczby;
    printf("Liczby większe od 10 i mniejsze od 100\n");
    for (int j = 0; j < n; j++)
    {
        if (*p > 10 && *p < 100)
        {
            printf("%d\n", *p);
        }

        p += 1;
    }

    return 0;
}