package com.me.harris.cameralib

import android.opengl.GLES20

// https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-仿抖音缩放滤镜.md

class FilterScaleAttributer : FilterAttributer {

    val TEXTURE_SCALE = "scale"


    var mScale = 1.0f

    var mScaleHandle = -1

    var mOffset = 0.0f

    var plus = false

    override fun onShaderLoaded(engine: FilterEngine) {

    }

    override fun onDraw(engine: FilterEngine) {
        if (mScaleHandle == -1) {
            mScaleHandle = GLES20.glGetUniformLocation(engine.shaderProgram, TEXTURE_SCALE)
        }
        mOffset += if (plus) +0.06f else -0.06f
        if (mOffset >= 1.0f) {
            plus = false
        } else if (mOffset <= 0.0f) {
            plus = true
        }
        mScale = 1.0f + 0.5f * getInterpolation(mOffset)
        GLES20.glUniform1f(mScaleHandle, mScale)
    }

    override fun fragmentShader(): Int {
        return R.raw.base_fragment_shader_scale
    }

    private fun getInterpolation(input: Float): Float {
        return (Math.cos((input + 1) * Math.PI) / 2.0f).toFloat() + 0.5f
    }




}