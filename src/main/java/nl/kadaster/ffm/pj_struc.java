package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.VarHandle;

public class pj_struc {
  public static void main(String[] args) throws Throwable {

    System.load("/home/wouter/work/repo/github.com/WouterVisscher/ffm/src/main/c/libhello.so");
    SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

    StructLayout T_Version = MemoryLayout.structLayout(ADDRESS.withName("version"),
        JAVA_INT.withName("major"), MemoryLayout.paddingLayout(4L));

    final var gethelloSymbol =
        symbolLookup.find("version").orElseThrow(() -> new Exception("Could not find version"));
    final var gethelloSig = FunctionDescriptor.of(T_Version);
    final var gethello = Linker.nativeLinker().downcallHandle(gethelloSymbol, gethelloSig);

    try (Arena offHeap = Arena.ofConfined()) {
      SegmentAllocator allocator = SegmentAllocator.prefixAllocator(offHeap.allocate(32));

      MemorySegment result = (MemorySegment) gethello.invoke(allocator);

      VarHandle major = T_Version.varHandle(PathElement.groupElement("major"));
      VarHandle version = T_Version.varHandle(PathElement.groupElement("version"));

      System.out.println(major.get(result, 0L));

      MemorySegment r_version = (MemorySegment) version.get(result, 0L);
      r_version = r_version.reinterpret(MemoryLayout.sequenceLayout(30, JAVA_BYTE).byteSize());

      byte[] bytes = r_version.toArray(JAVA_BYTE);
      var out = new String(bytes);
      System.out.println(out);
    }
  }
}
