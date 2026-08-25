#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

uniform float ShapePower;
uniform vec2 ShapeRadius;
uniform float ShapeRotation;
uniform vec3 CoreColor;
uniform vec3 MiddleColor;
uniform vec3 EdgeColor;
uniform float CoreWhiteMix;
uniform vec2 FirstGradient;
uniform vec2 SecondGradient;
uniform vec2 BrightnessGradient;
uniform vec2 Brightness;
uniform float AuraScale;
uniform float AuraBrightness;
uniform float AuraAlpha;

in float vertexDistance;
in vec2 localUv;
in vec4 vertexColor;

out vec4 fragColor;

float superellipseMetric(vec2 position) {
    float cosine = cos(ShapeRotation);
    float sine = sin(ShapeRotation);
    vec2 rotatedPosition = vec2(
        position.x * cosine + position.y * sine,
        position.y * cosine - position.x * sine
    );
    vec2 q = abs(rotatedPosition) / ShapeRadius;
    float power = max(ShapePower, 0.001);
    return pow(pow(q.x, power) + pow(q.y, power), 1.0 / power);
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
    float shapeMetric = superellipseMetric(position);
    float bodyMask = boundaryCoverage(shapeMetric, 1.0);
    float auraMask = boundaryCoverage(shapeMetric, AuraScale);
    if(auraMask < 0.001) {
        discard;
    }

    vec4 particleColor = vertexColor * ColorModulator;
    vec3 coreColor = CoreColor * particleColor.rgb;
    coreColor = mix(coreColor, vec3(1.0), CoreWhiteMix);
    vec3 middleColor = MiddleColor * particleColor.rgb;
    vec3 edgeColor = EdgeColor * particleColor.rgb;
    vec3 bodyColor = mix(
        coreColor,
        middleColor,
        smoothstep(FirstGradient.x, FirstGradient.y, shapeMetric)
    );
    bodyColor = mix(
        bodyColor,
        edgeColor,
        smoothstep(SecondGradient.x, SecondGradient.y, shapeMetric)
    );

    float brightness = mix(
        Brightness.x,
        Brightness.y,
        smoothstep(BrightnessGradient.x, BrightnessGradient.y, shapeMetric)
    );
    float outerAuraMask = max(auraMask - bodyMask, 0.0);
    vec3 weightedEmission =
        bodyColor * bodyMask * brightness
        + edgeColor * outerAuraMask * AuraBrightness;

    float shapeAlpha = max(bodyMask, outerAuraMask * AuraAlpha);
    vec3 sourceColor = weightedEmission / max(shapeAlpha, 0.0001);
    float fogFade = linear_fog_fade(vertexDistance, FogStart, FogEnd);
    float alpha = shapeAlpha * particleColor.a * fogFade;
    fragColor = vec4(sourceColor, alpha);
}
