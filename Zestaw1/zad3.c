#include <stdio.h>
#include <stdbool.h>

bool drukowalne_byte(const void *buf, int len)
{
    const char *buf_in = buf;

    for (int i = 0; i < len; i++)
    {
        if (*buf_in < 32 || *buf_in > 126)
        {
            return false;
        }

        buf_in += 1;
    }

    return true;
}

bool drukowalne_arr(const void *buf, int len)
{
    const char *buf_in = buf;

    for (int i = 0; i < len; i++)
    {
        if (buf_in[i] < 32 || buf_in[i] > 126)
        {
            return false;
        }
    }

    return true;
}

int main(int argc, char **argv)
{
    char array[30];
    int znak;
    int i = 0;
    while (scanf("%d", &znak))
    {
        if (znak == 0)
            break;

        array[i] = znak;
        i++;
        if (i == 30)
            break;
    }

    if (drukowalne_byte(array, i))
    {
        printf("Zgodne\n");
    }
    else
    {
        printf("Niezgodne\n");
    }

    if (drukowalne_arr(array, i))
    {
        printf("Zgodne\n");
    }
    else
    {
        printf("Niezgodne\n");
    }

    return 0;
}