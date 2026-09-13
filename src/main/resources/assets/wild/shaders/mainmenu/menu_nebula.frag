#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uResolution;
uniform vec2 uMouse;
uniform vec2 uMouseVelocity;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uActivity;
uniform float uDetail;
uniform float uLightMode;

out vec4 FragColor;

const vec3 GROUND_TOP = vec3(0.0132, 0.0126, 0.0288);
const vec3 GROUND_BOTTOM = vec3(0.0218, 0.0186, 0.0392);
const vec3 GROUND_DENSE = vec3(0.0208, 0.0176, 0.0392);
const float GROUND_ACCENT = 0.018;

const float FAR_PARALLAX = 0.0055;
const float MID_PARALLAX = 0.0185;
const float NEAR_PARALLAX = 0.0360;

const float FAR_GAIN = 0.026;
const float MID_GAIN = 0.170;

const float RIM_GAIN = 0.088;
const float FILAMENT_GAIN = 0.044;
const float DUST_DEPTH = 0.380;
const float SHAFT_GAIN = 0.026;
const float HALO_GAIN = 0.038;
const float HALO_CENTER_Y = 0.245;
const float HALO_RADIUS = 0.520;

const float VEIL_SCALE = 1.06;
const float VEIL_DRIFT = 0.0128;
const float VEIL_GAIN = 0.021;

float sq(float x) {
    return x * x;
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float v = vnoise(p) * 0.52;
    v += vnoise(p * 2.02 + vec2(2.7, 1.9)) * 0.27;
    v += vnoise(p * 4.07 + vec2(5.1, 8.3)) * 0.14;
    v += vnoise(p * 8.13 + vec2(1.3, 4.7)) * 0.07;
    return v;
}

float fbm2(vec2 p) {
    return vnoise(p) * 0.66 + vnoise(p * 2.07 + vec2(11.7, 3.3)) * 0.34;
}

float ridged(vec2 p) {
    float a = 1.0 - abs(vnoise(p) * 2.0 - 1.0);
    float b = 1.0 - abs(vnoise(p * 2.11 + vec2(7.3, 2.9)) * 2.0 - 1.0);
    return a * 0.66 + b * 0.34;
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0) + hash12(px * 0.731 + seed * 61.0) * 0.37);
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

float cloud(vec2 p, vec2 c, vec2 radii, float rot) {
    float s = sin(rot);
    float co = cos(rot);
    vec2 q = p - c;
    q = vec2(q.x * co - q.y * s, q.x * s + q.y * co) / max(radii, vec2(1.0e-4));
    return exp(-dot(q, q));
}

vec3 addLight(vec3 base, vec3 light) {
    return base + light * max(vec3(0.0), vec3(1.0) - base);
}

vec3 lift(vec3 c, float amount) {
    return mix(c, vec3(1.0), clamp(amount, 0.0, 1.0));
}

vec3 hueCompanion(vec3 accent, vec3 anchor, float amount) {
    return normalize(mix(accent, anchor, amount) + vec3(1.0e-4)) * length(accent);
}

