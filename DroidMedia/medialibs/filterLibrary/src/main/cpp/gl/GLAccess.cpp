#include <jni.h>
#include "GLAccess.h"
#include "EGLRoutine.h"

//
// Created by me on 2023/6/24.
//


GLint initShader(const char *source, GLint type) {
    //创建shader
    GLint sh = glCreateShader(type);
    if (sh == 0) {
        LOGD("glCreateShader %d failed", type);
        return 0;
    }
    //加载shader
    glShaderSource(sh,
                   1,//shader数量
                   &source,
                   0);//代码长度，传0则读到字符串结尾

    //编译shader
    glCompileShader(sh);

    GLint status;
    glGetShaderiv(sh, GL_COMPILE_STATUS, &status);
    if (status == 0) {
        LOGD("glCompileShader %d failed", type);
        LOGD("source %s", source);
        auto *infoLog = new GLchar[512];
        GLsizei length;
        glGetShaderInfoLog(sh, 512, &length, infoLog);
//        std::cout << "ERROR::SHADER::VERTEX::COMPILATION_FAILED\n" << infoLog << std::endl;

        LOGD("ERROR::SHADER::VERTEX::COMPILATION_FAILED %s", infoLog);
        return 0;
    }

    LOGD("glCompileShader %d success", type);
    return sh;
}




extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_filterlibrary_opengl_GLAccess_drawTexture(JNIEnv *env, jobject thiz,
                                                             jobject bitmap, jobject bitmap1,
                                                             jobject surface) {

    auto *routine = new EGLRoutine();
    routine->eglSetup(env,surface);

    GLint vsh = initShader(vertexSimpleTexture, GL_VERTEX_SHADER);
    GLint fsh = initShader(fragSimpleTexture, GL_FRAGMENT_SHADER);

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

    LOGD("bitmap width:%d,height:%d" ,bmpInfo.width,bmpInfo.height);

    AndroidBitmapInfo bmpInfo1;
    void *bmpPixels1;

    if (AndroidBitmap_getInfo(env, bitmap1, &bmpInfo1) < 0) {
        LOGD("AndroidBitmap_getInfo() failed ! ");
        return;
    }

    AndroidBitmap_lockPixels(env, bitmap1, &bmpPixels1);

    LOGD("bitmap width:%d,height:%d" ,bmpInfo1.width,bmpInfo1.height);

    if (bmpPixels == nullptr || bmpPixels1 == nullptr){
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
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);	// set texture wrapping to GL_REPEAT (default wrapping method)
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