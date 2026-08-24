#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec2 localUv;
in vec4 vertexColor;

out vec4 fragColor;

const float CORE_BRIGHTNESS = 3.0;
const float SHELL_BRIGHTNESS = 1.35;
const float AURA_BRIGHTNESS = 0.45;

float superellipseField(vec2 p, float halfLength, float halfWidth) {
    vec2 q = abs(p) / vec2(halfLength, halfWidth);
    return pow(q.x, 8.0) + q.y * q.y;
}

float fieldCoverage(float field) {
    float antialiasWidth = max(fwidth(field) * 0.75, 0.001);
    return 1.0 - smoothstep(
        1.0 - antialiasWidth,
        1.0 + antialiasWidth,
        field
    );
}

void main() {
    vec2 localPosition = localUv * 2.0 - 1.0;
    float auraMask = fieldCoverage(
        superellipseField(localPosition, 0.99, 0.98)
    );
    float bodyMask = fieldCoverage(
        superellipseField(localPosition, 0.94, 0.68)
    );
    float coreMask = fieldCoverage(
        superellipseField(localPosition, 0.86, 0.24)
    );
    if (auraMask < 0.001) {
        discard;
    }

    float shellMask = max(bodyMask - coreMask, 0.0);
    float outerAuraMask = max(auraMask - bodyMask, 0.0);
    vec4 particleColor = vertexColor * ColorModulator;
    vec3 weightedEmission =
        particleColor.rgb * shellMask * SHELL_BRIGHTNESS
        + particleColor.rgb * outerAuraMask * AURA_BRIGHTNESS
        + vec3(CORE_BRIGHTNESS) * coreMask;

    float shapeAlpha = max(
        coreMask,
        shellMask * 0.90 + outerAuraMask * 0.40
    );
    // 固定管线还会执行 source.rgb * source.a，因此先去掉颜色中已有的
    // shapeAlpha，确保覆盖率、生命周期和雾都只衰减一次。
    vec3 sourceColor = weightedEmission / max(shapeAlpha, 0.0001);
    float fogFade = linear_fog_fade(vertexDistance, FogStart, FogEnd);
    float alpha = shapeAlpha * particleColor.a * fogFade;
    fragColor = vec4(sourceColor, alpha);
}
