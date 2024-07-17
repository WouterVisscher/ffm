package nl.kadaster.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;


public class Proj {
  public static void main(String[] args) throws Throwable {

    /// Create proj_coord
    final var x = 155000d;
    final var y = 463000d;
    final var z = 0d;
    final var t = 0d;

    Api api = new Api();

    try (Arena arena = Arena.ofConfined()) {
      SegmentAllocator allocator = SegmentAllocator.slicingAllocator(arena.allocate(200));

      MemorySegment coord = (MemorySegment) api.coord.invokeExact(allocator, x, y, z, t);
      System.out.println("coord: " + coord);
      // coord = coord.reinterpret(DataTypes.PJ_COORD.byteSize());

      MemorySegment context = (MemorySegment) api.context.invoke();
      context = context.reinterpret(DataTypes.PJ_CONTEXT.byteSize());

      // MemorySegment ctx = arena.allocate(ADDRESS, context);

      final var sourceCrs = allocator.allocateUtf8String("EPSG:28992");
      final var targetCrs = allocator.allocateUtf8String("EPSG:9067");
      final var NULL = MemorySegment.NULL;

      MemorySegment pj = (MemorySegment) api.pj.invokeExact(context, sourceCrs, targetCrs, NULL);
      pj = pj.reinterpret(DataTypes.PJ.byteSize());
      // System.out.println("pj: " + pj);

      // var pj_ctx = ((MemorySegment)
      // DataTypes.pj_context.get(pj)).reinterpret(DataTypes.PJ_CONTEXT.byteSize());
      // var short_name = ((MemorySegment) DataTypes.short_name.get(pj));
      // short_name = short_name.reinterpret(MemoryLayout.sequenceLayout(128,
      // JAVA_BYTE).byteSize());

      // byte[] bytes = short_name.toArray(JAVA_BYTE);
      // var out = new String(bytes);
      // System.out.println(out);

      // System.out.println(DataTypes.WarnIfBestTransformationNotAvailableDefault.get(pj_ctx));
    }
  }
}
