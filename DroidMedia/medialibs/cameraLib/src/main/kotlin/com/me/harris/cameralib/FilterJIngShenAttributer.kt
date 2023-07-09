package com.me.harris.cameralib

// https://github.com/CainKernel/blog/blob/master/OpenGLES-滤镜开发汇总/OpenGLES滤镜开发汇总-——-景深特效.md
class FilterJIngShenAttributer:FilterAttributer {
    override fun onShaderLoaded(engine: FilterEngine) {
    }

    override fun onDraw(engine: FilterEngine) {
    }

    // todo : unfinished
    override fun fragmentShader() = R.raw.base_fragment_jingshen_shader
}