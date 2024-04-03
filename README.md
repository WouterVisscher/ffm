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
