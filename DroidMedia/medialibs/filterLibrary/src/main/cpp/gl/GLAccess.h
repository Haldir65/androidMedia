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
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <unistd.h>
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





static const char *vertexShader =
        "        #version 300 es\n"
        "        layout (location = 0) \n"
        "        in vec4 aPosition;//输入的顶点坐标，会在程序指定将数据输入到该字段\n"//如果传入的向量是不够4维的，自动将前三个分量设置为0.0，最后一个分量设置为1.0

        "        layout (location = 1) \n"
        "        in vec2 aTextCoord;//输入的纹理坐标，会在程序指定将数据输入到该字段\n"
        "\n"
        "        out\n"
        "        vec2 vTextCoord;//输出的纹理坐标;\n"
        "\n"
        "        void main() {\n"
        "            //这里其实是将上下翻转过来（因为安卓图片会自动上下翻转，所以转回来）\n"
        "             vTextCoord = vec2(aTextCoord.x, 1.0 - aTextCoord.y);\n"
        "            //直接把传入的坐标值作为传入渲染管线。gl_Position是OpenGL内置的\n"
        "            gl_Position = aPosition;\n"
        "        }";


//图元被光栅化为多少片段，就被调用多少次
static const char *fragYUV420P =
        "#version 300 es\n"

        "precision mediump float;\n"

        "in vec2 vTextCoord;\n"
        //输入的yuv三个纹理
        "uniform sampler2D yTexture;//采样器\n"
        "uniform sampler2D uTexture;//采样器\n"
        "uniform sampler2D vTexture;//采样器\n"
        "out vec4 FragColor;\n"
        "void main() {\n"
        "   vec3 yuv;\n"
        "    vec3 rgb;\n"
        "    //分别取yuv各个分量的采样纹理（r表示？）\n"
        "    yuv.x = texture(yTexture, vTextCoord).r;\n"
        "   yuv.y = texture(uTexture, vTextCoord).g - 0.5;\n"
        "    yuv.z = texture(vTexture, vTextCoord).b - 0.5;\n"
        "   rgb = mat3(\n"
        "            1.0, 1.0, 1.0,\n"
        "            0.0, -0.39465, 2.03211,\n"
        "            1.13983, -0.5806, 0.0\n"
        "    ) * yuv;\n"
        "    //gl_FragColor是OpenGL内置的\n"
        "    FragColor = vec4(rgb, 1.0);\n"
        " }";

// https://juejin.cn/post/7168042219163779108
// 关键点就是对于纹理坐标所在区域的判断，如果处于左上角，即x<0.5,y<0.5,则使用反色效果。如果处于右下角，即x>0.5,y>0.5,则使用灰度效果。其余区域不做额外处理。
static const char *fragYUV420P_SPLIT =
        "#version 300 es\n"

        "precision mediump float;\n"

        "in vec2 vTextCoord;\n"
        //输入的yuv三个纹理
        "uniform sampler2D yTexture;//采样器\n"
        "uniform sampler2D uTexture;//采样器\n"
        "uniform sampler2D vTexture;//采样器\n"
        "out vec4 FragColor;\n"
        "void main() {\n"
        "   vec3 yuv;\n"
        "    vec3 rgb;\n"
        "    //分别取yuv各个分量的采样纹理（r表示？）\n"
        "    yuv.x = texture(yTexture, vTextCoord).r;\n"
        "   yuv.y = texture(uTexture, vTextCoord).g - 0.5;\n"
        "    yuv.z = texture(vTexture, vTextCoord).b - 0.5;\n"
        "   rgb = mat3(\n"
        "            1.0, 1.0, 1.0,\n"
        "            0.0, -0.183, 1.816,\n"
        "            1.540, -0.459, 0.0\n"
        "    ) * yuv;\n"
        "if (vTextCoord.x < 0.5 && vTextCoord.y < 0.5) {\n"
        "      //左上角区域，反色滤镜\n"
        "   FragColor = vec4(vec3(1.0 - rgb.r, 1.0 - rgb.g, 1.0 - rgb.b), 1.0);\n"
        "} else if (vTextCoord.x > 0.5 && vTextCoord.y > 0.5){\n"
        "       //右下角区域，灰度滤镜\n"
        "   float gray = rgb.r * 0.2125 + rgb.g * 0.7154 + rgb.b * 0.0721;\n"
        "   FragColor = vec4(gray, gray, gray, 1.0);\n"
        "} else {\n"
        "   FragColor = vec4(rgb, 1.0);\n"
        "   }\n"
        " }";


//图元被光栅化为多少片段，就被调用多少次
static const char *fragYUV420P_split_vertical_two =
        "#version 300 es\n"

        "precision mediump float;\n"

        "in vec2 vTextCoord;\n"
        //输入的yuv三个纹理
        "uniform sampler2D yTexture;//采样器\n"
        "uniform sampler2D uTexture;//采样器\n"
        "uniform sampler2D vTexture;//采样器\n"
        "out vec4 FragColor;\n"
        "void main() {\n"
        "//采样到的yuv向量数据\n"
        "   vec3 yuv;\n"
        "// yuv转化得到的rgb向量数据\n"
        "    vec3 rgb;\n"
        "       \n"
        "    vec2 uv = vTextCoord.xy;\n"
        "    float y;\n"
        "   //关键点，对渲染图元不同位置的点采样纹理的不同位置\n"
        "    if (uv.y >= 0.0 && uv.y <= 0.5) {\n"
        "    //当渲染图元的点位于上半部分的时候，采样比其纵坐标大于0.25部分\n"
        "        uv.y = uv.y + 0.25; \n"
        "     } else { \n"
        "    //当渲染图元的点位于下半部分的时候，采样比其纵坐标小于0.25部分\n"
        "       uv.y = uv.y - 0.25;\n"
        "    }\n"
        "   yuv.x = texture(yTexture, uv).r;\n"
        "   yuv.y = texture(uTexture, uv).g - 0.5;\n"
        "   yuv.z = texture(vTexture, uv).b - 0.5;\n"
        "   rgb = mat3(\n"
        "            1.0, 1.0, 1.0,\n"
        "            0.0, -0.39465, 2.03211,\n"
        "            1.13983, -0.5806, 0.0\n"
        "    ) * yuv;\n"
        "    //gl_FragColor是OpenGL内置的\n"
        "    FragColor = vec4(rgb, 1.0);\n"
        " }";

#endif //DROIDMEDIA_GLACCESS_H
