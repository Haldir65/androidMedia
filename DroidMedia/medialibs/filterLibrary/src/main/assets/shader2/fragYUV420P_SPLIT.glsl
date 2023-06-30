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
        0.0, -0.183, 1.816,
        1.540, -0.459, 0.0
    ) * yuv;
    if (vTextCoord.x < 0.5 && vTextCoord.y < 0.5) {
        //左上角区域，反色滤镜
        FragColor = vec4(vec3(1.0 - rgb.r, 1.0 - rgb.g, 1.0 - rgb.b), 1.0);
    } else if (vTextCoord.x > 0.5 && vTextCoord.y > 0.5) {
        //右下角区域，灰度滤镜
        float gray = rgb.r * 0.2125 + rgb.g * 0.7154 + rgb.b * 0.0721;
        FragColor = vec4(gray, gray, gray, 1.0);
    } else {
        FragColor = vec4(rgb, 1.0);
    }
}