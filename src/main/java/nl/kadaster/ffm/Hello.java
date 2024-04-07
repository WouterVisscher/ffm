package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_CHAR;

import java.lang.foreign.AddressLayout;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class Hello {
    public static void main(String[] args) throws Throwable {

        System.load("/home/wouter/work/repo/github.com/WouterVisscher/ffm/src/main/c/libhello.so");
        SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

        final var helloSymbol = symbolLookup.find("hello")
                .orElseThrow(() -> new Exception("Could not find hello"));             
        final var helloSig = FunctionDescriptor.ofVoid();
        final var hello = Linker.nativeLinker().downcallHandle(helloSymbol, helloSig);

        
        final var gethelloSymbol = symbolLookup.find("get_hello")
                .orElseThrow(() -> new Exception("Could not find hello"));
        final var gethelloSig = FunctionDescriptor.of(JAVA_BYTE);
        final var gethello = Linker.nativeLinker().downcallHandle(gethelloSymbol, gethelloSig);                

        try (Arena offHeap = Arena.ofConfined()) {
            // invoke hello
            hello.invoke();
            
            MemorySegment str = offHeap.allocate(MemoryLayout.sequenceLayout(32, JAVA_BYTE));

            // str = (MemorySegment) gethello.invoke();

            // MethodHandle methodHandle = str.sliceHandle();
            str = (MemorySegment) gethello.invoke();
            System.out.println(str);
            byte[] namedData = str.toArray(ValueLayout.JAVA_BYTE);
            var out = new String(namedData);
            System.out.println(out);

            // System.out.println(str.getUtf8String(0));
        }
    }
}
