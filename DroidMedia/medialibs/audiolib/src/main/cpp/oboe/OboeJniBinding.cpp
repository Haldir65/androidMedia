#include "../AndroidLog.h"
#include <Oboe/Oboe.h>
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
                                                              jstring file_name,jint sample_rate) {

    mController=std::make_unique<PlayerController>(sample_rate);

    char* trackFileName = convertJString(env,file_name);
    mController->start(trackFileName);

//    oboeSinePlayer.startAudio();

}



extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_audiolib_oboe_OboeAudioPlayer_stopPlaying(JNIEnv *env, jobject thiz) {
    mController->stop();
//    oboeSinePlayer.stopAudio();

}