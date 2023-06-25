#version 300 es
//输入的顶点坐标，会在程序指定将数据输入到该字段
layout (location = 0) in vec4 aPosition;
layout (location = 1) in vec2 aTexCoord;
out vec2 TexCoord;
void main() {
    //直接把传入的坐标值作为传入渲染管线。gl_Position是OpenGL内置的
    gl_Position = aPosition;
    TexCoord = vec2(aTexCoord.x, 1.0 - aTexCoord.y);
  //   TexCoord = aTexCoord; 倒过来了
}