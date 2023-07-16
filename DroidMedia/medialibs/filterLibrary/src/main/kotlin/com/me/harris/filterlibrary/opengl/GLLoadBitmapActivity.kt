package com.me.harris.filterlibrary.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES30.GL_FLOAT
import android.opengl.GLES30.GL_LINEAR
import android.opengl.GLES30.GL_LINEAR_MIPMAP_LINEAR
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.GL_TEXTURE_MAG_FILTER
import android.opengl.GLES30.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES30.GL_TRIANGLE_FAN
import android.opengl.GLES30.glBindTexture
import android.opengl.GLES30.glGenerateMipmap
import android.opengl.GLES30.glTexParameteri
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.GLUtils.texImage2D
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cgfay.filterlibrary.util.GlUtil
import com.me.harris.awesomelib.readAssetFileContentAsString
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityGlLoadbitmapBinding
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 把一张bitmap加载进opengl
 * https://blog.csdn.net/lb377463323/article/details/64452714
 *  https://github.com/glumes/AndroidOpenGLTutorial/blob/master/opengl_tutorial/src/main/java/com/glumes/openglbasicshape/transition/TransitionRender.java#L39
 */
class GLLoadBitmapActivity :AppCompatActivity(){

    private lateinit var binding:ActivityGlLoadbitmapBinding

    private val vertexData = floatArrayOf(
        0f, 0f, 0.5f, 0.5f,
        -0.5f, -0.8f, 0f, 1f,
        0.5f, -0.8f, 1f, 1f,
        0.5f, 0.8f, 1f, 0f,
        -0.5f, 0.8f, 0f, 0f,
        -0.5f, -0.8f, 0f, 1f
    )

    private val mFloatBuffer by lazy {
        ByteBuffer.allocateDirect(vertexData.size *4 )
            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlLoadbitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.glsurfaceview.setEGLContextClientVersion(3)
        binding.glsurfaceview.setRenderer(object :GLSurfaceView.Renderer{
            var textTure = 0
            private var uTextureUnitLocation = -1
            private var aPositionLocation = -1
            private var aTextureCoordinatesLocation = -1
            private var mProgramHandle = -1

            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                GLES30.glClearColor(0f, 0f, 0f, 1f)
                GLES30.glDisable(GLES20.GL_DEPTH_TEST)
                if (mProgramHandle<0){
                    val vertextShader = readAssetFileContentAsString(this@GLLoadBitmapActivity,"demo/gl_load_bitmap_vertex_shader.glsl")
                    val fragShader = readAssetFileContentAsString(this@GLLoadBitmapActivity,"demo/gl_load_bitmap_fragment_shader.glsl")
                    mProgramHandle = GlUtil.createProgram(vertextShader,fragShader)
                }
                if (textTure == 0){
                    textTure = loadTexture(this@GLLoadBitmapActivity, R.drawable.image_017)
                }
                if (aPositionLocation < 0){
                    aPositionLocation = GLES30.glGetAttribLocation(mProgramHandle, "a_Position");
                }
                if (aTextureCoordinatesLocation < 0){
                    aTextureCoordinatesLocation = GLES30.glGetAttribLocation(mProgramHandle, "a_TextureCoordinates");
                }
                if (uTextureUnitLocation<0){
                    uTextureUnitLocation = GLES30.glGetUniformLocation(mProgramHandle,"u_TextureUnit")
                }
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                GLES30.glViewport(0, 0, binding.glsurfaceview.width, binding.glsurfaceview.height)
            }

            override fun onDrawFrame(gl: GL10?) {

                if (textTure!=0){
                    // https://blog.csdn.net/lb377463323/article/details/64452714
                    GLES30.glUseProgram(mProgramHandle)
                    Log.w("=A=","mProgramHandle = ${mProgramHandle}")
                    Log.w("=A=","uTextureUnitLocation = ${uTextureUnitLocation}")
                    // 传递矩形顶点坐标
                    mFloatBuffer.position(0)
                    GLES30.glVertexAttribPointer(aPositionLocation,2,GLES30.GL_FLOAT,false,4,mFloatBuffer)
                    GLES30.glEnableVertexAttribArray(aPositionLocation)
                    // 传递纹理顶点坐标
                    mFloatBuffer.position(2)
                    GLES30.glVertexAttribPointer(aTextureCoordinatesLocation,2, GL_FLOAT,false,4,mFloatBuffer)
                    GLES30.glEnableVertexAttribArray(aTextureCoordinatesLocation)

                    if (textTure>0){
                        GLES30.glActiveTexture(GLES30.GL_TEXTURE0) // 激活纹理单元一
                        GLES30.glBindTexture(GL_TEXTURE_2D,textTure) // 绑定纹理到这个纹理单元
                        //把选定的纹理单元传递给片段着色器中的u_TextureUnit
                        GLES30.glUniform1i(uTextureUnitLocation,0)
                    }

                    GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN,0,6)
                    GLES30.glDisableVertexAttribArray(aPositionLocation)
                    GLES30.glDisableVertexAttribArray(aTextureCoordinatesLocation)
                    GLES30.glBindTexture(GL_TEXTURE_2D,0) // 绑定纹理到这个纹理单元
                }
            }
        })
        binding.glsurfaceview.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        binding.button.setOnClickListener {
            binding.glsurfaceview.requestRender()
        }
    }


    fun loadTexture(context:Context,resourceId:Int):Int{
        val textureObjectIdS = IntArray(1)
        // 1代表生成一个纹理
        GLES30.glGenTextures(1,textureObjectIdS,0)
        if (textureObjectIdS[0] == 0){
            Log.w("=A=","generate texture失败")
            return 0
        }
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources,resourceId)
        if (bitmap == null){
            Log.w("=A=","resourceId = "+ resourceId + " decode failure")
            GLES30.glDeleteTextures(1,textureObjectIdS,0);
            return 0
        }
        // 第一个参数表示这是一个2d纹理，第二个参数是opengl要绑定的纹理对象，也就是让opengl后续的纹理调用都使用此纹理对象
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureObjectIdS[0])

        // 设置缩小的情况下过滤方式
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        // 设置放大的情况下过滤方式
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // 加载纹理到 OpenGL，读入 Bitmap 定义的位图数据，并把它复制到当前绑定的纹理对象
        // 当前绑定的纹理对象就会被附加上纹理图像。
        texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        // 为当前绑定的纹理自动生成所有需要的多级渐远纹理
        // 生成 MIP 贴图
        glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        // 解除与纹理的绑定，避免用其他的纹理方法意外地改变这个纹理
        glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textureObjectIdS[0];

    }
}