void main() {
    vec2 uv = vLocal / max(uResolution, vec2(1.0));
    float aspect = uResolution.x / max(uResolution.y, 1.0);
    vec2 p = (uv - 0.5) * vec2(aspect, 1.0);
    float t = uTime;

    vec2 cursor = (uMouse - 0.5) * vec2(aspect, 1.0);
    vec2 toCursor = p - cursor;
    float cursorDist2 = dot(toCursor, toCursor);
    float speed = length(uMouseVelocity);
    vec2 flowDir = uMouseVelocity / max(speed, 1.0e-4);
    float wake = exp(-cursorDist2 * 3.2) * min(speed, 1.4);
    vec2 swirl = vec2(-toCursor.y, toCursor.x) * wake * 0.055;
    vec2 push = flowDir * wake * 0.06;
    vec2 breathe = vec2(0.0126 * sin(t * 0.0713), 0.0104 * cos(t * 0.0531)) * (0.4 + uActivity);

    vec2 shift = (uMouse - 0.5);
    vec2 farField = p - shift * FAR_PARALLAX + breathe * 0.4;
    vec2 midField = p - shift * MID_PARALLAX + swirl + push + breathe;
    vec2 nearField = p - shift * NEAR_PARALLAX + swirl * 1.6 + push * 1.4 + breathe * 1.5;

    vec2 q = vec2(
        fbm(midField * 0.78 + vec2(t * 0.0075, -t * 0.0052)),
        fbm(midField * 0.78 + vec2(9.2, 4.1) - vec2(t * 0.0061, t * 0.0048))
    );
    vec2 r = vec2(
        fbm(midField * 1.15 + q * 1.35 + vec2(1.7, 9.2) + vec2(t * 0.0034, t * 0.0029)),
        fbm(midField * 1.15 + q * 1.35 + vec2(8.3, 2.8) - vec2(t * 0.0041, t * 0.0026))
    );
    vec2 warped = midField + (q - 0.5) * 0.30 + (r - 0.5) * 0.22;
    float density = fbm(warped * 2.35 + vec2(t * 0.0021, -t * 0.0017));
    float shell = smoothstep(0.44, 0.58, density) - smoothstep(0.58, 0.76, density);
    float filament = smoothstep(0.40, 0.74, fbm(warped * 5.8 + r * 0.8 + vec2(-t * 0.0027, t * 0.0019)));

    vec3 aTop = uAccentTop;
    vec3 aBot = uAccentBottom;
    vec3 aMid = mix(aTop, aBot, 0.5);
    vec3 cool = hueCompanion(aTop, vec3(0.26, 0.44, 1.00), 0.42);
    vec3 warm = hueCompanion(aBot, vec3(1.00, 0.46, 0.58), 0.36);
    vec3 deep = hueCompanion(aMid, vec3(0.34, 0.22, 0.92), 0.50);

    float vertical = clamp(uv.y, 0.0, 1.0);
    vec3 color = mix(GROUND_TOP + aTop * GROUND_ACCENT * 0.5,
                     GROUND_BOTTOM + aBot * GROUND_ACCENT, vertical);
    color = mix(color, GROUND_DENSE, density * 0.50);

    float farBody = fbm2(farField * 0.72 + vec2(3.1, 8.4) + vec2(t * 0.0013, -t * 0.0011));
    float farMask = smoothstep(0.34, 0.86, farBody) * (0.55 + 0.45 * smoothstep(0.0, 0.72, 1.0 - vertical));
    color = addLight(color, mix(deep, cool, 0.35) * farMask * FAR_GAIN);

    float c1 = cloud(warped, vec2(-0.34 * aspect + 0.06 * sin(t * 0.0310), -0.22 + 0.04 * cos(t * 0.0271)), vec2(0.60, 0.40), 0.45);
    float c2 = cloud(warped, vec2(0.38 * aspect + 0.05 * cos(t * 0.0243), 0.24 + 0.04 * sin(t * 0.0293)), vec2(0.52, 0.34), -0.30);
    float c3 = cloud(warped, vec2(0.06 * aspect + 0.07 * sin(t * 0.0191), -0.47 + 0.03 * cos(t * 0.0373)), vec2(0.74, 0.26), 0.12);
    float c4 = cloud(warped, vec2(-0.50 * aspect, 0.44 + 0.03 * sin(t * 0.0173)), vec2(0.42, 0.30), -0.55);
    float c5 = cloud(warped, vec2(0.55 * aspect, -0.40), vec2(0.38, 0.32), 0.30);

    float shaped = pow(clamp(density, 0.0, 1.0), 1.45);
    float body = 0.26 + 1.05 * shaped;
    color = addLight(color, deep * c1 * 0.470 * body * MID_GAIN);
    color = addLight(color, warm * c2 * 0.330 * body * MID_GAIN);
    color = addLight(color, cool * c3 * 0.290 * body * MID_GAIN);
    color = addLight(color, mix(cool, aTop, 0.55) * c4 * 0.150 * body * MID_GAIN);
    color = addLight(color, mix(warm, aBot, 0.55) * c5 * 0.125 * body * MID_GAIN);

    float cloudMass = clamp(c1 + c2 + c3 + c4 * 0.6 + c5 * 0.6, 0.0, 1.6);
    color = addLight(color, lift(mix(aMid, cool, 0.40), 0.22) * shell * cloudMass * RIM_GAIN);
    color = addLight(color, lift(mix(deep, warm, 0.45), 0.10) * filament * FILAMENT_GAIN * (0.30 + cloudMass * 0.55));

    float dust = fbm2(nearField * 7.0 + vec2(2.3, 7.1) + vec2(-t * 0.0043, t * 0.0031));
    float dustLane = smoothstep(0.50, 0.90, dust) * clamp(cloudMass * 0.8, 0.0, 1.0);
    color *= 1.0 - DUST_DEPTH * dustLane;

    float shaftAngle = 0.66 + 0.055 * sin(t * 0.01427);
    vec2 shaftDir = vec2(cos(shaftAngle), sin(shaftAngle));
    float across = dot(p, vec2(-shaftDir.y, shaftDir.x)) - 0.06 + 0.05 * sin(t * 0.01973);
    float shaftNoise = 0.62 + 0.38 * fbm2(vec2(dot(p, shaftDir) * 1.7, t * 0.0091));
    float shaft = exp(-sq(across / 0.30)) * shaftNoise * (1.0 - smoothstep(0.25, 1.05, length(p)));
    color = addLight(color, lift(mix(aTop, cool, 0.45), 0.34) * shaft * SHAFT_GAIN * (uDetail > 0.5 ? 1.0 : 0.7));

    vec2 haloP = p + vec2(0.0, 0.5 - HALO_CENTER_Y);
    float haloR = length(haloP * vec2(0.82, 1.0)) / HALO_RADIUS;
    float halo = exp(-sq(haloR)) * (0.70 + 0.30 * density);
    color = addLight(color, lift(aMid, 0.30) * halo * HALO_GAIN);

    vec2 veilField = vec2(p.x * VEIL_SCALE + t * VEIL_DRIFT, p.y * VEIL_SCALE * 1.85 - t * VEIL_DRIFT * 0.42);
    float veilBase = ridged(veilField + (q - 0.5) * 0.30);
    float veil = veilBase;
    if (uDetail > 0.5) {
        float veilDeep = ridged(veilField * 2.35 + vec2(3.7, -1.9) - vec2(t * VEIL_DRIFT * 1.7, 0.0));
        veil = mix(veilBase, veilDeep, 0.35);
    }
    veil = pow(clamp(veil, 0.0, 1.0), 1.62);
    float veilMask = smoothstep(0.02, 0.55, vertical) * (1.0 - smoothstep(0.64, 1.0, vertical) * 0.55);
    color = addLight(color, lift(mix(cool, aMid, 0.45), 0.18) * veil * veilMask * VEIL_GAIN * (0.62 + density * 0.75));

    float torch = exp(-cursorDist2 * 4.4);
    color = addLight(color, lift(aMid, 0.16) * torch * (0.030 + 0.022 * uActivity) * (0.40 + wake * 0.9));

    float grain = fbm(p * 9.6 + vec2(0.0, -t * 0.006));
    color *= 0.968 + 0.032 * grain;

    if (uLightMode > 0.5) {
        vec3 porcelain = vec3(0.918, 0.912, 0.940);
        porcelain -= vec3(0.055, 0.052, 0.042) * smoothstep(0.30, 1.24, length(p)) * 0.6;
        porcelain = mix(porcelain, mix(porcelain, deep, 0.30), c1 * 0.5 + c3 * 0.3);
        porcelain = mix(porcelain, mix(porcelain, warm, 0.26), c2 * 0.45);
        porcelain = mix(porcelain, mix(porcelain, cool, 0.20), filament * 0.35);
        porcelain = mix(porcelain, mix(porcelain, aMid, 0.16), halo * 0.7);
        color = porcelain;
    }

    color += triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
    FragColor = vec4(max(color, vec3(0.0)), 1.0);
}
