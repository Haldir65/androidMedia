package com.cgfay.filterlibrary.ui;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import com.cgfay.filterlibrary.EGLRender;
import com.me.harris.filterlibrary.R;

public class NativeRenderMainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private SurfaceView mSurfaceView;

    private EGLRender mRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_render_activity_main);
        mRender = new EGLRender();
        mRender.init();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
    }


    @Override
    protected void onDestroy() {
        mRender.release();
        mRender = null;
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mRender.surfaceCreated(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mRender.surfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mRender.surfaceDestroyed();
    }
}
