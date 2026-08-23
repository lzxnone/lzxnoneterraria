#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelMat;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec3 vLocalPos;
out vec4 vColor;

void main() {
    vec4 viewPosition = ModelViewMat * ModelMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewPosition;

    vLocalPos = Position;
    vColor = Color;
}
