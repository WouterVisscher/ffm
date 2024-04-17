#include <stdio.h>

enum MY_ENUM { 
    A = 1,
    B = 2,
    C = 3 
};

int getenum(void)
{
    return B;
}

void setenum(enum MY_ENUM abc)
{
    printf("%d\n", abc);
}

int main(void)
{
    enum MY_ENUM e = getenum();
    setenum(e);
}