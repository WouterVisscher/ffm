#include<stdio.h>
#include<stdlib.h>

struct T_Version {
    int           major;
    int           minor;
    int           patch;
};

struct T_Version verion() {
  struct T_Version v;
  v.major = 3;
  v.minor = 2;
  v.patch = 1;

  printf("Struct lib called!\n");

  return v;
}