package com.me.harris.cameralib

import android.content.Context
import android.opengl.GLES20

// https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音灵魂出窍滤镜.md

class FilterSoulOutAttributer : FilterAttributer {

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
        mScale = (1.0f + 0.5f * getInterpolation(mOffset))
        mOffset += 0.04f;
        if (mOffset > 1.0f) {
            mOffset = 0.0f;
        }
        GLES20.glUniform1f(mScaleHandle, mScale)
    }

    override fun fragmentShader(): Int {
       return R.raw.base_fragment_soul_out
    }


    private fun getInterpolation(input: Float): Float {
        return (Math.cos((input + 1) * Math.PI) / 2.0f).toFloat() + 0.5f
    }


}