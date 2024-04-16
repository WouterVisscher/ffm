package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
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

        public static void main(String[] args) throws Throwable {

                System.load("/usr/local/lib/libproj.so");

                SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

                final var versionSymbol = symbolLookup.find("proj_info")
                                .orElseThrow(() -> new Exception("Could not find get_static_proj_info"));

                final var versionSig = FunctionDescriptor.of(DataTypes.PJ_INFO);
                final var proj_version = Linker.nativeLinker().downcallHandle(versionSymbol, versionSig);

                final var proj_coordSymbol = symbolLookup.find("proj_coord")
                                .orElseThrow(() -> new Exception("Could not find get_static_proj_info"));

                final var proj_coordSig = FunctionDescriptor.of(DataTypes.PJ_COORD, JAVA_DOUBLE, JAVA_DOUBLE, JAVA_DOUBLE, JAVA_DOUBLE);
                final var proj_coord = Linker.nativeLinker().downcallHandle(proj_coordSymbol, proj_coordSig);                

                try (Arena arena = Arena.ofConfined()) {
                        SegmentAllocator allocator = SegmentAllocator.slicingAllocator(arena.allocate(200));

                        MemorySegment result = (MemorySegment) proj_version.invokeExact(allocator);
                        // System.out.println(result);
                        result = result.reinterpret(DataTypes.PJ_INFO.byteSize());

                        VarHandle major = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("major"));
                        VarHandle minor = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("minor"));
                        VarHandle patch = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("patch"));
                        VarHandle release = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("release"));
                        VarHandle version = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("version"));
                        VarHandle searchpath = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("searchpath"));
                        VarHandle paths = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("paths"));
                        VarHandle path_count = DataTypes.PJ_INFO.varHandle(PathElement.groupElement("path_count"));

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
                        System.out.println(path_count.get(result));

                        /// Create proj_coord
                        final var x = 155000d;
                        final var y = 463000d;
                        final var z = 0d;
                        final var t = 0d;

                        MemorySegment ms_proj_coord = (MemorySegment) proj_coord.invokeExact(allocator, x, y, z, t);
                        var r_ms_proj_coord = ms_proj_coord.reinterpret(DataTypes.PJ_COORD.byteSize());

                        VarHandle xx = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("x"));
                        VarHandle yy = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("y"));
                        VarHandle zz = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("z"));
                        VarHandle tt = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("t"));

                        System.out.println(xx.get(r_ms_proj_coord));
                        System.out.println(yy.get(r_ms_proj_coord));
                        System.out.println(zz.get(r_ms_proj_coord));
                        System.out.println(tt.get(r_ms_proj_coord));


                }
        }

}
