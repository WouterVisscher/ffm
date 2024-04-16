package nl.kadaster.ffm;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BOOLEAN;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;

public class DataTypes {

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

    static final StructLayout PJ_INFO = MemoryLayout.structLayout(
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

    // https://github.com/OSGeo/PROJ/blob/master/src/proj_internal.h#L780
    // static final AddressLayout PJ_CONTEXT = ADDRESS.withName("PJ_CONTEXT");

    static final StructLayout PJ =  MemoryLayout.structLayout(
        ADDRESS.withName("pj_context"),
        ADDRESS.withName("short_name"),
        ADDRESS.withName("descr"));

    static final StructLayout PJ_CONTEXT = MemoryLayout.structLayout(
            ADDRESS.withName("lastFullErrorMessage"),
            JAVA_INT.withName("last_errno"),
            JAVA_INT.withName("debug_level"),
            JAVA_BOOLEAN.withName("errorIfBestTransformationNotAvailableDefault"),
            JAVA_BOOLEAN.withName("warnIfBestTransformationNotAvailableDefault")).withName("PJ_CONTEXT");

    static final StructLayout PJ_LP = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("lam"),
            JAVA_DOUBLE.withName("phi")).withName("pj_lp");

    // https://github.com/OSGeo/PROJ/blob/master/src/proj.h#L315
    static final StructLayout PJ_COORD = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("x"),
            JAVA_DOUBLE.withName("y"),
            JAVA_DOUBLE.withName("z"),
            JAVA_DOUBLE.withName("t"),
            // MemoryLayout.paddingLayout(8L), //PJ_XYZ
            // MemoryLayout.paddingLayout(8L), //PJ_UVW
            // MemoryLayout.paddingLayout(8L), //PJ_LPZT
            // MemoryLayout.paddingLayout(8L), //PJ_GEOD
            // MemoryLayout.paddingLayout(8L), //PJ_OPK
            // MemoryLayout.paddingLayout(8L), //PJ_ENU
            // MemoryLayout.paddingLayout(8L), //PJ_XYZ
            // MemoryLayout.paddingLayout(8L), //PJ_UVW
            // MemoryLayout.paddingLayout(8L), //PJ_LPZ
            // MemoryLayout.paddingLayout(8L), //PJ_XY
            // MemoryLayout.paddingLayout(8L), //PJ_UV
            ADDRESS.withName("pj_lp")).withName("pj_coord");

}
