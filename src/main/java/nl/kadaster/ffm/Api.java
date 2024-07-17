package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

public class Api {

  final MethodHandle coord;

  final MethodHandle context;

  final MethodHandle pj;

  final MethodHandle transform;

  Api() throws Exception {
    System.load("/usr/local/lib/libproj.so");
    SymbolLookup symbolLookup = SymbolLookup.loaderLookup();

    // PJ_COORD proj_coord(double x, double y, double z, double t)
    coord = Linker.nativeLinker().downcallHandle(
        symbolLookup.find("proj_coord")
            .orElseThrow(() -> new Exception("Could not find proj_coord")),
        FunctionDescriptor.of(DataTypes.PJ_COORD, JAVA_DOUBLE, JAVA_DOUBLE, JAVA_DOUBLE,
            JAVA_DOUBLE));

    // PJ_CONTEXT *proj_context_create(void)
    context = Linker.nativeLinker().downcallHandle(
        symbolLookup.find("proj_context_create")
            .orElseThrow(() -> new Exception("Could not find proj_context_create")),
        FunctionDescriptor.of(ADDRESS));

    // PJ *proj_create_crs_to_crs(PJ_CONTEXT *ctx, const char *source_crs,
    // const char *target_crs, PJ_AREA *area) {
    pj = Linker.nativeLinker().downcallHandle(
        symbolLookup.find("proj_create_crs_to_crs")
            .orElseThrow(() -> new Exception("Could not find proj_create_crs_to_crs")),
        FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS));

    // PJ_COORD proj_trans(PJ *P, PJ_DIRECTION direction, PJ_COORD coord)
    transform = Linker.nativeLinker().downcallHandle(
        symbolLookup.find("proj_trans")
            .orElseThrow(() -> new Exception("Could not find proj_trans")),
        FunctionDescriptor.of(DataTypes.PJ_COORD, ADDRESS, JAVA_INT, DataTypes.PJ_COORD));
  }
}
