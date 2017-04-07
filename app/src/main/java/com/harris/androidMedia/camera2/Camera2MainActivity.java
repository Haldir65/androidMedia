package com.harris.androidMedia.camera2;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.harris.androidMedia.R;
import com.harris.androidMedia.camera2.album.AlbumActivity;
import com.harris.androidMedia.camera2.basic.project.Camera2Activity;
import com.harris.androidMedia.camera2.raw.Camera2RawActivity;
import com.harris.androidMedia.camera2.video.Camera2VideoActivity;
import com.harris.androidMedia.databinding.ActivityCamera2MianBinding;
import com.harris.androidMedia.util.ActionCallBack;

/**
 * Created by Fermi on 2017/3/31.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2MainActivity extends AppCompatActivity implements ActionCallBack {
    ActivityCamera2MianBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera2_mian);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.setCallback(this);
    }

    @Override
    public void onClickView(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.card1:
                intent = new Intent(this, Camera2Activity.class);
                break;
            case R.id.card2:
                intent = new Intent(this, Camera2VideoActivity.class);
                break;
            case R.id.card3:
                intent = new Intent(this, Camera2RawActivity.class);
                break;
            case R.id.card4:
                intent = new Intent(this, AlbumActivity.class);
                break;
            default:
                break;

        }
        if (intent != null) {
            startActivityForResult(intent, -1);
        }
    }
}
