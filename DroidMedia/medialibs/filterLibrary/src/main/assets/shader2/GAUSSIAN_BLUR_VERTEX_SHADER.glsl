#version 300 es
layout (location = 0)
in vec4 aPosition;//输入的顶点坐标，会在程序指定将数据输入到该字段
//如果传入的向量是不够4维的，自动将前三个分量设置为0.0，最后一个分量设置为1.0

layout (location = 1)
in vec2 aTextCoord;//输入的纹理坐标，会在程序指定将数据输入到该字段

out vec2 vTextCoord;//输出的纹理坐标;
uniform mat4 uMatrix;//变换矩阵
const int GAUSSIAN_SAMPLES = 9;
out vec2 blurCoordinates[GAUSSIAN_SAMPLES];

void main() {
    //这里其实是将上下翻转过来（因为安卓图片会自动上下翻转，所以转回来）
    vTextCoord = vec2(aTextCoord.x, 1.0 - aTextCoord.y);
    //直接把传入的坐标值作为传入渲染管线。gl_Position是OpenGL内置的
    gl_Position = uMatrix * aPosition;
    //横向和纵向的步长
    vec2 widthStep = vec2(10.0/1080.0, 0.0);
    vec2 heightStep = vec2(0.0, 10.0/1920.0);
    //计算出当前片段相邻像素的纹理坐标
    blurCoordinates[0] = vTextCoord.xy - heightStep - widthStep; // 左上
    blurCoordinates[1] = vTextCoord.xy - heightStep; // 上
    blurCoordinates[2] = vTextCoord.xy - heightStep + widthStep; // 右上
    blurCoordinates[3] = vTextCoord.xy - widthStep; // 左中
    blurCoordinates[4] = vTextCoord.xy; // 中
    blurCoordinates[5] = vTextCoord.xy + widthStep; // 右中
    blurCoordinates[6] = vTextCoord.xy + heightStep - widthStep; // 左下
    blurCoordinates[7] = vTextCoord.xy + heightStep; // 下
    blurCoordinates[8] = vTextCoord.xy + heightStep + widthStep; // 右下
}
