#version 330 core

in vec2 v_uv;
in vec2 v_local;
in vec2 v_pos;

uniform sampler2D u_texture;
uniform vec2 u_textureScale;
uniform vec2 u_resolution;
uniform float u_time;
uniform float u_progress;
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

vec2 hash22(vec2 p) {
    p = vec2(dot(p, vec2(127.1, 311.7)), dot(p, vec2(269.5, 183.3)));
    vec2 h = fract(sin(p) * 43758.5453123) * 2.0 - 1.0;
    return h * inversesqrt(max(dot(h, h), 0.0001));
}

float perlin(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    float a = dot(hash22(i + vec2(0.0, 0.0)), f - vec2(0.0, 0.0));
    float b = dot(hash22(i + vec2(1.0, 0.0)), f - vec2(1.0, 0.0));
    float c = dot(hash22(i + vec2(0.0, 1.0)), f - vec2(0.0, 1.0));
    float d = dot(hash22(i + vec2(1.0, 1.0)), f - vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y) * 0.5 + 0.5;
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    mat2 r = mat2(0.8, -0.6, 0.6, 0.8);
    for (int i = 0; i < 5; i++) {
        v += perlin(p) * a;
        p = r * p * 2.03 + vec2(11.7);
        a *= 0.5;
    }
    return v;
}

float radiusForQuadrant(vec2 p, float r) {
    return r;
}

