package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;

public class Hello {
  public static void main(String[] args) throws Throwable {

    System.load("/home/wouter/work/repo/github.com/WouterVisscher/ffm/src/main/c/libhello.so");
    SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

    // final var helloSymbol = symbolLookup.find("hello")
    // .orElseThrow(() -> new Exception("Could not find hello"));
    // final var helloSig = FunctionDescriptor.ofVoid();
    // final var hello = Linker.nativeLinker().downcallHandle(helloSymbol, helloSig);

    final var gethelloSymbol =
        symbolLookup.find("get_hello").orElseThrow(() -> new Exception("Could not find gethello"));
    final var gethelloSig = FunctionDescriptor.of(ADDRESS);
    final var gethello = Linker.nativeLinker().downcallHandle(gethelloSymbol, gethelloSig);

    try (Arena offHeap = Arena.ofConfined()) {
      // invoke hello
      // hello.invoke();

      var layOut = MemoryLayout.sequenceLayout(31, JAVA_BYTE);
      MemorySegment result = (MemorySegment) gethello.invoke();

      result = result.reinterpret(layOut.byteSize());

      byte[] bytes = result.toArray(JAVA_BYTE);
      var out = new String(bytes);
      System.out.println(out);
    }
  }
}
