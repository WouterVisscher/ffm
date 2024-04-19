#include <stdio.h>

enum MY_ENUM { 
    A = 1,
    B = 2,
    C = 3 
};
typedef enum MY_ENUM MY_ENUM;

int getenum(void)
{
    return B;
}

void setenum(MY_ENUM abc)
{
    printf("c: %d\n", abc);
}

int main(void)
{
    MY_ENUM e = getenum();
    setenum(e);
}