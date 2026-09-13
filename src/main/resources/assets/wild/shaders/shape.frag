#version 330 core
in vec2 vLocalPx;
in vec2 vSize;
flat in vec4 vColorTL;
flat in vec4 vColorTR;
flat in vec4 vColorBR;
flat in vec4 vColorBL;
flat in vec4 vRadii;
flat in uint vFlags;
flat in ivec4 vClip;
flat in vec4 vClipRadii;
in vec2 vUV;
flat in vec2 vUvScale;
flat in vec2 vUvOffset;
flat in int vTexSlot;
in vec2 vPosPx;

uniform sampler2D uTextures[16];

out vec4 FragColor;

const float PI = 3.14159265;
const float TAU = 6.2831853;
const float TEXT_GAMMA = 0.82;
const float TEXT_SUPERSAMPLE_RANGE = 2.75;

vec3 srgb_to_linear(vec3 c) {
    return vec3(
        c.r <= 0.04045 ? c.r / 12.92 : pow((c.r + 0.055) / 1.055, 2.4),
        c.g <= 0.04045 ? c.g / 12.92 : pow((c.g + 0.055) / 1.055, 2.4),
        c.b <= 0.04045 ? c.b / 12.92 : pow((c.b + 0.055) / 1.055, 2.4)
    );
}

vec3 linear_to_srgb(vec3 c) {
    c = clamp(c, 0.0, 1.0);
    return vec3(
        c.r <= 0.0031308 ? c.r * 12.92 : 1.055 * pow(c.r, 0.4166666666666667) - 0.055,
        c.g <= 0.0031308 ? c.g * 12.92 : 1.055 * pow(c.g, 0.4166666666666667) - 0.055,
        c.b <= 0.0031308 ? c.b * 12.92 : 1.055 * pow(c.b, 0.4166666666666667) - 0.055
    );
}

vec3 linear_srgb_to_oklab(vec3 c) {
    float l = 0.4122214708 * c.r + 0.5363325363 * c.g + 0.0514459929 * c.b;
    float m = 0.2119034982 * c.r + 0.6806995451 * c.g + 0.1073969566 * c.b;
    float s = 0.0883024619 * c.r + 0.2817188376 * c.g + 0.6299787005 * c.b;
    float l_ = sign(l) * pow(abs(l), 0.333333333333);
    float m_ = sign(m) * pow(abs(m), 0.333333333333);
    float s_ = sign(s) * pow(abs(s), 0.333333333333);
    return vec3(
        0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_,
        1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_,
        0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_
    );
}

vec3 oklab_to_linear_srgb(vec3 c) {
    float l_ = c.x + 0.3963377774 * c.y + 0.2158037573 * c.z;
    float m_ = c.x - 0.1055613458 * c.y - 0.0638541728 * c.z;
    float s_ = c.x - 0.0894841775 * c.y - 1.2914855480 * c.z;
    float l = l_ * l_ * l_;
    float m = m_ * m_ * m_;
    float s = s_ * s_ * s_;
    return vec3(
        4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
        -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
        -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
    );
}

vec3 oklab_mix(vec3 colA, vec3 colB, float t) {
    vec3 labA = linear_srgb_to_oklab(colA);
    vec3 labB = linear_srgb_to_oklab(colB);
    return oklab_to_linear_srgb(mix(labA, labB, t));
}

vec3 oklab_mix_srgb(vec3 colA, vec3 colB, float t) {
    return linear_to_srgb(oklab_mix(srgb_to_linear(colA), srgb_to_linear(colB), clamp(t, 0.0, 1.0)));
}

vec4 oklab_mix_srgba(vec4 colA, vec4 colB, float t) {
    float k = clamp(t, 0.0, 1.0);
    return vec4(oklab_mix_srgb(colA.rgb, colB.rgb, k), mix(colA.a, colB.a, k));
}


vec4 sampleTexture(int slot, vec2 uv) {
    switch (slot) {
        case 0:  return texture(uTextures[0], uv);
        case 1:  return texture(uTextures[1], uv);
        case 2:  return texture(uTextures[2], uv);
        case 3:  return texture(uTextures[3], uv);
        case 4:  return texture(uTextures[4], uv);
        case 5:  return texture(uTextures[5], uv);
        case 6:  return texture(uTextures[6], uv);
        case 7:  return texture(uTextures[7], uv);
        case 8:  return texture(uTextures[8], uv);
        case 9:  return texture(uTextures[9], uv);
        case 10: return texture(uTextures[10], uv);
        case 11: return texture(uTextures[11], uv);
        case 12: return texture(uTextures[12], uv);
        case 13: return texture(uTextures[13], uv);
        case 14: return texture(uTextures[14], uv);
        case 15: return texture(uTextures[15], uv);
        default: return vec4(0.0);
    }
}

ivec2 textureDimensions(int slot) {
    switch (slot) {
        case 0:  return textureSize(uTextures[0], 0);
        case 1:  return textureSize(uTextures[1], 0);
        case 2:  return textureSize(uTextures[2], 0);
        case 3:  return textureSize(uTextures[3], 0);
        case 4:  return textureSize(uTextures[4], 0);
        case 5:  return textureSize(uTextures[5], 0);
        case 6:  return textureSize(uTextures[6], 0);
        case 7:  return textureSize(uTextures[7], 0);
        case 8:  return textureSize(uTextures[8], 0);
        case 9:  return textureSize(uTextures[9], 0);
        case 10: return textureSize(uTextures[10], 0);
        case 11: return textureSize(uTextures[11], 0);
        case 12: return textureSize(uTextures[12], 0);
        case 13: return textureSize(uTextures[13], 0);
        case 14: return textureSize(uTextures[14], 0);
        case 15: return textureSize(uTextures[15], 0);
        default: return ivec2(1, 1);
    }
}

