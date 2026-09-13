#version 150

#moj_import <minecraft:globals.glsl>

uniform sampler2D u_SceneColor;
uniform sampler2D u_SceneDepth;

layout(std140) uniform ChinaHatMaterial {
    vec4 u_AccentTop;
    vec4 u_AccentBottom;
    vec4 u_ResolutionInfo;
    vec4 u_Material;
    vec4 u_KeyLightDirection;
    vec4 u_KeyLightColor;
    vec4 u_AuraParams;
    vec4 u_HatUp;
    mat4 u_InvProjection;
    vec4 u_AuraCenter;
    vec4 u_AuraMajorX;
    vec4 u_AuraMajorZ;
    vec4 u_AuraVertical;
};

in vec3 vViewPos;
in vec3 vNormal;
in vec2 vUv;
in float vCoverage;

out vec4 fragColor;

const float TAU = 6.28318530718;
const float DIELECTRIC_F0 = 0.0426;

float saturate(float value) {
    return clamp(value, 0.0, 1.0);
}

float square(float value) {
    return value * value;
}

vec3 safeNormalize(vec3 value) {
    return value * inversesqrt(max(dot(value, value), 1.0e-8));
}

float luminance(vec3 color) {
    return dot(color, vec3(0.2126, 0.7152, 0.0722));
}

vec3 srgbToLinear(vec3 color) {
    vec3 low = color / 12.92;
    vec3 high = pow((color + 0.055) / 1.055, vec3(2.4));
    return mix(low, high, step(vec3(0.04045), color));
}

vec3 linearToSrgb(vec3 color) {
    vec3 linear = max(color, vec3(0.0));
    vec3 low = linear * 12.92;
    vec3 high = 1.055 * pow(linear, vec3(1.0 / 2.4)) - 0.055;
    return mix(low, high, step(vec3(0.0031308), linear));
}

vec3 linearSrgbToOklab(vec3 color) {
    float l = 0.4122214708 * color.r + 0.5363325363 * color.g + 0.0514459929 * color.b;
    float m = 0.2119034982 * color.r + 0.6806995451 * color.g + 0.1073969566 * color.b;
    float s = 0.0883024619 * color.r + 0.2817188376 * color.g + 0.6299787005 * color.b;
    float lRoot = sign(l) * pow(abs(l), 1.0 / 3.0);
    float mRoot = sign(m) * pow(abs(m), 1.0 / 3.0);
    float sRoot = sign(s) * pow(abs(s), 1.0 / 3.0);
    return vec3(
        0.2104542553 * lRoot + 0.7936177850 * mRoot - 0.0040720468 * sRoot,
        1.9779984951 * lRoot - 2.4285922050 * mRoot + 0.4505937099 * sRoot,
        0.0259040371 * lRoot + 0.7827717662 * mRoot - 0.8086757660 * sRoot
    );
}

vec3 oklabToLinearSrgb(vec3 color) {
    float lRoot = color.x + 0.3963377774 * color.y + 0.2158037573 * color.z;
    float mRoot = color.x - 0.1055613458 * color.y - 0.0638541728 * color.z;
    float sRoot = color.x - 0.0894841775 * color.y - 1.2914855480 * color.z;
    float l = lRoot * lRoot * lRoot;
    float m = mRoot * mRoot * mRoot;
    float s = sRoot * sRoot * sRoot;
    return vec3(
        4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
        -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
        -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
    );
}

float smootherStep(float value) {
    float t = saturate(value);
    return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
}

bool inDisplayGamut(vec3 color) {
    return all(greaterThanEqual(color, vec3(0.0))) && all(lessThanEqual(color, vec3(1.0)));
}

vec3 gamutMapOklab(vec3 lab) {
    vec3 linear = oklabToLinearSrgb(lab);
    if (inDisplayGamut(linear)) {
        return linear;
    }
    float low = 0.0;
    float high = 1.0;
    for (int iteration = 0; iteration < 4; iteration++) {
        float middle = (low + high) * 0.5;
        vec3 candidate = oklabToLinearSrgb(vec3(lab.x, lab.yz * middle));
        if (inDisplayGamut(candidate)) {
            low = middle;
        } else {
            high = middle;
        }
    }
    return clamp(oklabToLinearSrgb(vec3(lab.x, lab.yz * low)), 0.0, 1.0);
}

