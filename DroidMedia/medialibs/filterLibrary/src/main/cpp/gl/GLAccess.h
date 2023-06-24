//
// Created by harris on 2023/6/24.
//

#ifndef DROIDMEDIA_GLACCESS_H
#define DROIDMEDIA_GLACCESS_H
#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include <string.h>
#include <android/bitmap.h>
#include "GDog.h"

static const char *vertexSimpleTexture =
        "        #version 300 es\n"
        "        //输入的顶点坐标，会在程序指定将数据输入到该字段\n"
        "        layout (location = 0) in vec4 aPosition;\n"
        "        layout (location = 1) in vec2 aTexCoord;\n"

        "        out vec2 TexCoord;"
        "\n"
        "        void main() {\n"
        "            //直接把传入的坐标值作为传入渲染管线。gl_Position是OpenGL内置的\n"
        "            gl_Position = aPosition;\n"
        "            TexCoord = vec2(aTexCoord.x, 1.0 - aTexCoord.y);\n"
        //                "            TexCoord = aTexCoord;\n"
        "        }";


//图元被光栅化为多少片段，就被调用多少次
static const char *fragSimpleTexture =
        "        #version 300 es\n"
        "        precision mediump float;\n"
        "        in vec2 TexCoord;\n"
        "        out vec4 FragColor;\n"
        "        //传入的纹理\n"
        "        uniform sampler2D ourTexture;\n"
        "        uniform sampler2D ourTexture1;\n"

        "        void main() {\n"
        "            //gl_FragColor是OpenGL内置的\n"
        //        "            FragColor = texture(ourTexture, TexCoord);\n"
        "            FragColor = mix(texture(ourTexture, TexCoord), texture(ourTexture1, TexCoord), 0.5);\n"
        "        }";

GLint initShader(const char *source, int type);



#endif //DROIDMEDIA_GLACCESS_H
