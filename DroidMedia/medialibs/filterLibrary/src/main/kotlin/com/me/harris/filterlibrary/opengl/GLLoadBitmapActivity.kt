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
 * https://github.com/1993hzw/OpenGLESIntroduction/tree/master
 *
 * https://blog.csdn.net/lb377463323/article/details/64452714
 *  https://github.com/glumes/AndroidOpenGLTutorial/blob/master/opengl_tutorial/src/main/java/com/glumes/openglbasicshape/transition/TransitionRender.java#L39
 *  https://www.cnblogs.com/8335IT/p/16390971.html
 */
class GLLoadBitmapActivity :AppCompatActivity(){

    private lateinit var binding:ActivityGlLoadbitmapBinding


    // 绘制图片的原来：定义一组矩形区域的定点，然后根据纹理坐标将
    // 图片作为纹理贴在该矩形区域内

    //原始的矩形区域的顶点坐标，因为后面使用了顶点法绘制顶点
    //所以不用定义绘制顶点的索引，
    // 窗口中心为opengl二维坐标系的原点(0,0)
    private val CUBES = floatArrayOf(
        -1.0f,-1.0f,
        1.0f,-1.0f,
        -1.0f,1.0f,
        1.0f,1.0f
    )

//    private val CUBES2 = floatArrayOf(
//        -0.5f,-0.5f,
//        0.5f,-0.5f,
//        -0.5f,0.5f,
//        0.5f,0.5f
//    )
    //代表这张图铺在什么位置
    private val CUBES2 = floatArrayOf(
        0f,-1f, // 左下
        1f,-1f, // 右下
        -0.0f,0.0f, // 左上
        1f,0.0f // 右上
    )

    // 纹理也有坐标，称为UV坐标，或者ST坐标。UV坐标定义为左上角（0，0）
    // 右下角(1,1)
    // 纹理坐标，每个坐标的纹理采样对应上面顶点坐标
    private val TEXTURE_NO_ROTATION = floatArrayOf(
        0.0f, 1.0f,
        1.0f,1.0f,
        0.0f,0.0f,
        1.0f,0.0f
    )


    private val TEXTURE_NO_ROTATION2 = floatArrayOf(
        0.0f, 1f,
        1f,1f,
        0.0f,0.0f,
        1f,0.0f
    )


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

    private val mGLCubeBuffer by lazy {
        ByteBuffer.allocateDirect(CUBES.size*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(CUBES).position(0)
    }

    private val mGLTextureBuffer by lazy {
        ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.size*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(TEXTURE_NO_ROTATION).position(0)
    }

    private val mGLCubeBuffer2 by lazy {
        ByteBuffer.allocateDirect(CUBES2.size*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(CUBES2).position(0)
    }

    private val mGLTextureBuffer2 by lazy {
        ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION2.size*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(TEXTURE_NO_ROTATION2).position(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlLoadbitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.glsurfaceview.setEGLContextClientVersion(3)
        binding.glsurfaceview.setRenderer(object :GLSurfaceView.Renderer{
            private var aPositionLocation = -1

            var textTure = 0 // 第一张图片
            var textTure2 = 0 //第二张图片

            private var uTextureUnitLocation = -1
            private var aTextureCoordinatesLocation = -1

            //第二张图片
            private var uTextureUnitLocation2= -1
            private var aTextureCoordinatesLocation2 = -1

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
                if (textTure2 == 0){
                    textTure2 = loadTexture(this@GLLoadBitmapActivity, R.drawable.p6 )
                }
                if (aPositionLocation < 0){
                    aPositionLocation = GLES30.glGetAttribLocation(mProgramHandle, "a_Position");
                }
                if (aTextureCoordinatesLocation < 0){
                    aTextureCoordinatesLocation = GLES30.glGetAttribLocation(mProgramHandle, "a_TextureCoordinates");
                }
                if (aTextureCoordinatesLocation2 < 0){
                    aTextureCoordinatesLocation2 = GLES30.glGetAttribLocation(mProgramHandle, "a_TextureCoordinates2");
                }

                if (uTextureUnitLocation<0){
                    uTextureUnitLocation = GLES30.glGetUniformLocation(mProgramHandle,"u_TextureUnit")
                }
                if (uTextureUnitLocation2<0){
                    uTextureUnitLocation2 = GLES30.glGetUniformLocation(mProgramHandle,"u_TextureUnit2")
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


                    // 传入图片纹理
                    if (textTure>0){
                        // 顶点着色器的顶点坐标
                        mGLCubeBuffer.position(0);
                        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 0, mGLCubeBuffer);
                        GLES30.glEnableVertexAttribArray(aPositionLocation);
                        // 顶点着色器的纹理坐标
                        mGLTextureBuffer.position(0);
                        GLES30.glVertexAttribPointer(uTextureUnitLocation, 2, GLES30.GL_FLOAT, false, 0, mGLTextureBuffer);

                        GLES30.glEnableVertexAttribArray(uTextureUnitLocation);
                        GLES30.glActiveTexture(GLES30.GL_TEXTURE0) // 激活纹理单元一
                        GLES30.glBindTexture(GL_TEXTURE_2D,textTure) // 绑定纹理到这个纹理单元
                        //把选定的纹理单元传递给片段着色器中的u_TextureUnit
                        GLES30.glUniform1i(uTextureUnitLocation,0)
                    }
                    //这里不画的话，底部是黑色的，不知道为什么
                    GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

                    // 传入图片纹理
                    if (textTure2>0){

                        // 顶点着色器的顶点坐标
                        mGLCubeBuffer2.position(0);
                        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 0, mGLCubeBuffer2);
                        GLES30.glEnableVertexAttribArray(aPositionLocation);
                        // 顶点着色器的纹理坐标
                        mGLTextureBuffer2.position(0);
                        GLES30.glVertexAttribPointer(uTextureUnitLocation2, 2, GLES30.GL_FLOAT, false, 0, mGLTextureBuffer2);

                        GLES30.glEnableVertexAttribArray(uTextureUnitLocation2);
                        GLES30.glActiveTexture(GLES30.GL_TEXTURE1) // 激活纹理单元一
                        GLES30.glBindTexture(GL_TEXTURE_2D,textTure2) // 绑定纹理到这个纹理单元
                        //把选定的纹理单元传递给片段着色器中的u_TextureUnit
                        GLES30.glUniform1i(uTextureUnitLocation2,1)
                    }

                    GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
//                    GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN,0,4)
                    GLES30.glDisableVertexAttribArray(aPositionLocation)
                    GLES30.glDisableVertexAttribArray(aTextureCoordinatesLocation)
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0) // 绑定纹理到这个纹理单元
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