#include <proj.h>
#include <stdio.h>

int main(void)
{
    PJ_CONTEXT *C = proj_context_create();

    PJ_INFO pj_str = proj_info();
    printf("%d.%d", pj_str.major, pj_str.minor);    
    return 0;
}