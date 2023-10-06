//
// Created by 43975 on 12/24/2021.
//
#include "../utils/logging.h"
#include "oboe/Oboe.h"
#include "AAssetDataSource.h"
#include <filesystem>
#include <fstream>
#ifdef __cplusplus
extern "C" {
#endif
#include <errno.h>
#include <stdio.h>
#include <string.h>

#ifdef __cplusplus
}
#endif



#if !defined(USE_FFMPEG)
#error USE_FFMPEG should be defined in app.gradle
#endif

#if USE_FFMPEF==1
    #include "FFMpegExtractor.h"
#else
    #include "NDKExtractor.h"

#endif


constexpr int kMaxCompressionRatio{12};

AAssetDataSource* AAssetDataSource::newFromCompressedAsset(std::string &filepath,
        const char *filename,
        const AudioProperties targetProperties) {

    // get the asset by filename via AAssetManager
//    AAsset *asset = AAssetManager_open(&assetManager, filename, AASSET_MODE_UNKNOWN);
    if (!std::filesystem::exists(filepath)) {
        LOGE("Failed to open asset %s",filename);
        return nullptr;
    }
    FILE* fp = fopen(filepath.c_str(), "r");
//    errno_t err = fopen_s(&fp, filepath, "r");
    // fopen_s is not part of C++'s standard library.
    // It is a MSVC-specific extension or optional part of the C standard library.
    fseek(fp,  0,  SEEK_END);
    long size = ftell(fp);
    fseek(fp,  0,  SEEK_SET);
//    off_t assetSize = AAsset_getLength(asset);
    LOGD("Opened %s, size ",filename);

    // Allocate memory to store decompressed audio. We don't know the exact
    // size of the decoded data until after decoding so we make an assumption about the
    // maximum compression ratio and the decoded sample format (float for FFmpeg, int16 for NDK)

#if USE_FFMPEG==true
    const long maximumDataSizeInBytes = kMaxCompressionRatio * assetSize * sizeof(float);
    auto decodedData = new uint8_t[maximumDataSizeInBytes];

    int64_t bytesDecoded = FFMpegExtractor::decode(asset, decodedData, targetProperties);
    auto numSamples = bytesDecoded / sizeof(float);

#else
    const long maximumDataSizeInBytes = kMaxCompressionRatio * size  * sizeof(int16_t);
    auto decodedData = new uint8_t[maximumDataSizeInBytes];

    int64_t bytesDecoded = NDKExtractor::decode(filepath,decodedData,targetProperties);
    auto numSamples = bytesDecoded / sizeof(int16_t);
#endif

    // Now we know the exact number of samples we can create a float array to hold the audio data
    auto outputBuffer  = std::make_unique<float[]>(numSamples);

#if USE_FFMPEF==1
    memcpy(outputBuffer.get(), decodedData, (size_t)bytesDecoded);
#else
    // The NDK decoder can only decode to int16, we need to convert to floats
    oboe::convertPcm16ToFloat(reinterpret_cast<int16_t*>(decodedData),outputBuffer.get(),
            bytesDecoded/sizeof(int16_t));
#endif

    delete [] decodedData;
//    AAsset_close(asset);
    fclose(fp);

    return new AAssetDataSource(std::move(outputBuffer), numSamples, targetProperties);

}

AAssetDataSource::~AAssetDataSource() {
    LOGE("AAssetDataSource DESTRUCTOR CALLED , should remove buffer now ");
}
