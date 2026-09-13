#version 330 core

layout(location = 0) in vec2 inLocal;
layout(location = 1) in vec4 inCorner0;
layout(location = 2) in vec4 inCorner1;
layout(location = 3) in ivec4 inClip;
layout(location = 4) in vec4 inClipRadii;
layout(location = 5) in vec4 inRect;
layout(location = 6) in uvec4 inColors;
layout(location = 7) in vec4 inRadii;
layout(location = 8) in vec4 inUvPack;
layout(location = 9) in int inFlags;
layout(location = 10) in int inTexSlot;

uniform vec2 uViewport;

out vec2 vLocalPx;
out vec2 vSize;
flat out vec4 vColorTL;
flat out vec4 vColorTR;
flat out vec4 vColorBR;
flat out vec4 vColorBL;
flat out vec4 vRadii;
flat out uint vFlags;
flat out ivec4 vClip;
flat out vec4 vClipRadii;
out vec2 vUV;
flat out vec2 vUvScale;
flat out vec2 vUvOffset;
flat out int vTexSlot;
out vec2 vPosPx;

void main() {
    vec2 size = vec2(inRect.z, inRect.w);
    vec2 local = inLocal;
    uint flags = uint(inFlags);
    vSize = size;
    vLocalPx = local * size;

    uint cq1, cq2, cq3;
    uint c0 = inColors.x; cq1 = c0 / 256u; cq2 = cq1 / 256u; cq3 = cq2 / 256u;
    vColorTL = vec4(float(c0 - cq1 * 256u), float(cq1 - cq2 * 256u), float(cq2 - cq3 * 256u), float(cq3)) / 255.0;
    uint c1 = inColors.y; cq1 = c1 / 256u; cq2 = cq1 / 256u; cq3 = cq2 / 256u;
    vColorTR = vec4(float(c1 - cq1 * 256u), float(cq1 - cq2 * 256u), float(cq2 - cq3 * 256u), float(cq3)) / 255.0;
    uint c2 = inColors.z; cq1 = c2 / 256u; cq2 = cq1 / 256u; cq3 = cq2 / 256u;
    vColorBR = vec4(float(c2 - cq1 * 256u), float(cq1 - cq2 * 256u), float(cq2 - cq3 * 256u), float(cq3)) / 255.0;
    uint c3 = inColors.w; cq1 = c3 / 256u; cq2 = cq1 / 256u; cq3 = cq2 / 256u;
    vColorBL = vec4(float(c3 - cq1 * 256u), float(cq1 - cq2 * 256u), float(cq2 - cq3 * 256u), float(cq3)) / 255.0;

    vRadii = inRadii;
    vFlags = flags;
    vClip = inClip;
    vClipRadii = inClipRadii;
    vTexSlot = inTexSlot;

    vec2 uv0 = inUvPack.xy;
    vec2 uv1 = inUvPack.zw;
    vUV = mix(uv0, uv1, local);
    vUvScale = uv0;
    vUvOffset = uv1;

    vec2 posTL = inCorner0.xy;
    vec2 posTR = inCorner0.zw;
    vec2 posBR = inCorner1.xy;
    vec2 posBL = inCorner1.zw;

    vec2 top = mix(posTL, posTR, local.x);
    vec2 bottom = mix(posBL, posBR, local.x);
    vec2 posPx = mix(top, bottom, local.y);
    vPosPx = posPx;

    vec2 ndc = vec2((posPx.x / uViewport.x) * 2.0 - 1.0,
                    1.0 - (posPx.y / uViewport.y) * 2.0);
    gl_Position = vec4(ndc, 0.0, 1.0);
}
