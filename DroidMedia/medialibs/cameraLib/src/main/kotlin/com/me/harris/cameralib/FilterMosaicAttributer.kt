package com.me.harris.cameralib

import android.opengl.GLES20
import com.me.harris.awesomelib.utils.Utils

class FilterMosaicAttributer:FilterAttributer {



    var imageWidth:Float = 0f // 图片宽度
    var imageHeight:Float = 0f //图片高度
    var mosaicSize:Float = 5f


    private var mImageWidthFactorHandle = 0
    private var mImageHeightFactorHandle = 0
    private var mMosaicHandle = 0

    override fun onShaderLoaded(engine: FilterEngine) {
        imageWidth = 0.004f
        imageHeight = 0.004f
    }

    override fun onDraw(engine: FilterEngine) {
        if (mImageWidthFactorHandle == 0){
            mImageWidthFactorHandle = GLES20.glGetUniformLocation(engine.shaderProgram, "imageWidthFactor")
        }
        if (mImageHeightFactorHandle == 0){
            mImageHeightFactorHandle = GLES20.glGetUniformLocation(engine.shaderProgram, "imageHeightFactor")
        }
        if (mMosaicHandle == 0){
            mMosaicHandle = GLES20.glGetUniformLocation(engine.shaderProgram, "mosaicSize")
        }
        GLES20.glUniform1f(mImageWidthFactorHandle, imageWidth)
        GLES20.glUniform1f(mImageHeightFactorHandle, imageHeight)
        GLES20.glUniform1f(mMosaicHandle, mosaicSize)
    }

    override fun fragmentShader() = R.raw.base_fragment_mosaic_shader
}