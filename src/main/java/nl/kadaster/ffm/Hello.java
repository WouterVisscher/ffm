package nl.kadaster.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;

public class Hello {
    public static void main(String[] args) throws Throwable {

        System.load("/home/wouter/work/tmp/ffm/cs2cs-ffm/src/main/c/libhello.so");
        SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

        final var helloSymbol = symbolLookup.find("hello")
                .orElseThrow(() -> new Exception("Could not find hello"));

        // strlen takes a pointer and returns an size_t which afaik is a Java long on a
        // 64bit machine
        final var helloSig = FunctionDescriptor.ofVoid();
        // create a java handle to strlen
        final var hello = Linker.nativeLinker().downcallHandle(helloSymbol, helloSig);

        try (Arena offHeap = Arena.ofConfined()) {
            // invoke hello
            hello.invoke();
        }
    }
}
