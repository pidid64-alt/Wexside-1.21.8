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

flat in vec3 vCenterView;
flat in vec3 vMajorXView;
flat in vec3 vMajorZView;
flat in vec3 vVerticalView;
flat in vec4 vCenterClip;
flat in vec4 vMajorXClip;
flat in vec4 vMajorZClip;
flat in vec4 vVerticalClip;
flat in float vProjectedRadiusPx;
flat in float vBoundVisibility;

out vec4 fragColor;

const float PI = 3.14159265359;
const float TAU = 6.28318530718;
const float RADIAL_RATIO = 0.025 / 1.025;
const float OCCLUSION_BIAS = 0.006;

struct AuraCandidate {
    float theta;
    float phiFirst;
    float phiSecond;
    float signedSurfaceDistancePx;
    float viewDepth;
    float deviceDepth;
    vec3 viewPosition;
    vec3 normal;
};

struct SurfaceHit {
    float theta;
    float phi;
    float residual2;
    float viewDepth;
    float deviceDepth;
    bool valid;
};

float saturate(float value) {
    return clamp(value, 0.0, 1.0);
}

float square(float value) {
    return value * value;
}

vec2 safeUnit(vec2 value) {
    return value * inversesqrt(max(dot(value, value), 1.0e-8));
}

vec3 safeNormalize(vec3 value) {
    return value * inversesqrt(max(dot(value, value), 1.0e-8));
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

vec3 fiberTint(float phase) {
    float loop = 0.5 + 0.5 * cos(TAU * phase);
    vec3 labA = linearSrgbToOklab(srgbToLinear(u_AccentBottom.rgb));
    vec3 labB = linearSrgbToOklab(srgbToLinear(u_AccentTop.rgb));
    return gamutMapOklab(mix(labA, labB, smootherStep(loop)));
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

float dither(vec2 pixel) {
    return fract(52.9829189 * fract(dot(pixel, vec2(0.06711056, 0.00583715)))) - 0.5;
}

float viewDepth(vec2 uv, float depth) {
    vec4 view = u_InvProjection * vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    return max(0.0, -view.z / max(abs(view.w), 1.0e-6));
}

float sceneVisibility(vec2 uv, float rimDepth, float feather) {
    float sceneDepth = viewDepth(uv, texture(u_SceneDepth, safeUv(uv)).r);
    float separation = sceneDepth - rimDepth - OCCLUSION_BIAS;
    return smootherStep(separation / max(feather, 1.0e-6));
}

vec2 clipPixel(vec4 clip) {
    return (clip.xy / clip.w * 0.5 + 0.5) * u_ResolutionInfo.xy;
}

void curveData(float theta, out vec4 clip, out vec4 firstClip, out vec4 secondClip,
               out vec2 pixel, out vec2 firstPixel, out vec2 secondPixel) {
    float cosine = cos(theta);
    float sine = sin(theta);
    clip = vCenterClip + vMajorXClip * cosine + vMajorZClip * sine;
    firstClip = -vMajorXClip * sine + vMajorZClip * cosine;
    secondClip = -vMajorXClip * cosine - vMajorZClip * sine;
    float inverseW = 1.0 / clip.w;
    float inverseW2 = inverseW * inverseW;
    float inverseW3 = inverseW2 * inverseW;
    vec2 ndc = clip.xy * inverseW;
    vec2 firstNdc = firstClip.xy * inverseW - clip.xy * firstClip.w * inverseW2;
    vec2 secondNdc = secondClip.xy * inverseW
            - 2.0 * firstClip.xy * firstClip.w * inverseW2
            - clip.xy * secondClip.w * inverseW2
            + 2.0 * clip.xy * firstClip.w * firstClip.w * inverseW3;
    pixel = (ndc * 0.5 + 0.5) * u_ResolutionInfo.xy;
    firstPixel = firstNdc * (u_ResolutionInfo.xy * 0.5);
    secondPixel = secondNdc * (u_ResolutionInfo.xy * 0.5);
}

float curveDistance2(float theta, vec2 target) {
    float cosine = cos(theta);
    float sine = sin(theta);
    vec4 clip = vCenterClip + vMajorXClip * cosine + vMajorZClip * sine;
    vec2 pixel = clipPixel(clip);
    vec2 delta = pixel - target;
    return dot(delta, delta);
}

float wrappedAngleDistance(float first, float second) {
    return abs(mod(first - second + PI, TAU) - PI);
}

float refineTheta(float theta, vec2 target) {
    for (int iteration = 0; iteration < 6; iteration++) {
        vec4 clip;
        vec4 firstClip;
        vec4 secondClip;
        vec2 pixel;
        vec2 firstPixel;
        vec2 secondPixel;
        curveData(theta, clip, firstClip, secondClip, pixel, firstPixel, secondPixel);
        vec2 residual = pixel - target;
        float currentDistance2 = dot(residual, residual);
        float gradient = dot(residual, firstPixel);
        float curvature = dot(firstPixel, firstPixel) + dot(residual, secondPixel);
        float stableCurvature = max(curvature, dot(firstPixel, firstPixel) * 0.125 + 1.0e-4);
        float stepAngle = clamp(gradient / stableCurvature, -PI * 0.125, PI * 0.125);
        float candidateTheta = theta - stepAngle;
        float candidateDistance2 = curveDistance2(candidateTheta, target);
        if (candidateDistance2 > currentDistance2) {
            candidateTheta = theta - stepAngle * 0.5;
            candidateDistance2 = curveDistance2(candidateTheta, target);
        }
        if (candidateDistance2 > currentDistance2) {
            candidateTheta = theta - stepAngle * 0.25;
            candidateDistance2 = curveDistance2(candidateTheta, target);
        }
        if (candidateDistance2 <= currentDistance2) {
            theta = candidateTheta;
        }
    }
    return mod(theta + TAU, TAU);
}

void coarseSeeds(vec2 target, out float firstTheta, out float secondTheta) {
    float distances[16];
    int firstIndex = 0;
    float firstDistance = 1.0e30;
    for (int index = 0; index < 16; index++) {
        float theta = TAU * float(index) * (1.0 / 16.0);
        distances[index] = curveDistance2(theta, target);
        if (distances[index] < firstDistance) {
            firstDistance = distances[index];
            firstIndex = index;
        }
    }
    firstTheta = TAU * float(firstIndex) * (1.0 / 16.0);
    secondTheta = mod(firstTheta + PI, TAU);
    float secondDistance = curveDistance2(secondTheta, target);
    for (int index = 0; index < 16; index++) {
        int previous = (index + 15) % 16;
        int next = (index + 1) % 16;
        float theta = TAU * float(index) * (1.0 / 16.0);
        float separation = wrappedAngleDistance(theta, firstTheta);
        bool localMinimum = distances[index] <= distances[previous] && distances[index] <= distances[next];
        if (localMinimum && separation >= PI * 0.25 && distances[index] < secondDistance) {
            secondDistance = distances[index];
            secondTheta = theta;
        }
    }
}

vec2 projectedDerivativePx(vec4 clip, vec4 derivativeClip) {
    float inverseW = 1.0 / clip.w;
    vec2 derivativeNdc = derivativeClip.xy * inverseW - clip.xy * derivativeClip.w * inverseW * inverseW;
    return derivativeNdc * (u_ResolutionInfo.xy * 0.5);
}

void surfaceData(float theta, float phi, out vec3 viewPosition, out vec4 clip,
                 out vec2 pixel, out vec2 thetaPixel, out vec2 phiPixel) {
    float thetaCosine = cos(theta);
    float thetaSine = sin(theta);
    float phiCosine = cos(phi);
    float phiSine = sin(phi);
    float radialScale = 1.0 + RADIAL_RATIO * phiCosine;
    vec3 ringView = vMajorXView * thetaCosine + vMajorZView * thetaSine;
    vec3 tangentView = -vMajorXView * thetaSine + vMajorZView * thetaCosine;
    vec4 ringClip = vMajorXClip * thetaCosine + vMajorZClip * thetaSine;
    vec4 tangentClip = -vMajorXClip * thetaSine + vMajorZClip * thetaCosine;
    viewPosition = vCenterView + ringView * radialScale + vVerticalView * phiSine;
    clip = vCenterClip + ringClip * radialScale + vVerticalClip * phiSine;
    vec4 thetaClip = tangentClip * radialScale;
    vec4 phiClip = ringClip * (-RADIAL_RATIO * phiSine) + vVerticalClip * phiCosine;
    pixel = clipPixel(clip);
    thetaPixel = projectedDerivativePx(clip, thetaClip);
    phiPixel = projectedDerivativePx(clip, phiClip);
}

SurfaceHit solveSurfaceHit(float thetaSeed, float phiSeed, vec2 target) {
    float theta = mod(thetaSeed + TAU, TAU);
    float phi = mod(phiSeed + TAU, TAU);
    for (int iteration = 0; iteration < 5; iteration++) {
        vec3 viewPosition;
        vec4 clip;
        vec2 pixel;
        vec2 thetaPixel;
        vec2 phiPixel;
        surfaceData(theta, phi, viewPosition, clip, pixel, thetaPixel, phiPixel);
        vec2 residual = pixel - target;
        float currentResidual2 = dot(residual, residual);
        if (currentResidual2 <= 9.0e-4) {
            break;
        }
        float thetaLength2 = dot(thetaPixel, thetaPixel);
        float phiLength2 = dot(phiPixel, phiPixel);
        float coupling = dot(thetaPixel, phiPixel);
        float regularization = max(1.0e-4, max(thetaLength2, phiLength2) * 1.0e-6);
        float a = thetaLength2 + regularization;
        float d = phiLength2 + regularization;
        float determinant = a * d - coupling * coupling;
        if (!(determinant > 1.0e-10) || !(currentResidual2 < 1.0e20)) {
            break;
        }
        float thetaGradient = dot(thetaPixel, residual);
        float phiGradient = dot(phiPixel, residual);
        float thetaStep = (d * thetaGradient - coupling * phiGradient) / determinant;
        float phiStep = (a * phiGradient - coupling * thetaGradient) / determinant;
        thetaStep = clamp(thetaStep, -PI * 0.125, PI * 0.125);
        phiStep = clamp(phiStep, -PI * 0.25, PI * 0.25);
        bool accepted = false;
        float attenuation = 1.0;
        for (int lineSearch = 0; lineSearch < 3; lineSearch++) {
            float candidateTheta = mod(theta - thetaStep * attenuation + TAU, TAU);
            float candidatePhi = mod(phi - phiStep * attenuation + TAU, TAU);
            vec3 candidateView;
            vec4 candidateClip;
            vec2 candidatePixel;
            vec2 candidateThetaPixel;
            vec2 candidatePhiPixel;
            surfaceData(candidateTheta, candidatePhi, candidateView, candidateClip,
                    candidatePixel, candidateThetaPixel, candidatePhiPixel);
            vec2 candidateResidual = candidatePixel - target;
            float candidateResidual2 = dot(candidateResidual, candidateResidual);
            if (candidateResidual2 <= currentResidual2) {
                theta = candidateTheta;
                phi = candidatePhi;
                accepted = true;
                break;
            }
            attenuation *= 0.5;
        }
        if (!accepted) {
            break;
        }
    }
    vec3 viewPosition;
    vec4 clip;
    vec2 pixel;
    vec2 thetaPixel;
    vec2 phiPixel;
    surfaceData(theta, phi, viewPosition, clip, pixel, thetaPixel, phiPixel);
    vec2 residual = pixel - target;
    SurfaceHit hit;
    hit.theta = theta;
    hit.phi = phi;
    hit.residual2 = dot(residual, residual);
    hit.viewDepth = -viewPosition.z;
    hit.deviceDepth = clip.z / clip.w * 0.5 + 0.5;
    hit.valid = hit.residual2 <= 0.0625
            && clip.w > 1.0e-5
            && hit.viewDepth > 0.0
            && hit.deviceDepth >= 0.0
            && hit.deviceDepth <= 1.0;
    return hit;
}

void selectFrontHit(inout SurfaceHit best, SurfaceHit candidate) {
    if (candidate.valid && (!best.valid || candidate.viewDepth < best.viewDepth)) {
        best = candidate;
    }
}

SurfaceHit resolveFrontHit(AuraCandidate first, AuraCandidate second, vec2 target) {
    SurfaceHit best;
    best.theta = 0.0;
    best.phi = 0.0;
    best.residual2 = 1.0e30;
    best.viewDepth = 1.0e30;
    best.deviceDepth = 1.0;
    best.valid = false;
    selectFrontHit(best, solveSurfaceHit(first.theta, first.phiFirst, target));
    selectFrontHit(best, solveSurfaceHit(first.theta, first.phiSecond, target));
    selectFrontHit(best, solveSurfaceHit(second.theta, second.phiFirst, target));
    selectFrontHit(best, solveSurfaceHit(second.theta, second.phiSecond, target));
    return best;
}

AuraCandidate evaluateCandidate(float theta, vec2 target) {
    vec4 lineClip;
    vec4 firstClip;
    vec4 secondClip;
    vec2 linePixel;
    vec2 firstPixel;
    vec2 secondPixel;
    curveData(theta, lineClip, firstClip, secondClip, linePixel, firstPixel, secondPixel);
    float cosine = cos(theta);
    float sine = sin(theta);
    vec3 radialView = (vMajorXView * cosine + vMajorZView * sine) * RADIAL_RATIO;
    vec4 radialClip = (vMajorXClip * cosine + vMajorZClip * sine) * RADIAL_RATIO;
    vec2 radialPixel = (clipPixel(lineClip + radialClip) - clipPixel(lineClip - radialClip)) * 0.5;
    vec2 verticalPixel = (clipPixel(lineClip + vVerticalClip) - clipPixel(lineClip - vVerticalClip)) * 0.5;
    vec2 residual = target - linePixel;
    vec2 supportAxis = dot(radialPixel, radialPixel) >= dot(verticalPixel, verticalPixel) ? radialPixel : verticalPixel;
    vec2 tangentNormal = safeUnit(vec2(-firstPixel.y, firstPixel.x));
    vec2 screenNormal = dot(residual, residual) > 1.0e-6
            ? safeUnit(residual)
            : (dot(firstPixel, firstPixel) > 1.0e-6 ? tangentNormal : safeUnit(supportAxis));
    float radialSupport = dot(screenNormal, radialPixel);
    float verticalSupport = dot(screenNormal, verticalPixel);
    float carrierRadiusPx = sqrt(radialSupport * radialSupport + verticalSupport * verticalSupport);
    float centerDistance2 = dot(residual, residual);
    float signedSurfaceDistancePx = sqrt(centerDistance2) - carrierRadiusPx;
    float inverseCarrier = 1.0 / max(carrierRadiusPx, 1.0e-6);
    float radialCoefficient = radialSupport * inverseCarrier;
    float verticalCoefficient = verticalSupport * inverseCarrier;
    if (centerDistance2 <= 1.0e-6) {
        float frontSupport = sqrt(radialView.z * radialView.z + vVerticalView.z * vVerticalView.z);
        radialCoefficient = frontSupport > 1.0e-8 ? radialView.z / frontSupport : 0.0;
        verticalCoefficient = frontSupport > 1.0e-8 ? vVerticalView.z / frontSupport : 0.0;
    }
    vec3 lineView = vCenterView + vMajorXView * cosine + vMajorZView * sine;
    vec3 supportView = radialView * radialCoefficient + vVerticalView * verticalCoefficient;
    vec4 supportClip = radialClip * radialCoefficient + vVerticalClip * verticalCoefficient;
    vec3 surfaceView = lineView + supportView;
    vec4 surfaceClip = lineClip + supportClip;
    vec3 ringRadial = vMajorXView * cosine + vMajorZView * sine;
    vec3 ringTangent = -vMajorXView * sine + vMajorZView * cosine;
    vec3 planeNormal = safeNormalize(cross(vMajorXView, vMajorZView));
    vec3 geometricNormal = safeNormalize(cross(ringTangent, planeNormal));
    geometricNormal = dot(geometricNormal, geometricNormal) > 1.0e-6 ? geometricNormal : safeNormalize(ringRadial);
    geometricNormal *= dot(geometricNormal, ringRadial) < 0.0 ? -1.0 : 1.0;
    AuraCandidate candidate;
    candidate.theta = theta;
    candidate.phiFirst = mod(atan(verticalCoefficient, radialCoefficient) + TAU, TAU);
    candidate.phiSecond = mod(candidate.phiFirst + PI, TAU);
    candidate.signedSurfaceDistancePx = signedSurfaceDistancePx;
    candidate.viewDepth = max(0.0, -surfaceView.z);
    candidate.deviceDepth = surfaceClip.z / surfaceClip.w * 0.5 + 0.5;
    candidate.viewPosition = surfaceView;
    candidate.normal = geometricNormal;
    return candidate;
}

void main() {
    if (vBoundVisibility <= 1.0e-5) {
        discard;
    }
    vec2 target = gl_FragCoord.xy;
    float firstSeed;
    float secondSeed;
    coarseSeeds(target, firstSeed, secondSeed);
    float firstTheta = refineTheta(firstSeed, target);
    float secondTheta = refineTheta(secondSeed, target);
    if (wrappedAngleDistance(firstTheta, secondTheta) < PI * 0.0625) {
        secondTheta = refineTheta(mod(firstTheta + PI, TAU), target);
    }
    AuraCandidate first = evaluateCandidate(firstTheta, target);
    AuraCandidate second = evaluateCandidate(secondTheta, target);
    float firstDepthFeather = max(0.0025, 1.75 * fwidth(first.viewDepth));
    float secondDepthFeather = max(0.0025, 1.75 * fwidth(second.viewDepth));
    float signedDifference = abs(first.signedSurfaceDistancePx - second.signedSurfaceDistancePx);
    float metricDifference = signedDifference * signedDifference;
    float unionSignedDistancePx = min(first.signedSurfaceDistancePx, second.signedSurfaceDistancePx);
    SurfaceHit frontHit;
    frontHit.theta = 0.0;
    frontHit.phi = 0.0;
    frontHit.residual2 = 1.0e30;
    frontHit.viewDepth = first.viewDepth;
    frontHit.deviceDepth = first.deviceDepth;
    frontHit.valid = false;
    if (unionSignedDistancePx <= 0.0) {
        frontHit = resolveFrontHit(first, second, target);
    }
    float frontHitDepthFeather = max(0.0025, 1.75 * fwidth(frontHit.viewDepth));
    float distancePx = max(unionSignedDistancePx, 0.0);
    float aa = max(fwidth(distancePx), 0.72);
    float halfWidthPx = clamp(vProjectedRadiusPx * 0.13, 5.20, 10.50);
    float profileScale = clamp(halfWidthPx / 10.50, 0.50, 1.0);
    float tailStart = max(4.20, halfWidthPx - 2.05);
    float tailEnd = max(5.20, halfWidthPx - 0.30);
    float core = 1.0 - smoothstep(0.62 - aa * 0.62, 0.62 + aa * 0.84, distancePx);
    float inner = exp(-square(distancePx / (1.82 * profileScale)) * 1.62);
    float halo = exp(-square(max(distancePx - 0.45, 0.0) / (5.95 * profileScale)) * 1.36);
    float skirt = exp(-square(max(distancePx - 0.70, 0.0) / (8.40 * profileScale)) * 2.55);
    float boundary = 1.0 - smoothstep(tailStart, tailEnd, distancePx);
    bool firstValid = first.deviceDepth >= 0.0 && first.deviceDepth <= 1.0 && first.viewDepth > 0.0;
    bool secondValid = second.deviceDepth >= 0.0 && second.deviceDepth <= 1.0 && second.viewDepth > 0.0;
    bool firstContributes = firstValid && first.signedSurfaceDistancePx <= tailEnd + aa;
    bool secondContributes = secondValid && second.signedSurfaceDistancePx <= tailEnd + aa;
    if (!firstContributes && !secondContributes) {
        discard;
    }
    bool secondWins = secondContributes && (!firstContributes || second.viewDepth < first.viewDepth);
    AuraCandidate winner = first;
    if (secondWins) {
        winner = second;
    }
    float depthViewDepth = frontHit.valid ? frontHit.viewDepth : winner.viewDepth;
    float depthDeviceDepth = frontHit.valid ? frontHit.deviceDepth : winner.deviceDepth;
    float winnerDepthFeather = frontHit.valid
            ? frontHitDepthFeather
            : (secondWins ? secondDepthFeather : firstDepthFeather);
    vec2 sceneUv = gl_FragCoord.xy * u_ResolutionInfo.zw;
    float visibility = vBoundVisibility * u_Material.x * boundary * sceneVisibility(sceneUv, depthViewDepth, winnerDepthFeather);
    if (boundary * vBoundVisibility * u_Material.x <= 1.0e-4 || visibility <= 1.0e-4) {
        discard;
    }
    float phase = fract(winner.theta / TAU);
    float ambiguityFade = smoothstep(0.0625, 0.50, metricDifference);
    bool bothContribute = firstContributes && secondContributes;
    float ambiguity = bothContribute ? 1.0 - ambiguityFade : 0.0;
    ambiguityFade = bothContribute ? ambiguityFade : 1.0;
    float firstNoV = saturate(abs(dot(first.normal, safeNormalize(-first.viewPosition))));
    float secondNoV = saturate(abs(dot(second.normal, safeNormalize(-second.viewPosition))));
    float winnerNoV = secondWins ? secondNoV : firstNoV;
    float noV = mix(winnerNoV, (firstNoV + secondNoV) * 0.5, ambiguity);
    float contour = 0.76 + 0.24 * pow(1.0 - noV, 1.35);
    float travelling = pow(saturate(0.5 + 0.5 * cos(TAU * (phase - GameTime * 12.0))), 14.0) * ambiguityFade;
    vec3 tint;
    if (ambiguity > 1.0e-3) {
        vec3 firstTint = fiberTint(fract(first.theta / TAU));
        vec3 secondTint = fiberTint(fract(second.theta / TAU));
        vec3 winnerTint = secondWins ? secondTint : firstTint;
        tint = mix(winnerTint, (firstTint + secondTint) * 0.5, ambiguity);
    } else {
        tint = fiberTint(phase);
    }
    vec3 hot = mix(tint, vec3(1.0), 0.24 + 0.16 * travelling);
    vec3 keyDirection = safeNormalize(u_KeyLightDirection.xyz);
    float firstKey = saturate(dot(first.normal, keyDirection) * 0.5 + 0.5);
    float secondKey = saturate(dot(second.normal, keyDirection) * 0.5 + 0.5);
    float winnerKey = secondWins ? secondKey : firstKey;
    float keyWeight = mix(winnerKey, (firstKey + secondKey) * 0.5, ambiguity);
    vec3 key = max(u_KeyLightColor.rgb, vec3(0.0)) * keyWeight;
    vec3 emission = tint * (core * 0.70 + inner * 0.330 + halo * 0.340 + skirt * 0.145);
    emission += hot * (core * 0.280 + inner * 0.105 + halo * 0.072 + travelling * (inner * 0.315 + halo * 0.130));
    emission += key * tint * (core * 0.120 + inner * 0.072 + halo * 0.028);
    vec3 scene = sceneLinear(sceneUv);
    vec3 energy = max(emission * contour * visibility, vec3(0.0));
    vec3 composed = vec3(1.0) - (vec3(1.0) - clamp(scene, 0.0, 1.0)) * exp(-energy);
    vec3 encodedScene = linearToSrgb(clamp(scene, 0.0, 1.0));
    vec3 encoded = linearToSrgb(clamp(composed, 0.0, 1.0));
    encoded = max(encodedScene, clamp(encoded + dither(gl_FragCoord.xy) * (1.0 / 255.0), 0.0, 1.0));
    gl_FragDepth = clamp(depthDeviceDepth, 0.0, 1.0);
    fragColor = vec4(encoded, 1.0);
}
