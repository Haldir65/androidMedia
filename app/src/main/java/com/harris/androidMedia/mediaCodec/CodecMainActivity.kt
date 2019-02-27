package com.harris.androidMedia.mediaCodec

import android.app.Activity
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.harris.androidMedia.exoPlayer.customize.ChooseLocalVideoActivity
import com.harris.androidMedia.exoPlayer.customize.ChooseLocalVideoActivity.FLAG_CHOOSE_AND_RETURN_URL
import com.harris.androidMedia.exoPlayer.customize.CustomPlayerViewActivity.CUSTOM_PLAYER_VIEW_URL_STRING
import com.harris.androidMedia.util.LogUtil
import com.harris.androidMedia.util.getAllVideoOnDevice
import kotlinx.android.synthetic.main.activity_code_main.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

// https://blog.csdn.net/zhi184816/article/details/52514138
class CodecMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.harris.androidMedia.R.layout.activity_code_main)
        btn1.setOnClickListener {
            Thread {
                exactorMedia()
            }.start()
        }
        btn2.setOnClickListener {
            Thread {
                extractVideoContent()
            }.start()
        }
        btn3.setOnClickListener {
            Thread {
                extractAudioContent()
            }.start()
        }
        btn4.setOnClickListener {
            Thread {
                combineVideo()
            }.start()
        }
        btn5.setOnClickListener {
            startActivityForResult(Intent(this,ChooseLocalVideoActivity::class.java).apply {
                putExtra(FLAG_CHOOSE_AND_RETURN_URL,true)
            }, REQUEST_CODE_CHOOSE_LOCAL_VIDEO_THEN_TO_H256)
        }
        btn6.setOnClickListener {
            startActivity(Intent(this,CameraToMpegTestActivity::class.java))
        }
    }

    companion object {
        val REQUEST_CODE_CHOOSE_LOCAL_VIDEO_THEN_TO_H256 = 1090
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK&&data!=null){
            if (requestCode== REQUEST_CODE_CHOOSE_LOCAL_VIDEO_THEN_TO_H256){
                startActivityForResult(Intent(this,H256VideoPlayerActivity::class.java).apply {
                    putExtra(CUSTOM_PLAYER_VIEW_URL_STRING,data.getStringExtra(CUSTOM_PLAYER_VIEW_URL_STRING))
                }, REQUEST_CODE_CHOOSE_LOCAL_VIDEO_THEN_TO_H256)
            }
        }
    }

    //由此生成的视频文件不能播放，音频文件不能播放
    fun exactorMedia() {
        val mediaExtractor = MediaExtractor()
        val videoPath = getAllVideoOnDevice(this, mutableListOf())[0]
                .path
        LogUtil.d("CodecMainActivity", "videoPath = $videoPath")
        val outPutVideoPath = "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Environment.DIRECTORY_DOWNLOADS}${File.separator}output_video.mp4"
        val outputAudioPath = "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Environment.DIRECTORY_DOWNLOADS}${File.separator}output_audio"
        try {
            val videoOutputStream = FileOutputStream(outPutVideoPath)
            val audioOutputStream = FileOutputStream(outputAudioPath)
            mediaExtractor.setDataSource(videoPath)
            var audioTrackIndex = -1
            var videoTrackIndex = -1

            val trackCount = mediaExtractor.trackCount
            for (i in 0 until trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(i)
                val mimeType = trackFormat.getString(MediaFormat.KEY_MIME)
                //视频信道
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i
                }
                //音频信道
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i
                }
            }
            val byteBuffer = ByteBuffer.allocate(500 * 1024) //每一帧不会超过这个大小吧

            //切换到视频信号的信道
            mediaExtractor.selectTrack(videoTrackIndex)
            while (!stopSignal) {
                val readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleCount < 0) {
                    break
                }
                val buffer = ByteArray(readSampleCount)
                byteBuffer.get(buffer)
                videoOutputStream.write(buffer)
                byteBuffer.clear()
                LogUtil.d("CodecMainActivity", "extracting video content , frame size = $readSampleCount")
                mediaExtractor.advance()
            }
            //切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex)
            while (true) {
                val readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleCount < 0) {
                    break
                }
                val buffer = ByteArray(readSampleCount)
                byteBuffer.get(buffer)
                audioOutputStream.write(buffer)
                byteBuffer.clear()
                LogUtil.d("CodecMainActivity", "extracting audio content , frame size = $readSampleCount")
                mediaExtractor.advance()
            }
            videoOutputStream.close()
            audioOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaExtractor.release()
        }
        Log.d("CodecMainActivity", "retrive completed")
    }

    //由此得到的视频是可以播放的，但是没有音频
    fun extractVideoContent() {
        Log.d("CodecMainActivity", "extractVideoContent start")
        try {
            val mediaExtractor = MediaExtractor()
            val outPutVideoPath = "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Environment.DIRECTORY_DOWNLOADS}${File.separator}output_video_can_play.mp4"
            var videoTrackIndex = -1
            val videoPath = getAllVideoOnDevice(this, mutableListOf())[0]
                    .path
            LogUtil.d("CodecMainActivity", "videoPath = $videoPath")
            mediaExtractor.setDataSource(videoPath)
            val trackCount = mediaExtractor.trackCount
            for (i in 0 until trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(i)
                val mimeType = trackFormat.getString(MediaFormat.KEY_MIME)
                //视频信道
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i
                    break
                }
            }
            mediaExtractor.selectTrack(videoTrackIndex)
            val mediaFormat = mediaExtractor.getTrackFormat(videoTrackIndex)
            val mediaMuxer = MediaMuxer(outPutVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            //追踪此信道
            val trackIndex = mediaMuxer.addTrack(mediaFormat)
            val byteBuffer = ByteBuffer.allocate(1025 * 500)
            val bufferInfo = MediaCodec.BufferInfo()
            mediaMuxer.start()

            //获取每帧的之间的时间
            var videSampleTime = 0L
            mediaExtractor.readSampleData(byteBuffer, 0)
            //skip first I frame
            if (mediaExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC)
                mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val firstVideoPTS = mediaExtractor.sampleTime
            mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val SecondVideoPTS = mediaExtractor.getSampleTime();
            videSampleTime = Math.abs(SecondVideoPTS - firstVideoPTS);
            Log.d("CodecMainActivity", "videoSampleTime is $videSampleTime")
            //就是舍弃第一帧，读完第二帧和第三帧，看下第二帧和第三帧之间的时间间隔
            //重新切换此信道，不然上面跳过了3帧,造成前面的帧数模糊
            mediaExtractor.unselectTrack(videoTrackIndex)
            mediaExtractor.selectTrack(videoTrackIndex)
            while (!stopSignal) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) {
                    break
                }
                mediaExtractor.advance()
                bufferInfo.size = readSampleSize
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.presentationTimeUs += videSampleTime
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
            }
            mediaMuxer.stop()
            mediaExtractor.release()
            mediaMuxer.release()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CodecMainActivity", "================error")
        } finally {

        }
        Log.d("CodecMainActivity", "extractVideoContent completed")
    }


    //由此生成的音频文件完全可以播放
    fun extractAudioContent() {
        Log.d("CodecMainActivity", "extractAudioContent process  start")
        try {
            val mediaExtractor = MediaExtractor()
            val outPutAduioPath = "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Environment.DIRECTORY_DOWNLOADS}${File.separator}output_audio_can_play"
            var audioTrackIndex = -1
            val videoPath = getAllVideoOnDevice(this, mutableListOf())[0]
                    .path
            LogUtil.d("CodecMainActivity", "videoPath = $videoPath")
            mediaExtractor.setDataSource(videoPath)
            val trackCount = mediaExtractor.trackCount
            for (i in 0 until trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(i)
                val mimeType = trackFormat.getString(MediaFormat.KEY_MIME)
                //音频信道
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i
                    break
                }
            }
            mediaExtractor.selectTrack(audioTrackIndex)
            val mediaFormat = mediaExtractor.getTrackFormat(audioTrackIndex)
            val mediaMuxer = MediaMuxer(outPutAduioPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            //追踪此信道
            val trackIndex = mediaMuxer.addTrack(mediaFormat)
            val byteBuffer = ByteBuffer.allocate(1024 * 500)
            val bufferInfo = MediaCodec.BufferInfo()
            mediaMuxer.start()

            //获取每帧的之间的时间
            var audioStampTime = 0L
            mediaExtractor.readSampleData(byteBuffer, 0)
            //skip first I frame
            if (mediaExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC)
                mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val secondAudioTime = mediaExtractor.sampleTime
            mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val thirdAudioTime = mediaExtractor.sampleTime
            audioStampTime = Math.abs(thirdAudioTime - secondAudioTime)
            Log.d("CodecMainActivity", "Audio sample time  is $audioStampTime")
            //就是舍弃第一帧，读完第二帧和第三帧，看下第二帧和第三帧之间的时间间隔
            //重新切换此信道，不然上面跳过了3帧,造成前面的帧数模糊
            mediaExtractor.unselectTrack(audioTrackIndex)
            mediaExtractor.selectTrack(audioTrackIndex)
            var curFrame = 0
            while (!stopSignal) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) {
                    break
                }
                LogUtil.d("CodecMainActivity", "currentFrame $curFrame readingSampleSize =$readSampleSize audioStampTime is $audioStampTime")
                mediaExtractor.advance()
                bufferInfo.size = readSampleSize
                bufferInfo.offset = 0
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.presentationTimeUs += audioStampTime
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
                curFrame += 1
            }
            mediaMuxer.stop()
            mediaExtractor.release()
            mediaMuxer.release()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CodecMainActivity", "================error")
        } finally {

        }
        Log.d("CodecMainActivity", "extractVideoContent completed")
    }


    fun combineVideo() {
        try {
            val OriginaloutPutAudioPath = "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Environment.DIRECTORY_DOWNLOADS}${File.separator}output_audio_can_play"
            val OriginaloutPutVideoPath = "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Environment.DIRECTORY_DOWNLOADS}${File.separator}output_video_can_play.mp4"
            val combinedVideoPath = "${Environment.getExternalStorageDirectory().absolutePath}${File.separator}${Environment.DIRECTORY_DOWNLOADS}${File.separator}combined_video.mp4"
            val videoExtractor = MediaExtractor().also {
                it.setDataSource(OriginaloutPutVideoPath)
            }
            var videoTrackIndex = -1;
            var videoFormat: MediaFormat? = null
            val trackCount = videoExtractor.trackCount
            for (i in 0 until trackCount) {
                videoFormat = videoExtractor.getTrackFormat(i)
                val mimeType = videoFormat.getString(MediaFormat.KEY_MIME)
                //视频信道
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i
                    break
                }
            }
            // begin of audio

            val audioExtractor = MediaExtractor().apply { setDataSource(OriginaloutPutAudioPath) }
            var audioTrackIndex = -1;
            val audiotrackCount = videoExtractor.trackCount
            var audioFormat: MediaFormat? = null
            for (i in 0 until audiotrackCount) {
                audioFormat = audioExtractor.getTrackFormat(i)
                val mimeType = audioFormat.getString(MediaFormat.KEY_MIME)
                //视频信道
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i
                    break
                }
            }

            // end of audio
            videoExtractor.selectTrack(videoTrackIndex)
            audioExtractor.selectTrack(audioTrackIndex)

            val videoBufferInfo = MediaCodec.BufferInfo()
            val audioBufferInfo = MediaCodec.BufferInfo()

            val mediaMuxer = MediaMuxer(combinedVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat!!)
            val writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat!!)
            mediaMuxer.start()

            val byteBuffer = ByteBuffer.allocate(1024 * 1024)
            var VideosampleTime = 0L

            videoExtractor.readSampleData(byteBuffer, 0)
            if (videoExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC)
                videoExtractor.advance()
            videoExtractor.readSampleData(byteBuffer, 0)
            val secondTime = videoExtractor.sampleTime
            videoExtractor.advance()
            val thirdTime = videoExtractor.sampleTime
            VideosampleTime = Math.abs(thirdTime - secondTime)


            val byteBuffer2 = ByteBuffer.allocate(1024 * 1024)
            var audioSampleTime = 0L
            audioExtractor.readSampleData(byteBuffer2, 0)
            if (audioExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC)
                audioExtractor.advance()
            audioExtractor.readSampleData(byteBuffer2, 0)
            val secondTimeAudio = audioExtractor.sampleTime
                audioExtractor.advance()
            val thirdTimeAudio = audioExtractor.sampleTime
            audioSampleTime = Math.abs(thirdTimeAudio - secondTimeAudio)



            videoExtractor.unselectTrack(videoTrackIndex)
            videoExtractor.selectTrack(videoTrackIndex)
            while (!stopSignal) {
                val readSampleSize = videoExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) {
                    break
                }
                Log.d("CodecMainActivity", "reading video  in progress ${(readSampleSize / 1024.0F)} KB buffer remain =${byteBuffer.remaining()/1024.0F}")
                videoBufferInfo.size = readSampleSize;
                videoBufferInfo.presentationTimeUs += VideosampleTime
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.sampleFlags;
                mediaMuxer.writeSampleData(writeVideoTrackIndex, byteBuffer, videoBufferInfo);
                videoExtractor.advance()
            }
            Log.e("CodecMainActivity", "joining video completed!")

            audioExtractor.selectTrack(audioTrackIndex)

            while (!stopSignal) {
                val readSampleSize = audioExtractor.readSampleData(byteBuffer2, 0)
                if (readSampleSize < 0) {
                    break
                }
                Log.d("CodecMainActivity", "reading audio  in progress ${readSampleSize / 1024.0F} KB,  buffer2 remain =${byteBuffer2.remaining()/1024.0F}")
                audioBufferInfo.size = readSampleSize;
                audioBufferInfo.presentationTimeUs += audioSampleTime
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = videoExtractor.sampleFlags;
                mediaMuxer.writeSampleData(writeAudioTrackIndex, byteBuffer2, audioBufferInfo)
                audioExtractor.advance()
            }
            Log.e("CodecMainActivity", "joining audio completed!")

            mediaMuxer.stop()
            mediaMuxer.release()
            videoExtractor.release()
            audioExtractor.release()
