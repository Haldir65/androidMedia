package com.me.harris.droidmedia.textureview

import android.media.*
import android.util.Log
import com.me.harris.droidmedia.video.AudioPlayer

class AudioDecoder:TypicalDecoder {

    companion object {
        const val TAG = "AudioDecoder"
    }

    @Volatile
    var mStop:Boolean = false
    override fun isStopped():Boolean = mStop


    override fun start(url: String) {
        decodeAndPlayAudio(url)
    }


    override fun stop() {
        mStop = true
    }


    private val DEFAULT_TIME_OUT = 10_000L



    private fun decodeAndPlayAudio(url:String){
        val info = MediaCodec.BufferInfo()
        val extractor = MediaExtractor()
        var decoder: MediaCodec? = null
        extractor.setDataSource(url)
        var audioTrack:AudioTrack? = null
        for(i in 0 until extractor.trackCount){
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if(mime?.startsWith("audio/")==true){
                val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                val bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT)
                extractor.selectTrack(i)
                decoder = MediaCodec.createDecoderByType(mime)
                decoder.configure(format,null,null,0)
                audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT,bufferSize,AudioTrack.MODE_STREAM)
                audioTrack.play()
                break
            }
        }
        if (decoder == null) throw IllegalStateException("unable to initiate AUDIO codec")
        var sawEOS = false
        val startMs = System.currentTimeMillis()
        decoder.start()
        while (!isStopped()){
            if (!sawEOS)
            {
                val inIndex = decoder.dequeueInputBuffer(DEFAULT_TIME_OUT)
                if (inIndex>=0){
                    val inputBuffer = decoder.getInputBuffer(inIndex)
                    val sampleSize = inputBuffer?.let { extractor.readSampleData(it,0) }?:-1
                    if (sampleSize<0){
                        decoder.queueInputBuffer(inIndex,0,0,0,MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        sawEOS = true
                    }else{
                        decoder.queueInputBuffer(inIndex,0,sampleSize,extractor.sampleTime,0)
                        extractor.advance()
                    }
                }
            }
            val outIndex = decoder.dequeueOutputBuffer(info,DEFAULT_TIME_OUT)
            when(outIndex){
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED ->{
                    val format = decoder.outputFormat
                    Log.d(TAG, "New format " + format)
//                    audioTrack?.playbackRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                }
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED ->{
                    Log.d(TAG,"INFO_OUTPUT_BUFFERS_CHANGED")
                }
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.d(TAG,"dequeueOutputBuffer timed out!")
                }
                else ->{
                    val outBuffer = decoder.getOutputBuffer(outIndex)
                    val chunk = ByteArray(info.size)
                    outBuffer?.get(chunk) // Read alll buffer at once
                    outBuffer?.clear() // MUST DO ! OtherWise next time you get this same buffer, bad things will happen!
                    audioTrack?.write(chunk,info.offset,info.size)
                    decoder.releaseOutputBuffer(outIndex,false)
                    sleepRender(info,startMs)
                }
            }
            if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0){
                stop()
                break
            }
        }
        try {
            decoder?.stop()
            decoder?.release()
            extractor?.release()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}