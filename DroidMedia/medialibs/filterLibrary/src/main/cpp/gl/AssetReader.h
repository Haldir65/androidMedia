//
// Created by me on 2023/6/25.
//

#ifndef DROIDMEDIA_ASSETREADER_H
#define DROIDMEDIA_ASSETREADER_H

#include <android/bitmap.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <unistd.h>
#include "GDog.h"

class AssetReader {
public:
    AssetReader();

    virtual ~AssetReader();

    char *readAssets(JNIEnv *env,const char *name, jobject& assetmanager);
};

#endif //DROIDMEDIA_ASSETREADER_H
