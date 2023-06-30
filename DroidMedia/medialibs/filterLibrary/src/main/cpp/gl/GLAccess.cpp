#include <jni.h>
#include "GLAccess.h"
#include "EGLRoutine.h"
#include "AssetReader.h"
#include "../glm/glm/gtc/matrix_transform.hpp"
#include "../glm/glm/ext.hpp"
#include "../glm/glm/detail/_noise.hpp"

//
// Created by me on 2023/6/24.
//







extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_filterlibrary_opengl_GLAccess_drawTexture(JNIEnv *env, jobject thiz,
                                                             jobject bitmap, jobject bitmap1,
                                                             jobject surface, jobject assetmanager) {

    auto *routine = new EGLRoutine();
    routine->eglSetup(env, surface);
    auto assetReader = new AssetReader();
    const char* vertexSimpleTexture = assetReader->readAssets(env,"shader2/vertexSimpleTexture.glsl",assetmanager);
    const char* fragSimpleTexture = assetReader->readAssets(env,"shader2/fragSimpleTexture.glsl",assetmanager);
    delete assetReader;
    GLint vsh = routine->initShader(vertexSimpleTexture, GL_VERTEX_SHADER);
    GLint fsh = routine->initShader(fragSimpleTexture, GL_FRAGMENT_SHADER);

    //创建渲染程序
    GLint program = glCreateProgram();
    if (program == 0) {
        LOGD("glCreateProgram failed");
        return;
    }

    //向渲染程序中加入着色器
    glAttachShader(program, vsh);
    glAttachShader(program, fsh);

    //链接程序
    glLinkProgram(program);
    GLint status = 0;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == 0) {
        LOGD("glLinkProgram failed");
        return;
    }
    LOGD("glLinkProgram success");
    //激活渲染程序
    glUseProgram(program);


    float vertices[] = {
            // positions         // texture coords
            0.8f, 0.4f, 0.0f, 1.0f, 1.0f, // top right
            0.8f, -0.4f, 0.0f, 1.0f, 0.0f, // bottom right
            -0.8f, -0.4f, 0.0f, 0.0f, 0.0f, // bottom left
            -0.8f, 0.4f, 0.0f, 0.0f, 1.0f  // top left
    };
    unsigned int indices[] = {
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };
    unsigned int VBO, VAO, EBO;
    glGenVertexArrays(1, &VAO);
    glGenBuffers(1, &VBO);
    glGenBuffers(1, &EBO);

    glBindVertexArray(VAO);

    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    LOGD("glBufferData GL_ELEMENT_ARRAY_BUFFER");

    // position attribute
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) 0);
    glEnableVertexAttribArray(0);
    // texture coord attribute
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float),
                          (void *) (3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    LOGD("glEnableVertexAttribArray(1)");

    AndroidBitmapInfo bmpInfo;
    void *bmpPixels;

    if (AndroidBitmap_getInfo(env, bitmap, &bmpInfo) < 0) {
        LOGD("AndroidBitmap_getInfo() failed ! ");
        return;
    }

    AndroidBitmap_lockPixels(env, bitmap, &bmpPixels);

    LOGD("bitmap width:%d,height:%d", bmpInfo.width, bmpInfo.height);

    AndroidBitmapInfo bmpInfo1;
    void *bmpPixels1;

    if (AndroidBitmap_getInfo(env, bitmap1, &bmpInfo1) < 0) {
        LOGD("AndroidBitmap_getInfo() failed ! ");
        return;
    }

    AndroidBitmap_lockPixels(env, bitmap1, &bmpPixels1);

    LOGD("bitmap width:%d,height:%d", bmpInfo1.width, bmpInfo1.height);

    if (bmpPixels == nullptr || bmpPixels1 == nullptr) {
        return;
    }


    // load and create a texture
    // -------------------------
    unsigned int texture1, texture2;
    //-------------------- texture1的配置start ------------------------------
    glGenTextures(1, &texture1);
    glBindTexture(GL_TEXTURE_2D, texture1);
    // set the texture wrapping parameters（配置纹理环绕）
    //横坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                    GL_REPEAT);    // set texture wrapping to GL_REPEAT (default wrapping method)
    //纵坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // set texture filtering parameters（配置纹理过滤）
    //纹理分辨率大于图元分辨率，即纹理需要被缩小的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    //纹理分辨率小于图元分辨率，即纹理需要被放大的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // load image, create texture and generate mipmaps

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo.width, bmpInfo.height, 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, bmpPixels);
    AndroidBitmap_unlockPixels(env, bitmap);
    //-------------------- texture1的配置end ------------------------------


    //-------------------- texture2的配置start ------------------------------
    glGenTextures(1, &texture2);
    glBindTexture(GL_TEXTURE_2D, texture2);
    // set the texture wrapping parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                    GL_REPEAT);    // set texture wrapping to GL_REPEAT (default wrapping method)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // set texture filtering parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo1.width, bmpInfo1.height, 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, bmpPixels1);

    LOGD("glTexImage2D bmpPixels1 called");

    AndroidBitmap_unlockPixels(env, bitmap1);

    //-------------------- texture2的配置end ------------------------------

    //对着色器中的纹理单元变量进行赋值
    glUniform1i(glGetUniformLocation(program, "ourTexture"), 0);
    glUniform1i(glGetUniformLocation(program, "ourTexture1"), 1);

    //将纹理单元和纹理对象进行绑定
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture1);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texture2);

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
//    glBindVertexArray(VAO);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

    //    glDrawArrays(GL_TRIANGLES, 0, 3);


    //窗口显示，交换双缓冲区

    routine->eglSwapBuffer();
    delete routine;
    LOGD("delete routine; called");


    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);

    glDeleteVertexArrays(1, &VAO);
    glDeleteBuffers(1, &VBO);
    glDeleteBuffers(1, &EBO);
    //释放着色器程序对象
    glDeleteProgram(program);
    LOGD("glDeleteProgram called");


}
extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_filterlibrary_opengl_GLAccess_loadYuv(JNIEnv *env, jobject thiz, jobject surface,
                                                         jobject assetmanager) {
    LOGD("load yuv start");
    auto *routine = new EGLRoutine();
    routine->eglSetup(env, surface);
    auto assetReader = new AssetReader();

    const char* vertexShader = assetReader->readAssets(env,"shader2/vertexShader.glsl",assetmanager);
    GLint vsh = routine->initShader(vertexShader, GL_VERTEX_SHADER);
//    GLint fsh = routine->initShader(fragYUV420P, GL_FRAGMENT_SHADER);

//    GLint fsh = routine->initShader(fragYUV420P_SPLIT, GL_FRAGMENT_SHADER);
    const char* fragYUV420P_split_vertical_two = assetReader->readAssets(env,"shader2/fragYUV420P_SPLIT.glsl",assetmanager);
    GLint fsh = routine->initShader(fragYUV420P_split_vertical_two, GL_FRAGMENT_SHADER);
    delete assetReader;


    //创建渲染程序
    GLint program = glCreateProgram();
    if (program == 0) {
        LOGD("glCreateProgram failed");
        return;
    }

    //向渲染程序中加入着色器
    glAttachShader(program, vsh);
    glAttachShader(program, fsh);

    //链接程序
    glLinkProgram(program);
    GLint status = 0;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == 0) {
        LOGD("glLinkProgram failed");
        return;
    }
    LOGD("glLinkProgram success");
    //激活渲染程序
    glUseProgram(program);

    //加入三维顶点数据
    static float ver[] = {
            1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f
    };

    GLuint apos = static_cast<GLuint>(glGetAttribLocation(program, "aPosition"));
    glEnableVertexAttribArray(apos);
    glVertexAttribPointer(apos, 3, GL_FLOAT, GL_FALSE, 0, ver);

    //加入纹理坐标数据
    static float fragment[] = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };
    GLuint aTex = static_cast<GLuint>(glGetAttribLocation(program, "aTextCoord"));
    glEnableVertexAttribArray(aTex);
    glVertexAttribPointer(aTex, 2, GL_FLOAT, GL_FALSE, 0, fragment);

    int width = 640;
    int height = 272;

    //纹理初始化
    //设置纹理层对应的对应采样器？

    /**
     *  //获取一致变量的存储位置
    GLint textureUniformY = glGetUniformLocation(program, "SamplerY");
    GLint textureUniformU = glGetUniformLocation(program, "SamplerU");
    GLint textureUniformV = glGetUniformLocation(program, "SamplerV");
    //对几个纹理采样器变量进行设置
    glUniform1i(textureUniformY, 0);
    glUniform1i(textureUniformU, 1);
    glUniform1i(textureUniformV, 2);
     */
    //对sampler变量，使用函数glUniform1i和glUniform1iv进行设置
    glUniform1i(glGetUniformLocation(program, "yTexture"), 0);
    glUniform1i(glGetUniformLocation(program, "uTexture"), 1);
    glUniform1i(glGetUniformLocation(program, "vTexture"), 2);
    //纹理ID
    GLuint textures[3] = {0};
    //创建若干个纹理对象，并且得到纹理ID
    glGenTextures(3, textures);

    //绑定纹理。后面的的设置和加载全部作用于当前绑定的纹理对象
    //GL_TEXTURE0、GL_TEXTURE1、GL_TEXTURE2 的就是纹理单元，GL_TEXTURE_1D、GL_TEXTURE_2D、CUBE_MAP为纹理目标
    //通过 glBindTexture 函数将纹理目标和纹理绑定后，对纹理目标所进行的操作都反映到对纹理上
    glBindTexture(GL_TEXTURE_2D, textures[0]);
    //缩小的过滤器
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //放大的过滤器
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    //设置纹理的格式和大小
    // 加载纹理到 OpenGL，读入 buffer 定义的位图数据，并把它复制到当前绑定的纹理对象
    // 当前绑定的纹理对象就会被附加上纹理图像。
    //width,height表示每几个像素公用一个yuv元素？比如width / 2表示横向每两个像素使用一个元素？
    glTexImage2D(GL_TEXTURE_2D,
                 0,//细节基本 默认0
                 GL_LUMINANCE,//gpu内部格式 亮度，灰度图（这里就是只取一个亮度的颜色通道的意思）
                 width,//加载的纹理宽度。最好为2的次幂(这里对y分量数据当做指定尺寸算，但显示尺寸会拉伸到全屏？)
                 height,//加载的纹理高度。最好为2的次幂
                 0,//纹理边框
                 GL_LUMINANCE,//数据的像素格式 亮度，灰度图
                 GL_UNSIGNED_BYTE,//像素点存储的数据类型
                 NULL //纹理的数据（先不传）
    );

    //绑定纹理
    glBindTexture(GL_TEXTURE_2D, textures[1]);
    //缩小的过滤器
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    //设置纹理的格式和大小
    glTexImage2D(GL_TEXTURE_2D,
                 0,//细节基本 默认0
                 GL_LUMINANCE,//gpu内部格式 亮度，灰度图（这里就是只取一个颜色通道的意思）
                 width / 2,//u数据数量为屏幕的4分之1
                 height / 2,
                 0,//边框
                 GL_LUMINANCE,//数据的像素格式 亮度，灰度图
                 GL_UNSIGNED_BYTE,//像素点存储的数据类型
                 NULL //纹理的数据（先不传）
    );

    //绑定纹理
    glBindTexture(GL_TEXTURE_2D, textures[2]);
    //缩小的过滤器
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    //设置纹理的格式和大小
    glTexImage2D(GL_TEXTURE_2D,
                 0,//细节基本 默认0
                 GL_LUMINANCE,//gpu内部格式 亮度，灰度图（这里就是只取一个颜色通道的意思）
                 width / 2,
                 height / 2,//v数据数量为屏幕的4分之1
                 0,//边框
                 GL_LUMINANCE,//数据的像素格式 亮度，灰度图
                 GL_UNSIGNED_BYTE,//像素点存储的数据类型
                 NULL //纹理的数据（先不传）
    );

    //创建3个buffer数组分别用于存放YUV三个分量
    unsigned char *buf[3] = {0};
    buf[0] = new unsigned char[width * height];//y
    buf[1] = new unsigned char[width * height / 4];//u
    buf[2] = new unsigned char[width * height / 4];//v

    //得到AAssetManager对象指针
    AAssetManager *mManeger = AAssetManager_fromJava(env, assetmanager);
    //得到AAsset对象
    AAsset *dataAsset = AAssetManager_open(mManeger, "video1_640_272.yuv",
                                           AASSET_MODE_STREAMING);//get file read AAsset
    //文件总长度
    off_t dataBufferSize = AAsset_getLength(dataAsset);
    //纵帧数
    long frameCount = dataBufferSize / (width * height * 3 / 2);

    LOGD("frameCount:%d", frameCount);


    for (int i = 0; i < frameCount; ++i) {
        //读取y分量
        int bufYRead = AAsset_read(dataAsset, buf[0],
                                   width * height);  //begin to read data once time
        //读取u分量
        int bufURead = AAsset_read(dataAsset, buf[1],
                                   width * height / 4);  //begin to read data once time
        //读取v分量
        int bufVRead = AAsset_read(dataAsset, buf[2],
                                   width * height / 4);  //begin to read data once time
        LOGD("bufYRead:%d,bufURead:%d,bufVRead:%d", bufYRead, bufURead, bufVRead);

        //读到文件末尾了
        if (bufYRead <= 0 || bufURead <= 0 || bufVRead <= 0) {
            AAsset_close(dataAsset);
            return;
        }

        //  int c = dataRead(mManeger, "video1_640_272.yuv");

        //激活第一层纹理，绑定到创建的纹理
        //下面的width,height主要是显示尺寸？
        glActiveTexture(GL_TEXTURE0);
        //绑定y对应的纹理
        glBindTexture(GL_TEXTURE_2D, textures[0]);
        //替换纹理，比重新使用glTexImage2D性能高多
        glTexSubImage2D(GL_TEXTURE_2D, 0,
                        0, 0,//相对原来的纹理的offset
                        width, height,//加载的纹理宽度、高度。最好为2的次幂
                        GL_LUMINANCE, GL_UNSIGNED_BYTE,
                        buf[0]);

        //激活第二层纹理，绑定到创建的纹理
        glActiveTexture(GL_TEXTURE1);
        //绑定u对应的纹理
        glBindTexture(GL_TEXTURE_2D, textures[1]);
        //替换纹理，比重新使用glTexImage2D性能高
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE,
                        buf[1]);

        //激活第三层纹理，绑定到创建的纹理
        glActiveTexture(GL_TEXTURE2);
        //绑定v对应的纹理
        glBindTexture(GL_TEXTURE_2D, textures[2]);
        //替换纹理，比重新使用glTexImage2D性能高
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE,
                        buf[2]);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        //窗口显示，交换双缓冲区
        routine->eglSwapBuffer();


        usleep(4000);
    }

    delete routine;

}
extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_filterlibrary_opengl_GLAccess_readAssests(JNIEnv *env, jobject thiz,
                                                             jstring name, jobject assetmanager) {
    auto reader = new AssetReader();
    const char* nativeString = env->GetStringUTFChars(name, nullptr);
    if (nativeString == nullptr) return;
    const char* contents = reader->readAssets(env, const_cast<char *>(nativeString), assetmanager);
    LOGD("%s", contents);
    env->ReleaseStringUTFChars(name, nativeString);
    delete reader;
}