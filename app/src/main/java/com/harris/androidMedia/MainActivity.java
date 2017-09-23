package com.harris.androidMedia;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.harris.androidMedia.album.AlbumMainActivity;
import com.harris.androidMedia.camera2.Camera2MainActivity;
import com.harris.androidMedia.databinding.ActivityMainBinding;
import com.harris.androidMedia.exoPlayer.ExoPlayerMainActivity;
import com.harris.androidMedia.ijkPlayers.IjkPlayerDemoActivity;
import com.harris.androidMedia.mediaPlayBack.LockScreenNotificationControl;
import com.harris.androidMedia.mediaPlayBack.MediaPlayBackActivity;
import com.harris.androidMedia.util.ActionCallBack;
import com.harris.androidMedia.util.ToastUtil;

/**
 * Created by Harris on 2017/2/18.
 */

public class MainActivity extends AppCompatActivity implements ActionCallBack {
    public static final String TRANSIT_FAB = "transit_fab";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        binding.setCallback(this);
    }

    @Override
    public void onClickView(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.card1:
                intent = new Intent(this, Camera2MainActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                } else {
                    ToastUtil.showTextShort(this, "Current Sdk minus Lollipop!");
                }
                break;
            case R.id.card2:
                intent = new Intent(this, ExoPlayerMainActivity.class);
                break;
            case R.id.card3:
                intent = new Intent(this, MediaPlayBackActivity.class);
                break;
            case R.id.card4:
                intent = new Intent(this, AlbumMainActivity.class);
                break;
            case R.id.fab:
                intent = new Intent(this, LockScreenNotificationControl.class);
                break;
            case R.id.card5:
                intent = new Intent(this, IjkPlayerDemoActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, binding.fab, TRANSIT_FAB);
            ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
        }
    }
}
