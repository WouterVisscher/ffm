#include<stdio.h>
#include<stdlib.h>

struct pj {
    const char *version;
    int major;
};

struct pj version() {
  struct pj v;
  v.version = "Rel. 8.3.1, December 1st, 2023";
  v.major = 8;

  return v;
}

const char * get_hello(void)
{

    printf("in func() get_hello\n");
    return "Rel. 9.3.1, December 1st, 2023";
}

void hello()
{
    printf("enter in hello(): %s\n", get_hello());
}
