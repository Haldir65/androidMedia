//
// Created by Harris on 2023/9/26.
//

#include "openslesAudioPlayer.h"
#include <cstdlib>

void openslesAudioPlayer::createPlayer(const char *path) {

    int channels = 2;
    int rate = 44000;

    out_buffer = (uint8_t *) malloc(rate * channels);
    SLDataLocator_AndroidBufferQueue android_queue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,2};
    /**
    typedef struct SLDataFormat_PCM_ {
        SLuint32 		formatType;  pcm
        SLuint32 		numChannels;  通道数
        SLuint32 		samplesPerSec;  采样率
        SLuint32 		bitsPerSample;  采样位数
        SLuint32 		containerSize;  包含位数
        SLuint32 		channelMask;     立体声
        SLuint32		endianness;    end标志位
    } SLDataFormat_PCM;
     */
    SLDataFormat_PCM pcm = {SL_DATAFORMAT_PCM,static_cast<SLuint32>(channels),static_cast<SLuint32>(rate*1000)
            ,SL_PCMSAMPLEFORMAT_FIXED_16
            ,SL_PCMSAMPLEFORMAT_FIXED_16
            ,SL_SPEAKER_FRONT_LEFT|SL_SPEAKER_FRONT_RIGHT,SL_BYTEORDER_LITTLEENDIAN};

    /*
     * typedef struct SLDataSource_ {
            void *pLocator;//缓冲区队列
            void *pFormat;//数据样式,配置信息
        } SLDataSource;
     * */
    SLDataSource dataSource = {&android_queue,&pcm};


    SLDataLocator_OutputMix slDataLocator_outputMix={SL_DATALOCATOR_OUTPUTMIX,outputMixObject};


    SLDataSink slDataSink = {&slDataLocator_outputMix,NULL};


    const SLInterfaceID ids[3]={SL_IID_BUFFERQUEUE,SL_IID_EFFECTSEND,SL_IID_VOLUME};
    const SLboolean req[3]={SL_BOOLEAN_FALSE,SL_BOOLEAN_FALSE,SL_BOOLEAN_FALSE};

    /*
     * SLresult (*CreateAudioPlayer) (
        SLEngineItf self,
        SLObjectItf * pPlayer,
        SLDataSource *pAudioSrc,//数据设置
        SLDataSink *pAudioSnk,//关联混音器
        SLuint32 numInterfaces,
        const SLInterfaceID * pInterfaceIds,
        const SLboolean * pInterfaceRequired
    );
     * */
    ALOGE("执行到此处");
   (*engineEngine)->CreateAudioPlayer(engineEngine,&audioplayer,&dataSource,&slDataSink,3,ids,req);
    (*audioplayer)->Realize(audioplayer,SL_BOOLEAN_FALSE);
    ALOGE("执行到此处2");
    (*audioplayer)->GetInterface(audioplayer,SL_IID_PLAY,&slPlayItf);//初始化播放器
    //注册缓冲区,通过缓冲区里面 的数据进行播放
    (*audioplayer)->GetInterface(audioplayer,SL_IID_BUFFERQUEUE,&slBufferQueueItf);

    //设置回调接口
    (*slBufferQueueItf)->RegisterCallback(slBufferQueueItf,
                                          reinterpret_cast<slAndroidSimpleBufferQueueCallback>(getQueueCallBack2),
                                          nullptr);
    //播放
    (*slPlayItf)->SetPlayState(slPlayItf,SL_PLAYSTATE_PLAYING);

    //开始播放
    getQueueCallBack(slBufferQueueItf, nullptr);
}

int openslesAudioPlayer::getPcm(void **pcm, size_t *pcm_size) {
    while (1){
        //
        *pcm = out_buffer;
        *pcm_size = 0;
    }
    return 0;
}

void openslesAudioPlayer::createEngine() {
    slCreateEngine(&engineObject,0,NULL,0,NULL,NULL);//创建引擎
    (*engineObject)->Realize(engineObject,SL_BOOLEAN_FALSE);//实现engineObject接口对象
    (*engineObject)->GetInterface(engineObject,SL_IID_ENGINE,&engineEngine);//通过引擎调用接口初始化SLEngineItf
}

void openslesAudioPlayer::getQueueCallBack(SLAndroidSimpleBufferQueueItf slBufferQueueItf, void *context) {

    buffersize=0;
    getPcm(&buffer,&buffersize);
    if(buffer!=nullptr&&buffersize!=0){
        //将得到的数据加入到队列中
        (*slBufferQueueItf)->Enqueue(slBufferQueueItf,buffer,buffersize);
    }
}

void openslesAudioPlayer::createMixVolume() {
    (*engineEngine)->CreateOutputMix(engineEngine,&outputMixObject,0,0,0);//用引擎对象创建混音器接口对象
    (*outputMixObject)->Realize(outputMixObject,SL_BOOLEAN_FALSE);//实现混音器接口对象
    SLresult   sLresult = (*outputMixObject)->GetInterface(outputMixObject,SL_IID_ENVIRONMENTALREVERB,&outputMixEnvironmentalReverb);//利用混音器实例对象接口初始化具体的混音器对象
    //设置
    if (SL_RESULT_SUCCESS == sLresult) {
        (*outputMixEnvironmentalReverb)->
                SetEnvironmentalReverbProperties(outputMixEnvironmentalReverb, &settings);
    }
}

void openslesAudioPlayer::releaseResource() {
    //释放资源
    if(audioplayer!=nullptr){
        (*audioplayer)->Destroy(audioplayer);
        audioplayer=nullptr;
        slBufferQueueItf=nullptr;
        slPlayItf=nullptr;
    }
    if(outputMixObject!=nullptr){
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject=nullptr;
        outputMixEnvironmentalReverb=nullptr;
    }
    if(engineObject!=nullptr){
        (*engineObject)->Destroy(engineObject);
        engineObject=nullptr;
        engineEngine=nullptr;
    }
    if (out_buffer!= nullptr){
        free(out_buffer);
        out_buffer = nullptr;
    }
}

openslesAudioPlayer openslAudioPlayer;


void *getQueueCallBack2(SLAndroidSimpleBufferQueueItf slBufferQueueItf, void *context) {
    openslAudioPlayer.getQueueCallBack(slBufferQueueItf,context);
    return nullptr;
}
