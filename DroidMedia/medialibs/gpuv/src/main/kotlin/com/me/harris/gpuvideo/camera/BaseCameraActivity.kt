package com.me.harris.gpuvideo.camera

import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.daasuu.gpuv.R
import com.daasuu.gpuv.camerarecorder.CameraRecordListener
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.daasuu.gpuv.camerarecorder.LensFacing
import com.daasuu.gpuv.gpuvideoandroid.FilterType
import com.daasuu.gpuv.gpuvideoandroid.widget.SampleCameraGLView
import com.me.harris.awesomelib.exportMp4ToGallery
import com.me.harris.awesomelib.exportPngToGallery
import com.me.harris.awesomelib.utils.Utils
import com.me.harris.gpuv.FilterAdapter
import java.io.File
import java.io.FileOutputStream
import java.nio.IntBuffer
import java.text.SimpleDateFormat
import java.util.Date
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.opengles.GL10
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder as Recorder

open class BaseCameraActivity :AppCompatActivity() {
    private lateinit var sampleGLView: SampleCameraGLView

    lateinit var GPUCameraRecorder:Recorder

    var filepath:String = ""

    private lateinit var recordBtn:Button;
     var lensFacing = LensFacing.BACK

    var cameraWidth = 1280
    var cameraHeight = 720

    var videoWidth = 720
    var videoHeight = 720

    private var toggleClick = false

    private lateinit var lv:ListView


    protected fun onCreateActivity(){
        supportActionBar?.hide()
        recordBtn = findViewById<Button>(R.id.btn_record)
        recordBtn.setOnClickListener {
            if (recordBtn.text.equals(getString(R.string.app_record))){
                filepath = getVideoFilePath()
                GPUCameraRecorder.start(filepath)
                recordBtn.text = "Stop"
                lv.isVisible = false
            } else {
                GPUCameraRecorder.stop()
                recordBtn.setText(R.string.app_record)
                lv.isVisible = true
            }
        }
        findViewById<Button>(R.id.btn_flash).setOnClickListener {
            if (GPUCameraRecorder.isFlashSupport){
                GPUCameraRecorder.switchFlashMode()
                GPUCameraRecorder.changeAutoFocus()
            }else {
                Toast.makeText(this, "当前设备不支持闪光灯", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btn_switch_camera).setOnClickListener {
            releaseCamera()
            lensFacing = if (lensFacing == LensFacing.BACK){
                LensFacing.FRONT
            }else {
                LensFacing.BACK
            }
            toggleClick = true
        }

        findViewById<Button>(R.id.btn_image_capture).setOnClickListener {
            captureBitmap {bitmap ->
                Handler().post {
                    val imagePath = getImageFilePath()
                    saveAsPngImage(bitmap = bitmap,imagePath)
                    exportPngToGallery(applicationContext,imagePath)
                }
            }
        }

        lv = findViewById(R.id.filter_list)
        val filterTypes = FilterType.createFilterList()
        lv.adapter = FilterAdapter(this,R.layout.row_white_text,filterTypes).whiteMode()
        lv.setOnItemClickListener { parent, view, position, id ->
            GPUCameraRecorder.setFilter(FilterType.createGlFilter(filterTypes[position],applicationContext))
        }
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
    }

    override fun onResume() {
        super.onResume()
        setUpCamera()
    }

    override fun onStop() {
        super.onStop()
        releaseCamera()
    }

    private fun getVideoFilePath():String{
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath + File.separator + "gpuvidepmp4"
        File(dir).takeIf { !it.exists() }?.mkdirs()
        return dir + File.separator +
                SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date())+"GPUCameraRecord.mp4"
    }

    private fun getImageFilePath():String{
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + "gpuvidepmp4"
        File(dir).takeIf { !it.exists() }?.mkdirs()
        return dir + File.separator +
                SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date())+"GPUCameraRecorder.png"
    }



