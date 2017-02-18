package com.harris.androidMedia.camera2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.harris.androidMedia.R;
import com.harris.androidMedia.camera2.subManagers.CameraHolder;
import com.harris.androidMedia.camera2.tasks.ThreadManager;
import com.harris.androidMedia.camera2.widget.AutoFitTextureView;

public class Camera2Activity extends AppCompatActivity implements View.OnClickListener {


    private AutoFitTextureView mTextureView;

    ThreadManager threadManager;
    CameraHolder cameraHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        mTextureView = (AutoFitTextureView) findViewById(R.id.texture);
        findViewById(R.id.picture).setOnClickListener(this);
        findViewById(R.id.album).setOnClickListener(this);
        threadManager = new ThreadManager();
        threadManager.startBackgroundThread();
        cameraHolder = new CameraHolder(threadManager,this);
        if (mTextureView.isAvailable()) {
            //openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            cameraHolder.openCamera(mTextureView.getWidth(), mTextureView.getHeight(),this,mTextureView);
        } else {
            mTextureView.setSurfaceTextureListener(cameraHolder);
        }
    }

    @Override
    protected void onPause() {
        cameraHolder.closeCamera();
        threadManager.stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picture:
                takePicture();
                break;
            case R.id.album:
                Toast.makeText(Camera2Activity.this, "跳转至文件浏览器", Toast.LENGTH_SHORT).show();
               /* String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Happy/camera2";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Uri myUri = Uri.parse(folderPath);
                intent.setDataAndType(myUri , "file/jpg");
                startActivity(Intent.createChooser(intent,"Open folder"));*/
                break;

        }
    }

    private void takePicture() {
        cameraHolder.lockFocus();
    }

    public AutoFitTextureView getmTextureView() {
        return mTextureView;
    }


}
