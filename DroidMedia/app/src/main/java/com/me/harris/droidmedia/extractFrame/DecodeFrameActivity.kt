package com.me.harris.droidmedia.extractFrame

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.extractFrame.render.JavaRenderer
import java.io.File
import java.nio.IntBuffer
import kotlin.concurrent.thread


// https://blog.csdn.net/afei__/article/details/114886960
class DecodeFrameActivity:AppCompatActivity()
{

    companion object {
        const val TAG = "DecodeFrameActivity"

        const val GL_SURFACEVIEW_ENABLED = true
    }

    lateinit var mButton:Button
    lateinit var mImageView1:ImageView
    lateinit var mImageView2:ImageView
    lateinit var mGlSurfaceView:GLSurfaceView
    lateinit var mRenderer: JavaRenderer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codec_deco_frame)
        mButton = findViewById(R.id.button1)
        mImageView1 = findViewById(R.id.image1)
        mImageView2 = findViewById(R.id.image2)
        mGlSurfaceView = findViewById(R.id.glsurfaceView)
        if (GL_SURFACEVIEW_ENABLED){
            mGlSurfaceView.setEGLContextClientVersion(3); // 设置OpenGL版本号
            mRenderer = JavaRenderer(this)
            mGlSurfaceView.setRenderer(mRenderer);
            mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
        mButton.setOnClickListener {
            startDecode()
        }
    }

    var decoder: VideoDecoder? = null

    private fun startDecode(){
        val firsVideoFile = File(Environment.getExternalStorageDirectory(),Environment.DIRECTORY_MOVIES).listFiles { f ->
            f.name.endsWith(".webm") || f.name.endsWith(".mkv") || f.name.endsWith(".mp4")
        }.random().absolutePath

        val mVideoDecoder = VideoDecoder().also { decoder = it }
        mVideoDecoder.outputFormat = VideoDecoder.COLOR_FORMAT_NV21// 设置输出nv21的数据
        val renderScriptConverter = NV21ToBitmap(this)
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        var glRgbBuffer: IntBuffer? = null
        decoder?.stop()
        thread {
            mVideoDecoder.decode(firsVideoFile,object :VideoDecoder.DecodeCallback{
                override fun onDecode(
                    yuv: ByteArray?,
                    width: Int,
                    height: Int,
                    formatCount: Int,
                    presentationTimeUs: Long
                ) {

                    Log.d(TAG,"frameCount : ${formatCount} , presentationTimeUs: ${presentationTimeUs} ")
                    val finalWidth = Math.min(screenHeight,width)
                    val finalHeight = Math.min(screenWidth,width)

                    val bmp1  = ImageUtil.yuvDataToBitMap(yuv,Math.max(width,finalWidth),Math.max(height,finalHeight),finalWidth,finalHeight)

                    // 2. 使用renderScript
                    val bmp2  = renderScriptConverter.nv21ToBitmap(yuv,finalWidth,finalHeight)


                    // 3. 使用yuv to rgb, 可用，但是卡
//                    if (glRgbBuffer==null){
//                        glRgbBuffer = IntBuffer.allocate(width*height)
//                    }
//                    GPUImageNativeLibrary.YUVtoRBGA(yuv,width,height,glRgbBuffer!!.array())
//                    // NI critical lock held for 45.303ms on Thread block
//                    val bm3 = Bitmap.createBitmap(finalWidth,finalHeight,Bitmap.Config.ARGB_8888)
//                    bm3.copyPixelsFromBuffer(glRgbBuffer)
//                    glRgbBuffer?.rewind()
                    //


                    // 4. render onGLSurfaceView
                    if (GL_SURFACEVIEW_ENABLED){
                        renderOnGLSurfaceView(yuv,width,height)
                    }

                    runOnUiThread {
                        mImageView1.setImageBitmap(bmp1)
                        mImageView2.setImageBitmap(bmp2)
                    }
                }
                override fun onFinish() {
                    Log.d(TAG,"onFinish")
                }

                override fun onStop() {
                    Log.d(TAG,"onStop")
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        decoder?.stop()
    }


    private fun renderOnGLSurfaceView(yuv:ByteArray?,width:Int ,height:Int){
        if (checkOpenGLES30()){
            mGlSurfaceView.queueEvent {
                mRenderer.setYuvData(yuv, width, height);
            }
            mGlSurfaceView.requestRender(); // 手动触发渲染
        }else{
            runOnUiThread {
                Toast.makeText(this,"con't support OpenGL ES 3.0!",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkOpenGLES30(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info: ConfigurationInfo = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }

    override fun onPause() {
        super.onPause()
        mGlSurfaceView.onPause();
    }

    override fun onResume() {
        super.onResume()
        mGlSurfaceView.onResume();
    }


}