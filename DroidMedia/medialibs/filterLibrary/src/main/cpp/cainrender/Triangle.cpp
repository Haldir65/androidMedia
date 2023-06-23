//
// Created by cain on 2018/4/7.
//

#include "Triangle.h"

const GLint COORDS_PER_VERTEX = 3;
const GLint vertexStride = COORDS_PER_VERTEX * 4;

Triangle::Triangle() {}

Triangle::~Triangle() {

}

int Triangle::init() {
    char vertexShader[] =
            "#version 300 es\n"
                    "layout(location = 0) in vec4 a_position;\n"
                    "layout(location = 1) in vec4 a_color;\n"
                    "out vec4 v_color;"
                    "void main()\n"
                    "{\n"
                    "   gl_Position = a_position;\n"
                    "   gl_PointSize = 100.0;\n" // 必须是float，之前写错成100，死活不显示
                    "   v_color = a_color;\n"
                    "}\n";

    char fragmentShader[] =
            "#version 300 es\n"
                    "precision mediump float;\n"
                    "in vec4 v_color;\n"
                    "uniform vec4 uTextColor;\n" //输出的颜色
                    "out vec4 fragColor;\n"
                    "void main()\n"
                    "{\n"
//                    "   fragColor = vec4 ( 1.0, 0.5, 0.5, 1.0 );\n"
                    "   fragColor = v_color;\n"
                    "}\n";
    programHandle = createProgram(vertexShader, fragmentShader);
    if (programHandle <= 0) {
        return -1;
    }
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    return 0;
}

// https://juejin.cn/post/7145094035521470500
static float triangleVerWithColor[] = {
        0.0f, 0.8f, 0.0f,//顶点
        1.0, 0.0, 0.0,//颜色

        -0.8f, -0.8f, 0.0f,//顶点
        0.0, 1.0, 0.0,//颜色

        0.8f, -0.8f, 0.0f,//顶点
        0.0, 0.0, 1.0,//颜色
};




void Triangle::onDraw(int width, int height) {
    ALOGD("hahahah");
    GLfloat vertices[] = {
            0.8f, 0.8f, 0.0f,
            0.8f, -0.8f, 0.0f,
            -0.8f, 0.8f, 0.0f,
            -0.8f, -0.8f, 0.0f,
    };

    GLfloat color[] = {
            0.0f, 1.0f, 0.0f, 1.0f
    };

    GLint vertexCount = sizeof(vertices) / (sizeof(vertices[0]) * COORDS_PER_VERTEX);

    glViewport(0, 0, width, height);

    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(programHandle);



    //旧的传输数据方式，已经out了
//    GLuint apos = static_cast<GLuint>(glGetAttribLocation(program, "aPosition"));
    //通过layout传输数据，传给了着色器中layout为0的变量
//    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vertices);
    //打开layout为0的变量传输开关
//    glEnableVertexAttribArray(0);
// 对应的着色器也要改成layout方式去写 https://juejin.cn/post/7144335420644392991

//    GLint positionHandle = glGetAttribLocation(programHandle, "a_position");
//    glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GL_FLOAT, GL_FALSE, vertexStride, vertices);


//    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vertices);
//    glEnableVertexAttribArray(0);

    //解析顶点坐标数据
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 24, triangleVerWithColor);
    // 解析颜色数据
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 24, triangleVerWithColor+3);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

//    glVertexAttrib4fv(1, color); // index = 1因为前面的glsl里面layout(position = 1)的是color
    int colorLocation = glGetUniformLocation(programHandle, "uTextColor");
    //通过location去传入一个color向量
//    glUniform4fv(colorLocation,1, color);
    // glUniform + n维向量 + 向量元素数据类型 + v

//    glLineWidth(20);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 3); // 正方形

//    glDisableVertexAttribArray(0);
}

void Triangle::destroy() {
    if (programHandle > 0) {
        glDeleteProgram(programHandle);
    }
    programHandle = -1;
}