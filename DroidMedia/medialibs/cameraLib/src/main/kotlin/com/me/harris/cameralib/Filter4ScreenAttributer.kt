package com.me.harris.cameralib

class Filter4ScreenAttributer:FilterAttributer {
    override fun onShaderLoaded(engine: FilterEngine) {
    }

    override fun onDraw(engine: FilterEngine) {
    }

    override fun fragmentShader() = R.raw.base_fragment_4_screen_shader
}