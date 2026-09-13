#version 330 core

in vec2 v_uv;
in vec2 v_local;
in vec2 v_pos;

uniform sampler2D u_texture;
uniform vec2 u_textureScale;
uniform vec2 u_resolution;
uniform float u_time;
uniform float u_progress;
uniform float u_contentReveal;
uniform float u_focus;
uniform float u_threat;
uniform float u_exposure;
uniform vec4 u_color;
uniform vec4 u_borderColor;
uniform vec4 u_emissiveColor;
uniform vec4 u_emissiveColor2;
uniform float u_radius;
uniform float u_alpha;
uniform vec4 u_clipRect;
uniform vec4 u_clipRadii;

out vec4 FragColor;

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

vec3 mod289(vec3 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
    return mod289((x * 34.0 + 1.0) * x);
}

float simplex(vec2 v) {
    const vec4 C = vec4(
        0.211324865405187,
        0.366025403784439,
       -0.577350269189626,
        0.024390243902439
    );

    vec2 i = floor(v + dot(v, C.yy));
    vec2 x0 = v - i + dot(i, C.xx);
    vec2 i1 = x0.x > x0.y ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod289(i);
    vec3 p = permute(permute(i.y + vec3(0.0, i1.y, 1.0)) + i.x + vec3(0.0, i1.x, 1.0));
    vec3 m = max(0.5 - vec3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
    m *= m;
    m *= m;
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);
    vec3 g;
    g.x = a0.x * x0.x + h.x * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    mat2 r = mat2(0.8, -0.6, 0.6, 0.8);
    for (int i = 0; i < 5; i++) {
        v += (simplex(p) * 0.5 + 0.5) * a;
        p = r * p * 2.03 + vec2(11.7);
        a *= 0.5;
    }
    return v;
}