vec3 oklabMix(vec3 encodedA, vec3 encodedB, float t) {
    vec3 labA = linearSrgbToOklab(srgbToLinear(encodedA));
    vec3 labB = linearSrgbToOklab(srgbToLinear(encodedB));
    return gamutMapOklab(mix(labA, labB, smootherStep(t)));
}

vec2 safeUv(vec2 uv) {
    vec2 margin = max(u_ResolutionInfo.zw * 0.5, vec2(0.00001));
    return clamp(uv, margin, vec2(1.0) - margin);
}

vec3 sceneLinear(vec2 uv) {
    vec2 sampleUv = safeUv(uv);
    vec2 texelPosition = sampleUv * u_ResolutionInfo.xy - 0.5;
    ivec2 base = ivec2(floor(texelPosition));
    ivec2 maximum = ivec2(u_ResolutionInfo.xy) - ivec2(1);
    ivec2 p00 = clamp(base, ivec2(0), maximum);
    ivec2 p10 = clamp(base + ivec2(1, 0), ivec2(0), maximum);
    ivec2 p01 = clamp(base + ivec2(0, 1), ivec2(0), maximum);
    ivec2 p11 = clamp(base + ivec2(1), ivec2(0), maximum);
    vec2 fraction = fract(texelPosition);
    vec3 c00 = srgbToLinear(texelFetch(u_SceneColor, p00, 0).rgb);
    vec3 c10 = srgbToLinear(texelFetch(u_SceneColor, p10, 0).rgb);
    vec3 c01 = srgbToLinear(texelFetch(u_SceneColor, p01, 0).rgb);
    vec3 c11 = srgbToLinear(texelFetch(u_SceneColor, p11, 0).rgb);
    return mix(mix(c00, c10, fraction.x), mix(c01, c11, fraction.x), fraction.y);
}

float viewDepth(vec2 uv, float depth) {
    vec4 view = u_InvProjection * vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    return max(0.0, -view.z / max(abs(view.w), 1.0e-6));
}

float texelDepthPermission(ivec2 coordinate, float hatDepth) {
    vec2 uv = (vec2(coordinate) + vec2(0.5)) * u_ResolutionInfo.zw;
    float sceneDepth = viewDepth(uv, texelFetch(u_SceneDepth, coordinate, 0).r);
    float feather = max(0.0025, 1.75 * fwidth(hatDepth));
    return smootherStep((sceneDepth - hatDepth + feather) / (2.0 * feather));
}

vec4 depthPermissions(ivec2 p00, ivec2 p10, ivec2 p01, ivec2 p11, float hatDepth) {
    return vec4(
        texelDepthPermission(p00, hatDepth),
        texelDepthPermission(p10, hatDepth),
        texelDepthPermission(p01, hatDepth),
        texelDepthPermission(p11, hatDepth)
    );
}

vec3 guardedScene(vec3 fallback, vec2 sampleUv, float hatDepth) {
    vec2 uv = safeUv(sampleUv);
    vec2 texelPosition = uv * u_ResolutionInfo.xy - 0.5;
    ivec2 base = ivec2(floor(texelPosition));
    ivec2 maximum = ivec2(u_ResolutionInfo.xy) - ivec2(1);
    ivec2 p00 = clamp(base, ivec2(0), maximum);
    ivec2 p10 = clamp(base + ivec2(1, 0), ivec2(0), maximum);
    ivec2 p01 = clamp(base + ivec2(0, 1), ivec2(0), maximum);
    ivec2 p11 = clamp(base + ivec2(1), ivec2(0), maximum);
    vec2 fraction = fract(texelPosition);
    vec4 weights = vec4(
        (1.0 - fraction.x) * (1.0 - fraction.y),
        fraction.x * (1.0 - fraction.y),
        (1.0 - fraction.x) * fraction.y,
        fraction.x * fraction.y
    );
    vec4 selected = weights * depthPermissions(p00, p10, p01, p11, hatDepth);
    float selectedWeight = dot(selected, vec4(1.0));
    if (selectedWeight <= 1.0e-5) {
        return fallback;
    }
    vec3 filtered = (
        srgbToLinear(texelFetch(u_SceneColor, p00, 0).rgb) * selected.x +
        srgbToLinear(texelFetch(u_SceneColor, p10, 0).rgb) * selected.y +
        srgbToLinear(texelFetch(u_SceneColor, p01, 0).rgb) * selected.z +
        srgbToLinear(texelFetch(u_SceneColor, p11, 0).rgb) * selected.w
    ) / selectedWeight;
    return mix(fallback, filtered, smootherStep(saturate(selectedWeight * 4.0)));
}

