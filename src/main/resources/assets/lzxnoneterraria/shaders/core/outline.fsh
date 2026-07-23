#version 150

in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform vec2 TexelSize;
uniform vec4 OutlineColor;
uniform float Radius;

out vec4 fragColor;

void main() {
    float center = texture(Sampler0, texCoord0).a;
    float around = 0.0;
    vec2 px = TexelSize * Radius;

    around = max(around, texture(Sampler0, texCoord0 + vec2(px.x, 0.0)).a);
    around = max(around, texture(Sampler0, texCoord0 - vec2(px.x, 0.0)).a);
    around = max(around, texture(Sampler0, texCoord0 + vec2(0.0, px.y)).a);
    around = max(around, texture(Sampler0, texCoord0 - vec2(0.0, px.y)).a);
    around = max(around, texture(Sampler0, texCoord0 + vec2(px.x, px.y)).a);
    around = max(around, texture(Sampler0, texCoord0 + vec2(px.x, -px.y)).a);
    around = max(around, texture(Sampler0, texCoord0 + vec2(-px.x, px.y)).a);
    around = max(around, texture(Sampler0, texCoord0 - vec2(px.x, px.y)).a);

    float outline = max(around - center, 0.0);
    fragColor = vec4(OutlineColor.rgb, outline * OutlineColor.a);
}
