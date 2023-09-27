#pragma once


#ifndef DROIDMEDIA_OPENSLESAUDIOPLAYER_H
#define DROIDMEDIA_OPENSLESAUDIOPLAYER_H

#ifdef __cplusplus
extern "C" {
#endif

#include <android/native_window_jni.h>
#include <unistd.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "../AndroidLog.h"

#ifdef __cplusplus
}
#endif

void * getQueueCallBack2(SLAndroidSimpleBufferQueueItf  slBufferQueueItf, void* context);




class openslesAudioPlayer {
public:

    int getPcm(void **pcm,size_t *pcm_size);

    void getQueueCallBack(SLAndroidSimpleBufferQueueItf  slBufferQueueItf, void* context);


    void createEngine();
    void createMixVolume();

    //释放资源
    void releaseResource();

    //创建播放器
    void createPlayer(const char* path);

    SLObjectItf engineObject=NULL;//用SLObjectItf声明引擎接口对象
    SLEngineItf engineEngine = NULL;//声明具体的引擎对象


    SLObjectItf outputMixObject = NULL;//用SLObjectItf创建混音器接口对象
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;////具体的混音器对象实例
    SLEnvironmentalReverbSettings settings = SL_I3DL2_ENVIRONMENT_PRESET_DEFAULT;//默认情况


    SLObjectItf audioplayer=NULL;//用SLObjectItf声明播放器接口对象
    SLPlayItf  slPlayItf=NULL;//播放器接口
    SLAndroidSimpleBufferQueueItf  slBufferQueueItf=NULL;//缓冲区队列接口


    size_t buffersize =0;
    void *buffer;

private:
    uint8_t *out_buffer;
    int out_channer_nb;
    int audio_stream_idx=-1;

};
extern openslesAudioPlayer openslAudioPlayer; // c++的全局变量设置方式，先在.h文件里面 声明，加一个extern，然后再在cpp文件里面不加extern再写一遍（这个叫定义）



#endif //DROIDMEDIA_OPENSLESAUDIOPLAYER_H