vec3 shellTint(float lane) {
    return oklabMix(u_AccentTop.rgb, u_AccentBottom.rgb, lane);
}

vec3 fiberTint(float phase) {
    float loop = 0.5 + 0.5 * cos(TAU * phase);
    return oklabMix(u_AccentBottom.rgb, u_AccentTop.rgb, loop);
}

float surfaceField(float lane, float phase) {
    float primary = 0.5 + 0.5 * cos(TAU * (phase - GameTime * 4.0));
    float secondary = 0.5 + 0.5 * cos(TAU * (phase * 2.0 + lane * 0.55 + GameTime * 2.0));
    return mix(primary, secondary, 0.30);
}

vec3 frostedRefraction(vec3 fallback, vec2 refractedUv, float hatDepth, float roughness) {
    vec2 radius = u_ResolutionInfo.zw * mix(0.44, 1.08, roughness);
    vec3 center = guardedScene(fallback, refractedUv, hatDepth);
    vec3 xPositive = guardedScene(fallback, refractedUv + vec2(radius.x, 0.0), hatDepth);
    vec3 xNegative = guardedScene(fallback, refractedUv - vec2(radius.x, 0.0), hatDepth);
    vec3 yPositive = guardedScene(fallback, refractedUv + vec2(0.0, radius.y), hatDepth);
    vec3 yNegative = guardedScene(fallback, refractedUv - vec2(0.0, radius.y), hatDepth);
    return center * 0.36 + (xPositive + xNegative + yPositive + yNegative) * 0.16;
}

float distributionGgx(float noH, float roughness) {
    float a = max(roughness * roughness, 0.0025);
    float a2 = a * a;
    float denominator = noH * noH * (a2 - 1.0) + 1.0;
    return a2 / max(3.14159265359 * denominator * denominator, 1.0e-5);
}

float geometrySchlickGgx(float noX, float roughness) {
    float k = square(roughness + 1.0) * 0.125;
    return noX / max(noX * (1.0 - k) + k, 1.0e-5);
}

float geometrySmith(float noV, float noL, float roughness) {
    return geometrySchlickGgx(noV, roughness) * geometrySchlickGgx(noL, roughness);
}

vec3 fresnelSchlick(float voH, vec3 f0) {
    return f0 + (vec3(1.0) - f0) * pow(1.0 - saturate(voH), 5.0);
}

vec3 addThroughHeadroom(vec3 base, vec3 energy) {
    vec3 boundedBase = clamp(base, 0.0, 1.0);
    return boundedBase + (vec3(1.0) - boundedBase) * (vec3(1.0) - exp(-max(energy, vec3(0.0))));
}

vec3 limitLuminance(vec3 energy, float limit) {
    vec3 positive = max(energy, vec3(0.0));
    float energyLuminance = max(luminance(positive), 0.0);
    return positive * (limit / max(limit + energyLuminance, 1.0e-5));
}

float filteredRoughness(vec3 normal, float roughness) {
    vec3 normalDx = dFdx(normal);
    vec3 normalDy = dFdy(normal);
    float variance = max(dot(normalDx, normalDx), dot(normalDy, normalDy));
    return clamp(sqrt(roughness * roughness + min(variance * 0.72, 0.12)), roughness, 0.42);
}

