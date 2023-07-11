package com.me.harris.filterlibrary.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.GL_LINEAR
import android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR
import android.opengl.GLES20.GL_TEXTURE_2D
import android.opengl.GLES20.GL_TEXTURE_MAG_FILTER
import android.opengl.GLES20.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES20.glBindTexture
import android.opengl.GLES20.glGenerateMipmap
import android.opengl.GLES20.glTexParameteri
import android.opengl.GLUtils.texImage2D
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.filterlibrary.databinding.ActivityGlLoadbitmapBinding

/**
 * 把一张bitmap加载进opengl
 * https://blog.csdn.net/lb377463323/article/details/64452714
 *  https://github.com/glumes/AndroidOpenGLTutorial/blob/master/opengl_tutorial/src/main/java/com/glumes/openglbasicshape/transition/TransitionRender.java#L39
 */
class GLLoadBitmapActivity :AppCompatActivity(){

    private lateinit var binding:ActivityGlLoadbitmapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlLoadbitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    fun loadTexture(context:Context,resourceId:Int):Int{
        val textureObjectIdS = IntArray(1)
        // 1代表生成一个纹理
        GLES20.glGenTextures(1,textureObjectIdS,0)
        if (textureObjectIdS[0] == 1){
            Log.w("=A=","generate texture失败")
            return 0
        }
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources,resourceId)
        if (bitmap == null){
            Log.w("=A=","resourceId = "+ resourceId + " decode failure")
            GLES20.glDeleteTextures(1,textureObjectIdS,0);
            return 0
        }
        // 第一个参数表示这是一个2d纹理，第二个参数是opengl要绑定的纹理对象，也就是让opengl后续的纹理调用都使用此纹理对象
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureObjectIdS[0])

        // 设置缩小的情况下过滤方式
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        // 设置放大的情况下过滤方式
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // 加载纹理到 OpenGL，读入 Bitmap 定义的位图数据，并把它复制到当前绑定的纹理对象
        // 当前绑定的纹理对象就会被附加上纹理图像。
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        // 为当前绑定的纹理自动生成所有需要的多级渐远纹理
        // 生成 MIP 贴图
        glGenerateMipmap(GL_TEXTURE_2D);

        // 解除与纹理的绑定，避免用其他的纹理方法意外地改变这个纹理
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIdS[0];

    }
}