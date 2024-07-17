#include <proj.h>

PJ_COORD px_proj_coord(double x, double y, double z, double t)
{
    return proj_coord(x,y,z,t);
}

PJ_CONTEXT *px_proj_context_create()
{
    return proj_context_create();
}

PJ *px_proj_create_crs_to_crs(PJ_CONTEXT *ctx, const char *source_crs,
                                    const char *target_crs, PJ_AREA *area)
{
    return proj_create_crs_to_crs(ctx, source_crs, target_crs, area);
}
