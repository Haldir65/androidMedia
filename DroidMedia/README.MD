[使用MediaCodec实现一个VideoPlayer](https://github.com/JavaNoober/MedioDecode)


[闲谈，喷MediaCodec的坑的一篇文章](https://ragnraok.github.io/android_video_record.html)



```
   @SuppressLint("WrongConstant")
    @JvmStatic
    fun extractAudio(videoPath: String, audioPath: String, callback: ((result: Boolean, msg: String) -> Unit)) {
        BLog.d(TAG, "extractAudio audioPath:$audioPath")
        BLog.d(TAG, "extractAudio videoPath:$videoPath")
        val videoFile = File(videoPath)
        val audioFile = File(audioPath)
        if (!videoFile.exists()) {
            callback.invoke(false, "视频文件不存在")
            return
        }
        //删除旧的音频
        audioFile.deleteOnExit()
        val mediaExtractor = MediaExtractor()
        try {
            mediaExtractor.setDataSource(videoFile.absolutePath)
        } catch (e: Exception) {
            mediaExtractor.release()
            callback.invoke(false, "setDataSource error")
        }
        var muxer: MediaMuxer? = null
        var sampleRate = 0
        try {
            muxer = MediaMuxer(audioPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            var audioTrackIndex = -1
            for (i in 0 until mediaExtractor.trackCount) {
                val format = mediaExtractor.getTrackFormat(i)
                //只需音轨
                if (format.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true) {
                    audioTrackIndex = muxer.addTrack(format)
                    mediaExtractor.selectTrack(i)
                    sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                }
            }
            if (audioTrackIndex == -1) {
                callback.invoke(false, "视频不存在音轨")
                return
            }
            val bufferSize = 512 * 1024
            val offset = 0
            val dstBuf = ByteBuffer.allocate(bufferSize)
            val bufferInfo = MediaCodec.BufferInfo()
            muxer.start()
            while (true) {
                bufferInfo.offset = offset
                bufferInfo.size = mediaExtractor.readSampleData(dstBuf, offset)
                if (bufferInfo.size < 0) {
                    mediaExtractor.unselectTrack(audioTrackIndex)
                    break
                } else {
                    bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
                    bufferInfo.flags = mediaExtractor.sampleFlags
                    muxer.writeSampleData(audioTrackIndex, dstBuf, bufferInfo)
                    mediaExtractor.advance()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.invoke(false, e.toString())
        } finally {
            try {
                muxer?.stop()
                muxer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaExtractor.release()
        }
        callback.invoke(true, sampleRate.toString())
    }
```


[MediaEditSDK](https://github.com/JeffMony/MediaEditSDK)
[VideoClipEditViewTest](https://github.com/shaopx/VideoClipEditViewTest)

export JAVA_HOME=`/usr/libexec/java_home -v 17`
export https_proxy="http://127.0.0.1:6666" && export http_proxy=$https_proxy

首先，开发者选项 ->  ​启动MIUI优化​​ ->关闭 （第一步过后授权管理 右上角才有设置按钮入口）

其次，​​设置​​​ -> ​​授权管理​​​ -> ​​右上角设置​​​ -> ​​USB安装管理、应用权限监控​​ ->两个都关闭



[FFMEPEG MEDIACODEC](https://glumes.com/post/ffmpeg/ffmpeg-call-android-mediacodec/)

[FFmpeg之硬解码](https://blog.51cto.com/u_13861442/5262317)

https://blog.51cto.com/u_13861442


https://github.com/inotify-tools/inotify-tools/blob/master/libinotifytools/src/example.c

https://github.com/fsnotify/fsnotify