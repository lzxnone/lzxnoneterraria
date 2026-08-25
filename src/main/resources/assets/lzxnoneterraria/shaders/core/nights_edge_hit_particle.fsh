#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec2 localUv;
in vec4 vertexColor;

out vec4 fragColor;

const vec3 CORE_PURPLE = vec3(0.82, 0.48, 1.00);
const vec3 VIVID_PURPLE = vec3(0.48, 0.10, 0.88);
const vec3 DEEP_PURPLE = vec3(0.16, 0.015, 0.34);

float superellipseMetric(vec2 position, vec2 radius) {
    const float POWER = 4.0;
    vec2 q = abs(position) / radius;
    return pow(pow(q.x, POWER) + pow(q.y, POWER), 1.0 / POWER);
}

float boundaryCoverage(float metric, float boundary) {
    float antialiasWidth = max(fwidth(metric) * 0.90, 0.0015);
    return 1.0 - smoothstep(
        boundary - antialiasWidth,
        boundary + antialiasWidth,
        metric
    );
}

void main() {
    vec2 position = localUv * 2.0 - 1.0;
    const float DIAGONAL = 0.70710678;

    vec2 diagonalA = vec2(
        (position.x + position.y) * DIAGONAL,
        (position.y - position.x) * DIAGONAL
    );
    vec2 diagonalB = vec2(
        (position.x - position.y) * DIAGONAL,
        (position.x + position.y) * DIAGONAL
    );
    vec2 vertical = vec2(position.y, position.x);

    // 两条对角线加一条竖线，形成六个放射端点。
    float metricA = superellipseMetric(diagonalA, vec2(0.83, 0.070));
    float metricB = superellipseMetric(diagonalB, vec2(0.83, 0.070));
    float metricVertical = superellipseMetric(vertical, vec2(0.82, 0.065));
    float shapeMetric = min(metricVertical, min(metricA, metricB));
    float bodyMask = boundaryCoverage(shapeMetric, 1.0);

    float auraA = boundaryCoverage(superellipseMetric(diagonalA, vec2(0.86, 0.105)), 1.0);
    float auraB = boundaryCoverage(superellipseMetric(diagonalB, vec2(0.86, 0.105)), 1.0);
    float auraVertical = boundaryCoverage(superellipseMetric(vertical, vec2(0.85, 0.098)), 1.0);
    float auraMask = max(auraVertical, max(auraA, auraB));
    if(auraMask < 0.001) {
        discard;
    }

    vec3 bodyColor = mix(
        CORE_PURPLE,
        VIVID_PURPLE,
        smoothstep(0.14, 0.66, shapeMetric)
    );
    bodyColor = mix(
        bodyColor,
        DEEP_PURPLE,
        smoothstep(0.64, 1.0, shapeMetric)
    );
    float brightness = mix(1.65, 1.15, smoothstep(0.16, 1.0, shapeMetric));
    float outerAuraMask = max(auraMask - bodyMask, 0.0);
    vec4 particleColor = vertexColor * ColorModulator;
    vec3 weightedEmission =
        bodyColor * bodyMask * brightness
        + DEEP_PURPLE * outerAuraMask * 0.52;

    float shapeAlpha = max(bodyMask, outerAuraMask * 0.36);
    vec3 sourceColor = weightedEmission * particleColor.rgb
        / max(shapeAlpha, 0.0001);
    float fogFade = linear_fog_fade(vertexDistance, FogStart, FogEnd);
    float alpha = shapeAlpha * particleColor.a * fogFade;
    fragColor = vec4(sourceColor, alpha);
}
