//
// Created by me on 2023/6/25.
//

#include "AssetReader.h"

AssetReader::AssetReader() {
    LOGD("AssetReader constructor ");
}

AssetReader::~AssetReader() {
    LOGD("AssetReader destructor");
}

char *AssetReader::readAssets(JNIEnv *env, const char *name, jobject& assetmanager) {
    //得到AAssetManager对象指针
    AAssetManager *mManeger = AAssetManager_fromJava(env, assetmanager);
    //得到AAsset对象
    AAsset *assetFile = AAssetManager_open(mManeger, name,AASSET_MODE_BUFFER);//get file read AAsset

    //文件总长度
    size_t fileLength = AAsset_getLength(assetFile);

    // Allocate memory to read your file
    char* fileContent = new char[fileLength+1];
    AAsset_read(assetFile, fileContent, fileLength);
    fileContent[fileLength] = '\0';
    AAsset_close(assetFile);

    return fileContent;
}


