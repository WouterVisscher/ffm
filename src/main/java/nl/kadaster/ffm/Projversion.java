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

public class Projversion {

        // {
        // major = 9,
        // minor = 3,
        // patch = 1,
        // release = 0x7f361a4ddf30 <pj_release> "Rel. 9.3.1, December 1st, 2023",
        // version = 0x7f361a5a05a0 <version> "9.3.1",
        // searchpath = 0x7f364c2976b0
        // "/home/wouter/.local/share/proj:/usr/local/share/proj:/usr/local/share/proj",
        // paths = 0x0,
        // path_count = 0
        // }
        public static void main(String[] args) throws Throwable {

                StructLayout T_Version = MemoryLayout.structLayout(
                                JAVA_INT.withName("major"),
                                JAVA_INT.withName("minor"),
                                JAVA_INT.withName("patch"),
                                MemoryLayout.paddingLayout(4L),
                                ADDRESS.withName("release"),
                                ADDRESS.withName("version"),
                                ADDRESS.withName("searchpath"),
                                ADDRESS.withName("paths"),
                                JAVA_INT.withName("path_count"),
                                MemoryLayout.paddingLayout(4L)).withName("PJ_INFO");

                // System.load("/home/wouter/work/repo/github.com/WouterVisscher/ffm/src/main/c/libprojversion.so");
                System.load("/usr/local/lib/libproj.so");

                SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

                final var versionSymbol = symbolLookup.find("proj_info")
                                .orElseThrow(() -> new Exception("Could not find get_static_proj_info"));

                final var versionSig = FunctionDescriptor.of(T_Version);
                final var proj_version = Linker.nativeLinker().downcallHandle(versionSymbol, versionSig);

                try (Arena arena = Arena.ofConfined()) {
                        SegmentAllocator allocator = SegmentAllocator.slicingAllocator(arena.allocate(200));

                        MemorySegment result = (MemorySegment) proj_version.invokeExact(allocator);
                        // System.out.println(result);
                        result = result.reinterpret(T_Version.byteSize());

                        VarHandle major = T_Version.varHandle(PathElement.groupElement("major"));
                        VarHandle minor = T_Version.varHandle(PathElement.groupElement("minor"));
                        VarHandle patch = T_Version.varHandle(PathElement.groupElement("patch"));
                        VarHandle release = T_Version.varHandle(PathElement.groupElement("release"));
                        VarHandle version = T_Version.varHandle(PathElement.groupElement("version"));
                        VarHandle searchpath = T_Version.varHandle(PathElement.groupElement("searchpath"));
                        VarHandle paths = T_Version.varHandle(PathElement.groupElement("paths"));
                        VarHandle path_count = T_Version.varHandle(PathElement.groupElement("path_count"));

                        System.out.println(major.get(result));
                        System.out.println(minor.get(result));
                        System.out.println(patch.get(result));

                        // release
                        MemorySegment ms_release = (MemorySegment) release.get(result);
                        var r_release = ms_release.reinterpret(MemoryLayout.sequenceLayout(30, JAVA_BYTE).byteSize());

                        byte[] bytes = r_release.toArray(JAVA_BYTE);
                        var out = new String(bytes);
                        System.out.println(out);

                        MemorySegment ms_version = (MemorySegment) version.get(result);
                        var r_version = ms_version.reinterpret(MemoryLayout.sequenceLayout(30, JAVA_BYTE).byteSize());

                        bytes = r_version.toArray(JAVA_BYTE);
                        out = new String(bytes);
                        System.out.println(out);

                        MemorySegment ms_searchpath = (MemorySegment) searchpath.get(result);
                        var r_searchpath = ms_searchpath
                                        .reinterpret(MemoryLayout.sequenceLayout(128, JAVA_BYTE).byteSize());

                        bytes = r_searchpath.toArray(JAVA_BYTE);
                        out = new String(bytes);
                        System.out.println(out);

                        MemorySegment ms_paths = (MemorySegment) paths.get(result);

                        System.out.println(ms_paths.address());
                        // var r_paths = ms_paths.reinterpret(MemoryLayout.sequenceLayout(0,
                        // JAVA_BYTE).byteSize());

                        // bytes = r_paths.toArray(JAVA_BYTE);
                        // out = new String(bytes);
                        // System.out.println(out);

                        System.out.println(path_count.get(result));
                }
        }

}
