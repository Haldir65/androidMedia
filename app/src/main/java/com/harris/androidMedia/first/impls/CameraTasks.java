package com.harris.androidMedia.first.impls;

/**
 * Created by Harris on 2016/4/9.
 */
public interface CameraTasks {
    void takePicture();

    void setUpCameraOutputs(int width, int height);

    void createCameraPreviewSession();

    void lockFocus();

    void unlockFocus();

    void runPrecaptureSequence();

    void captureStillPicture();
}
