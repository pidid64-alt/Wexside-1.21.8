#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uViewport;
uniform vec4 uContent;
uniform float uRadius;
uniform float uEntry;
uniform float uReveal;
uniform float uRevealDir;
uniform vec4 uContact;
uniform vec4 uAmbient;
uniform vec2 uGains;
uniform float uOpacity;
uniform float uLightMode;

out vec4 FragColor;

const int CONTACT_STEPS = 8;
const int AMBIENT_STEPS = 14;
const float INV_SQRT_TAU = 0.3989423;
const float INV_SQRT_2 = 0.7071068;
const float TAIL_SIGMAS = 3.4;

const vec3 SHADOW_DARK = vec3(0.0);
const vec3 SHADOW_LIGHT = vec3(0.396, 0.384, 0.451);
const float LIGHT_SCALE = 0.46;

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float gaussian(float x, float sigma) {
    return exp(-(x * x) / (2.0 * sigma * sigma)) * (INV_SQRT_TAU / sigma);
}

vec2 erf2(vec2 x) {
    vec2 s = sign(x);
    vec2 a = abs(x);
    vec2 r = 1.0 + (0.278393 + (0.230389 + 0.078108 * (a * a)) * a) * a;
    r = r * r;
    return s - s / (r * r);
}

float rowCoverage(float x, float y, float sigma, float corner, vec2 halfSize) {
    float delta = min(halfSize.y - corner - abs(y), 0.0);
    float curved = halfSize.x - corner + sqrt(max(corner * corner - delta * delta, 0.0));
    vec2 integral = 0.5 + 0.5 * erf2((x + vec2(-curved, curved)) * (INV_SQRT_2 / sigma));
    return integral.y - integral.x;
}

float blurredBox(vec2 p, vec2 halfSize, float sigma, float corner, int steps) {
    float low = p.y - halfSize.y;
    float high = p.y + halfSize.y;
    float start = clamp(-TAIL_SIGMAS * sigma, low, high);
    float end = clamp(TAIL_SIGMAS * sigma, low, high);
    float span = end - start;
    if (span <= 0.0) {
        return 0.0;
    }
    float dy = span / float(steps);
    float y = start + dy * 0.5;
    float value = 0.0;
    for (int i = 0; i < steps; i++) {
        value += rowCoverage(p.x, p.y - y, sigma, corner, halfSize) * gaussian(y, sigma) * dy;
        y += dy;
    }
    return value;
}

float shadowLayer(vec2 p, vec2 halfSize, float radius, vec4 spec, int steps) {
    vec2 inset = max(spec.zw, vec2(0.0));
    vec2 shrunk = max(halfSize - inset, vec2(0.75));
    float corner = clamp(radius - min(inset.x, inset.y), 0.0, min(shrunk.x, shrunk.y));
    return blurredBox(vec2(p.x, p.y - spec.y), shrunk, max(spec.x, 0.75), corner, steps);
}

float sdRoundBox(vec2 p, vec2 b, float r) {
    r = min(r, min(b.x, b.y));
    vec2 q = abs(p) - b + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

void main() {
    float opacity = clamp(uOpacity, 0.0, 1.0) * clamp(uEntry, 0.0, 1.0);
    if (opacity <= 0.0015) {
        discard;
    }

    vec2 size = max(uContent.zw, vec2(1.0));
    vec2 half_ = size * 0.5;
    vec2 p = vLocal - uContent.xy - half_;
    float radius = min(uRadius, min(half_.x, half_.y));

    float top = 0.0;
    float bottom = size.y;
    float reveal = clamp(uReveal, 0.0, 1.0);
    if (uRevealDir < -0.5) {
        top = clamp(mix(size.y, 0.0, reveal), 0.0, size.y);
    } else if (uRevealDir > 0.5) {
        bottom = clamp(mix(0.0, size.y, reveal), 0.0, size.y);
    }
    float casterHeight = bottom - top;
    if (casterHeight <= 1.5) {
        discard;
    }

    vec2 casterHalf = vec2(half_.x, casterHeight * 0.5);
    float casterRadius = min(radius, min(casterHalf.x, casterHalf.y));
    vec2 q = vec2(p.x, p.y + half_.y - (top + casterHeight * 0.5));

    float ambientReach = TAIL_SIGMAS * max(uAmbient.x, 0.75);
    float ambientOut = sdRoundBox(vec2(q.x, q.y - uAmbient.y),
            max(casterHalf - max(uAmbient.zw, vec2(0.0)), vec2(0.75)), casterRadius);
    if (ambientOut > ambientReach) {
        discard;
    }

    float contactReach = TAIL_SIGMAS * max(uContact.x, 0.75);
    float contactOut = sdRoundBox(vec2(q.x, q.y - uContact.y),
            max(casterHalf - max(uContact.zw, vec2(0.0)), vec2(0.75)), casterRadius);
    float contact = contactOut > contactReach
            ? 0.0
            : shadowLayer(q, casterHalf, casterRadius, uContact, CONTACT_STEPS);
    float ambient = shadowLayer(q, casterHalf, casterRadius, uAmbient, AMBIENT_STEPS);

    float shade = 1.0 - (1.0 - clamp(contact, 0.0, 1.0) * clamp(uGains.x, 0.0, 1.0))
            * (1.0 - clamp(ambient, 0.0, 1.0) * clamp(uGains.y, 0.0, 1.0));

    float occluder = sdRoundBox(q, casterHalf, casterRadius);
    float aa = max(length(vec2(dFdx(occluder), dFdy(occluder))) * 0.70, 0.55);
    float inside = 1.0 - smoothstep(-aa, aa, occluder);

    float alpha = shade * opacity * (1.0 - inside);
    vec3 tint = SHADOW_DARK;
    if (uLightMode > 0.5) {
        tint = SHADOW_LIGHT;
        alpha *= LIGHT_SCALE;
    }
    if (alpha <= 0.0015) {
        discard;
    }

    tint = max(tint + (interleavedGradient(vScreen) - 0.5) * (1.0 / 255.0), vec3(0.0));
    FragColor = vec4(tint, clamp(alpha, 0.0, 1.0));
}