vec4 sampleTextureLod0(int slot, vec2 uv) {
    switch (slot) {
        case 0:  return textureLod(uTextures[0], uv, 0.0);
        case 1:  return textureLod(uTextures[1], uv, 0.0);
        case 2:  return textureLod(uTextures[2], uv, 0.0);
        case 3:  return textureLod(uTextures[3], uv, 0.0);
        case 4:  return textureLod(uTextures[4], uv, 0.0);
        case 5:  return textureLod(uTextures[5], uv, 0.0);
        case 6:  return textureLod(uTextures[6], uv, 0.0);
        case 7:  return textureLod(uTextures[7], uv, 0.0);
        case 8:  return textureLod(uTextures[8], uv, 0.0);
        case 9:  return textureLod(uTextures[9], uv, 0.0);
        case 10: return textureLod(uTextures[10], uv, 0.0);
        case 11: return textureLod(uTextures[11], uv, 0.0);
        case 12: return textureLod(uTextures[12], uv, 0.0);
        case 13: return textureLod(uTextures[13], uv, 0.0);
        case 14: return textureLod(uTextures[14], uv, 0.0);
        case 15: return textureLod(uTextures[15], uv, 0.0);
        default: return vec4(0.0);
    }
}




float getRadius(vec2 p, vec4 r) {
    return (p.x > 0.0) ?
           ((p.y > 0.0) ? r.z : r.y) :
           ((p.y > 0.0) ? r.w : r.x);
}


float sdRoundBox(vec2 p, vec2 halfSize, vec4 radii) {
    vec4 safeRadii = min(radii, vec4(min(halfSize.x, halfSize.y)));
    float rad = getRadius(p, safeRadii);
    vec2 q = abs(p) - halfSize + rad;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - rad;
}

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

float msdfCoverage(int slot, vec2 uv, float screenPxRange) {
    vec3 s = sampleTextureLod0(slot, uv).rgb;
    return clamp(screenPxRange * (median(s.r, s.g, s.b) - 0.5) + 0.5, 0.0, 1.0);
}


float getCoverage(float d) {
    float px = max(length(vec2(dFdx(d), dFdy(d))), 0.0001);
    return 1.0 - smoothstep(-px, px, d);
}

float hash12(vec2 p) {
    p = fract(p * vec2(0.1031, 0.11369));
    p += dot(p, p.yx + 19.19);
    return fract((p.x + p.y) * p.x);
}

float valueNoise(vec2 p) {
    vec2 cell = floor(p);
    vec2 local = fract(p);
    vec2 curve = local * local * (3.0 - 2.0 * local);
    float a = hash12(cell);
    float b = hash12(cell + vec2(1.0, 0.0));
    float c = hash12(cell + vec2(0.0, 1.0));
    float d = hash12(cell + vec2(1.0, 1.0));
    return mix(mix(a, b, curve.x), mix(c, d, curve.x), curve.y);
}

float fbm2(vec2 p) {
    return valueNoise(p) * 0.68 + valueNoise(p * 2.07 + vec2(17.2, 9.2)) * 0.32;
}

vec2 roundBoxNormal(vec2 p, vec2 halfSize, vec4 radii) {
    vec4 safeRadii = min(radii, vec4(min(halfSize.x, halfSize.y)));
    float radius = getRadius(p, safeRadii);
    vec2 q = abs(p) - halfSize + radius;
    vec2 signP = vec2(p.x < 0.0 ? -1.0 : 1.0, p.y < 0.0 ? -1.0 : 1.0);
    vec2 outer = max(q, vec2(0.0));
    if (dot(outer, outer) > 1.0e-6) {
        return signP * normalize(outer);
    }
    return q.x > q.y ? vec2(signP.x, 0.0) : vec2(0.0, signP.y);
}

float gearSat(float v) {
    return clamp(v, 0.0, 1.0);
}

float gearSmin(float a, float b, float k) {
    float h = gearSat(0.5 + 0.5 * (b - a) / k);
    return mix(b, a, h) - k * h * (1.0 - h);
}

const float GEAR_TIP = 0.5815;
const float GEAR_ROOT = 0.4361;
const float GEAR_BORE = 0.1977;
const float GEAR_TOOTH_HALF = 0.0820;
const float GEAR_TOOTH_ROUND = 0.0420;
const float GEAR_FILLET = 0.0520;
const vec2 GEAR_FLANK = vec2(0.15643, 0.98769);
const vec2 GEAR_AXIS_A = vec2(1.0, 0.0);
const vec2 GEAR_AXIS_B = vec2(0.5, 0.8660254);
const vec2 GEAR_AXIS_C = vec2(-0.5, 0.8660254);
const vec2 GEAR_KEY = vec2(-0.419, -0.908);
const vec3 GEAR_HALF_VECTOR = vec3(-0.228, -0.315, 0.922);
const vec2 GEAR_SWEEP_DIR = vec2(0.62469, 0.78087);