vec3 dielectricSpecular(vec3 normal, vec3 viewDirection, float roughness) {
    vec3 lightDirection = safeNormalize(u_KeyLightDirection.xyz);
    vec3 halfDirection = safeNormalize(viewDirection + lightDirection);
    float noV = saturate(dot(normal, viewDirection));
    float noL = saturate(dot(normal, lightDirection));
    float noH = saturate(dot(normal, halfDirection));
    float voH = saturate(dot(viewDirection, halfDirection));
    vec3 f = fresnelSchlick(voH, vec3(DIELECTRIC_F0));
    float d = distributionGgx(noH, roughness);
    float g = geometrySmith(noV, noL, roughness);
    vec3 direct = f * (d * g * noL / max(4.0 * noV * noL, 1.0e-4));
    return direct * max(u_KeyLightColor.rgb, vec3(0.0)) * u_KeyLightDirection.w;
}

vec3 dielectricComposite(float lane, vec3 rawNormal, vec3 viewDirection, vec2 sceneUv) {
    float normalizedLane = saturate(lane);
    float noV = saturate(dot(rawNormal, viewDirection));
    float fresnel = DIELECTRIC_F0 + (1.0 - DIELECTRIC_F0) * pow(1.0 - noV, 5.0);
    float coverage = saturate(vCoverage * u_Material.x);
    float phase = fract(vUv.x);
    float angularWeight = smootherStep((normalizedLane - 0.105) / 0.180);
    float shellLightWeight = smootherStep((normalizedLane - 0.160) / 0.400);
    float field = mix(0.5, surfaceField(normalizedLane, phase), angularWeight);
    float roughnessFloor = mix(0.240, 0.110, smootherStep(normalizedLane / 0.650));
    float roughness = max(mix(0.220, mix(0.090, 0.185, field), angularWeight), roughnessFloor);
    roughness = filteredRoughness(rawNormal, roughness);
    float hatDepth = max(0.001, -vViewPos.z);
    vec3 scene = sceneLinear(sceneUv);
    vec3 tint = shellTint(normalizedLane);
    float bendPx = 1.18 * (0.50 + 0.50 * roughness) * length(rawNormal.xy) * (1.0 - fresnel) * smoothstep(0.04, 0.28, noV);
    vec2 refractedUv = safeUv(sceneUv - rawNormal.xy * u_ResolutionInfo.zw * bendPx);
    vec3 refracted = frostedRefraction(scene, refractedUv, hatDepth, roughness);
    vec3 reflected = guardedScene(scene, safeUv(sceneUv + rawNormal.xy * u_ResolutionInfo.zw * 0.72), hatDepth);
    vec3 sigmaA = mix(vec3(0.22, 0.16, 0.07), vec3(1.10, 0.84, 0.24), vec3(1.0) - clamp(tint, 0.0, 1.0));
    float opticalLength = 0.438 + 0.45 * square(1.0 - noV) + 0.08 * field;
    vec3 beer = exp(-sigmaA * opticalLength);
    float incident = sqrt(max(luminance(refracted), 0.0));
    vec3 scatter = tint * (1.0 - beer) * (0.055 + 0.055 * roughness) * (0.25 + 0.75 * incident) * u_Material.y;
    float topBand = exp(-square((normalizedLane - 0.105) * 10.0));
    float glint = pow(saturate(0.5 + 0.5 * cos(TAU * (phase - GameTime * 12.0))), 15.0) * angularWeight;
    float mica = smoothstep(0.76, 0.94, field) * (0.18 + 0.82 * topBand);
    vec3 thinFilm = mix(tint, vec3(1.0), 0.30) * fresnel * (0.012 + 0.045 * topBand * glint + 0.018 * mica);
    float edgeFrost = pow(1.0 - noV, 0.68);
    vec3 frost = mix(tint, vec3(0.78, 0.88, 1.0), 0.12) * (0.158 + 0.235 * edgeFrost + 0.044 * field);
    vec3 directSpecular = dielectricSpecular(rawNormal, viewDirection, roughness) * mix(0.10, 0.82, shellLightWeight);
    directSpecular = limitLuminance(directSpecular, mix(0.10, 0.38, shellLightWeight));
    vec3 ambient = mix(vec3(0.032, 0.052, 0.105), tint, 0.28) * fresnel * (0.36 + u_KeyLightColor.w);
    vec3 reflectionDirection = reflect(-viewDirection, rawNormal);
    vec3 lightDirection = safeNormalize(u_KeyLightDirection.xyz);
    float skyWeight = saturate(reflectionDirection.y * 0.5 + 0.5);
    float sunWeight = pow(saturate(dot(reflectionDirection, lightDirection)), mix(30.0, 12.0, roughness)) * shellLightWeight;
    vec3 analyticEnvironment = mix(vec3(0.035, 0.060, 0.130), max(u_KeyLightColor.rgb, vec3(0.0)), skyWeight * 0.78 + 0.08);
    analyticEnvironment += max(u_KeyLightColor.rgb, vec3(0.0)) * sunWeight * 0.38;
    vec3 transport = refracted * beer * (1.0 - fresnel) + reflected * fresnel;
    vec3 glass = addThroughHeadroom(transport, analyticEnvironment * fresnel * 1.35 + scatter + thinFilm + frost + ambient);
    glass = addThroughHeadroom(glass, directSpecular);
    return mix(scene, glass, coverage);
}

