#version 330 core

in vec2 vUv;

uniform vec2  uResolution;
uniform float uTime;
uniform vec4  uDrawRect;
uniform vec4  uElementRect;
uniform float uRadius;
uniform float uAlpha;
uniform float uInset;
uniform vec4  uSurfaceColor;
uniform vec4  uOutlineColor;
uniform vec3  uAccentTop;
uniform vec3  uAccentBottom;
uniform vec2  uMouse;
uniform float uMouseVel;
uniform float uShadow;
uniform float uOutline;
uniform float uLightMode;
uniform float uPad;
uniform float uSweepSpeed;
uniform float uIor;
uniform float uDispersion;
uniform float uDecay;
uniform float uCausticGain;
uniform float uGlintGain;
uniform sampler2D uScene;
uniform vec2  uSceneSize;
uniform float uHasScene;

out vec4 fragColor;

const float TAU = 6.2831853;
const float GA  = 2.39996323;

const int FROST_TAPS = 24;

const float BEVEL_FRAC    = 0.075;
const float BEVEL_MIN     = 6.0;
const float BEVEL_MAX     = 8.0;
const float DECAY_FALLBACK = 0.62;
const float DISP_PX       = 6.5;
const float REFRACT_GAIN  = 0.88;
const float FRINGE_COUNT  = 4.5;
const float FRINGE_SHARP  = 16.0;
const float INTERF_GAIN   = 0.80;
const float RIM_GAIN      = 0.55;
const float HALO_FRAC     = 1.55;
const float HALO_GAIN     = 0.32;
const float TINT_STRENGTH = 0.12;
const float FROST_DENSITY = 0.82;
const float SAT_BOOST     = 0.20;

float sat(float v){ return clamp(v, 0.0, 1.0); }
vec3  sat3(vec3 v){ return clamp(v, 0.0, 1.0); }
float luma(vec3 c){ return dot(c, vec3(0.2126, 0.7152, 0.0722)); }
float maxc(vec3 c){ return max(max(c.r, c.g), c.b); }

