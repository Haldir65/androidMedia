package com.harris.androidMedia.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tinghan_Chang on 2016/2/2.
 */
public class CamBaseV2 {

    private static String TAG = SimpleCameraApp.TAG;
    private Activity mApp = null;
    private CameraDevice mCamera = null;
    private CameraManager mCameraManager = null;
    private CameraCharacteristics mCameraCharacteristics = null;
    private CameraCaptureSession mPreviewSession = null;
    private CaptureRequest.Builder mPreviewBuilder = null;
    private String[] mCameraId = null;
    private HandlerThread mCameraThread = null;
    private Handler mCameraHandler = null;
    private Surface mPreviewSurface = null;
    private boolean mIsPreviewing = false;
    private LinearLayout mRootView = null;
    private Size mPreviewSize = null;
    private PreviewGLSurfaceView mPreviewSurfaceView = null;
    private SurfaceTexture mPreviewSurfaceTexture = null;
    private boolean mIsFullDeviceHeight = false;

    public CamBaseV2(Activity app, LinearLayout rootView) {
        mApp = app;
        mRootView = rootView;
    }

    public void onActivityResume() {
        Log.e(TAG, "LifeCycle, onActivityResume");
        initCameraThread();
        openCamera();
    }

    private void initCameraThread() {
        Log.e(TAG, "init camera thread begin.");
        mCameraThread = new HandlerThread("Camera Handler Thread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
        Log.e(TAG, "nit camera thread done");
    }

    public void onActivityPause() {
        Log.e(TAG, "LifeCycle, onActivityPause");
        releaseCamera();
        releaseCameraThread();
        releaseSurfaceView();
        Log.e(TAG, "LifeCycle, onActivityPause done");
    }

    private void releaseCamera() {
        // release camera
        if (mPreviewSession != null) {
            try {
                mPreviewSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mPreviewSession.close();
            mPreviewSession = null;
            mIsPreviewing = false;
        }
        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
        }
    }

    private void releaseCameraThread() {
        if (mCameraThread != null) {
            mCameraThread.interrupt();
            mCameraThread = null;
        }
        if (mCameraHandler != null) {
            mCameraHandler = null;
        }
    }

    private void releaseSurfaceView() {
        if (mPreviewSurface != null) {
            mRootView.removeView(mPreviewSurfaceView);
            mPreviewSurfaceTexture = null;
            mPreviewSurface = null;
        }
    }

    private void openCamera() {
        mCameraManager = (CameraManager) mApp.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList();
            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId[0]);

            // Because camera2.0 only can control view size.
            // So we need to dynamic create view to fit sensor size.
            createSurfaceView(mRootView);
            Log.e(TAG, "camera open begin");
            mCameraManager.openCamera(mCameraId[0], mCameraDeviceStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void createSurfaceView(LinearLayout rootLayout) {
        LinearLayout.LayoutParams layoutParams = getPreviewLayoutParams();
        mPreviewSize = new Size(layoutParams.width, layoutParams.height);
        mPreviewSurfaceView = new PreviewGLSurfaceView(mApp, mPreviewSize);
        mPreviewSurfaceView.setLayoutParams(layoutParams);
        mPreviewSurfaceView.setSurfaceTextureListener(mSurfaceextureListener);
        rootLayout.addView(mPreviewSurfaceView);
    }

    private LinearLayout.LayoutParams getPreviewLayoutParams() {
        Point screenSize = new Point();
        mApp.getWindowManager().getDefaultDisplay().getSize(screenSize);
        Rect activeArea = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        int sensorOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int sensorWidth, sensorHeight, previewWidth, previewHeight;
        // Make sensor's orientation same as screen.
        switch (sensorOrientation) {
            case 90:
            case 180:
                sensorWidth = activeArea.height();
                sensorHeight = activeArea.width();
                break;
            case 270:
            case 0:
            default:
                sensorWidth = activeArea.width();
                sensorHeight = activeArea.height();
                break;
        }
        Log.i(TAG, "Sensor Orientation angle:" + sensorOrientation);
        Log.i(TAG, "Sensor Width/Height : " + sensorWidth + "/" + sensorHeight);
        Log.i(TAG, "Screen Width/Height : " + screenSize.x + "/" + screenSize.y);
        // Preview's View size must same as sensor ratio.
        if (mIsFullDeviceHeight) {
            // full device height, maybe 16:9 at phone
            previewWidth = screenSize.y * sensorWidth / sensorHeight;
            previewHeight = screenSize.y;
        } else {
            // full device width, maybe 4:3 at phone
            previewWidth = screenSize.x;
            previewHeight = screenSize.x * sensorHeight / sensorWidth;
        }
        // Set margin to center at screen.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(previewWidth, previewHeight);
        int widthMargin = (previewWidth - screenSize.x) / 2;
        int heightMargin = (previewHeight - screenSize.y) / 2;
        layoutParams.leftMargin = -widthMargin;
        layoutParams.topMargin = -heightMargin;
        return layoutParams;
    }

    private PreviewGLSurfaceView.SurfaceTextureListener mSurfaceextureListener = new PreviewGLSurfaceView.SurfaceTextureListener() {
        public void onSurfaceTextureAvailable(SurfaceTexture surface) {
            mPreviewSurfaceTexture = surface;
            mPreviewSurface = new Surface(mPreviewSurfaceTexture);
            startPreview();
        }
    };

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.e(TAG, "camera open done");
            mCamera = camera;
            startPreview();
        }

        @Override
        public void onClosed(CameraDevice camera) {
            mCamera = null;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };

    /**
     * Maybe need to sync Camera and SurfaceView.
     * Maybe need to create SurfaceView after get camera size.
     */
    private void startPreview() {
        Log.e(TAG, "Try start preview.");
        if (mCamera != null && mPreviewSurface != null && !mIsPreviewing) {
            mIsPreviewing = true;
            List<Surface> outputSurfaces = new ArrayList<Surface>(1);
            outputSurfaces.add(mPreviewSurface);
            try {
                Log.e(TAG, "createCaptureSession begin");
                mCamera.createCaptureSession(outputSurfaces, mPreviewSessionCallback, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if (mPreviewSurface == null) {
            Log.e(TAG, "mPreviewSurface is null");
        } else if (mCamera == null) {
            Log.e(TAG, "mCamera is null");
        } else if (mIsPreviewing) {
            Log.e(TAG, "mIsPreviewing");
        }
    }

    private CameraCaptureSession.StateCallback mPreviewSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.e(TAG, "createCaptureSession done");
            mPreviewSession = session;
            CaptureRequest.Builder previewBuilder = getPreviewBuilder();
            CaptureRequest request = previewBuilder.build();
            try {
                Log.e(TAG, "setRepeatingRequest begin");
                mPreviewSession.setRepeatingRequest(request, null, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };

    private CaptureRequest.Builder getPreviewBuilder() {
        try {
            mPreviewBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(mPreviewSurface);
        return mPreviewBuilder;
    }
}