//            02-27 00:27:17.010 30933-31102/com.harris.androidMedia D/CodecMainActivity: reading audio  in progress 0.36328125 KB,  buffer2 remain =0.36328125
//            02-27 00:27:17.012 30933-31102/com.harris.androidMedia D/CodecMainActivity: reading audio  in progress 0.3623047 KB,  buffer2 remain =0.3623047
//            02-27 00:27:17.013 30933-31102/com.harris.androidMedia D/CodecMainActivity: reading audio  in progress 0.36328125 KB,  buffer2 remain =0.36328125
//            02-27 00:27:17.013 30933-31102/com.harris.androidMedia E/CodecMainActivity: joining audio completed!
//            02-27 00:27:17.014 30933-31117/com.harris.androidMedia I/MPEG4Writer: Received total/0-length (105980/0) buffers and encoded 105980 frames. - Audio
//            02-27 00:27:17.014 30933-31116/com.harris.androidMedia I/MPEG4Writer: Received total/0-length (73752/0) buffers and encoded 73752 frames. - Video
//            02-27 00:27:17.014 30933-31117/com.harris.androidMedia I/MPEG4Writer: Audio track drift time: 0 us
//            02-27 00:27:17.014 30933-31102/com.harris.androidMedia D/MPEG4Writer: Video track stopping
//            02-27 00:27:17.014 30933-31102/com.harris.androidMedia D/MPEG4Writer: Video track source stopping
//            02-27 00:27:17.014 30933-31102/com.harris.androidMedia D/MPEG4Writer: Video track source stopped
//            02-27 00:27:17.015 30933-31102/com.harris.androidMedia D/MPEG4Writer: Video track stopped
//            02-27 00:27:17.015 30933-31102/com.harris.androidMedia D/MPEG4Writer: Audio track stopping
//            02-27 00:27:17.015 30933-31102/com.harris.androidMedia D/MPEG4Writer: Audio track source stopping
//            02-27 00:27:17.015 30933-31102/com.harris.androidMedia D/MPEG4Writer: Audio track source stopped
//            02-27 00:27:17.015 30933-31102/com.harris.androidMedia D/MPEG4Writer: Audio track stopped
//            02-27 00:27:17.015 30933-31102/com.harris.androidMedia D/MPEG4Writer: Duration from tracks range is [2460809320, 4611613720] us
//            02-27 00:27:17.016 30933-31102/com.harris.androidMedia D/MPEG4Writer: Stopping writer thread
//            02-27 00:27:17.024 30933-31115/com.harris.androidMedia D/MPEG4Writer: 0 chunks are written in the last batch
//            02-27 00:27:17.025 30933-31102/com.harris.androidMedia D/MPEG4Writer: Writer thread stopped
//            02-27 00:27:17.094 30933-31102/com.harris.androidMedia I/MPEG4Writer: The mp4 file will not be streamable.
//            02-27 00:27:17.094 30933-31102/com.harris.androidMedia D/MPEG4Writer: Video track stopping
//            02-27 00:27:17.095 30933-31102/com.harris.androidMedia D/MPEG4Writer: Audio track stopping

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CodecMainActivity", "joining video error")
        } finally {

        }

    }

    override fun onPause() {
        stopSignal = true
        super.onPause()
    }

    override fun onResume() {
        stopSignal = false
        super.onResume()
    }


    @Volatile
    var stopSignal = false
}