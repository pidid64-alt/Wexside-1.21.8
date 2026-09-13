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
uniform float uSpectralBloomStrength;
uniform float uRefractionDensityFade;
uniform sampler2D uScene;
uniform vec2  uSceneSize;
uniform float uHasScene;

out vec4 fragColor;

const float TAU     = 6.2831853;
const float INV_TAU = 0.15915494;
const float GA      = 2.39996323;

const int   FROST_TAPS    = 32;
const float FROST_FRAC    = 0.28;
const float FROST_MIN     = 6.0;
const float FROST_MAX     = 48.0;
const float TINT_STRENGTH = 0.46;
const float RIM_FRAC      = 0.045;
const float RIM_MIN       = 1.0;
const float RIM_MAX       = 2.6;
const float RIM_BRIGHT    = 0.95;
const float INNER_GLOW    = 0.22;
const float BEVEL_FRAC    = 0.135;
const float BEVEL_MIN     = 4.0;
const float BEVEL_MAX     = 11.0;
const float SB_REFR_PX    = 9.0;
const float SB_BLUR_PX    = 5.5;
const float SB_SAT        = 2.1;
const float SPARK_GAIN    = 1.7;
const float GLOW_FRAC     = 0.28;
const float GLOW_GAIN     = 0.72;
const float SHEEN_GAIN    = 0.55;
const float SAT_BOOST     = 0.24;

const int SB_BANDS = 6;
const vec3 SB_COL[6] = vec3[6](
    vec3(1.00, 0.20, 0.14),
    vec3(1.00, 0.58, 0.16),
    vec3(0.85, 1.00, 0.22),
    vec3(0.20, 1.00, 0.60),
    vec3(0.22, 0.55, 1.00),
    vec3(0.62, 0.24, 1.00)
);
const float SB_OFF[6]  = float[6](0.34, 0.50, 0.66, 0.80, 0.92, 1.00);
const float SB_BLUR[6] = float[6](1.00, 0.84, 0.70, 0.56, 0.44, 0.34);

float sat(float v){ return clamp(v, 0.0, 1.0); }
vec3  sat3(vec3 v){ return clamp(v, 0.0, 1.0); }
float luma(vec3 c){ return dot(c, vec3(0.2126, 0.7152, 0.0722)); }

