# JAVA ffm stuff

## C

```sh
cd /src/main/c

gcc proj_version.cpp -lproj -o proj_version -lstdc++
./proj_version

gcc proj_example.c -lproj -o proj_example -lstdc++
./proj_example

gcc -shared -o libprojversion.so -fPIC proj_version.cpp -lproj -lstdc++

gcc enum.c  -o enum -lstdc++
./enum
```

build lib

```sh
gcc -shared -o libhello.so -fPIC hello.c
gcc -shared -o libstruct.so -fPIC struct.c
gcc -shared -o libenum.so -fPIC enum.c
```

## Jextract

```sh
/usr/local/bin/jextract/jextract-21/bin/jextract -l proj -t nl.kadaster.proj /usr/include/proj.h
```