float sdRoundBox(vec2 p, vec2 halfSize, float radius) {
    float r = min(radiusForQuadrant(p, radius), min(halfSize.x, halfSize.y));
    vec2 q = abs(p) - halfSize + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

float coverage(float d) {
    float px = max(fwidth(d) * 0.70710678, 0.0001);
    return smoothstep(px, -px, d);
}

float roundedClipMask(vec2 pos) {
    if (u_clipRect.z <= 0.0 || u_clipRect.w <= 0.0) {
        return 0.0;
    }
    if (pos.x < u_clipRect.x || pos.y < u_clipRect.y ||
        pos.x >= u_clipRect.x + u_clipRect.z || pos.y >= u_clipRect.y + u_clipRect.w) {
        return 0.0;
    }
    if (u_clipRadii.x + u_clipRadii.y + u_clipRadii.z + u_clipRadii.w <= 0.0001) {
        return 1.0;
    }
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
    if (top <= right && top <= bottom && top <= left) {
        return local.x / perimeter;
    }
    if (right <= bottom && right <= left) {
        return (size.x + local.y) / perimeter;
    }
    if (bottom <= left) {
        return (size.x + size.y + (size.x - local.x)) / perimeter;
    }
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
    float a = max(max(r.a, g.a), b.a);
    return vec4(r.r, g.g, b.b, a);
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
    vec2 halfSize = u_resolution * 0.5;
    vec2 p = v_local - halfSize;
    float d = sdRoundBox(p, halfSize, max(0.0, u_radius));
    float shape = coverage(d);
    float clipMask = roundedClipMask(v_pos);

    if (shape <= 0.0 || clipMask <= 0.0 || u_alpha <= 0.0) {
        discard;
    }

    vec2 uvLocal = clamp(v_local / max(u_resolution, vec2(1.0)), vec2(0.0), vec2(1.0));
    float revealEase = progress * progress * (3.0 - 2.0 * progress);
    float transitionPulse = pow(max(sin(progress * 3.14159265), 0.0), 0.82);
    vec2 flow = vec2(u_time * 0.22, -u_time * 0.14);
    float noise = fbm(uvLocal * vec2(6.6, 5.0) + flow);
    float fineNoise = fbm(uvLocal * vec2(20.0, 15.4) - flow * 1.52);
    float directional = dot(uvLocal, normalize(vec2(0.72, -0.48))) * 0.22;
    float field = noise * 0.72 + fineNoise * 0.18 + directional;
    float cutoff = mix(1.12, -0.24, revealEase);
    float feather = mix(0.095, 0.26, transitionPulse);
    float dissolve = progress >= 0.995 ? 1.0 : smoothstep(cutoff - feather, cutoff + feather, field);
    float dissolveEdge = progress >= 0.995 ? 0.0 : (1.0 - smoothstep(0.0, feather * 3.8, abs(field - cutoff)));
    dissolveEdge *= smoothstep(0.0, feather * 0.8, abs(field - cutoff));

    vec2 distortionFlow = vec2(fbm(uvLocal * 10.0 + u_time * 0.27), fbm(uvLocal * 10.0 - u_time * 0.23)) * 2.0 - 1.0;
    float transitionEnergy = pow(max(1.0 - progress, 0.0), 0.78)
            * smoothstep(0.015, 0.24, progress)
            * (1.0 - smoothstep(0.965, 1.0, progress));
    vec2 distortedUv = logicalTextureUv(v_uv + distortionFlow * transitionEnergy * mix(0.010, 0.018, transitionPulse));
    vec2 chromaDirection = normalize(distortionFlow + vec2(0.35, -0.21));
    float focusBlur = transitionEnergy * 6.2 + dissolveEdge * 2.35;
    vec4 sharpContent = sampleChromatic(distortedUv, chromaDirection, transitionEnergy * 0.0062);
    vec4 blurredContent = sampleLayerBlur(distortedUv, chromaDirection, transitionEnergy * 0.0048, focusBlur);
    float focusMix = smoothstep(0.0, 5.8, focusBlur);
    vec4 content = mix(sharpContent, blurredContent, focusMix);

    float innerEdge = 1.0 - smoothstep(0.0, max(1.2, u_radius * 0.28), -d);
    float border = innerEdge * shape;
    float edgeMask = (1.0 - smoothstep(0.0, 8.5, abs(d))) * shape;
    float sweepHead = fract(progress * 0.72 + 0.06 + transitionPulse * 0.08);
    float perimeter = perimeterPosition(v_local, u_resolution);
    float sweepDelta = abs(fract(perimeter - sweepHead + 0.5) - 0.5);
    float sweepCore = exp(-sweepDelta * sweepDelta * 520.0);
    float sweepTrail = exp(-sweepDelta * sweepDelta * 64.0) * 0.38;
    float sweepGate = smoothstep(0.035, 0.36, progress) * (1.0 - smoothstep(0.94, 1.0, progress));
    float sweep = (sweepCore + sweepTrail) * edgeMask * sweepGate;
    float glintWave = sin((uvLocal.x * 1.42 - uvLocal.y * 0.86 + progress * 2.25 - u_time * 0.32) * 6.2831853) * 0.5 + 0.5;
    float glint = pow(glintWave, 8.0) * dissolveEdge * transitionPulse * 0.34;

    float accentMix = smoothstep(0.0, 1.0, uvLocal.x * 0.68 + (1.0 - uvLocal.y) * 0.32);
    vec3 accentA = u_emissiveColor.rgb;
    vec3 accentB = u_emissiveColor2.rgb;
    vec3 accentMid = oklab_mix_srgb(accentA, accentB, accentMix);
    vec3 accentFlash = oklab_mix_srgb(accentB, vec3(1.0), 0.28);
    vec3 accentGradient = oklab_mix_srgb(accentMid, accentFlash, glint * 0.55 + sweep * 0.18);

    vec3 baseRgb = oklab_mix_srgb(u_color.rgb, accentMid, dissolveEdge * 0.055 + sweep * 0.035);
    float baseAlpha = u_color.a;
    vec3 borderRgb = u_borderColor.rgb * u_borderColor.a * border;
    float emissiveAlpha = max(u_emissiveColor.a, u_emissiveColor2.a);
    vec3 emissive = accentGradient * emissiveAlpha * (dissolveEdge * 0.78 + sweep * 0.72 + glint * 0.58);
    float glowAlpha = (dissolveEdge * 0.22 + sweep * 0.18 + glint * 0.14) * emissiveAlpha;

    float materialMask = shape * clipMask * dissolve;
    float backgroundAlpha = max(baseAlpha, glowAlpha) * materialMask;
    vec3 backgroundPremul = (baseRgb * baseAlpha + borderRgb + emissive) * materialMask;

    content.a *= materialMask;
    content.rgb *= materialMask;

    vec3 outRgb = content.rgb + backgroundPremul * (1.0 - content.a);
    float outAlpha = content.a + backgroundAlpha * (1.0 - content.a);

    outRgb *= u_alpha;
    outAlpha *= u_alpha;

    if (outAlpha <= 0.001) {
        discard;
    }

    FragColor = vec4(outRgb, outAlpha);
}
