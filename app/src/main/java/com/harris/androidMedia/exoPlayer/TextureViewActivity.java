package com.harris.androidMedia.exoPlayer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityTextureViewPlayerBinding;

/**
 * Created by Harris on 2017/2/19.
 */

public class TextureViewActivity extends AppCompatActivity {
    public static final String TAG = TextureViewActivity.class.getSimpleName();

    ActivityTextureViewPlayerBinding binding;

    private Handler mHandler;
    private SimpleExoPlayer player;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_texture_view_player);

    }


}