    private fun releaseCamera(){
        sampleGLView.onPause()
        GPUCameraRecorder.run {
            stop()
            release()
        }
        findViewById<FrameLayout>(R.id.wrap_view).removeView(sampleGLView)
    }

    private fun setUpCameraView(){
        runOnUiThread {
            val frameLayout = findViewById<FrameLayout>(R.id.wrap_view)
            frameLayout.removeAllViews()
            sampleGLView = SampleCameraGLView(applicationContext)
            sampleGLView.setTouchListener {  event, width,height ->
               GPUCameraRecorder.changeManualFocusPoint(event.x,event.y,width,height)
            }
            frameLayout.addView(sampleGLView)
        }
    }

    private fun setUpCamera(){
        setUpCameraView()
        GPUCameraRecorder = GPUCameraRecorderBuilder(this,sampleGLView)
            .cameraRecordListener(object :CameraRecordListener{
                override fun onGetFlashSupport(flashSupport: Boolean) {
                  runOnUiThread { findViewById<Button>(R.id.btn_flash).isEnabled = flashSupport }
                }

                override fun onRecordComplete() {
                    exportMp4ToGallery(applicationContext,filepath)
                }

                override fun onRecordStart() {
                   runOnUiThread { lv.isVisible = false }
                }

                override fun onError(exception: Exception?) {
                    Log.e("GPUCameraRecorder", exception.toString())
                }

                override fun onCameraThreadFinish() {
                    if (toggleClick){
                        runOnUiThread { setUpCamera() }
                    }
                    toggleClick = false
                }

                override fun onVideoFileReady() {
                    Log.e("GPUCameraRecorder", "视频文件已生成并存储到 ${filepath}")
                }

            }).videoSize(videoWidth,videoHeight)
            .cameraSize(cameraWidth,cameraHeight)
            .lensFacing(lensFacing)
            .build()
    }

    private fun interface BitmapReadyCallbacks {
        fun onBitmapReady(bitmap: Bitmap)
    }

    private fun captureBitmap(bitmapReadyCallbacks: BitmapReadyCallbacks){
        sampleGLView.queueEvent {
            val egl = EGLContext.getEGL() as EGL10
            val gl = egl.eglGetCurrentContext().gl as GL10
            val sanpshotBitmap = createBitmapFromGLSurface(sampleGLView.measuredWidth,sampleGLView.measuredHeight,gl)
            runOnUiThread {
                if (sanpshotBitmap != null) {
                    bitmapReadyCallbacks.onBitmapReady(sanpshotBitmap)
                }
            }
        }
    }

    private fun createBitmapFromGLSurface(w:Int,h:Int,gl:GL10):Bitmap?{
        val bitmapBuffer = IntArray(w*h)
        val bitmapSource = IntArray(w*h)
        val intBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)
        val bmp = kotlin.runCatching {
            gl.glReadPixels(0,0,w,h,GL10.GL_RGBA,GL10.GL_UNSIGNED_BYTE,intBuffer)
            var offset1: Int
            var offset2: Int
            var texturePixel: Int
            var blue: Int
            var red: Int
            var pixel: Int
            for (i in 0 until h) {
                offset1 = i * w
                offset2 = (h - i - 1) * w
                for (j in 0 until w) {
                    texturePixel = bitmapBuffer[offset1 + j]
                    blue = texturePixel shr 16 and 0xff
                    red = texturePixel shl 16 and 0x00ff0000
                    pixel = texturePixel and -0xff0100 or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        }.onFailure {
            return null
        }.getOrNull()
        return Bitmap.createBitmap(bitmapSource,w,h,Bitmap.Config.ARGB_8888)

    }

     fun saveAsPngImage(bitmap:Bitmap,filepath:String){
        kotlin.runCatching {
            FileOutputStream(File(filepath)).use {
                bitmap.compress(Bitmap.CompressFormat.PNG,100,it)
            }
        }
    }

}