float hash12(vec2 p){
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec3 spectrum(float t){
    t = fract(t);
    vec3 c = 0.5 + 0.5 * cos(TAU * (t + vec3(0.00, 0.33, 0.67)));
    return c * c * (3.0 - 2.0 * c);
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
    vec2 q = abs(p) - b + vec2(r);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
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

vec3 spectralBloom(vec2 px, vec2 nrm, vec2 tng, float amt, float blur){
    vec3 acc = vec3(0.0);
    vec3 wsum = vec3(1e-3);
    for (int b = 0; b < SB_BANDS; b++){
        float off = amt * SB_OFF[b];
        float br = blur * SB_BLUR[b];
        vec3 s = sceneAt(px + nrm * off)
               + sceneAt(px + nrm * (off + br) + tng * br * 0.65)
               + sceneAt(px + nrm * (off - br * 0.5) - tng * br * 0.65)
               + sceneAt(px + nrm * off + tng * br * 1.1);
        acc += s * 0.25 * SB_COL[b];
        wsum += SB_COL[b];
    }
    return acc / wsum;
}

void main(){
    vec2 pixel = vec2(vUv.x * uResolution.x, (1.0 - vUv.y) * uResolution.y);
    vec2 clipMin = uDrawRect.xy;
    vec2 clipMax = uDrawRect.xy + uDrawRect.zw;
    vec2 clipE = min(pixel - clipMin, clipMax - pixel);
    float clipMask = smoothstep(-1.0, 2.0, min(clipE.x, clipE.y));
    if (clipMask <= 0.001) discard;

    vec2 uv = (pixel - uElementRect.xy) / max(uElementRect.zw, vec2(1.0));
    float inset = sat(uInset);
    float insetPx = inset * max(1.0, min(uElementRect.z, uElementRect.w) * 0.05);
    vec2 halfSize = max(vec2(1.0), uElementRect.zw * 0.5 - vec2(insetPx));
    float radius = clamp(uRadius - insetPx, 0.0, min(halfSize.x, halfSize.y));
    vec2 center = uElementRect.xy + uElementRect.zw * 0.5;
    vec2 rel = pixel - center;

    float d = sdRoundBox(rel, halfSize, radius);
    float aa = max(fwidth(d), 0.75);
    float fill = 1.0 - smoothstep(0.0, aa, d);
    float outside = max(d, 0.0);
    float coreDist = max(-d, 0.0);
    float exterior = 1.0 - fill;
    float isPanel = 1.0 - inset;

    vec2 innerHalf = max(halfSize - vec2(radius), vec2(0.0));
    vec2 nv = rel - clamp(rel, -innerHalf, innerHalf);
    float nl = length(nv);
    vec2 edgeN = nl > 1e-4 ? nv / nl : vec2(0.0, -1.0);
    vec2 tang = vec2(-edgeN.y, edgeN.x);

    float light = sat(uLightMode);
    float aspect = clamp(uElementRect.z / max(uElementRect.w, 1.0), 0.25, 4.0);
    float vert = sat(uv.y);
    float vertOut = sat((pixel.y - uElementRect.y) / max(uElementRect.w, 1.0));
    float pmin = min(uElementRect.z, uElementRect.w);
    float pad = max(uPad, max(40.0, radius * 4.0));
    float sizeFade = smoothstep(22.0, 95.0, pmin);

    vec3 themeAccent = themeGrad(vert);
    vec3 accentMid = themeGrad(0.5);

    float sweepPhase = uTime * max(uSweepSpeed, 0.0001);
    vec2 lightDir = normalize(vec2(-0.62, -0.78));
    float lit = sat(0.5 + 0.5 * dot(edgeN, lightDir));

    float rimW = clamp(pmin * RIM_FRAC, RIM_MIN, RIM_MAX);
    float blurR = clamp(pmin * FROST_FRAC, FROST_MIN, FROST_MAX) * (0.72 + 0.28 * isPanel);
    float glowReach = clamp(pad * GLOW_FRAC, 6.0, 64.0);
    float bevelW = clamp(pmin * BEVEL_FRAC, BEVEL_MIN, BEVEL_MAX);

    float edgeProx = 1.0 - smoothstep(0.0, rimW, coreDist);
    float edgeWide = 1.0 - smoothstep(0.0, rimW * 3.0, coreDist);
    float fresnelEdge = pow(edgeWide, 1.5);

    float spread = sat(outside / max(pad, 1.0));
    float halAng = atan(rel.y, rel.x + 1e-5) * INV_TAU + 0.5;
    float glowFall = exp(-outside / max(glowReach, 1.0));
    float glowDir = 0.78 + 0.22 * lit;
    float glowFade = 1.0 - smoothstep(0.55, 1.0, spread);
    vec3 glowBase = themeGrad(vertOut);
    vec3 glowSpec = mix(glowBase, spectrum(spread * 0.5 + halAng * 0.12 + sweepPhase * 0.03), 0.40);
    vec3 glowEmit = glowSpec * glowFall * glowDir * exterior * isPanel * GLOW_GAIN * glowFade * (0.5 + 0.5 * sizeFade);

    float shSize = max(pad * 0.32, 7.0);
    float shadow = exp(-(outside * outside) / (shSize * shSize)) * exterior * uShadow;

    float spectralDensity = pow(smoothstep(bevelW, 0.0, abs(d)), max(uRefractionDensityFade, 0.5));
    spectralDensity *= 0.55 + 0.45 * sizeFade;
    vec3 spectralEmit = vec3(0.0);
    if (spectralDensity > 0.0025 && uHasScene > 0.5){
        float amt = SB_REFR_PX * (0.40 + 0.60 * fresnelEdge);
        float blur = SB_BLUR_PX * (0.45 + 0.70 * spectralDensity);
        vec3 bloom = spectralBloom(pixel, edgeN, tang, amt, blur);
        float bl = luma(bloom);
        bloom = max(vec3(0.0), mix(vec3(bl), bloom, SB_SAT));
        bloom *= 0.65 + 0.95 * themeGrad(vertOut);
        spectralEmit = bloom * spectralDensity * max(uSpectralBloomStrength, 0.0);
    }

    vec3 color = glowEmit;
    float rimI = 0.0;
    float topSheen = 0.0;
    float sweep = 0.0;
    float cursorG = 0.0;
    float sparkI = 0.0;

    if (fill > 0.0005){
        vec3 frosted = frostedSample(pixel, blurR);
        frosted = mix(frosted, frostedSample(pixel, blurR * 0.46), 0.42);
        float fl = luma(frosted);
        frosted = mix(frosted, vec3(fl), 0.08) * 1.05;
        vec3 frostFallback = mix(vec3(0.05, 0.06, 0.09), accentMid * 0.45, 0.30);
        frosted = mix(frostFallback, frosted, sat(uHasScene));

        vec3 tint = uSurfaceColor.rgb;
        vec3 body = mix(frosted, tint, TINT_STRENGTH + 0.07 * inset);
        body *= 0.93 + 0.13 * (1.0 - vert);
        body += themeAccent * 0.05 * (1.0 - vert) * isPanel;
        body = mix(body, body * 0.93, inset * 0.5);

        float innerShade = edgeProx * (1.0 - lit) * 0.5;
        body *= 1.0 - innerShade * 0.40;
        float innerBand = sat(edgeWide - edgeProx);
        body += themeAccent * innerBand * INNER_GLOW * (0.6 + 0.4 * isPanel);

        vec3 rimTint = mix(themeAccent, vec3(1.0), 0.62);
        rimI = edgeProx * (0.32 + 0.68 * lit) * (0.7 + 0.3 * fresnelEdge);

        topSheen = smoothstep(0.62, 0.0, vert) * smoothstep(0.0, 0.22, uv.x) * smoothstep(1.0, 0.74, uv.x);
        float diag = uv.x * 0.5 + (1.0 - vert) * 0.5;
        float sweepC = 0.5 + 0.42 * sin(sweepPhase * 0.5);
        sweep = exp(-pow((diag - sweepC) * 2.6, 2.0));
        vec3 sheen = vec3(1.0) * (topSheen * 0.5 + sweep * 0.16) * SHEEN_GAIN;

        vec2 mUv = (uMouse - uElementRect.xy) / max(uElementRect.zw, vec2(1.0));
        float mIn = step(-0.2, mUv.x) * step(mUv.x, 1.2) * step(-0.2, mUv.y) * step(mUv.y, 1.2);
        vec2 mdv = (uv - mUv) * vec2(aspect, 1.0);
        cursorG = exp(-dot(mdv, mdv) / 0.05) * mIn;
        vec3 cursorGlow = rimTint * cursorG * (edgeWide * 0.5 + 0.12) * 0.35;

        vec2 cq = abs(rel) - innerHalf;
        float inCorner = step(0.0, cq.x) * step(0.0, cq.y);
        float sparkBand = 1.0 - smoothstep(0.0, bevelW * 1.6, coreDist);
        float hsp = hash12(floor(pixel * 0.6) + floor(uTime * 13.0));
        float spark = pow(hsp, 9.0);
        sparkI = inCorner * sparkBand * spark * sat(uMouseVel) * SPARK_GAIN;
        vec3 sparkCol = mix(vec3(1.0), spectrum(hash12(floor(pixel * 0.5)) + sweepPhase * 0.3), 0.45);

        color = body;
        color += rimTint * rimI * RIM_BRIGHT;
        color += sheen;
        color += cursorGlow;
        color += sparkCol * sparkI;
    }

    color += spectralEmit;

    color = color / (1.0 + max(vec3(0.0), color - 1.0) * 0.7);
    float L = luma(color);
    color = mix(vec3(L), color, 1.0 + SAT_BOOST);
    color = max(color, vec3(0.0));
    color += (hash12(pixel + sweepPhase) - 0.5) * (1.0 / 255.0);
    color = sat3(color);
    if (any(isnan(color)) || any(isinf(color))) color = vec3(0.0);

    float bodyA = clamp(uSurfaceColor.a, 0.0, 1.0);
    float coreA = mix(bodyA, max(bodyA, 0.80), 0.55);
    float glowL = max(max(glowEmit.r, glowEmit.g), glowEmit.b);
    float specL = max(max(spectralEmit.r, spectralEmit.g), spectralEmit.b);
    float a = fill * coreA * uAlpha
            + rimI * 0.5 * uOutline * uAlpha
            + specL * 0.7 * uAlpha
            + glowL * 0.5 * uAlpha
            + cursorG * fill * 0.10 * uAlpha
            + sparkI * 0.5 * uAlpha
            + (topSheen * 0.5 + sweep * 0.16) * SHEEN_GAIN * 0.10 * fill * uAlpha
            + shadow * 0.34 * uAlpha * (1.0 - sat(glowL * 3.0));
    a *= clipMask;
    a = (a != a) ? 0.0 : a;
    a = clamp(a, 0.0, 1.0);
    if (a <= 0.001) discard;

    fragColor = vec4(color, a);
}
