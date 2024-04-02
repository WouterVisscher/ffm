package nl.kadaster.ffm;

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

public class Struct {
    public static void main(String[] args) throws Throwable {

        StructLayout T_Version = MemoryLayout.structLayout(
                JAVA_INT.withName("major"),
                JAVA_INT.withName("minor"),
                JAVA_INT.withName("patch"));

        System.load("/home/wouter/work/tmp/ffm/cs2cs-ffm/src/main/c/libstruct.so");
        SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

        final var versionSymbol = symbolLookup.find("verion")
                .orElseThrow(() -> new Exception("Could not find verion"));

        // strlen takes a pointer and returns an size_t which afaik is a Java long on a
        // 64bit machine
        final var versionSig = FunctionDescriptor.of(T_Version);
        // create a java handle to strlen
        final var version = Linker.nativeLinker().downcallHandle(versionSymbol, versionSig);

        try (Arena offHeap = Arena.ofConfined()) {
            SegmentAllocator allocator = SegmentAllocator.slicingAllocator(offHeap.allocate(100));

            MemorySegment result = (MemorySegment) version.invoke(allocator);
            System.out.println(result);
            result = result.reinterpret(T_Version.byteSize());
            VarHandle major // (MemorySegment, long) -> int
                    = T_Version.varHandle(PathElement.groupElement("major"));
            VarHandle minor // (MemorySegment, long) -> int
                    = T_Version.varHandle(PathElement.groupElement("minor"));
            VarHandle patch // (MemorySegment, long) -> int
                    = T_Version.varHandle(PathElement.groupElement("patch"));

            System.out.println(major.get(result));
            System.out.println(minor.get(result));
            System.out.println(patch.get(result));

        }
    }
}
