package com.me.harris.droidmedia.extractFrame

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.extractFrame.render.JavaRendererGL30
import com.me.harris.droidmedia.extractFrame.render.YUVRender
import com.me.harris.droidmedia.video.VideoPlayView
import java.nio.IntBuffer
import kotlin.concurrent.thread


// https://blog.csdn.net/afei__/article/details/114886960
class DecodeFrameActivity:AppCompatActivity()
{

    companion object {
        const val TAG = "DecodeFrameActivity"

        const val GL_SURFACEVIEW_2_ENABLED = true //是否用gl 2.0渲染
        const val GL_SURFACEVIEW_3_ENABLED = true//是否用gl 3.0渲染

    }

    lateinit var mButton:Button
    lateinit var mImageView1:ImageView
    lateinit var mImageView2:ImageView
    lateinit var mGlSurfaceViewv3:GLSurfaceView
    lateinit var mGlSurfaceViewv2:GLSurfaceView
    lateinit var mRendererGL30: JavaRendererGL30
    lateinit var mRendererGL20: YUVRender


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codec_deco_frame)
        mButton = findViewById(R.id.button1)
        mImageView1 = findViewById(R.id.image1)
        mImageView2 = findViewById(R.id.image2)
        mGlSurfaceViewv2 = findViewById(R.id.glsurfaceView2)
        mGlSurfaceViewv3 = findViewById(R.id.glsurfaceView3)
        if (GL_SURFACEVIEW_3_ENABLED){
            mGlSurfaceViewv3.setEGLContextClientVersion(3); // 设置OpenGL版本号
            mRendererGL30 =
                JavaRendererGL30(
                    this
                )
            mGlSurfaceViewv3.setRenderer(mRendererGL30);
            mGlSurfaceViewv3.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY;
        }

        if (GL_SURFACEVIEW_2_ENABLED){
            mGlSurfaceViewv2.setEGLContextClientVersion(2); // 设置OpenGL版本号
            mRendererGL20 = YUVRender(this)
            mGlSurfaceViewv2.setRenderer(mRendererGL20);
            mGlSurfaceViewv2.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY;
        }

        mButton.setOnClickListener {
            startDecode()
        }
    }

    var decoder: VideoDecoder? = null

    private fun startDecode(){
        val firsVideoFile =  VideoPlayView.strVideo


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


//                     3. 使用yuv to rgb, 可用，但是卡
//                    if (glRgbBuffer==null){
//                        glRgbBuffer = IntBuffer.allocate(width*height)
//                    }
//                    GPUImageNativeLibrary.YUVtoRBGA(yuv,width,height,glRgbBuffer!!.array())
//                    // NI critical lock held for 45.303ms on Thread block
//                    val bmp3 = Bitmap.createBitmap(finalWidth,finalHeight,Bitmap.Config.ARGB_8888)
//                    bmp3.copyPixelsFromBuffer(glRgbBuffer)
//                    glRgbBuffer?.rewind()



                    // 4. render onGLSurfaceView
                    if (GL_SURFACEVIEW_3_ENABLED){
                        renderOnGLSurfaceViewv3(yuv,width,height)
                    }
                    if (GL_SURFACEVIEW_2_ENABLED){
                        renderOnGLSurfaceViewV2(yuv,width,height)
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


    private fun renderOnGLSurfaceViewv3(yuv:ByteArray?, width:Int, height:Int){
        if (checkOpenGLES30()){
            mGlSurfaceViewv3.queueEvent {
                mRendererGL30.setYuvData(yuv, width, height);
            }
            mGlSurfaceViewv3.requestRender(); // 手动触发渲染
        }else{
            runOnUiThread {
                Toast.makeText(this,"con't support OpenGL ES 3.0!",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun renderOnGLSurfaceViewV2(yuv:ByteArray?, width:Int, height:Int){
        mGlSurfaceViewv2.queueEvent {
            mRendererGL20.setYuvData(yuv, width, height);
        }
        mGlSurfaceViewv2.requestRender(); // 手动触发渲染
    }

    private fun checkOpenGLES30(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info: ConfigurationInfo = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }

    override fun onPause() {
        super.onPause()
        if (GL_SURFACEVIEW_2_ENABLED){
            mGlSurfaceViewv2.onPause();
        }
        if (GL_SURFACEVIEW_3_ENABLED) {
            mGlSurfaceViewv3.onPause();
        }
    }

    override fun onResume() {
        super.onResume()
        if (GL_SURFACEVIEW_2_ENABLED){
            mGlSurfaceViewv2.onResume();
        }
        if (GL_SURFACEVIEW_3_ENABLED) {
            mGlSurfaceViewv3.onResume();
        }
    }



}