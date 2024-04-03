package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.JAVA_CHAR;
import static java.lang.foreign.ValueLayout.JAVA_CHAR_UNALIGNED;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.ADDRESS;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.StructLayout;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;


public class Projversion {
        public static void main(String[] args) throws Throwable {

                StructLayout T_Version = MemoryLayout.structLayout(
                                JAVA_INT.withName("major"),
                                JAVA_INT.withName("minor"),
                                JAVA_INT.withName("patch"),                        
                                JAVA_CHAR.withName("release"),
                                JAVA_CHAR.withName("version"),
                                JAVA_CHAR.withName("searchpath"),
                                JAVA_CHAR.withName("paths"),
                                JAVA_INT.withName("path_count"));

                System.load("/home/wouter/work/repo/github.com/WouterVisscher/ffm/src/main/c/libprojversion.so");
                // System.load("/usr/local/lib/libproj.so");
                
                SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

                final var versionSymbol = symbolLookup.find("get_static_proj_info")
                                .orElseThrow(() -> new Exception("Could not find get_static_proj_info"));

                final var versionSig = FunctionDescriptor.of(T_Version);
                final var version = Linker.nativeLinker().downcallHandle(versionSymbol, versionSig);

                try (Arena arena = Arena.ofConfined()) {
                        SegmentAllocator allocator = SegmentAllocator.slicingAllocator(arena.allocate(24));

                        MemorySegment result = (MemorySegment) version.invoke(allocator);
                        System.out.println(result);
                        result = result.reinterpret(T_Version.byteSize());
                        
                        VarHandle major = T_Version.varHandle(PathElement.groupElement("major"));
                        VarHandle minor = T_Version.varHandle(PathElement.groupElement("minor"));
                        VarHandle patch = T_Version.varHandle(PathElement.groupElement("patch"));
                        // VarHandle paths = T_Version.varHandle(PathElement.groupElement("paths"));

                        System.out.println(major.get(result));
                        System.out.println(minor.get(result));
                        System.out.println(patch.get(result));
                        // System.out.println(patch.get(paths));
                }
        }
}
