#include <proj.h>
#include <stdio.h>

struct T_Version
{
    int major;
    int minor;
    int patch;
};

typedef struct T_Version TT_Version;

// static PJ_INFO info = {0, 0, 0, nullptr, nullptr, nullptr, nullptr, 0};

extern "C" TT_Version get_proj_info(void)
{
    printf("I'm in get_proj_info()\n");
    PJ_INFO pj_str = proj_info();
    TT_Version v;
    v.major = pj_str.major;
    v.minor = pj_str.minor;
    v.patch = pj_str.patch;
    return v;
}

extern "C" struct PJ_INFO get_static_proj_info(void)
{  
    printf("I'm in get_static_proj_info()\n");
    struct PJ_INFO pj_str = proj_info();
    printf("I've got a PJ_INFO: %d.%d.%d\n", pj_str.major, pj_str.minor, pj_str.patch);
    return pj_str;
}

int main(void)
{
    // PJ_CONTEXT *C = proj_context_create();

    struct PJ_INFO pj_str = get_static_proj_info();
    printf("%d.%d.%d\n", pj_str.major, pj_str.minor, pj_str.patch);
    return 0;
}