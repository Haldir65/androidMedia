#version 300 es
precision mediump float;

in vec2 texture_coord;
layout(location = 0) uniform sampler2D sample_y;
layout(location = 1) uniform sampler2D sample_u;
layout(location = 2) uniform sampler2D sample_v;

out vec4 out_color;


void main()
{
    float y = texture(sample_y, texture_coord).x;
    float u = texture(sample_u, texture_coord).x- 0.5;
    float v = texture(sample_v, texture_coord).x- 0.5;

    vec3 rgb;
    rgb.r = y + 1.4022 * v;
    rgb.g = y - 0.3456 * u - 0.7145 * v;
    rgb.b = y + 1.771 * u;
    if (texture_coord.x < 0.3){
        out_color = vec4(vec3(rgb.r*0.299 + rgb.g*0.587 + rgb.b*0.114), 1.0);
    }else {
        out_color = vec4(rgb, 1);
    }

}