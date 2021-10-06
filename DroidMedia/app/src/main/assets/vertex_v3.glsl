#version 300 es

layout(location = 0) in vec4 a_position;
layout(location = 1) in vec2 a_texCoord;

out vec2 texture_coord;

void main() {
    gl_Position = a_position;
    texture_coord = a_texCoord;
}
