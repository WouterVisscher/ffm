package nl.kadaster.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.ValueLayout.JAVA_INT;

public class Cs2cs {

    public static void main(String[] args) throws Throwable {

        StructLayout PJ_INFO = MemoryLayout.structLayout(
                JAVA_INT.withName("major"),
                JAVA_INT.withName("minor"),
                JAVA_INT.withName("patch"));

        System.load("/usr/local/lib/libproj.so");
        SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

        final var proj_info_addr = symbolLookup.find("proj_info")
                .orElseThrow(() -> new Exception("Could not find verion"));

        final var proj_info_sig = FunctionDescriptor.of(PJ_INFO);
        final var test_proj_info = Linker.nativeLinker().downcallHandle(proj_info_addr, proj_info_sig);

        try (Arena offHeap = Arena.ofConfined()) {
            SegmentAllocator allocator = SegmentAllocator.slicingAllocator(offHeap.allocate(100));

            MemorySegment result = (MemorySegment) test_proj_info.invoke(allocator);
            result = result.reinterpret(PJ_INFO.byteSize());

            VarHandle major = PJ_INFO.varHandle(PathElement.groupElement("major"));
            VarHandle minor = PJ_INFO.varHandle(PathElement.groupElement("minor"));
            VarHandle patch = PJ_INFO.varHandle(PathElement.groupElement("patch"));

            System.out.println(result);

            // System.out.println(major.get(result));
            // System.out.println(minor.get(result));
            // System.out.println(patch.get(result));
        }
    }
}
