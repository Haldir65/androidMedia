package com.harris.androidMedia.mediaCodec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.harris.androidMedia.R;

public class CameraToMpegTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_to_mpeg);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new CameraToMpegTest().testEncodeCameraToMp4();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
