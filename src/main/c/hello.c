#include<stdio.h>
#include<stdlib.h>


const char * get_hello(void)
{
    // char *str = malloc(3);
    // str[0] = 'h';
    // str[1] = 'w';
    // str[2] = '\0';

    printf("in func() get_hello\n");
    return "Zmy str from get_hello\n";
}

void hello()
{
    printf("enter in hello(): %s\n", get_hello());
}