float gearTooth(vec2 v, vec2 axis) {
    vec2 t = abs(vec2(dot(v, axis), dot(v, vec2(-axis.y, axis.x))));
    vec2 hp = vec2(t.x - GEAR_TIP + GEAR_TOOTH_ROUND,
                   dot(t - vec2(GEAR_ROOT, GEAR_TOOTH_HALF), GEAR_FLANK));
    return min(max(hp.x, hp.y), 0.0) + length(max(hp, vec2(0.0))) - GEAR_TOOTH_ROUND;
}

float gearField(vec2 v, float radial) {
    float d = radial - GEAR_ROOT;
    d = gearSmin(d, gearTooth(v, GEAR_AXIS_A), GEAR_FILLET);
    d = gearSmin(d, gearTooth(v, GEAR_AXIS_B), GEAR_FILLET);
    d = gearSmin(d, gearTooth(v, GEAR_AXIS_C), GEAR_FILLET);
    return max(d, GEAR_BORE - radial);
}

vec4 gearShade(vec2 q, float radial, float angle, float aaQ, float bevelW,
               vec3 inkTop, vec3 inkBottom, vec3 accA, vec3 accB,
               float hoverAmt, float openAmt, float sweepPos, float lightMode) {
    float ca = cos(angle);
    float sa = sin(angle);
    vec2 v = vec2(q.x * ca + q.y * sa, -q.x * sa + q.y * ca);

    float d = gearField(v, radial);
    vec2 slope = vec2(dFdx(d), dFdy(d));
    float cover = 1.0 - smoothstep(-aaQ, aaQ, d);
    if (cover <= 0.0) {
        return vec4(0.0);
    }

    vec2 outward = slope / max(length(slope), 1.0e-5);

    float depth = gearSat(max(-d, 0.0) / bevelW);
    float rise = 1.0 - depth * depth * (3.0 - 2.0 * depth);
    vec3 normal = vec3(outward * rise, sqrt(max(1.0 - rise * rise, 0.0)));

    float ramp = gearSat(q.y * 0.72 + 0.5);
    vec3 ink = mix(inkTop, inkBottom, ramp);
    vec3 accent = mix(accA, accB, ramp);

    float facing = gearSat(dot(outward, GEAR_KEY));
    float faceLight = dot(q, -GEAR_KEY) * 0.5 + 0.5;
    vec3 body = mix(ink, accent, 0.10 + 0.26 * openAmt + 0.12 * hoverAmt);
    body *= mix(0.840, 1.099, faceLight);
    body *= 1.0 + 0.20 * (facing * 2.0 - 1.0) * rise;

    float bore = gearSat(1.0 - (radial - GEAR_BORE) / 0.14);
    body *= 1.0 - 0.22 * bore * bore;

    float glance = 1.0 - gearSat(normal.z);
    glance = glance * glance;
    float fresnel = glance * glance;
    float microfacet = pow(gearSat(dot(normal, GEAR_HALF_VECTOR)), 28.0);

    float sweepAxis = dot(q, GEAR_SWEEP_DIR) * 0.83 + 0.5;
    float sweepOffset = (sweepAxis - sweepPos) * 6.4;
    float band = exp(-sweepOffset * sweepOffset);
    float sheen = band * rise * (0.55 + 0.45 * band * band);

    vec3 specular = mix(vec3(1.0), accent, mix(0.42, 0.64, lightMode));
    float gain = mix(1.0, 0.70, lightMode);
    float energy = microfacet * (0.42 + 0.40 * hoverAmt + 0.30 * openAmt)
                 + fresnel * (0.052 + 0.048 * hoverAmt)
                 + sheen * (0.52 + 0.34 * openAmt);

    vec3 headroom = max(vec3(0.0), vec3(0.985) - body);
    body += min(specular * energy * gain, headroom);

    return vec4(body, cover);
}

