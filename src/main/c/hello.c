#include<stdio.h>
#include<stdlib.h>

void hello()
{
    printf("Hello World!\n");
}

const char * get_hello(void)
{
    // char *str = malloc(3);
    // str[0] = 'h';
    // str[1] = 'w';
    // str[2] = '\0';

    printf("in get_hello");
    return "my str from get_hello";
}
