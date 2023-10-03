package com.me.harris.audiolib.audioPlayer

import com.me.harris.audiolib.audioPlayer.interfaces.AudioPlayInterface

class ADOpenSLES private constructor(): AudioPlayInterface {

    companion object {
        init {
            System.loadLibrary("myaudio")
        }
    }

    private var mPath: String? = null
    private var mSample_rate = 0
    private var mCh_layout = 0
    private var mFormat = 0


    /** 播放手机中的音频PCM文件
     * path:PCM文件路径
     * sample_rate:采样率 取值8000-48000
     * ch_layout:声道类型 0 单声道 1双声道
     * format:采样格式 0 8bit位宽 1 16bit位宽 2 32bit位宽
     * 采样率 取值8000-48000 ch_layout
     */
    constructor(path: String, sample_rate: Int, ch_layout: Int, format: Int):this(){
        mPath = path
        mSample_rate = sample_rate
        mCh_layout = ch_layout
        mFormat = format
    }


    private var mThread: Thread? = null



    override fun play() {
        if (mThread == null) {
            mThread = Thread { playAudio(mPath, mSample_rate, mCh_layout, mFormat) }
            mThread!!.start()
        }
    }

    override fun stop() {
        stopAudio()
        if (mThread != null) {
            try {
                mThread!!.join() // 等待线程结束
            } catch (io: InterruptedException) {
            }
            mThread = null
        }
    }

    /** 播放手机中的音频PCM文件
     * path:PCM文件路径
     * sample_rate:采样率 取值8000-48000
     * ch_layout:声道类型 0 单声道 1双声道
     * format:采样格式 0 8bit位宽 1 16bit位宽 2 32bit位宽
     * 采样率 取值8000-48000 ch_layout
     */
    external fun playAudio(path: String?, sample_rate: Int, ch_layout: Int, format: Int)
    external fun stopAudio()
}