vec3 fiberComposite(float phase, vec3 rawNormal, vec3 viewDirection, vec2 sceneUv) {
    float noV = saturate(dot(rawNormal, viewDirection));
    float fresnel = DIELECTRIC_F0 + (1.0 - DIELECTRIC_F0) * pow(1.0 - noV, 5.0);
    float coverage = saturate(vCoverage * u_Material.x);
    float cross = fract(vUv.y);
    float carrier = 0.50 + 0.50 * pow(saturate(0.5 + 0.5 * cos(TAU * cross)), 0.80);
    float travelling = pow(saturate(0.5 + 0.5 * cos(TAU * (phase - GameTime * 17.0))), 16.0);
    float hatDepth = max(0.001, -vViewPos.z);
    vec3 scene = sceneLinear(sceneUv);
    vec3 tint = fiberTint(phase);
    vec3 refracted = guardedScene(scene, safeUv(sceneUv - rawNormal.xy * u_ResolutionInfo.zw * 0.38), hatDepth);
    vec3 reflected = guardedScene(scene, safeUv(sceneUv + rawNormal.xy * u_ResolutionInfo.zw * 0.55), hatDepth);
    vec3 guided = tint * (0.48 + 0.76 * carrier + 0.38 * travelling) * u_Material.z;
    float roughness = filteredRoughness(rawNormal, 0.110);
    vec3 specular = dielectricSpecular(rawNormal, viewDirection, roughness) * (0.72 + 0.42 * carrier);
    specular = limitLuminance(specular, 0.48);
    vec3 opticalLift = max(refracted - scene, vec3(0.0)) * (0.14 + 0.10 * noV);
    opticalLift += max(reflected - scene, vec3(0.0)) * fresnel * 0.65;
    vec3 fiber = addThroughHeadroom(scene, opticalLift + guided);
    fiber = addThroughHeadroom(fiber, specular);
    return mix(scene, fiber, coverage);
}

float dither(vec2 pixel) {
    return fract(52.9829189 * fract(dot(pixel, vec2(0.06711056, 0.00583715)))) - 0.5;
}

void main() {
    float pass = floor(vUv.y + 0.0001);
    vec2 sceneUv = gl_FragCoord.xy * u_ResolutionInfo.zw;
    vec3 normal = safeNormalize(vNormal);
    vec3 viewDirection = safeNormalize(-vViewPos);
    if (dot(normal, viewDirection) < 0.0) {
        normal = -normal;
    }
    vec3 color = vec3(0.0);
    float ditherWeight = saturate(vCoverage * u_Material.x);

    if (pass < 0.5) {
        color = dielectricComposite(fract(vUv.y), normal, viewDirection, sceneUv);
    } else if (pass < 1.5) {
        color = fiberComposite(fract(vUv.x), normal, viewDirection, sceneUv);
    } else if (pass < 2.5) {
        color = dielectricComposite(fract(vUv.y), normal, viewDirection, sceneUv);
    } else {
        discard;
    }

    vec3 encoded = linearToSrgb(clamp(color, 0.0, 1.0));
    encoded = clamp(encoded + dither(gl_FragCoord.xy) * ditherWeight * (1.0 / 255.0), 0.0, 1.0);
    fragColor = vec4(encoded, 1.0);
}
