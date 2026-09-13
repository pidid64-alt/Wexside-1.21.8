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
uniform float u_radius;
uniform float u_alpha;
uniform vec4 u_clipRect;
uniform vec4 u_clipRadii;

out vec4 FragColor;

const float PI = 3.14159265358979323846;
const vec2 BLUR_AXIS_LOCAL = vec2(0.81915204, -0.57357644);

float saturate(float value) {
    return clamp(value, 0.0, 1.0);
}

vec2 safeUnit(vec2 value, vec2 fallback) {
    float lengthSquared = dot(value, value);
    return lengthSquared > 0.00001 ? value * inversesqrt(lengthSquared) : fallback;
}

float quinticEase(float value) {
    return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
}

float radiusAt(vec2 p, vec4 radii) {
    return p.x > 0.0 ? (p.y > 0.0 ? radii.z : radii.y) : (p.y > 0.0 ? radii.w : radii.x);
}

float sdRoundBox(vec2 p, vec2 halfSize, float radius) {
    float r = min(max(radius, 0.0), min(halfSize.x, halfSize.y));
    vec2 q = abs(p) - halfSize + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

float sdRoundBox4(vec2 p, vec2 halfSize, vec4 radii) {
    vec4 safeRadii = min(max(radii, vec4(0.0)), min(halfSize.x, halfSize.y));
    float r = radiusAt(p, safeRadii);
    vec2 q = abs(p) - halfSize + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

float coverage(float distanceToShape) {
    float pixelWidth = max(length(vec2(dFdx(distanceToShape), dFdy(distanceToShape))), 0.0001);
    return 1.0 - smoothstep(-pixelWidth, pixelWidth, distanceToShape);
}

float roundedClipMask(vec2 pos) {
    if (u_clipRect.z <= 0.0 || u_clipRect.w <= 0.0) {
        return 0.0;
    }

    vec2 halfSize = u_clipRect.zw * 0.5;
    vec2 center = u_clipRect.xy + halfSize;
    float rounded = coverage(sdRoundBox4(pos - center, halfSize, u_clipRadii));
    float insideRect = step(u_clipRect.x, pos.x)
            * step(u_clipRect.y, pos.y)
            * (1.0 - step(u_clipRect.x + u_clipRect.z, pos.x))
            * (1.0 - step(u_clipRect.y + u_clipRect.w, pos.y));
    return rounded * insideRect;
}

vec2 localToUv(vec2 local, vec2 size) {
    vec2 texel = 1.0 / max(vec2(textureSize(u_texture, 0)), vec2(1.0));
    vec2 uvMin = texel * 0.5;
    vec2 uvMax = max(uvMin, u_textureScale - texel * 0.5);
    vec2 logicalUv = clamp(vec2(local.x, size.y - local.y) / size, vec2(0.0), vec2(1.0));
    return clamp(logicalUv * u_textureScale, uvMin, uvMax);
}

vec2 clampTextureUv(vec2 uv) {
    vec2 texel = 1.0 / max(vec2(textureSize(u_texture, 0)), vec2(1.0));
    vec2 uvMin = texel * 0.5;
    vec2 uvMax = max(uvMin, u_textureScale - texel * 0.5);
    return clamp(uv, uvMin, uvMax);
}

vec4 sampleContent(vec2 local, vec2 size) {
    return texture(u_texture, localToUv(local, size));
}

vec2 hash22(vec2 p) {
    p = vec2(dot(p, vec2(127.1, 311.7)), dot(p, vec2(269.5, 183.3)));
    vec2 h = fract(sin(p) * 43758.5453123) * 2.0 - 1.0;
    return h * inversesqrt(max(dot(h, h), 0.0001));
}

float perlin(vec2 p) {
    vec2 cell = floor(p);
    vec2 local = fract(p);
    vec2 curve = local * local * local * (local * (local * 6.0 - 15.0) + 10.0);
    float a = dot(hash22(cell), local);
    float b = dot(hash22(cell + vec2(1.0, 0.0)), local - vec2(1.0, 0.0));
    float c = dot(hash22(cell + vec2(0.0, 1.0)), local - vec2(0.0, 1.0));
    float d = dot(hash22(cell + vec2(1.0, 1.0)), local - vec2(1.0, 1.0));
    return mix(mix(a, b, curve.x), mix(c, d, curve.x), curve.y) * 0.5 + 0.5;
}

float fbm2(vec2 p) {
    float value = perlin(p) * 0.64;
    const mat2 TURN = mat2(0.8, -0.6, 0.6, 0.8);
    p = TURN * p * 2.03 + vec2(11.7, 7.3);
    return value + perlin(p) * 0.36;
}

float asymmetricVolume(vec2 p, vec2 center, vec2 axis, vec2 inverseRadius, float skew) {
    vec2 normal = vec2(-axis.y, axis.x);
    vec2 delta = p - center;
    vec2 local = vec2(dot(delta, axis), dot(delta, normal));
    local.x += skew * local.y * local.y;
    vec2 scaled = local * inverseRadius;
    return exp(-dot(scaled, scaled));
}

vec4 sampleLayerBlur(vec2 uv, vec2 direction, float fringePx, float radiusPx, vec4 center) {
    vec2 texel = 1.0 / max(vec2(textureSize(u_texture, 0)), vec2(1.0));
    vec2 axisA = safeUnit(direction, vec2(1.0, 0.0));
    vec2 axisB = vec2(-axisA.y, axisA.x);
    vec2 innerA = axisA * texel * radiusPx * 0.50;
    vec2 innerB = axisB * texel * radiusPx * 0.50;
    vec2 outerA = axisA * texel * radiusPx * 0.60;
    vec2 outerB = axisB * texel * radiusPx * 0.60;
    vec2 fringe = axisA * texel * fringePx;

    vec4 red = texture(u_texture, clampTextureUv(uv + fringe));
    vec4 blue = texture(u_texture, clampTextureUv(uv - fringe));
    vec4 chromaticCenter = vec4(red.r, center.g, blue.b, max(max(red.a, center.a), blue.a));
    chromaticCenter.rgb = min(chromaticCenter.rgb, vec3(chromaticCenter.a));

    vec4 result = chromaticCenter * 0.24;
    result += texture(u_texture, clampTextureUv(uv + innerA)) * 0.115;
    result += texture(u_texture, clampTextureUv(uv - innerA)) * 0.115;
    result += texture(u_texture, clampTextureUv(uv + innerB)) * 0.115;
    result += texture(u_texture, clampTextureUv(uv - innerB)) * 0.115;
    result += texture(u_texture, clampTextureUv(uv + outerA + outerB)) * 0.075;
    result += texture(u_texture, clampTextureUv(uv + outerA - outerB)) * 0.075;
    result += texture(u_texture, clampTextureUv(uv - outerA + outerB)) * 0.075;
    result += texture(u_texture, clampTextureUv(uv - outerA - outerB)) * 0.075;
    return result;
}

void main() {
    float progress = saturate(u_progress);
    if (u_alpha <= 0.0) {
        discard;
    }

    vec2 size = max(u_resolution, vec2(1.0));
    vec2 halfTarget = size * 0.5;
    vec2 p = v_local - halfTarget;
    float radius = min(max(u_radius, 0.0), min(halfTarget.x, halfTarget.y));
    float shapeDist = sdRoundBox(p, halfTarget, radius);
    float shape = coverage(shapeDist);
    float clipMask = roundedClipMask(v_pos);
    if (shape <= 0.0 || clipMask <= 0.0) {
        discard;
    }

    float mask = shape * clipMask;
    if (progress >= 0.9995) {
        vec4 restingContent = sampleContent(v_local, size) * mask;
        vec3 restingRgb = restingContent.rgb * u_alpha;
        float restingAlpha = restingContent.a * u_alpha;
        if (restingAlpha <= 0.001) {
            discard;
        }
        FragColor = vec4(restingRgb, restingAlpha);
        return;
    }

    float opening = step(0.5, u_color.a);
    float closingEnergy = mix(0.82, 1.0, opening);
    float minDimension = max(min(size.x, size.y), 1.0);
    vec2 localUv = clamp(v_local / size, vec2(0.0), vec2(1.0));
    vec2 metric = size / minDimension;
    vec2 q = (localUv - 0.5) * metric;

    vec2 axisA = safeUnit(vec2(0.92, 0.39), BLUR_AXIS_LOCAL);
    vec2 axisB = safeUnit(vec2(-0.48, 0.88), BLUR_AXIS_LOCAL);
    vec2 axisC = safeUnit(vec2(0.34, -0.94), BLUR_AXIS_LOCAL);
    vec2 axisD = safeUnit(vec2(-0.86, -0.51), BLUR_AXIS_LOCAL);
    float volumeA = asymmetricVolume(q, vec2(-0.78, -0.18), axisA, vec2(0.62, 2.36), 0.16);
    float volumeB = asymmetricVolume(q, vec2(0.66, 0.14), axisB, vec2(0.66, 2.18), -0.13);
    float volumeC = asymmetricVolume(q, vec2(-0.14, 0.34), axisC, vec2(0.74, 2.52), 0.18);
    float volumeD = asymmetricVolume(q, vec2(0.20, -0.35), axisD, vec2(0.70, 2.30), -0.16);
    float volumeDensity = 1.0 - (1.0 - volumeA * 0.46)
            * (1.0 - volumeB * 0.39)
            * (1.0 - volumeC * 0.33)
            * (1.0 - volumeD * 0.28);
    float grain = fbm2(q * vec2(1.72, 2.16) + vec2(7.1, 3.7)) - 0.5;

    float density = saturate(0.50 + (volumeDensity - 0.42) * 0.24 + grain * 0.14);
    float focusBase = quinticEase(progress);
    float phaseBias = clamp((density - 0.50) * 0.34, -0.10, 0.10);
    float focus = saturate(focusBase + phaseBias * 4.0 * focusBase * (1.0 - focusBase));
    float defocus = pow(1.0 - focus, 1.20);
    float transitionEnergy = max(sin(progress * PI), 0.0) * closingEnergy;
    float restFade = 1.0 - smoothstep(0.92, 0.995, progress);

    float baseBlurPx = min(7.0, minDimension * 0.050 + max(u_radius, 0.0) * 0.14);
    float blurRadiusPx = baseBlurPx * pow(defocus, 1.30);
    vec2 staticWarp = vec2(volumeA - volumeC, volumeB - volumeD);
    float opticalEnvelope = defocus * transitionEnergy * restFade;
    vec2 refractionOffset = staticWarp * minDimension * 0.0017 * opticalEnvelope;
    vec2 refractedLocal = v_local + refractionOffset;
    vec2 refractedUv = localToUv(refractedLocal, size);
    vec2 blurUvAxis = vec2(BLUR_AXIS_LOCAL.x, -BLUR_AXIS_LOCAL.y);
    vec2 chromaUvAxis = blurUvAxis;
    vec4 content = texture(u_texture, refractedUv);

    if (defocus > 0.0001) {
        float fringePx = min(blurRadiusPx * 0.055, minDimension * 0.0016) * transitionEnergy * restFade;
        vec4 blurredContent = sampleLayerBlur(refractedUv, chromaUvAxis, fringePx, blurRadiusPx, content);
        content = mix(content, blurredContent, defocus);

        float contentAlpha = max(content.a, 0.0001);
        vec3 straightContent = content.rgb / contentAlpha;
        float luminance = dot(straightContent, vec3(0.2126, 0.7152, 0.0722));
        float frostAmount = pow(defocus, 0.88) * restFade;
        vec3 frostTint = mix(u_color.rgb, u_borderColor.rgb, 0.50);
        vec3 milkyContent = mix(straightContent, vec3(mix(luminance, 1.0, 0.055)), 0.40);
        milkyContent = mix(milkyContent, frostTint, 0.11);
        content.rgb = mix(content.rgb, milkyContent * content.a, frostAmount * 0.24);

        float highlight = max(max(straightContent.r, straightContent.g), straightContent.b);
        float bokehAmount = smoothstep(0.62, 0.98, highlight)
                * defocus * transitionEnergy * restFade * 0.075;
        vec3 bokehTarget = mix(straightContent, vec3(mix(luminance, 1.0, 0.08)), 0.58);
        bokehTarget = mix(bokehTarget, u_emissiveColor.rgb, 0.10);
        content.rgb = mix(content.rgb, min(bokehTarget * content.a, vec3(content.a)), bokehAmount);
    }

    float contentAlpha = max(content.a, 0.0001);
    vec3 contentStraight = content.rgb / contentAlpha;
    float contentLuminance = dot(contentStraight, vec3(0.2126, 0.7152, 0.0722));
    float frostVeilAlpha = pow(defocus, 0.96) * restFade * (0.016 + transitionEnergy * 0.020);
    vec3 frostVeilColor = mix(vec3(mix(contentLuminance, 1.0, 0.040)), mix(u_color.rgb, u_borderColor.rgb, 0.50), 0.10);
    vec3 frostVeilPremul = frostVeilColor * frostVeilAlpha;

    float themeEmission = saturate(u_emissiveColor.a / max(progress, 0.05));
    float diffusePocket = volumeA * 0.40 + volumeB * 0.30 + volumeC * 0.19 + volumeD * 0.11;
    float emissiveAlpha = transitionEnergy * restFade * pow(defocus, 0.70)
            * themeEmission * (0.016 + diffusePocket * 0.020);
    vec3 emissivePremul = u_emissiveColor.rgb * emissiveAlpha;

    vec3 materialRgb = frostVeilPremul + content.rgb * (1.0 - frostVeilAlpha);
    float materialAlpha = frostVeilAlpha + content.a * (1.0 - frostVeilAlpha);
    vec3 outRgb = (emissivePremul + materialRgb * (1.0 - emissiveAlpha)) * mask * u_alpha;
    float outAlpha = (emissiveAlpha + materialAlpha * (1.0 - emissiveAlpha)) * mask * u_alpha;
    if (outAlpha <= 0.001) {
        discard;
    }
    FragColor = vec4(outRgb, outAlpha);
}
