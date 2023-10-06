#pragma once

#ifndef DROIDMEDIA_AAUDIOENGINE_H
#define DROIDMEDIA_AAUDIOENGINE_H


#include <aaudio/AAudio.h>
#include <assert.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <string>
#include "../AndroidLog.h"
#include <thread>
#include <mutex>
#include <queue>
#include <condition_variable>

namespace aaudiodemo {
    class AAudioEngine {
    public:
        AAudioEngine(const std::string &filePath, uint32_t sampleRate,
                     uint16_t channel, uint32_t format);

        virtual ~AAudioEngine();

        AAudioEngine(const AAudioEngine &) = delete;

        AAudioEngine &operator=(const AAudioEngine &) = delete;

    public:
        /**
         * 初始化播放引擎
         * @return 初始化结果
         */
        bool Init();

        /**
         * 开始播放
         */
        void Start();

        /**
         * 暂停播放
         */
        void Pause();

        /**
         * 停止播放
         */
        void Stop();

//        virtual bool isXRunCountSupported() const override;


#if AAUDIO_CALLBACK

        aaudio_data_callback_result_t dataCallback(AAudioStream *stream,
                                                   void *audioData,
                                                   int32_t numFrames);

        void errorCallback(AAudioStream *stream,
                           aaudio_result_t  __unused error);

#endif

    private:
        bool hasPlay{false};
        bool mValid{false};
        int32_t mSampleRate{44100};
        int16_t mChannel{2};
        aaudio_format_t mFormat{AAUDIO_FORMAT_PCM_I16};
        ///pcm file path
        AAudioStream *mAudioStream{nullptr};
//        AAsset *mAsset{nullptr};
        FILE *fp{nullptr};
        std::mutex mRestartingLock{};

        std::deque<uint8_t> buffer; // 缓冲区
        std::mutex mtx; // 缓冲区互斥锁
        std::condition_variable cond; // 条件变量
//        std::unique_ptr<std::thread> readerThread;


        const int BUFFER_MAX = 1024*1000;//1MB？
#if !AAUDIO_CALLBACK
        uint8_t *mBufferData{nullptr};
        std::condition_variable mPlayCV;
        std::mutex mPlayMutex;
        std::thread mPlayThread;
#endif
    private:
#if !AAUDIO_CALLBACK

        void workFunc();

#endif

        AAudioStreamBuilder *createStreamBuilder();

        bool createPlaybackStream();

        void setupPlaybackStreamParameters(AAudioStreamBuilder *builder);

        void restartStream();

        void destroy();


        void startFileReaderThread();

    };

}


#endif //DROIDMEDIA_AAUDIOENGINE_H
