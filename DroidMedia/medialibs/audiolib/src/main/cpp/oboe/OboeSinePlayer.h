//
// Created by Jimmy on 2023/10/3.
//

#ifndef DROIDMEDIA_OBOESINEPLAYER_H
#define DROIDMEDIA_OBOESINEPLAYER_H


#include <oboe/Oboe.h>
#include <math.h>
using namespace oboe;

class OboeSinePlayer: public oboe::AudioStreamDataCallback {
public:

    virtual ~OboeSinePlayer() = default;

    // Call this from Activity onResume()
    int32_t startAudio() {
        std::lock_guard<std::mutex> lock(mLock);
        oboe::AudioStreamBuilder builder;
        // The builder set methods can be chained for convenience.
        Result result = builder.setSharingMode(oboe::SharingMode::Exclusive)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setChannelCount(kChannelCount)
                ->setSampleRate(kSampleRate)
                ->setSampleRateConversionQuality(oboe::SampleRateConversionQuality::Medium)
                ->setFormat(oboe::AudioFormat::Float)
                ->setDataCallback(this)
                ->openStream(mStream);
        if (result != Result::OK) return (int32_t) result;

        // Typically, start the stream after querying some stream information, as well as some input from the user
        result = mStream->requestStart();
        return (int32_t) result;
    }

    // Call this from Activity onPause()
    void stopAudio() {
        // Stop, close and delete in case not already closed.
        std::lock_guard<std::mutex> lock(mLock);
        if (mStream) {
            mStream->stop();
            mStream->close();
            mStream.reset();
        }
    }

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override {
        float *floatData = (float *) audioData;
        for (int i = 0; i < numFrames; ++i) {
            float sampleValue = kAmplitude * sinf(mPhase);
            for (int j = 0; j < kChannelCount; j++) {
                floatData[i * kChannelCount + j] = sampleValue;
            }
            mPhase += mPhaseIncrement;
            if (mPhase >= kTwoPi) mPhase -= kTwoPi;
        }
        return oboe::DataCallbackResult::Continue;
    }

private:
    std::mutex         mLock;
    std::shared_ptr<oboe::AudioStream> mStream;

    // Stream params
    static int constexpr kChannelCount = 2;
    static int constexpr kSampleRate = 48000;
    // Wave params, these could be instance variables in order to modify at runtime
    static float constexpr kAmplitude = 0.5f;
    static float constexpr kFrequency = 440;
    static float constexpr kPI = M_PI;
    static float constexpr kTwoPi = kPI * 2;
    static double constexpr mPhaseIncrement = kFrequency * kTwoPi / (double) kSampleRate;
    // Keeps track of where the wave is
    float mPhase = 0.0;
};


#endif //DROIDMEDIA_OBOESINEPLAYER_H
