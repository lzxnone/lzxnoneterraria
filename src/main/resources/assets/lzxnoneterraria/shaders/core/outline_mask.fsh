#version 150

in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform float AlphaCutoff;

out vec4 fragColor;

void main() {
    if(texture(Sampler0, texCoord0).a <= AlphaCutoff) discard;
    fragColor = vec4(1.0);
}
