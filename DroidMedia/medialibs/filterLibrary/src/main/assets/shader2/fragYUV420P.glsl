#version 300 es

precision mediump float;

in vec2 vTextCoord;
//输入的yuv三个纹理
uniform sampler2D yTexture;//采样器
uniform sampler2D uTexture;//采样器
uniform sampler2D vTexture;//采样器
out vec4 FragColor;
void main() {
    vec3 yuv;
    vec3 rgb;
    //分别取yuv各个分量的采样纹理（r表示？）
    yuv.x = texture(yTexture, vTextCoord).r;
    yuv.y = texture(uTexture, vTextCoord).g - 0.5;
    yuv.z = texture(vTexture, vTextCoord).b - 0.5;
    rgb = mat3(
        1.0, 1.0, 1.0,
        0.0, -0.39465, 2.03211,
        1.13983, -0.5806, 0.0
    ) * yuv;
    //gl_FragColor是OpenGL内置的
    FragColor = vec4(rgb, 1.0);
}