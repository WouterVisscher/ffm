package nl.kadaster.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;

public class Stdlen {
  public static void main(final String[] args) throws Throwable {
    final var linker = Linker.nativeLinker();
    final var cStdLib = linker.defaultLookup();
    final var strlenSymbol = cStdLib.find("strlen")
        .orElseThrow(() -> new Exception("Could not find strlen"));
    // strlen takes a pointer and returns an size_t which afaik is a Java long on a
    // 64bit machine
    final var strlenSig = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
    // create a java handle to strlen
    final var strlen = Linker.nativeLinker().downcallHandle(strlenSymbol, strlenSig);
    // arena is a FFM concept that models memory segment lifecycle
    final var arena = Arena.ofAuto();
    // a pointer to a C string
    final var testStr = arena.allocateUtf8String("example");
    // invoke strlen
    final var result = strlen.invokeWithArguments(testStr);
    // 7
    System.out.println(result);
  }
}
