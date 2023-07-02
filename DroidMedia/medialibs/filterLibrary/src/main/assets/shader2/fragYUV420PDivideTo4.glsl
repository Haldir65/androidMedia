#version 300 es

precision mediump float;
//纹理坐标
in vec2 vTextCoord;
//输入的yuv三个纹理
uniform sampler2D yTexture;//采样器
uniform sampler2D uTexture;//采样器
uniform sampler2D vTexture;//采样器
out vec4 FragColor;
void main() {
    //采样到的yuv向量数据
    vec3 yuv;
    //yuv转化得到的rgb向量数据
    vec3 rgb;

    vec2 uv = vTextCoord.xy;
    if (uv.x <= 0.5) {
        uv.x = uv.x * 2.0;
    } else {
        uv.x = (uv.x - 0.5) * 2.0;
    }

    if (uv.y <= 0.5) {
        uv.y = uv.y * 2.0;
    } else {
        uv.y = (uv.y - 0.5) * 2.0;
    }
    //分别取yuv各个分量的采样纹理
    yuv.x = texture(yTexture, uv).r;
    yuv.y = texture(uTexture, uv).g - 0.5;
    yuv.z = texture(vTexture, uv).b - 0.5;
    rgb = mat3(
    1.0, 1.0, 1.0,
    0.0, -0.183, 1.816,
    1.540, -0.459, 0.0
    ) * yuv;
    //gl_FragColor是OpenGL内置的`
    FragColor = vec4(rgb, 1.0);
}