void main() {
    vec2 dUvDx = dFdx(vUV);
    vec2 dUvDy = dFdy(vUV);
    vec2 dPosDx = dFdx(vPosPx);
    vec2 dPosDy = dFdy(vPosPx);

    uint mode = vFlags & 3u;
    float thickness = float((vFlags >> 2) & 0xFFu);
    float startRad = float((vFlags >> 10) & 0xFFu) / 255.0 * TAU;
    float arcPct = float((vFlags >> 18) & 0xFFu) / 255.0;

    bool texturedMode = mode == 3u;
    bool msdfMode = texturedMode && ((vFlags >> 4) & 0x1u) == 1u;
    bool glassSurfaceMode = texturedMode && ((vFlags >> 7) & 0x1u) == 1u;
    bool shadowMode = ((vFlags >> 26) & 0x1u) == 1u;
    if (texturedMode || shadowMode) {
        vec2 localMin = min(vec2(0.0), vSize);
        vec2 localMax = max(vec2(0.0), vSize);
        if (any(lessThan(vLocalPx, localMin)) || any(greaterThan(vLocalPx, localMax))) {
            discard;
        }
    }

    float safeWx = (abs(vSize.x) > 1e-6) ? vSize.x : (vSize.x >= 0.0 ? 1e-6 : -1e-6);
    float safeHy = (abs(vSize.y) > 1e-6) ? vSize.y : (vSize.y >= 0.0 ? 1e-6 : -1e-6);

    vec2 halfSize = 0.5 * vSize;
    vec2 p = vLocalPx - halfSize;


    if (vClip.z <= 0 || vClip.w <= 0) discard;
    if (vPosPx.x < float(vClip.x) || vPosPx.y < float(vClip.y) ||
        vPosPx.x >= float(vClip.x + vClip.z) || vPosPx.y >= float(vClip.y + vClip.w)) {
        discard;
    }

    float clipMask = 1.0;
    vec4 clipRadii = max(vClipRadii, vec4(0.0));
    if (clipRadii.x + clipRadii.y + clipRadii.z + clipRadii.w > 1e-6) {
        vec2 clipSize = vec2(float(vClip.z), float(vClip.w));
        if (clipSize.x <= 0.0 || clipSize.y <= 0.0) discard;
        vec2 clipHalf = clipSize * 0.5;
        vec2 clipCenter = vec2(float(vClip.x), float(vClip.y)) + clipHalf;
        float clipDistance = sdRoundBox(vPosPx - clipCenter, clipHalf, clipRadii);
        clipMask = 1.0 - smoothstep(-1.0, 1.0, clipDistance);
        if (clipMask <= 0.0) discard;
    }

    vec4 cornerRadii = max(vRadii, vec4(0.0));
    bool themeCardMode = ((vFlags >> 27) & 0x1u) == 1u;
    bool themeCardSelected = ((vFlags >> 28) & 0x1u) == 1u;
    uint themeCardMaterial = (vFlags >> 29) & 0x7u;

    if (themeCardMode) {
        float dS = sdRoundBox(p, halfSize, cornerRadii);
        float coverage = getCoverage(dS);

        vec2 cardUv = clamp(vLocalPx / vec2(safeWx, safeHy), 0.0, 1.0);
        float hover = clamp(vUvOffset.x, 0.0, 1.0);
        float selected = clamp(vUvOffset.y, 0.0, 1.0);
        float impulse = themeCardSelected ? 4.0 * selected * (1.0 - selected) : 0.0;

        vec3 materialTop = srgb_to_linear(vColorTL.rgb);
        vec3 materialBottom = srgb_to_linear(vColorTR.rgb);
        vec3 accentTop = srgb_to_linear(vColorBL.rgb);
        vec3 accentBottom = srgb_to_linear(vColorBR.rgb);

        if (coverage <= 0.0) discard;

        if (themeCardMaterial == 5u) {
            float materialPhase = smoothstep(0.0, 1.0, cardUv.y);
            vec3 base = mix(materialTop, materialBottom, materialPhase);
            float materialAlpha = mix(vColorTL.a, vColorTR.a, materialPhase);
            float field = clamp(cardUv.x * 0.72 + fbm2(cardUv * vec2(7.20, 4.10) + accentTop.xy * 11.70) * 0.28, 0.0, 1.0);
            float cutoff = mix(-0.14, 1.16, hover);
            float dissolve = smoothstep(cutoff - 0.085, cutoff + 0.115, field);
            float travel = smoothstep(0.035, 0.90, hover) * (1.0 - smoothstep(0.82, 0.99, hover));
            float front = (1.0 - smoothstep(0.018, 0.145, abs(field - cutoff))) * travel;
            vec3 edge = mix(accentTop, vec3(1.0), 0.48);
            base += min(edge * front * 0.16, max(vec3(0.0), vec3(0.985) - base));
            float alpha = materialAlpha * coverage * clipMask * dissolve;
            if (alpha <= 0.001) discard;
            FragColor = vec4(linear_to_srgb(base) * alpha, alpha);
            return;
        }

        if (themeCardMaterial == 6u) {
            float materialPhase = smoothstep(0.0, 1.0, cardUv.y);
            vec3 base = mix(materialTop, materialBottom, materialPhase);
            float materialAlpha = mix(vColorTL.a, vColorTR.a, materialPhase);
            float insideDistance = max(-dS, 0.0);
            float aa = max(fwidth(dS), 0.0001);
            float core = 1.0 - smoothstep(0.35 * aa, 1.05 + aa, insideDistance);
            float halo = 1.0 - smoothstep(1.05, 6.40 + aa, insideDistance);
            float bevel = max(core, halo * 0.16);
            vec2 edgeNormal = roundBoxNormal(p, halfSize, cornerRadii);
            vec2 studioLight = normalize(vec2(-0.42, -0.91));
            float litEdge = pow(max(dot(edgeNormal, studioLight), 0.0), 2.4);
            float shadedEdge = pow(max(dot(edgeNormal, -studioLight), 0.0), 1.8);
            float materialLuma = dot(base, vec3(0.2126, 0.7152, 0.0722));
            float lightGate = mix(1.0, 0.55, smoothstep(0.30, 0.80, materialLuma));
            base += vec3(1.0) * bevel * (0.004 + 0.021 * litEdge) * lightGate;
            base *= 1.0 - bevel * (0.003 + 0.010 * shadedEdge) * lightGate;

            vec2 pointerPx = clamp(vUvScale, 0.0, 1.0) * abs(vSize);
            vec2 pointerP = pointerPx - halfSize;
            vec2 edgePoint = p - dS * edgeNormal;
            vec2 edgeFromPointer = edgePoint - pointerP;
            float edgeDistance2 = dot(edgeFromPointer, edgeFromPointer);
            vec2 edgeDirection = edgeFromPointer * inversesqrt(max(edgeDistance2, 0.00001));
            float reach = clamp(min(abs(vSize.x), abs(vSize.y)) * 0.48, 14.0, 48.0);
            float facing = pow(max(dot(edgeNormal, edgeDirection), 0.0), 1.75);
            float proximity = exp(-edgeDistance2 / (reach * reach));
            float compact = smoothstep(20.0, 46.0, min(abs(vSize.x), abs(vSize.y)));
            float interactive = max(hover, max(selected, impulse));
            float edgeVariation = interactive > 0.0001 ? 0.72 + 0.28 * fbm2(edgePoint * 0.052 + accentTop.xy * 13.17) : 1.0;
            edgeVariation = mix(1.0, edgeVariation, compact);
            float localField = facing * proximity * edgeVariation * mix(0.62, 1.0, compact);
            float hoverField = hover * smoothstep(0.02, 0.98, localField);
            float hoverEdge = 0.020 * hoverField;
            float selectedEdge = selected * (0.007 + 0.024 * edgeVariation);
            float impulseEdge = impulse * (0.018 + 0.072 * localField);
            float accentPhase = clamp(cardUv.x * 0.62 + cardUv.y * 0.38, 0.0, 1.0);
            vec3 hoverAccent = mix(accentTop, accentBottom, accentPhase);
            vec3 specular = mix(vec3(1.0), hoverAccent, 0.24);
            float neonCore = core * hoverField * 0.026;
            float neonSkirt = halo * hoverField * hoverField * 0.009;
            vec3 headroom = max(vec3(0.0), vec3(0.985) - base);
            vec3 edgeLight = specular * bevel * (hoverEdge + selectedEdge + impulseEdge)
                    + hoverAccent * (neonCore + neonSkirt);
            base += min(edgeLight * lightGate, headroom);

            float alpha = materialAlpha * coverage * clipMask;
            if (alpha <= 0.001) discard;
            FragColor = vec4(linear_to_srgb(base) * alpha, alpha);
            return;
        }

        if (themeCardMaterial == 7u) {
            vec2 gearQ = p / max(halfSize, vec2(1.0));
            float gearRadial = length(gearQ);
            float aaQ = max(length(vec2(dFdx(gearQ.x), dFdy(gearQ.x))), 1.0e-5);
            float bevelW = max(0.070, aaQ * 2.2);
            float spinAngle = vUvScale.x;
            float spinArc = clamp(abs(vUvScale.y), 0.0, 0.6981);
            float gearHover = clamp(vUvOffset.x, 0.0, 1.0);
            float gearOpen = clamp(vUvOffset.y, 0.0, 1.0);
            float sweepPos = gearOpen * 1.9 - 0.45;
            float lightMode = themeCardSelected ? 1.0 : 0.0;

            vec3 sumRgb = vec3(0.0);
            float sumA = 0.0;
            if (spinArc < 0.006) {
                vec4 tap = gearShade(gearQ, gearRadial, spinAngle, aaQ, bevelW,
                        materialTop, materialBottom, accentTop, accentBottom,
                        gearHover, gearOpen, sweepPos, lightMode);
                sumRgb = tap.rgb * tap.a;
                sumA = tap.a;
            } else {
                const float tapWeight[7] = float[7](0.42, 0.66, 0.88, 1.0, 0.88, 0.66, 0.42);
                float jitter = hash12(floor(gl_FragCoord.xy * 0.5)) - 0.5;
                float weightSum = 0.0;
                for (int i = 0; i < 7; ++i) {
                    float offset = (float(i) + 0.5 + jitter) * 0.142857 - 0.5;
                    vec4 tap = gearShade(gearQ, gearRadial, spinAngle + spinArc * offset, aaQ, bevelW,
                            materialTop, materialBottom, accentTop, accentBottom,
                            gearHover, gearOpen, sweepPos, lightMode);
                    sumRgb += tap.rgb * tap.a * tapWeight[i];
                    sumA += tap.a * tapWeight[i];
                    weightSum += tapWeight[i];
                }
                sumRgb /= weightSum;
                sumA /= weightSum;
            }

            float alpha = sumA * vColorTL.a * coverage * clipMask;
            if (alpha <= 0.001) discard;
            vec3 base = sumRgb / max(sumA, 1.0e-4);
            FragColor = vec4(linear_to_srgb(base) * alpha, alpha);
            return;
        }


        vec2 noiseSeed = vec2(
            dot(accentTop, vec3(19.19, 31.73, 7.13)),
            dot(accentBottom, vec3(11.79, 3.91, 47.23))
        );
        float micro = fbm2(vLocalPx * vec2(0.030, 0.056) + noiseSeed);
        float materialPhase = clamp(0.5 + (cardUv.y - 0.5) * 0.12 + (micro - 0.5) * 0.18, 0.0, 1.0);
        vec3 base = mix(materialTop, materialBottom, materialPhase);
        float materialAlpha = mix(vColorTL.a, vColorTR.a, materialPhase);

        float accentPhase = clamp(cardUv.x * 0.67 + cardUv.y * 0.33 + (micro - 0.5) * 0.045, 0.0, 1.0);
        vec3 accent = mix(accentTop, accentBottom, accentPhase);
        float materialLuminance = dot(base, vec3(0.2126, 0.7152, 0.0722));
        float lightMaterial = smoothstep(0.30, 0.80, materialLuminance);
        float pigmentGate = mix(1.0, 0.58, lightMaterial);
        float ambientField = fbm2(cardUv * vec2(3.4, 6.2) + noiseSeed * 0.061);
        float ambientPigment = (0.012 + hover * 0.003 + selected * 0.008) * pigmentGate;
        base = mix(base, accent, ambientPigment * (0.78 + ambientField * 0.22));
        base *= 1.0 + (micro - 0.5) * 0.008;

        vec2 fieldP = (cardUv - 0.5) * vec2(abs(vSize.x) / max(abs(vSize.y), 1.0), 1.0);
        vec3 materialTint = accent;
        float materialVeil = 0.0;
        float materialLift = 0.0;
        float materialTexture = 0.5;

        if (themeCardMaterial == 1u) {
            vec2 petalA = (fieldP - vec2(-0.68, -0.13)) * vec2(0.92, 1.28);
            vec2 petalB = (fieldP - vec2(-0.08, 0.21)) * vec2(1.08, 1.42);
            vec2 petalC = (fieldP - vec2(0.62, -0.22)) * vec2(0.96, 1.16);
            float blossomA = exp(-dot(petalA, petalA) * 3.8);
            float blossomB = exp(-dot(petalB, petalB) * 4.9);
            float blossomC = exp(-dot(petalC, petalC) * 4.2);
            materialTexture = fbm2(cardUv * vec2(4.6, 8.4) + noiseSeed * 0.15);
            float bloomField = blossomA * 0.54 + blossomB * 0.30 + blossomC * 0.22;
            materialTint = mix(accent, vec3(1.0), 0.33);
            materialVeil = 0.024 + bloomField * 0.040 + (materialTexture - 0.5) * 0.010;
            materialLift = bloomField * 0.012 + materialTexture * 0.003;
        } else if (themeCardMaterial == 2u) {
            vec2 flowUv = cardUv * vec2(2.7, 6.8) + noiseSeed * 0.11;
            float flowA = fbm2(flowUv);
            float flowB = fbm2(flowUv * vec2(1.71, 0.72) + vec2(flowA * 1.9, -flowA * 0.8));
            float caustic = pow(smoothstep(0.58, 0.91, flowA * 0.68 + flowB * 0.42), 2.2);
            vec2 lensP = fieldP - vec2(-0.42, 0.06);
            float lens = exp(-dot(lensP * vec2(0.92, 1.10), lensP * vec2(0.92, 1.10)) * 1.9);
            materialTexture = mix(flowA, flowB, 0.48);
            materialTint = mix(mix(accentTop, accentBottom, clamp(cardUv.y * 0.86 + 0.08, 0.0, 1.0)), vec3(0.82, 0.98, 1.0), 0.18);
            materialVeil = 0.025 + caustic * 0.043 + lens * 0.018;
            materialLift = caustic * 0.013 + lens * 0.008;
        } else if (themeCardMaterial == 3u) {
            float canopy = fbm2(cardUv * vec2(2.3, 5.5) + noiseSeed * 0.10);
            float understory = fbm2(cardUv * vec2(5.2, 9.4) + vec2(canopy * 1.7, -canopy * 0.9));
            float chlorophyll = pow(smoothstep(0.57, 0.88, canopy * 0.62 + understory * 0.48), 1.75);
            vec2 sunP = fieldP - vec2(-0.74, -0.20);
            float sunwell = exp(-dot(sunP * vec2(0.78, 1.02), sunP * vec2(0.78, 1.02)) * 1.75);
            materialTexture = mix(canopy, understory, 0.54);
            materialTint = mix(accent, vec3(0.84, 1.0, 0.78), 0.17);
            materialVeil = 0.022 + chlorophyll * 0.040 + sunwell * 0.020;
            materialLift = chlorophyll * 0.010 + sunwell * 0.009;
        } else if (themeCardMaterial == 4u) {
            float cloudA = fbm2(cardUv * vec2(2.7, 5.1) + noiseSeed * 0.13);
            float cloudB = fbm2(cardUv * vec2(5.3, 8.6) + vec2(cloudA * 1.5, -cloudA * 1.1));
            float nebula = pow(smoothstep(0.48, 0.86, cloudA * 0.61 + cloudB * 0.46), 1.6);
            vec2 haloP = fieldP - vec2(0.46, -0.12);
            float halo = exp(-dot(haloP * vec2(0.86, 1.22), haloP * vec2(0.86, 1.22)) * 2.5);
            materialTexture = mix(cloudA, cloudB, 0.45);
            materialTint = mix(accent, vec3(0.84, 0.91, 1.0), 0.19);
            materialVeil = 0.019 + nebula * 0.044 + halo * 0.024;
            materialLift = nebula * 0.011 + halo * 0.010;
        } else if (themeCardMaterial == 5u) {
            float polish = smoothstep(0.50, 0.84, ambientField * 0.56 + micro * 0.48);
            vec2 dawnP = fieldP - vec2(-0.58, -0.26);
            float dawn = exp(-dot(dawnP * vec2(0.86, 1.18), dawnP * vec2(0.86, 1.18)) * 2.0);
            materialTexture = mix(ambientField, micro, 0.45);
            materialTint = mix(accent, vec3(1.0, 0.88, 0.70), 0.30);
            materialVeil = 0.020 + polish * 0.035 + dawn * 0.021;
            materialLift = polish * 0.008 + dawn * 0.010;
        }

        float signature = themeCardMaterial == 0u ? 0.0 : 1.0;
        float materialGain = pigmentGate * (0.82 + hover * 0.18 + selected * 0.46);
        float veil = clamp(materialVeil * materialGain, 0.0, 0.092);
        base = mix(base, materialTint, veil);
        base *= 1.0 + (materialTexture - 0.5) * (0.004 + signature * 0.007) * pigmentGate;

        float insideDistance = max(-dS, 0.0);
        float aa = max(fwidth(dS), 0.0001);
        float bevel = 1.0 - smoothstep(0.72, 4.80 + aa, insideDistance);
        vec2 edgeNormal = roundBoxNormal(p, halfSize, cornerRadii);
        vec3 normal3 = normalize(vec3(edgeNormal * (0.52 * bevel), 1.0));
        const vec2 key2 = vec2(-0.419, -0.908);
        const vec3 halfVector = vec3(-0.228, -0.315, 0.922);
        float roughness = clamp(0.58 + (micro - 0.5) * 0.18, 0.48, 0.68);
        float keyFacing = clamp(dot(edgeNormal, key2), 0.0, 1.0);
        float rearFacing = clamp(dot(edgeNormal, -key2), 0.0, 1.0);
        float fresnel = 0.028 + 0.972 * pow(1.0 - clamp(normal3.z, 0.0, 1.0), 5.0);
        float microfacet = pow(clamp(dot(normal3, halfVector), 0.0, 1.0), mix(76.0, 36.0, roughness));
        float radiusTL = max(cornerRadii.x, 1.0);
        vec2 cornerCenter = vec2(-halfSize.x + radiusTL, -halfSize.y + radiusTL);
        vec2 glintCenter = cornerCenter + vec2(-0.47, -0.88) * radiusTL;
        vec2 boundaryPoint = p - dS * edgeNormal;
        vec2 glintDelta = boundaryPoint - glintCenter;
        float glintGate = exp(-dot(glintDelta, glintDelta) / 30.25);
        base *= 1.0 - bevel * rearFacing * (0.004 + selected * 0.008);
        float bevelEnergy = bevel * keyFacing * (0.006 + selected * 0.010 + signature * 0.003);
        float fresnelEnergy = bevel * fresnel * (0.010 + hover * 0.004 + selected * 0.012 + signature * 0.003);
        float glintEnergy = microfacet * glintGate
                * (0.005 + hover * 0.014 + selected * 0.030 + impulse * 0.045 + signature * 0.008)
                * (0.84 + micro * 0.16);
        vec3 specular = mix(vec3(1.0), mix(accent, materialTint, 0.36), 0.17 + signature * 0.10);
        vec3 headroom = max(vec3(0.0), vec3(0.985) - base);
        base += min(materialTint * materialLift * pigmentGate, headroom);
        base += min(specular * (bevelEnergy + fresnelEnergy + glintEnergy), headroom);

        float alpha = materialAlpha * coverage * clipMask;
        if (alpha <= 0.001) discard;
        FragColor = vec4(linear_to_srgb(base) * alpha, alpha);
        return;
    }

    vec2 gradUV = clamp(vLocalPx / vec2(safeWx, safeHy), 0.0, 1.0);
    vec4 col;
    if (all(equal(vColorTL, vColorTR)) && all(equal(vColorTL, vColorBR)) && all(equal(vColorTL, vColorBL))) {
        col = vColorTL;
    } else {
        vec4 colTop = oklab_mix_srgba(vColorTL, vColorTR, gradUV.x);
        vec4 colBottom = oklab_mix_srgba(vColorBL, vColorBR, gradUV.x);
        col = oklab_mix_srgba(colTop, colBottom, gradUV.y);
    }
    vec3 baseRgb = col.rgb;
    float baseAlpha = col.a;

    if (shadowMode) {
        vec2 innerSizeRaw = vUvScale;
        vec2 resolvedInner = vec2(innerSizeRaw.x > 0.0 ? innerSizeRaw.x : vSize.x, innerSizeRaw.y > 0.0 ? innerSizeRaw.y : vSize.y);
        vec2 innerHalf = max(resolvedInner * 0.5, vec2(0.0));
        vec2 center = 0.5 * (vSize - resolvedInner) + innerHalf;

        float distInner = sdRoundBox(vLocalPx - center, innerHalf, cornerRadii);
        float blur = max(vUvOffset.x, 1e-3);
        float spread = max(vUvOffset.y, 0.0);

        float distFromSpread = max(distInner - spread, 0.0);
        float norm = distFromSpread / blur;
        float gaussian = exp(-0.5 * norm * norm);

        float limit = blur * 3.0;
        float outerMask = (limit > 0.0) ? (1.0 - smoothstep(limit - fwidth(distFromSpread), limit + fwidth(distFromSpread), distFromSpread)) : 1.0;
        float innerMask = clamp((distInner + fwidth(distInner)) / max(fwidth(distInner), 1e-4), 0.0, 1.0);

        float alpha = baseAlpha * gaussian * innerMask * outerMask * clipMask;
        if (alpha <= 0.001) discard;

        FragColor = vec4(baseRgb * alpha, alpha);
        return;
    }


    if (mode == 3u) {
        if (vTexSlot < 0 || vTexSlot >= 16) discard;

        bool isMsdf = msdfMode;
        bool useScreenSpaceUv = ((vFlags >> 5) & 0x1u) == 1u;
        bool preservePremul = ((vFlags >> 6) & 0x1u) == 1u;

        vec2 sampleUv = useScreenSpaceUv ?
                        vec2(clamp(vPosPx.x * vUvScale.x + vUvOffset.x, 0.0, 1.0), clamp(vPosPx.y * vUvScale.y + vUvOffset.y, 0.0, 1.0)) :
                        vUV;

        if (isMsdf) {
            float pxRange = max(cornerRadii.x, 1e-6);
            vec2 atlasSize = max(vec2(textureDimensions(vTexSlot)), vec2(1.0));
            vec2 unitRange = vec2(pxRange) / atlasSize;

            vec2 uvDx = useScreenSpaceUv ? (dPosDx * vUvScale) : dUvDx;
            vec2 uvDy = useScreenSpaceUv ? (dPosDy * vUvScale) : dUvDy;
            vec2 uvFootprint = max(abs(uvDx) + abs(uvDy), vec2(1.0e-6));
            float screenPxRange = max(0.5 * dot(unitRange, vec2(1.0) / uvFootprint), 1.0);

            vec2 offsetLimit = 0.5 * unitRange;
            vec2 offsetA = clamp(uvDx * 0.125 + uvDy * 0.375, -offsetLimit, offsetLimit);
            vec2 offsetB = clamp(uvDx * 0.375 - uvDy * 0.125, -offsetLimit, offsetLimit);

            float opacity;
            if (screenPxRange < TEXT_SUPERSAMPLE_RANGE) {
                opacity = 0.25 * (msdfCoverage(vTexSlot, sampleUv + offsetA, screenPxRange)
                        + msdfCoverage(vTexSlot, sampleUv - offsetA, screenPxRange)
                        + msdfCoverage(vTexSlot, sampleUv + offsetB, screenPxRange)
                        + msdfCoverage(vTexSlot, sampleUv - offsetB, screenPxRange));
            } else {
                opacity = msdfCoverage(vTexSlot, sampleUv, screenPxRange);
            }

            opacity = pow(clamp(opacity, 0.0, 1.0), TEXT_GAMMA);
            float alpha = col.a * opacity;

            if (alpha <= 0.001) discard;
            FragColor = vec4(col.rgb * alpha, alpha);
            return;
        }

        vec4 tex = sampleTexture(vTexSlot, sampleUv);

        if (glassSurfaceMode) {
            float dS = sdRoundBox(p, halfSize, cornerRadii);
            float mask = getCoverage(dS) * clipMask;
            float tintAlpha = clamp(vColorTL.a, 0.0, 1.0);
            float foundationAlpha = clamp(vColorTR.a, 0.0, 1.0);
            float opacity = clamp(vColorBR.a, 0.0, 1.0);
            float blurAlpha = clamp(vColorBL.a, 0.0, 1.0);
            vec3 tintLinear = srgb_to_linear(vColorTL.rgb);
            vec3 foundationLinear = srgb_to_linear(vColorTR.rgb);
            vec3 blurLinear = srgb_to_linear(tex.rgb);
            float behindAlpha = foundationAlpha + blurAlpha * (1.0 - foundationAlpha);
            vec3 behindPremul = foundationLinear * foundationAlpha
                    + blurLinear * blurAlpha * (1.0 - foundationAlpha);
            float interiorAlpha = tintAlpha + behindAlpha * (1.0 - tintAlpha);
            vec3 interiorPremul = tintLinear * tintAlpha + behindPremul * (1.0 - tintAlpha);
            vec3 interior = interiorPremul / max(interiorAlpha, 0.0001);
            float alpha = interiorAlpha * mask * opacity;
            if (alpha <= 0.001) discard;
            FragColor = vec4(linear_to_srgb(interior) * alpha, alpha);
            return;
        }

        float dS = sdRoundBox(p, halfSize, cornerRadii);
        float mask = getCoverage(dS);

        bool isRGBA = ((vFlags >> 2) & 0x1u) == 1u;
        bool forceOpaque = ((vFlags >> 3) & 0x1u) == 1u;

        vec4 sampled = isRGBA ? vec4(tex.rgb, forceOpaque ? 1.0 : tex.a) : vec4(vec3(pow(tex.r, 1.0/1.6)), tex.r);
        vec4 colTex = sampled * col;

        if (!preservePremul) colTex.rgb *= colTex.a;
        colTex *= mask * clipMask;

        if (colTex.a <= 0.001) discard;
        FragColor = colTex;
        return;
    }


    else if (mode == 2u) {
        float radius = halfSize.x;
        float dC = length(p) - radius;

        if (thickness > 0.0) {
            dC = abs(length(p) - (radius - thickness * 0.5)) - thickness * 0.5;
        }

        float aR = getCoverage(dC);
        float aA = 1.0;

        if (arcPct * TAU < TAU - 1e-6) {
            float ang = mod(atan(p.y, p.x) - startRad + TAU, TAU);
            float center = (arcPct * TAU) * 0.5;
            float delta = max(abs(ang - center) - center, 0.0);
            aA = getCoverage(radius * delta);
        }

        col.a *= aR * aA * clipMask;
    }


    else if (mode == 1u) {
        float dOuter = sdRoundBox(p, halfSize, cornerRadii);

        vec2 halfInner = max(halfSize - thickness, vec2(0.0));
        vec4 innerRadius = max(cornerRadii - thickness, vec4(0.0));
        float dInner = sdRoundBox(p, halfInner, innerRadius);

        float alphaOuter = getCoverage(dOuter);
        float alphaInner = getCoverage(dInner);

        col.a *= clamp(alphaOuter - alphaInner, 0.0, 1.0) * clipMask;
    }


    else {
        float dS = sdRoundBox(p, halfSize, cornerRadii);
        col.a *= getCoverage(dS) * clipMask;
    }

    col.rgb *= col.a;
    if (col.a <= 0.001) discard;
    FragColor = col;
}
