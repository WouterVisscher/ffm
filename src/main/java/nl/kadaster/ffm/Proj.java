package nl.kadaster.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.invoke.VarHandle;


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

      MemorySegment context = (MemorySegment) api.context.invoke();
      context = context.reinterpret(DataTypes.PJ_CONTEXT.byteSize());

      final var sourceCrs = allocator.allocateFrom("EPSG:28992");
      final var targetCrs = allocator.allocateFrom("EPSG:9067");
      final var NULL = MemorySegment.NULL;

      MemorySegment pj = (MemorySegment) api.pj.invokeExact(context, sourceCrs, targetCrs, NULL);
      pj = pj.reinterpret(DataTypes.PJ.byteSize());

      MemorySegment transformedpoint =
          (MemorySegment) api.transform.invoke(allocator, pj, 1, coord);

      VarHandle xx = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("x"));
      VarHandle yy = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("y"));
      VarHandle zz = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("z"));
      VarHandle tt = DataTypes.PJ_COORD.varHandle(PathElement.groupElement("t"));

      System.out.println(xx.get(transformedpoint, 0L));
      System.out.println(yy.get(transformedpoint, 0L));
      System.out.println(zz.get(transformedpoint, 0L));
      System.out.println(tt.get(transformedpoint, 0L));
    }
  }
}
