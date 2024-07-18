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
gcc -shared -o libprojproxyapi.so -fPIC projproxyapi.c
```

## Debug

```sh
ps -ef
gdb -p <pidnr> /usr/local/lib/libproj.so

add-symbol-file /usr/local/lib/libproj.so
break proj_context_create
cont
```

## Jextract

```sh
/usr/local/bin/jextract/jextract-22/bin/jextract -l proj -t nl.kadaster.proj /usr/include/proj.h
```
