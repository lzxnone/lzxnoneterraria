#version 150

in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform vec2 SampleOffset;

out vec4 fragColor;

void main() {
    if(SampleOffset.x == 0.0 && SampleOffset.y == 0.0) {
        fragColor = texture(Sampler0, texCoord0);
        return;
    }

    fragColor = (
        texture(Sampler0, texCoord0 + vec2(-SampleOffset.x, -SampleOffset.y))
        + texture(Sampler0, texCoord0 + vec2(SampleOffset.x, -SampleOffset.y))
        + texture(Sampler0, texCoord0 + vec2(-SampleOffset.x, SampleOffset.y))
        + texture(Sampler0, texCoord0 + vec2(SampleOffset.x, SampleOffset.y))
    ) * 0.25;
}
