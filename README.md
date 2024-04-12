# JAVA ffm stuff

## C

```sh
gcc proj_version.cpp -lproj -o proj_version -lstdc++
./proj_version

gcc -shared -o libprojversion.so -fPIC proj_version.cpp -lproj -lstdc++
```

build lib

```sh
gcc -shared -o libhello.so -fPIC hello.c
gcc -shared -o libstruct.so -fPIC struct.c
```

## Jextract

```sh
/usr/local/bin/jextract/jextract-21/bin/jextract -l proj -t nl.kadaster.proj /usr/include/proj.h
```
