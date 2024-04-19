package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;

public class Enum {

    public static void main(String[] args) throws Throwable {

        System.load("/home/wouter/work/repo/github.com/WouterVisscher/ffm/src/main/c/libenum.so");
        SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

        final var getenumSymbol = symbolLookup.find("getenum")
                .orElseThrow(() -> new Exception("Could not find getenum"));
        final var getenumSig = FunctionDescriptor.of(JAVA_INT);
        final var getenum = Linker.nativeLinker().downcallHandle(getenumSymbol, getenumSig);

        final var setenumSymbol = symbolLookup.find("setenum")
                .orElseThrow(() -> new Exception("Could not find setenum"));
        final var setenumSig = FunctionDescriptor.ofVoid(JAVA_INT);
        final var setenum = Linker.nativeLinker().downcallHandle(setenumSymbol, setenumSig);

        try (Arena arena = Arena.ofConfined()) {

            final int result = (int) getenum.invoke();
            System.out.println("java: " + result);
            setenum.invoke(result);
        }
    }

}
