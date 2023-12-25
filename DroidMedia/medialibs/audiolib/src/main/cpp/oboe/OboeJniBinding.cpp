#include "../AndroidLog.h"
#include <oboe/Oboe.h>
#include "audio/Player.h"
#include "audio/PlayerController.h"
#include "OboeSinePlayer.h"
#include <jni.h>
#include <android/asset_manager_jni.h>


char* convertJString(JNIEnv* env, jstring str)
{
    if ( !str ) std::string();

    const jsize len = env->GetStringUTFLength(str);
    const char* strChars = env->GetStringUTFChars(str, (jboolean *)0);

    return const_cast<char *>(strChars);
}

std::unique_ptr<PlayerController> mController;
OboeSinePlayer oboeSinePlayer;

extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_audiolib_oboe_OboeAudioPlayer_startPlaying(JNIEnv *env, jobject thiz,
                                                              jstring file_name,
                                                              jobject assetManager,jint sample_rate) {
    AAssetManager  *m = AAssetManager_fromJava(env,assetManager);
    mController=std::make_unique<PlayerController>(sample_rate, m);
    char* trackFileName = convertJString(env,file_name);
    mController->start(trackFileName);
    if (trackFileName!= nullptr){
        env->ReleaseStringUTFChars(file_name, trackFileName);
    }

//    oboeSinePlayer.startAudio();

}



extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_audiolib_oboe_OboeAudioPlayer_stopPlaying(JNIEnv *env, jobject thiz) {
    mController->stop();
    delete mController.get();
//    oboeSinePlayer.stopAudio();

}
