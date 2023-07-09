package com.me.harris.cameralib

import android.opengl.GLES20

/// https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音黑白三屏特效.md
class FilterThreeScreenWhiteBlack :FilterAttributer{

    // 手机横过来，中间是原始颜色，上面1/3, 下面1/3是黑白的

    val TEXTURE_SCALE = "scale"


    var mScale = 1.0f

    var mScaleHandle = -1

    var mOffset = 0.0f

    override fun onShaderLoaded(engine: FilterEngine) {
    }

    override fun onDraw(engine: FilterEngine) {
        if (mScaleHandle == -1) {
            mScaleHandle = GLES20.glGetUniformLocation(engine.shaderProgram, TEXTURE_SCALE)
        }
        mScale = 1.0f
        mOffset += 0.04f;
        if (mOffset > 1.0f) {
            mOffset = 0.0f;
        }
        GLES20.glUniform1f(mScaleHandle, mScale)
    }

    override fun fragmentShader() = R.raw.base_fragment_three_screen_white_black_shader
}