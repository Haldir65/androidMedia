#version 300 es
precision mediump float;
////图元被光栅化为多少片段，就被调用多少次

in vec2 vTextCoord;
//输入的yuv三个纹理
uniform sampler2D yTexture;//采样器
uniform sampler2D uTexture;//采样器
uniform sampler2D vTexture;//采样器
out vec4 FragColor;
void main() {
    //采样到的yuv向量数据
    vec3 yuv;
    // yuv转化得到的rgb向量数据
    vec3 rgb;

    vec2 uv = vTextCoord.xy;
    float y;
    //关键点，对渲染图元不同位置的点采样纹理的不同位置
    if (uv.y >= 0.0 && uv.y <= 0.5) {
        //当渲染图元的点位于上半部分的时候，采样比其纵坐标大于0.25部分
        uv.y = uv.y + 0.25;
    } else {
        //当渲染图元的点位于下半部分的时候，采样比其纵坐标小于0.25部分
        uv.y = uv.y - 0.25;
    }
    yuv.x = texture(yTexture, uv).r;
    yuv.y = texture(uTexture, uv).g - 0.5;
    yuv.z = texture(vTexture, uv).b - 0.5;
    rgb = mat3(
        1.0, 1.0, 1.0,
        0.0, -0.39465, 2.03211,
        1.13983, -0.5806, 0.0
    ) * yuv;
    //gl_FragColor是OpenGL内置的
    FragColor = vec4(rgb, 1.0);
}