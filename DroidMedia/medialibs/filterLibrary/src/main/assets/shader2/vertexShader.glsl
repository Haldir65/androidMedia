#version 300 es
layout (location = 0)
in vec4 aPosition;//输入的顶点坐标，会在程序指定将数据输入到该字段//如果传入的向量是不够4维的，自动将前三个分量设置为0.0，最后一个分量设置为1.0

layout (location = 1)
in vec2 aTextCoord;//输入的纹理坐标，会在程序指定将数据输入到该字段

out vec2 vTextCoord;//输出的纹理坐标;

void main() {
    //这里其实是将上下翻转过来（因为安卓图片会自动上下翻转，所以转回来）
    vTextCoord = vec2(aTextCoord.x, 1.0 - aTextCoord.y);
    //直接把传入的坐标值作为传入渲染管线。gl_Position是OpenGL内置的
    gl_Position = aPosition;
}