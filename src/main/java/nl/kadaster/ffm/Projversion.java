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
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.VarHandle;

public class Projversion {

  public static void main(String[] args) throws Throwable {

    System.load("/usr/local/lib/libproj.so");
    SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

    final var versionSymbol =
        symbolLookup.find("proj_info").orElseThrow(() -> new Exception("Could not find proj_info"));

    final var versionSig = FunctionDescriptor.of(DataTypes.PJ_INFO);
    final var proj_version = Linker.nativeLinker().downcallHandle(versionSymbol, versionSig);

    final var proj_coordSymbol = symbolLookup.find("proj_coord")
        .orElseThrow(() -> new Exception("Could not find proj_coord"));

    final var proj_coordSig = FunctionDescriptor.of(DataTypes.PJ_COORD, JAVA_DOUBLE, JAVA_DOUBLE,
        JAVA_DOUBLE, JAVA_DOUBLE);
    final var proj_coord = Linker.nativeLinker().downcallHandle(proj_coordSymbol, proj_coordSig);

    final var proj_context_createSymbol = symbolLookup.find("proj_context_create")
        .orElseThrow(() -> new Exception("Could not find proj_context_create"));

    final var proj_context_createSig = FunctionDescriptor.of(ADDRESS);
    final var proj_context_create =
        Linker.nativeLinker().downcallHandle(proj_context_createSymbol, proj_context_createSig);

    final var proj_create_crs_to_crsSymbol = symbolLookup.find("proj_create_crs_to_crs")
        .orElseThrow(() -> new Exception("Could not find proj_create_crs_to_crs"));

    final var proj_create_crs_to_crsSig =
        FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS);
    final var proj_create_crs_to_crs = Linker.nativeLinker()
        .downcallHandle(proj_create_crs_to_crsSymbol, proj_create_crs_to_crsSig);

    // PROJ_TRANSFORM
    final var proj_transSymbol = symbolLookup.find("proj_trans")
        .orElseThrow(() -> new Exception("Could not find proj_trans"));

    final var proj_transSig = FunctionDescriptor.of(DataTypes.PJ_COORD, ADDRESS, JAVA_INT, ADDRESS);
    final var proj_trans = Linker.nativeLinker().downcallHandle(proj_transSymbol, proj_transSig);

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
      var r_searchpath =
          ms_searchpath.reinterpret(MemoryLayout.sequenceLayout(128, JAVA_BYTE).byteSize());

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

      MemorySegment ms_proj_context = (MemorySegment) proj_context_create.invokeExact();
      // var r_proj_context =
      // ms_proj_context.reinterpret(DataTypes.PJ_CONTEXT.byteSize());

      // VarHandle last_errno =
      // DataTypes.PJ_CONTEXT.varHandle(PathElement.groupElement("last_errno"));
      // VarHandle debug_level =
      // DataTypes.PJ_CONTEXT.varHandle(PathElement.groupElement("debug_level"));
      // VarHandle errorIfBestTransformationNotAvailableDefault =
      // DataTypes.PJ_CONTEXT.varHandle(
      // PathElement.groupElement("errorIfBestTransformationNotAvailableDefault"));
      // VarHandle warnIfBestTransformationNotAvailableDefault =
      // DataTypes.PJ_CONTEXT.varHandle(
      // PathElement.groupElement("warnIfBestTransformationNotAvailableDefault"));
      // VarHandle cpp_context = DataTypes.PJ_CONTEXT.varHandle(
      // PathElement.groupElement("warnIfBestTransformationNotAvailableDefault"));

      // // System.out.println(last_errno.get(r_proj_context));
      // // System.out.println(debug_level.get(r_proj_context));
      // //
      // System.out.println(errorIfBestTransformationNotAvailableDefault.get(r_proj_context));
      // //
      // System.out.println(warnIfBestTransformationNotAvailableDefault.get(r_proj_context));
      // System.out.println(cpp_context.get(r_proj_context));

      final var sourceCrs = allocator.allocateFrom("EPSG:28992");
      final var targetCrs = allocator.allocateFrom("EPSG:9067");
      final var NULL = MemorySegment.NULL;

      MemorySegment ms_proj_create_crs_to_crs = (MemorySegment) proj_create_crs_to_crs
          .invokeExact(ms_proj_context, sourceCrs, targetCrs, NULL);
      // var r_proj_create_crs_to_crs =
      // ms_proj_create_crs_to_crs.reinterpret(DataTypes.PJ.byteSize());
      // System.out.println(r_proj_create_crs_to_crs);

      // VarHandle pj_context =
      // DataTypes.PJ.varHandle(PathElement.groupElement("pj_context"));
      // // VarHandle short_name =
      // DataTypes.PJ.varHandle(PathElement.groupElement("short_name"));
      // // VarHandle descr =
      // DataTypes.PJ.varHandle(PathElement.groupElement("descr"));

      // MemorySegment ms_pj_context = (MemorySegment)
      // pj_context.get(r_proj_create_crs_to_crs);
      // var r_pj_context = ms_pj_context
      // .reinterpret(DataTypes.PJ_CONTEXT.byteSize());

      // // System.out.println(last_errno.get(r_pj_context));
      // // System.out.println(debug_level.get(r_pj_context));
      // //
      // System.out.println(errorIfBestTransformationNotAvailableDefault.get(r_pj_context));
      // //
      // System.out.println(warnIfBestTransformationNotAvailableDefault.get(r_pj_context));
      // System.out.println(cpp_context.get(r_pj_context));

      // bytes = r_short_name.toArray(JAVA_BYTE);
      // out = new String(bytes);
      // System.out.println(r_pj_context);

      // MemorySegment ms_short_name = (MemorySegment)
      // short_name.get(r_proj_create_crs_to_crs);
      // var r_short_name = ms_short_name
      // .reinterpret(MemoryLayout.sequenceLayout(128, JAVA_BYTE).byteSize());

      // bytes = r_short_name.toArray(JAVA_BYTE);
      // out = new String(bytes);
      // System.out.println(out);

      // MemorySegment ms_descr = (MemorySegment) descr.get(r_proj_create_crs_to_crs);
      // var r_descr = ms_descr
      // .reinterpret(MemoryLayout.sequenceLayout(32, JAVA_BYTE).byteSize());

      // bytes = r_descr.toArray(JAVA_BYTE);
      // out = new String(bytes);
      // System.out.println(out);

      final int fwd = 1;

      MemorySegment ms_proj_trans =
          (MemorySegment) proj_trans.invoke(ms_proj_create_crs_to_crs, fwd, r_ms_proj_coord);
      System.out.println(ms_proj_trans);
      // var r_proj_trans = ms_proj_trans
      // .reinterpret(DataTypes.PJ_COORD.byteSize());

      // System.out.println(xx.get(r_proj_trans));
      // System.out.println(yy.get(r_proj_trans));
      // System.out.println(zz.get(r_proj_trans));
    }
  }
}
