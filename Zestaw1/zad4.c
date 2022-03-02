#include <stdio.h>
#include <stdbool.h>

bool drukowalne_byte(const char *buf)
{
    const char *buf_in = buf;

    while (true)
    {
        if (*buf_in == 0)
            return true;
        if (*buf_in < 32 || *buf_in > 126)
            return false;
        buf_in += 1;
    }
}

bool drukowalne_arr(const char *buf)
{
    const char *buf_in = buf;

    int i = 0;
    while (true)
    {
        if (buf_in[i] == 0)
            return true;
        if (buf_in[i] < 32 || buf_in[i] > 126)
            return false;
        i++;
    }
}

int main(int argc, char **argv)
{

    char *array = "Ala ma ókota";

    if (drukowalne_byte(array))
    {
        printf("Zgodne\n");
    }
    else
    {
        printf("Niezgodne\n");
    }

    if (drukowalne_arr(array))
    {
        printf("Zgodne\n");
    }
    else
    {
        printf("Niezgodne\n");
    }

    return 0;
}