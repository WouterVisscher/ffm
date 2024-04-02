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

        try (Arena offHeap = Arena.ofConfined()) {

            Linker linker = Linker.nativeLinker();
            SymbolLookup proj = SymbolLookup.libraryLookup("libproj.so", offHeap);
            MemorySegment proj_info_addr = proj.find("proj_info").get();
            FunctionDescriptor proj_info_sig = FunctionDescriptor.of(PJ_INFO);

            MethodHandle test_proj_info = linker.downcallHandle(proj_info_addr,
                    proj_info_sig);

            SegmentAllocator allocator = SegmentAllocator.slicingAllocator(offHeap.allocate(100));
            MemorySegment result = (MemorySegment) test_proj_info.invokeWithArguments(allocator);
            result = result.reinterpret(PJ_INFO.byteSize());

            VarHandle major = PJ_INFO.varHandle(PathElement.groupElement("major"));
            VarHandle minor = PJ_INFO.varHandle(PathElement.groupElement("minor"));
            VarHandle patch = PJ_INFO.varHandle(PathElement.groupElement("patch"));

            System.out.println(result);

            System.out.println(major.get(result));
            System.out.println(minor.get(result));
            System.out.println(patch.get(result));
        }
    }
}