float sdRoundBox(vec2 p, vec2 halfSize, float radius) {
    float r = min(radius, min(halfSize.x, halfSize.y));
    vec2 q = abs(p) - halfSize + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

float coverage(float d) {
    float px = max(fwidth(d) * 0.70710678, 0.0001);
    return smoothstep(px, -px, d);
}

float roundedClipMask(vec2 pos) {
    if (u_clipRect.z <= 0.0 || u_clipRect.w <= 0.0) return 0.0;
    if (pos.x < u_clipRect.x || pos.y < u_clipRect.y ||
        pos.x >= u_clipRect.x + u_clipRect.z || pos.y >= u_clipRect.y + u_clipRect.w) return 0.0;
    if (u_clipRadii.x + u_clipRadii.y + u_clipRadii.z + u_clipRadii.w <= 0.0001) return 1.0;
    vec2 size = u_clipRect.zw;
    vec2 halfSize = size * 0.5;
    vec2 center = u_clipRect.xy + halfSize;
    float r = max(max(u_clipRadii.x, u_clipRadii.y), max(u_clipRadii.z, u_clipRadii.w));
    return coverage(sdRoundBox(pos - center, halfSize, r));
}

float perimeterPosition(vec2 local, vec2 size) {
    float top = local.y;
    float right = size.x - local.x;
    float bottom = size.y - local.y;
    float left = local.x;
    float perimeter = max(1.0, 2.0 * (size.x + size.y));
    if (top <= right && top <= bottom && top <= left) return local.x / perimeter;
    if (right <= bottom && right <= left) return (size.x + local.y) / perimeter;
    if (bottom <= left) return (size.x + size.y + (size.x - local.x)) / perimeter;
    return (size.x + size.y + size.x + (size.y - local.y)) / perimeter;
}

vec2 logicalTextureUv(vec2 logicalUv) {
    vec2 texel = 1.0 / max(vec2(textureSize(u_texture, 0)), vec2(1.0));
    vec2 uvMin = texel * 0.5;
    vec2 uvMax = max(uvMin, u_textureScale - texel * 0.5);
    return clamp(clamp(logicalUv, vec2(0.0), vec2(1.0)) * u_textureScale, uvMin, uvMax);
}

vec2 clampTextureUv(vec2 uv) {
    vec2 texel = 1.0 / max(vec2(textureSize(u_texture, 0)), vec2(1.0));
    vec2 uvMin = texel * 0.5;
    vec2 uvMax = max(uvMin, u_textureScale - texel * 0.5);
    return clamp(uv, uvMin, uvMax);
}

vec4 sampleChromatic(vec2 uv, vec2 direction, float strength) {
    vec2 delta = direction * strength * u_textureScale;
    vec4 r = texture(u_texture, clampTextureUv(uv + delta));
    vec4 g = texture(u_texture, clampTextureUv(uv));
    vec4 b = texture(u_texture, clampTextureUv(uv - delta));
    return vec4(r.r, g.g, b.b, max(max(r.a, g.a), b.a));
}

vec4 sampleLayerBlur(vec2 uv, vec2 direction, float chromaStrength, float radiusPx) {
    vec2 texel = 1.0 / max(vec2(textureSize(u_texture, 0)), vec2(1.0));
    vec2 axisA = normalize(direction + vec2(0.19, 0.37));
    vec2 axisB = vec2(-axisA.y, axisA.x);
    vec2 rA = axisA * texel * radiusPx;
    vec2 rB = axisB * texel * radiusPx;
    vec4 c = sampleChromatic(uv, direction, chromaStrength) * 0.22;
    c += sampleChromatic(uv + rA, direction, chromaStrength) * 0.11;
    c += sampleChromatic(uv - rA, direction, chromaStrength) * 0.11;
    c += sampleChromatic(uv + rB, direction, chromaStrength) * 0.11;
    c += sampleChromatic(uv - rB, direction, chromaStrength) * 0.11;
    c += sampleChromatic(uv + rA + rB, direction, chromaStrength) * 0.085;
    c += sampleChromatic(uv + rA - rB, direction, chromaStrength) * 0.085;
    c += sampleChromatic(uv - rA + rB, direction, chromaStrength) * 0.085;
    c += sampleChromatic(uv - rA - rB, direction, chromaStrength) * 0.085;
    return c;
}

void main() {
    float progress = clamp(u_progress, 0.0, 1.0);
    float contentReveal = clamp(u_contentReveal, 0.0, 1.0);
    float focus = clamp(u_focus, 0.0, 1.0);
    float threat = clamp(u_threat, 0.0, 1.0);
    float exposure = clamp(u_exposure, 0.0, 1.0);

    vec2 halfSize = u_resolution * 0.5;
    vec2 p = v_local - halfSize;
    float d = sdRoundBox(p, halfSize, max(0.0, u_radius));
    float shape = coverage(d);
    float clipMask = roundedClipMask(v_pos);

    if (shape <= 0.0 || clipMask <= 0.0 || u_alpha <= 0.0) discard;

    vec2 uvLocal = clamp(v_local / max(u_resolution, vec2(1.0)), vec2(0.0), vec2(1.0));
    float revealEase = smoothstep(0.0, 1.0, progress);
    float contentEase = smoothstep(0.16, 0.98, contentReveal);
    float readableGate = smoothstep(0.02, 0.26, progress);
    float transitionPulse = pow(max(sin(revealEase * 3.14159265), 0.0), 0.82);

    vec2 flow = vec2(u_time * 0.22, -u_time * 0.14);
    float structureNoise = fbm(uvLocal * vec2(5.8, 4.6) + flow);
    float mistNoise = fbm(uvLocal * vec2(13.8, 10.6) - flow * 1.42);
    float filamentNoise = fbm(uvLocal * vec2(25.0, 18.6) + flow * 2.08);
    float arcNoise = fbm(uvLocal * vec2(33.0, 24.0) - flow * 2.72);
    float curtainNoise = fbm(uvLocal * vec2(7.6, 17.2) + vec2(-u_time * 0.18, u_time * 0.26));
    float ionNoise = fbm(uvLocal * vec2(18.4, 7.0) - vec2(u_time * 0.34, -u_time * 0.22));
    float sparkleNoise = fbm(uvLocal * vec2(45.0, 39.0) + vec2(u_time * 0.44, -u_time * 0.38));
    float directional = dot(uvLocal - 0.5, normalize(vec2(0.76, -0.48))) * 0.30;
    float field = structureNoise * 0.60 + mistNoise * 0.24 + filamentNoise * 0.10 + directional;

    float cutoff = mix(1.18, -0.20, revealEase);
    float feather = mix(0.16, 0.28, transitionPulse);
    float dissolve = progress >= 0.998 ? 1.0 : smoothstep(cutoff - feather, cutoff + feather, field);
    float edgeBand = 1.0 - smoothstep(0.0, feather * 4.8, abs(field - cutoff));
    float dissolveEdge = edgeBand * (1.0 - smoothstep(0.0, feather * 1.25, abs(field - cutoff)));
    float smoke = max(smoothstep(cutoff - feather * 1.85, cutoff + feather * 0.65, field) - dissolve, 0.0);
    float plasmaBand = pow(max(1.0 - abs(field - cutoff + (arcNoise - 0.5) * feather * 1.28) / (feather * 1.05), 0.0), 2.2);
    float emberBand = pow(max(1.0 - abs(field - cutoff - (filamentNoise - 0.5) * feather * 0.72) / (feather * 0.56), 0.0), 5.0);

    vec2 distortionFlow = vec2(fbm(uvLocal * 8.6 + u_time * 0.23), fbm(uvLocal * 8.6 - u_time * 0.19)) * 2.0 - 1.0;
    float transitionEnergy = pow(max(1.0 - progress, 0.0), 0.72)
            * smoothstep(0.02, 0.30, progress)
            * (1.0 - smoothstep(0.94, 1.0, progress));
    float liveEnergy = focus * 0.14 + threat * 0.18 + exposure * 0.26;
    float fogEnergy = transitionEnergy + liveEnergy + (1.0 - contentEase) * 0.24;
    float radial = max(0.0, 1.0 - length((uvLocal - 0.5) * vec2(1.18, 1.62)));
    float innerBloom = pow(radial, 2.00) * (transitionPulse * 0.38 + exposure * 0.26 + focus * 0.32);
    float auroraShape = 1.0 - abs((uvLocal.y - 0.50) + (curtainNoise - 0.5) * 0.38 + sin(uvLocal.x * 6.2831853 + u_time * 0.74) * 0.06);
    float aurora = smoothstep(0.22, 0.86, auroraShape + radial * 0.18) * (transitionPulse * 0.52 + focus * 0.46 + exposure * 0.20);
    float ionRibbon = smoothstep(0.42, 0.90, 1.0 - abs((uvLocal.x - 0.52) * 1.1 - (ionNoise - 0.5) * 0.44 + (uvLocal.y - 0.5) * 0.32))
            * (0.30 * (1.0 - contentEase) + 0.12 * exposure + 0.08 * focus);
    float sparkle = pow(max(sparkleNoise - 0.80, 0.0) / 0.20, 3.2) * (transitionPulse * 0.36 + exposure * 0.18);
    vec2 distortedUv = logicalTextureUv(v_uv + distortionFlow * fogEnergy * mix(0.010, 0.018, transitionPulse));
    vec2 chromaDirection = normalize(distortionFlow + vec2(0.35, -0.21));

    float blurRadius = transitionEnergy * 7.4 + dissolveEdge * 2.8 + exposure * 1.9 + (1.0 - contentEase) * 8.0;
    vec4 crispContent = sampleChromatic(distortedUv, chromaDirection, (liveEnergy + (1.0 - contentEase) * 0.10) * 0.0056);
    vec4 softContent = sampleLayerBlur(distortedUv, chromaDirection, fogEnergy * 0.0048, blurRadius);
    float contentNoiseGate = progress >= 0.998 ? 1.0 : smoothstep(cutoff - feather * 0.58 - contentEase * 0.14, cutoff + feather * 1.18, field + contentEase * 0.12);
    float contentFloor = max(readableGate * 0.94, smoothstep(0.06, 0.52, contentEase) * 0.90);
    float contentMask = shape * clipMask * max(mix(contentNoiseGate, 1.0, 0.52 + contentEase * 0.24), contentFloor);
    vec4 content = mix(softContent, crispContent, max(contentEase, readableGate * 0.74));

    float innerEdge = 1.0 - smoothstep(0.0, max(1.2, u_radius * 0.28), -d);
    float border = innerEdge * shape;
    float edgeMask = (1.0 - smoothstep(0.0, 8.5, abs(d))) * shape;

    float sweepHead = fract(revealEase * 0.74 + 0.05 + transitionPulse * 0.08);
    float perimeter = perimeterPosition(v_local, u_resolution);
    float sweepDelta = abs(fract(perimeter - sweepHead + 0.5) - 0.5);
    float sweepCore = exp(-sweepDelta * sweepDelta * 520.0);
    float sweepTrail = exp(-sweepDelta * sweepDelta * 64.0) * 0.40;
    float sweepGate = smoothstep(0.03, 0.36, progress) * (1.0 - smoothstep(0.94, 1.0, progress));
    float sweep = (sweepCore + sweepTrail) * edgeMask * sweepGate;

    float focusSweepHead = fract(u_time * 0.20);
    float focusSweepDelta = abs(fract(perimeter - focusSweepHead + 0.5) - 0.5);
    float focusPulse = 0.72 + 0.28 * sin(u_time * 4.2);
    float focusSweep = exp(-focusSweepDelta * focusSweepDelta * 160.0) * edgeMask * focus * 0.82 * focusPulse;

    float threatPulse = 0.5 + 0.5 * sin(u_time * (8.0 + threat * 3.0) + perimeter * 16.0);
    float threatRim = edgeMask * threat * threatPulse * 0.35;
    float exposureRing = exp(-abs(d) * 0.11) * exposure * 0.55;
    float focusRing = exp(-abs(d) * 0.16) * focus * (0.52 + 0.48 * sin(u_time * 5.6)) * 0.32;

    float glintWave = sin((uvLocal.x * 1.42 - uvLocal.y * 0.86 + revealEase * 2.25 - u_time * 0.32) * 6.2831853) * 0.5 + 0.5;
    float glint = pow(glintWave, 8.0) * dissolveEdge * transitionPulse * 0.34;

    float accentMix = smoothstep(0.0, 1.0, uvLocal.x * 0.68 + (1.0 - uvLocal.y) * 0.32);
    vec3 accentA = u_emissiveColor.rgb;
    vec3 accentB = u_emissiveColor2.rgb;
    vec3 accentMid = oklab_mix_srgb(accentA, accentB, accentMix);
    vec3 accentFlash = oklab_mix_srgb(accentB, vec3(1.0), 0.28 + exposure * 0.18);
    vec3 prismColor = oklab_mix_srgb(oklab_mix_srgb(accentA, vec3(1.0), 0.18), oklab_mix_srgb(accentB, vec3(1.0), 0.12), clamp(curtainNoise * 0.88 + ionNoise * 0.12, 0.0, 1.0));
    vec3 mistColor = oklab_mix_srgb(accentMid, accentFlash, 0.42 + exposure * 0.18);
    vec3 auroraColor = oklab_mix_srgb(prismColor, accentFlash, 0.24 + transitionPulse * 0.20 + exposure * 0.10);

    float plasmaMist = smoke * (0.48 + 0.32 * (1.0 - contentEase))
            + (1.0 - contentEase) * (1.0 - dissolve) * 0.18;
    float plasmaCorona = plasmaBand * (0.46 + 0.54 * (1.0 - contentEase));
    float totalGlow = dissolveEdge * 0.82 + sweep * 0.72 + glint * 0.58
                    + focusSweep + threatRim + exposureRing + focusRing + plasmaMist * 0.58 + plasmaCorona * 0.92 + emberBand * 0.68
                    + aurora * 0.82 + ionRibbon * 0.56 + innerBloom * 0.74 + sparkle * 0.48;
    vec3 accentGradient = oklab_mix_srgb(accentMid, accentFlash,
            glint * 0.55 + sweep * 0.18 + focusSweep * 0.10 + exposureRing * 0.25 + plasmaMist * 0.18 + plasmaCorona * 0.26 + emberBand * 0.36
            + aurora * 0.22 + ionRibbon * 0.18 + innerBloom * 0.16);

    vec3 baseRgb = oklab_mix_srgb(u_color.rgb, accentMid,
            dissolveEdge * 0.08 + sweep * 0.04 + focus * 0.02 + threat * 0.03 + plasmaMist * 0.05 + plasmaCorona * 0.08 + aurora * 0.07 + innerBloom * 0.06);
    float baseAlpha = u_color.a;
    vec3 borderRgb = u_borderColor.rgb * u_borderColor.a * border;
    float emissiveAlpha = max(u_emissiveColor.a, u_emissiveColor2.a);
    vec3 bodyBloom = auroraColor * (aurora * 0.32 + ionRibbon * 0.24)
            + accentMid * innerBloom * 0.28
            + prismColor * sparkle * 0.34;
    vec3 emissive = accentGradient * emissiveAlpha
            * (dissolveEdge * 0.74 + sweep * 0.68 + glint * 0.54 + focusSweep + threatRim + exposureRing + focusRing + plasmaCorona * 0.78
            + aurora * 0.58 + ionRibbon * 0.42 + innerBloom * 0.34)
            + mistColor * emissiveAlpha * plasmaMist * 0.46
            + accentMid * emissiveAlpha * plasmaCorona * 0.72
            + accentFlash * emissiveAlpha * emberBand * 0.88
            + auroraColor * emissiveAlpha * aurora * 0.76
            + prismColor * emissiveAlpha * ionRibbon * 0.62
            + accentFlash * emissiveAlpha * innerBloom * 0.58
            + prismColor * emissiveAlpha * sparkle * 0.70;
    float glowAlpha = (dissolveEdge * 0.22 + sweep * 0.18 + glint * 0.14
                     + focusSweep * 0.18 + threatRim * 0.12 + exposureRing * 0.20 + focusRing * 0.20 + plasmaMist * 0.24 + plasmaCorona * 0.34 + emberBand * 0.22
                     + aurora * 0.22 + ionRibbon * 0.16 + innerBloom * 0.20 + sparkle * 0.08) * emissiveAlpha;

    float materialMask = shape * clipMask * max(dissolve, contentFloor * 0.98);
    float backgroundAlpha = max(baseAlpha, glowAlpha) * materialMask;
    vec3 backgroundPremul = (baseRgb * baseAlpha + borderRgb + emissive + bodyBloom * emissiveAlpha * (0.44 + totalGlow * 0.08)) * materialMask;

    content.a *= contentMask;
    content.rgb *= contentMask;

    vec3 outRgb = content.rgb + backgroundPremul * (1.0 - content.a);
    float outAlpha = content.a + backgroundAlpha * (1.0 - content.a);

    outRgb *= u_alpha;
    outAlpha *= u_alpha;

    if (outAlpha <= 0.001) discard;

    FragColor = vec4(outRgb, outAlpha);
}
