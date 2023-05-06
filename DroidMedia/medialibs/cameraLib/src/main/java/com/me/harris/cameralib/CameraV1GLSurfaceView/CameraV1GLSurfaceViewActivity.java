package com.me.harris.cameralib.CameraV1GLSurfaceView;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.me.harris.cameralib.CameraV1;


public class CameraV1GLSurfaceViewActivity extends Activity {
    private CameraV1GLSurfaceView mGLSurfaceView;
    private int mCameraId;
    private CameraV1 mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        doInit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            doInit();
        }
    }

    private void doInit(){
        mGLSurfaceView = new CameraV1GLSurfaceView(this);
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = new CameraV1(this);
        DisplayMetrics dm = new DisplayMetrics();
        if (!mCamera.openCamera(dm.widthPixels, dm.heightPixels, mCameraId)) {
            return;
        }
        mGLSurfaceView.init(mCamera, false, CameraV1GLSurfaceViewActivity.this);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGLSurfaceView != null) {
            mGLSurfaceView.onPause();
            mGLSurfaceView.deinit();
            mGLSurfaceView = null;
        }

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.releaseCamera();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
