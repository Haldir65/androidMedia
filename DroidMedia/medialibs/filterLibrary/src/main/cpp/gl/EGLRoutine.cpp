//
// Created by harris on 2023/6/24.
//

#include "EGLRoutine.h"

EGLRoutine::EGLRoutine() {

}

EGLRoutine::~EGLRoutine() {

}

void EGLRoutine::eglSetup(JNIEnv *env, jobject surface) {

    //LOGD("drawTexture width:%d,height:%d", width, height);
    //1.获取原始窗口
    //be sure to use ANativeWindow_release()
    // * when done with it so that it doesn't leak.
    ANativeWindow *nwin = ANativeWindow_fromSurface(env, surface);
    //获取Display
    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    mEglDisplay = static_cast<EGLDisplay *>(display);
    if (display == EGL_NO_DISPLAY) {
        LOGD("egl display failed");
        return;
    }
    //2.初始化egl，后两个参数为主次版本号
    if (EGL_TRUE != eglInitialize(display, 0, 0)) {
        LOGD("eglInitialize failed");
        return;
    }

    //3.1 surface配置，可以理解为窗口
    EGLConfig eglConfig;
    EGLint configNum;
    EGLint configSpec[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_RENDERABLE_TYPE,
            EGL_OPENGL_ES3_BIT,
            EGL_NONE
    };

    if (EGL_TRUE != eglChooseConfig(display, configSpec, &eglConfig, 1, &configNum)) {
        LOGD("eglChooseConfig failed");
        return;
    }

    //3.2创建surface(egl和NativeWindow进行关联。最后一个参数为属性信息，0表示默认版本)
    EGLSurface winSurface = eglCreateWindowSurface(display, eglConfig, nwin, 0);
    if (winSurface == EGL_NO_SURFACE) {
        LOGD("eglCreateWindowSurface failed");
        return;
    }
    mGlSurface = static_cast<EGLSurface *>(winSurface);

    //4 创建关联上下文
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
    };
    //EGL_NO_CONTEXT表示不需要多个设备共享上下文
    EGLContext context = eglCreateContext(display, eglConfig, EGL_NO_CONTEXT, ctxAttr);
    if (context == EGL_NO_CONTEXT) {
        LOGD("eglCreateContext failed");
        return;
    }
    //将egl和opengl关联
    //两个surface一个读一个写。第二个一般用来离线渲染？
    if (EGL_TRUE != eglMakeCurrent(display, winSurface, winSurface, context)) {
        LOGD("eglMakeCurrent failed");
        return;
    }

}

void EGLRoutine::eglSwapBuffer() {
    eglSwapBuffers(mEglDisplay, mGlSurface);
}


GLint EGLRoutine::initShader(const char *source, int type) {

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




