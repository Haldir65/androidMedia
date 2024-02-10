package com.me.harris.filterlibrary.opengl.shadertoy.art

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.cgfay.filterlibrary.util.GlUtil.createProgram
import com.me.harris.awesomelib.readAssetFileContentAsString
import com.me.harris.filterlibrary.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class ShaderToy(private val context: Context) : OnTouchListener {
    private val vertexCount = vertexData.size / COORDS_PER_VERTEX

    //每一次取的总的点 大小
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    //位置
    private val vertexBuffer: FloatBuffer

    //纹理
    private val textureBuffer: FloatBuffer
    private var program = 0

    //顶点位置
    private var mavPosition = 0

    //纹理位置
    private var mafPosition = 0

    //全局时间句柄
    private var miTimeHandle = 0

    //当前多少帧
    private var miFrameHandle = 0

    //宽高
    private var miResolutionHandle = 0

    //点击位置
    private var miMouseHandle = 0
    private val mMouse = floatArrayOf(0f, 0f, 0f, 0f)
    private lateinit var mResolution: FloatArray
    private var mStartTime: Long = 0
    private var iFrame = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        vertexBuffer.position(0)
        textureBuffer = ByteBuffer.allocateDirect(textureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(textureData)
        textureBuffer.position(0)
    }

    fun onSurfaceCreated() {
        val vertexSource = readAssetFileContentAsString(context,"shadertoy/vertex_shader.glsl")
        val fragmentSource = readAssetFileContentAsString(context,"shadertoy/fragment_shader_llgxwc2.glsl")
        program = createProgram(vertexSource, fragmentSource)
        if (program > 0) {
            //获取顶点坐标字段
            mavPosition = GLES20.glGetAttribLocation(program, "av_Position")
            //获取纹理坐标字段
            mafPosition = GLES20.glGetAttribLocation(program, "af_Position")
            //从运行的时候的时间
            miTimeHandle = GLES20.glGetUniformLocation(program, "iTime")
            //当前多少帧
            miFrameHandle = GLES20.glGetUniformLocation(program, "iFrame")
            //宽高
            miResolutionHandle = GLES20.glGetUniformLocation(program, "iResolution")
            //宽高
            miMouseHandle = GLES20.glGetUniformLocation(program, "iMouse")
        }
        mStartTime = System.currentTimeMillis()
    }

    fun draw() {
        //清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //设置背景颜色
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)

        //使用程序
        GLES20.glUseProgram(program)
        GLES20.glEnableVertexAttribArray(mavPosition)
        GLES20.glEnableVertexAttribArray(mafPosition)
        //设置顶点位置值
        GLES20.glVertexAttribPointer(mavPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)
        //设置纹理位置值
        GLES20.glVertexAttribPointer(
            mafPosition,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            textureBuffer
        )
        GLES20.glUniform4fv(miMouseHandle, 1, mMouse, 0)
        GLES20.glUniform3fv(miResolutionHandle, 1, mResolution, 0)
        val t = (System.currentTimeMillis() - mStartTime).toFloat() / 1000f
        GLES20.glUniform1f(miTimeHandle, t)
        GLES20.glUniform1i(miFrameHandle, ++iFrame)
        Log.e("zzz", "t=$t")
        Log.e("zzz", "iFrame=$iFrame")
        val iChannels = channels
        for (i in iChannels.indices) {
            val sTextureLocation = GLES20.glGetUniformLocation(program, "iChannel$i")
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iChannels[i])
            // TODO: zzw 2019-06-27 设置纹理数据
            GLES20.glUniform1i(sTextureLocation, i)
        }
        extraDraw()

        //绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)
        GLES20.glDisableVertexAttribArray(mavPosition)
        GLES20.glDisableVertexAttribArray(mafPosition)
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        mResolution = floatArrayOf(width.toFloat(), height.toFloat(), 1f)
        //宽高
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            mMouse[0] = event.x
            mMouse[1] = event.y
        }
        return true
    }

    protected fun extraDraw() {}
    protected val channels: IntArray
        protected get() = intArrayOf()

    companion object {
        //顶点坐标
        var vertexData = floatArrayOf( // in counterclockwise order:
            -1f, -1f, 0.0f,  // bottom left
            1f, -1f, 0.0f,  // bottom right
            -1f, 1f, 0.0f,  // top left
            1f, 1f, 0.0f
        )

        //纹理坐标
        var textureData = floatArrayOf( // in counterclockwise order:
            0f, 1f, 0.0f,  // bottom left
            1f, 1f, 0.0f,  // bottom right
            0f, 0f, 0.0f,  // top left
            1f, 0f, 0.0f
        )

        //每一次取点的时候取几个点
        private const val COORDS_PER_VERTEX = 3
    }
}
