#version 300 es

precision mediump float;
//纹理坐标
in vec2 vTextCoord;
//输入的yuv三个纹理
uniform sampler2D yTexture;//采样器
uniform sampler2D uTexture;//采样器
uniform sampler2D vTexture;//采样器
out vec4 FragColor;
const lowp int GAUSSIAN_SAMPLES = 9;
in highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];
//卷积核
mat3 kernelMatrix = mat3(
0.0947416f, 0.118318f, 0.0947416f,
0.118318f,  0.147761f, 0.118318f,
0.0947416f, 0.118318f, 0.0947416f
);
//yuv转rgb计算矩阵
mat3 colorConversionMatrix = mat3(
1.0, 1.0, 1.0,
0.0, -0.39465, 2.03211,
1.13983, -0.58060, 0.0
);

//yuv转化得到的rgb向量数据
vec3 yuv2rgb(vec2 pos)
{
    vec3 yuv;
    yuv.x = texture(yTexture, pos).r;
    yuv.y = texture(uTexture, pos).r - 0.5;
    yuv.z = texture(vTexture, pos).r - 0.5;
    return colorConversionMatrix * yuv;
}

void main() {
    //采样到的yuv向量数据
    vec3 yuv;
    //卷积处理
    lowp vec3 sum = (yuv2rgb(blurCoordinates[0]) * kernelMatrix[0][0]);
    sum += (yuv2rgb(blurCoordinates[1]) * kernelMatrix[0][1]);
    sum += (yuv2rgb(blurCoordinates[2]) * kernelMatrix[0][2]);
    sum += (yuv2rgb(blurCoordinates[3]) * kernelMatrix[1][0]);
    sum += (yuv2rgb(blurCoordinates[4]) * kernelMatrix[1][1]);
    sum += (yuv2rgb(blurCoordinates[5]) * kernelMatrix[1][2]);
    sum += (yuv2rgb(blurCoordinates[6]) * kernelMatrix[2][0]);
    sum += (yuv2rgb(blurCoordinates[7]) * kernelMatrix[2][1]);
    sum += (yuv2rgb(blurCoordinates[8]) * kernelMatrix[2][2]);
    FragColor = vec4(sum,1.0);
}