float hash12(vec2 p){
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec3 hue2rgb(float h){
    h = fract(h);
    vec3 c = clamp(abs(mod(h * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    return c * c * (3.0 - 2.0 * c);
}

float vnoise(vec2 p){
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float dd = hash12(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, dd, f.x), f.y);
}

float fbm(vec2 p){
    float v = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 4; i++){
        v += amp * vnoise(p);
        p *= 2.04;
        amp *= 0.5;
    }
    return v;
}

vec3 themeGrad(float v){
    v = clamp(v, 0.0, 1.0);
    float e = v * v * v * (v * (v * 6.0 - 15.0) + 10.0);
    vec3 mid = mix(uAccentTop, uAccentBottom, 0.5);
    mid += (mid - vec3(luma(mid))) * 0.14;
    mid *= 1.05;
    vec3 c = mix(uAccentTop, mid, smoothstep(0.0, 0.5, e));
    return mix(c, uAccentBottom, smoothstep(0.5, 1.0, e));
}

float sdRoundBox(vec2 p, vec2 b, float r){
    vec2 h = max(b, vec2(0.5));
    float rr = clamp(r, 0.0, min(h.x, h.y));
    vec2 q = abs(p) - h + vec2(rr);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - rr;
}

vec3 sceneAt(vec2 px){
    vec2 uv = vec2(px.x / max(uSceneSize.x, 1.0), 1.0 - px.y / max(uSceneSize.y, 1.0));
    return texture(uScene, clamp(uv, vec2(0.0012), vec2(0.9988))).rgb;
}

vec3 frostedSample(vec2 px, float radius){
    vec3 acc = vec3(0.0);
    float total = 0.0;
    float rot = hash12(px * 1.37 + uTime * 0.07) * TAU;
    for (int i = 0; i < FROST_TAPS; i++){
        float fi = float(i) + 0.5;
        float r = sqrt(fi / float(FROST_TAPS)) * radius;
        float a = fi * GA + rot;
        float w = 1.0 - 0.55 * (r / max(radius, 1.0));
        acc += sceneAt(px + vec2(cos(a), sin(a)) * r) * w;
        total += w;
    }
    return acc / max(total, 1e-3);
}

void main(){
    vec2 pixel = vec2(vUv.x * uResolution.x, (1.0 - vUv.y) * uResolution.y);

    vec2 clipMin = uDrawRect.xy;
    vec2 clipMax = uDrawRect.xy + uDrawRect.zw;
    vec2 clipE = min(pixel - clipMin, clipMax - pixel);
    float clipMask = smoothstep(-1.0, 2.0, min(clipE.x, clipE.y));
    if (clipMask <= 0.001) discard;

    float inset = sat(uInset);
    float insetPx = inset * max(1.0, min(uElementRect.z, uElementRect.w) * 0.055);
    vec2 halfSize = max(vec2(1.0), uElementRect.zw * 0.5 - vec2(insetPx));
    float radius = max(uRadius * 0.5, uRadius - insetPx);
    vec2 center = uElementRect.xy + uElementRect.zw * 0.5;
    vec2 rel = pixel - center;
    vec2 uv = (pixel - uElementRect.xy) / max(uElementRect.zw, vec2(1.0));

    float d = sdRoundBox(rel, halfSize, radius);
    float aa = max(fwidth(d), 0.75);
    float fill = 1.0 - smoothstep(0.0, aa, d);
    float outside = max(d, 0.0);
    float exterior = 1.0 - fill;
    float isPanel = 1.0 - inset;

    vec2 innerHalf = max(halfSize - vec2(radius), vec2(0.0));
    vec2 nv = rel - clamp(rel, -innerHalf, innerHalf);
    float nl = length(nv);
    vec2 edgeN = nl > 1e-4 ? nv / nl : vec2(0.0, -1.0);

    float pmin = min(uElementRect.z, uElementRect.w);
    float vert = sat(uv.y);
    float light = sat(uLightMode);
    vec3 themeAccent = themeGrad(vert);
    vec3 accentMid = themeGrad(0.5);

    float pad = max(uPad, 24.0);
    float decay = uDecay > 0.0001 ? uDecay : DECAY_FALLBACK;
    float bevelPx = clamp(pmin * BEVEL_FRAC, BEVEL_MIN, BEVEL_MAX);

    float ad = abs(d);
    float wedge = exp(-ad * decay) * (1.0 - smoothstep(bevelPx * 0.6, bevelPx, ad));

    float dispAmt = DISP_PX * max(uDispersion, 0.0) * wedge;
    float offR = dispAmt * 0.55;
    float offG = dispAmt * 1.00;
    float offB = dispAmt * 1.55;
    vec3 refractColor = vec3(
        sceneAt(pixel + edgeN * offR).r,
        sceneAt(pixel + edgeN * offG).g,
        sceneAt(pixel + edgeN * offB).b
    );
    vec3 dispFallback = hue2rgb(fract(d / max(bevelPx, 1.0) * 0.5 + uTime * 0.04));
    refractColor = mix(dispFallback, refractColor, sat(uHasScene));

    float band = clamp(0.5 + 0.5 * d / max(bevelPx, 1.0), 0.0, 1.0);
    float jitter = fbm(pixel * 0.06 + vec2(uTime * 0.05, -uTime * 0.04));
    float phase = band * FRINGE_COUNT + jitter * 1.1;
    float fr = sin(phase * TAU);
    float fringe = pow(max(fr, 0.0), FRINGE_SHARP) * wedge;
    vec3 fringeCol = hue2rgb(fract(band + jitter * 0.18 + uTime * 0.03));

    vec3 dispEdge = refractColor * wedge * REFRACT_GAIN
                  + fringeCol * fringe * INTERF_GAIN;

    float rim = exp(-ad * decay * 1.8) * RIM_GAIN;
    dispEdge += mix(themeAccent, vec3(1.0), 0.5) * rim;

    float halo = exp(-outside * decay) * (1.0 - smoothstep(bevelPx, bevelPx * HALO_FRAC, outside)) * exterior;
    dispEdge += mix(themeAccent, fringeCol, 0.5) * halo * HALO_GAIN;

    dispEdge = max(dispEdge, vec3(0.0));

    float shSize = max(pad * 0.32, 7.0);
    float shadow = exp(-(outside * outside) / (shSize * shSize)) * exterior * uShadow * isPanel;

    vec3 color = vec3(0.0);
    float topSheen = 0.0;
    float blurR = clamp(pmin * 0.26, 6.0, 44.0) * (0.72 + 0.28 * isPanel);

    if (fill > 0.0005){
        vec3 frosted = frostedSample(pixel, blurR);
        frosted = mix(frosted, frostedSample(pixel, blurR * 0.46), 0.40);
        float fl = luma(frosted);
        frosted = mix(frosted, vec3(fl), 0.06) * 1.04;
        vec3 frostFallback = mix(vec3(0.05, 0.06, 0.09), accentMid * 0.40, 0.30);
        frosted = mix(frostFallback, frosted, sat(uHasScene));

        vec3 slate = uSurfaceColor.rgb;
        vec3 body = mix(frosted, slate, TINT_STRENGTH + 0.06 * inset);
        body *= mix(FROST_DENSITY, 0.92, light);
        body *= 0.95 + 0.10 * (1.0 - vert);
        body += themeAccent * 0.04 * (1.0 - vert) * isPanel;

        topSheen = smoothstep(0.60, 0.0, vert) * smoothstep(0.0, 0.20, uv.x) * smoothstep(1.0, 0.74, uv.x);
        color = body + vec3(1.0) * topSheen * 0.30;
    }

    color += dispEdge;

    color = color / (1.0 + max(vec3(0.0), color - 1.0) * 0.7);
    float L = luma(color);
    color = mix(vec3(L), color, 1.0 + SAT_BOOST);
    color = max(color, vec3(0.0));
    color += (hash12(pixel + fract(uTime)) - 0.5) * (1.0 / 255.0);
    color = sat3(color);
    if (any(isnan(color)) || any(isinf(color))) color = vec3(0.0);

    float bodyA = clamp(uSurfaceColor.a, 0.0, 1.0);
    float coreA = mix(bodyA, max(bodyA, 0.62), 0.55);
    float dispL = maxc(dispEdge);
    float a = fill * coreA * uAlpha
            + sat(dispL) * 0.85 * uAlpha
            + topSheen * 0.05 * fill * uAlpha
            + shadow * 0.34 * uAlpha * (1.0 - sat(dispL * 2.0));
    a *= clipMask;
    a = (a != a) ? 0.0 : a;
    a = clamp(a, 0.0, 1.0);
    if (a <= 0.001) discard;

    fragColor = vec4(color, a);
}
