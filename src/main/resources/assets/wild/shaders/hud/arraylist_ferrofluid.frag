#version 330 core

in vec2 vUv;

uniform vec2 uResolution;
uniform float uTime;
uniform vec4 uDrawRect;
uniform int uRowCount;
uniform vec4 uRows[96];
uniform vec4 uMotionRows[96];
uniform float uRadius;
uniform float uDirection;
uniform float uAlpha;
uniform vec4 uPointer;
uniform float uExposure;
uniform vec4 uSurfaceColor;
uniform vec4 uOutlineColor;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uOutline;
uniform float uGlow;
uniform float uEdgeHighlight;
uniform float uLightMode;
uniform float uFluidCohesion;
uniform float uSoft;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += noise(p) * a;
        p = mat2(1.58, -1.11, 1.11, 1.58) * p + vec2(5.17, -8.41);
        a *= 0.5;
    }
    return v;
}

float sdRoundBox(vec2 p, vec2 halfSize, float radius) {
    vec2 h = max(halfSize, vec2(0.5));
    float r = clamp(radius, 0.0, min(h.x, h.y));
    vec2 q = abs(p) - h + vec2(r);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

float smin(float a, float b, float k) {
    float h = clamp(0.5 + 0.5 * (b - a) / max(k, 0.0001), 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0 - h);
}

void main() {
    vec2 pixel = vec2(vUv.x * uResolution.x, (1.0 - vUv.y) * uResolution.y);
    vec2 clipMin = uDrawRect.xy;
    vec2 clipMax = uDrawRect.xy + uDrawRect.zw;
    vec2 clipEdge = min(pixel - clipMin, clipMax - pixel);
    float clipMask = smoothstep(-1.0, 2.0, min(clipEdge.x, clipEdge.y));
    if (clipMask <= 0.001) {
        discard;
    }

    float d = 100000.0;
    float field = 0.0;
    float rowPhase = 0.0;
    float minLocalY = 100000.0;
    float maxLocalY = -100000.0;
    vec2 nearestCenter = vec2(0.0);
    vec4 nearestMotion = vec4(0.0, 0.0, 0.0, 1.0);
    float nearestDist = 100000.0;
    float exposureField = 0.0;
    float focusField = 0.0;
    float velocityField = 0.0;
    float k = uFluidCohesion > 0.5 ? clamp(uFluidCohesion, 1.0, 48.0) : 12.0;

    for (int i = 0; i < 96; i++) {
        if (i >= uRowCount) {
            break;
        }
        vec4 row = uRows[i];
        vec4 motion = uMotionRows[i];
        vec2 halfSize = row.zw * 0.5;
        vec2 center = row.xy + halfSize;
        float speed = min(length(motion.xy), 260.0);
        float shear = clamp((motion.x + motion.y * mix(-0.70, 0.70, uDirection)) * 0.026, -10.0, 10.0);
        vec2 p = pixel - center;
        p.x -= p.y / max(row.w, 1.0) * shear;
        vec2 stretch = vec2(abs(motion.x) * 0.020, abs(motion.y) * 0.014);
        float r = min(uRadius + speed * 0.006, min(row.z, row.w) * 0.5);
        float rd = sdRoundBox(p, halfSize + stretch, r);
        d = i == 0 ? rd : smin(d, rd, k);
        float band = exp(-max(rd, 0.0) * 0.040) * exp(-abs(rd) * 0.006);
        field += band;
        float cd = length(pixel - center);
        if (cd < nearestDist) {
            nearestDist = cd;
            nearestCenter = center;
            nearestMotion = motion;
        }
        minLocalY = min(minLocalY, row.y);
        maxLocalY = max(maxLocalY, row.y + row.w);
        float vicinity = exp(-max(rd, 0.0) * 0.050) * exp(-abs(rd) * 0.016);
        exposureField += motion.z * vicinity;
        focusField += motion.w * vicinity;
        velocityField += speed * 0.0042 * vicinity;
        rowPhase += float(i + 1) * 0.017;
    }

    float averageFocus = sat(focusField / max(field, 0.001));
    float focusPull = 1.0 - sat(max(nearestMotion.w, averageFocus * 0.86));
    vec2 local = pixel - nearestCenter;
    float angle = atan(local.y, local.x);
    float flow = uTime * (0.72 + uDirection * 0.20 + velocityField * 0.028) + rowPhase;
    float veins = fbm(pixel * 0.026 + vec2(flow * 0.26, -flow * 0.15));
    float micro = fbm(pixel * 0.082 + vec2(-flow * 0.58, flow * 0.35));
    float oil = fbm(pixel * 0.145 + vec2(flow * 0.21, flow * 0.18));
    float wave = sin(angle * 9.0 + pixel.y * 0.030 - pixel.x * (0.010 + uDirection * 0.014) + flow * 1.9 + veins * 3.8);
    float membrane = ((veins - 0.5) * 1.02 + wave * 0.46 + (micro - 0.5) * 0.54) * exp(-abs(d) * 0.132) * (0.62 + uGlow * 0.22 + velocityField * 0.10);
    float liquidD = d + membrane + focusPull * (micro - 0.5) * 2.35;
    float aa = max(fwidth(liquidD), 1.0) + focusPull * 2.75;
    float fill = 1.0 - smoothstep(0.0, aa, liquidD);
    float outer = exp(-max(liquidD, 0.0) * 0.120);
    float innerEdge = exp(-abs(liquidD) * 0.240);
    float rim = 1.0 - smoothstep(0.0, aa * 1.35 + 0.65, abs(liquidD));
    float needles = pow(sat(wave * 0.5 + 0.5), 5.4) * exp(-abs(liquidD + 0.9) * 0.190);
    float caustic = pow(sat(0.36 + 0.64 * sin(pixel.x * 0.045 + pixel.y * 0.020 + flow * 2.8 + micro * 3.2)), 2.8);
    float vertical = sat((pixel.y - minLocalY) / max(maxLocalY - minLocalY, 1.0));
    vec3 themeAccent = mix(uAccentTop, uAccentBottom, vertical);
    vec3 spectralA = vec3(0.17, 0.60, 1.00);
    vec3 spectralB = vec3(0.76, 0.36, 1.00);
    vec3 spectralC = vec3(0.20, 1.00, 0.78);
    vec3 prism = mix(mix(spectralA, spectralB, vertical), spectralC, oil * 0.18);
    vec3 accent = mix(themeAccent, prism, 0.10);
    float edgeX = mix(0.0, 1.0, uDirection);
    float localEdge = 0.0;
    float activeRowEdge = 0.0;
    float meniscusGlow = 0.0;
    float anchorSideShadow = 0.0;
    for (int i = 0; i < 96; i++) {
        if (i >= uRowCount) {
            break;
        }
        vec4 row = uRows[i];
        float inY = smoothstep(row.y - 1.5, row.y + 2.0, pixel.y) * (1.0 - smoothstep(row.y + row.w - 2.0, row.y + row.w + 1.5, pixel.y));
        float ex = mix(row.x, row.x + row.z, edgeX);
        float sideDistance = abs(pixel.x - ex);
        localEdge = max(localEdge, exp(-pow(sideDistance / 7.8, 2.0)) * inY);
        activeRowEdge = max(activeRowEdge, exp(-pow(sideDistance / 1.55, 2.0)) * inY);
        meniscusGlow = max(meniscusGlow, exp(-pow(sideDistance / 18.0, 2.0)) * inY);
        float outward = (pixel.x - ex) * mix(-1.0, 1.0, uDirection);
        anchorSideShadow = max(anchorSideShadow, smoothstep(0.0, 13.0, outward) * inY * exp(-(outward / 22.0) * (outward / 22.0)));
    }
    float pointerGlow = uPointer.z * exp(-pow(length(pixel - uPointer.xy) / max(uPointer.w, 1.0), 2.0)) * fill;
    float exposureBurst = sat(uExposure + exposureField * 0.36 + pointerGlow * 0.25);
    vec3 liquid = uSurfaceColor.rgb;
    float light = sat(uLightMode);
    vec3 cold = mix(vec3(0.016, 0.018, 0.028), liquid + themeAccent * 0.035, 0.86);
    vec3 brightGel = mix(vec3(0.955, 0.982, 1.0), liquid + themeAccent * 0.070, 0.52);
    float oilFilm = pow(sat(0.42 + 0.58 * sin(oil * 5.5 + veins * 3.0 + vertical * 2.4 - flow * 0.7)), 3.2);
    vec3 bodyDark = mix(cold, liquid + accent * 0.055, sat(0.18 + field * 0.10 + caustic * 0.052));
    float fresnel = pow(sat(1.0 - abs(dot(normalize(vec3(dFdx(liquidD), dFdy(liquidD), 1.35)), vec3(0.0, 0.0, 1.0)))), 2.2);
    vec3 bodyLight = mix(brightGel, vec3(1.0), 0.14 + fresnel * 0.18);
    bodyLight += accent * (0.070 + fresnel * 0.180 + caustic * 0.050);
    bodyLight -= vec3(0.035, 0.038, 0.045) * vertical * (0.20 + field * 0.05);
    vec3 body = mix(bodyDark, bodyLight, light);
    body += accent * (innerEdge * 0.112 + needles * 0.095 + caustic * fill * 0.046);
    body += accent * localEdge * fill * uEdgeHighlight * mix(0.220 + 0.070 * uGlow, 0.125 + fresnel * 0.090, light);
    body += vec3(0.90, 0.98, 1.0) * activeRowEdge * fill * uEdgeHighlight * mix(0.082, 0.118, light);
    body += mix(themeAccent, prism, 0.20) * oilFilm * fill * 0.060;
    body += vec3(1.0) * rim * mix(0.014, 0.040, light);
    body += vec3(0.95, 0.985, 1.0) * pointerGlow * mix(0.12, 0.16, light);
    body = mix(body, vec3(1.0), exposureBurst * (0.12 + rim * 0.16 + fresnel * 0.12));
    body += accent * exposureBurst * (0.13 + needles * 0.16 + innerEdge * 0.06);

    float glow = innerEdge * fill * mix(0.020 + needles * 0.030, 0.014 + needles * 0.020 + fresnel * 0.022, light) * uGlow * uAlpha;
    float outline = rim * uOutline * uOutlineColor.a;
    float edgeAlpha = fill * (localEdge * mix(0.22, 0.13, light) + activeRowEdge * mix(0.32, 0.20, light) + meniscusGlow * mix(0.12, 0.10, light)) * uEdgeHighlight * uAlpha;
    vec3 color = body;
    color = mix(color, uOutlineColor.rgb, sat(outline * 0.16));
    color += accent * glow * 0.52;
    color += accent * needles * fill * 0.080;

    float softness = sat(uSoft);
    if (softness > 0.001) {
        float lum = dot(color, vec3(0.2126, 0.7152, 0.0722));
        vec3 softCol = mix(vec3(lum), color, 0.32) + themeAccent * (innerEdge * 0.060 + rim * 0.050) * fill;
        color = mix(color, softCol, softness);
    }

    float alpha = fill * mix(uSurfaceColor.a, max(uSurfaceColor.a, 0.48) * 0.72, light) + outline * mix(0.66, 0.46, light) + glow + edgeAlpha;
    alpha += exposureBurst * fill * 0.045 + pointerGlow * 0.055;
    alpha *= clipMask;
    if (alpha <= 0.001) {
        discard;
    }
    fragColor = vec4(clamp(color, 0.0, 1.0), sat(alpha));
}
