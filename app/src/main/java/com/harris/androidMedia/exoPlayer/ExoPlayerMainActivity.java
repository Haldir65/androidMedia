package com.harris.androidMedia.exoPlayer;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityExoplayerBinding;
import com.harris.androidMedia.util.LogUtil;

import java.io.File;

import static com.harris.androidMedia.MainActivity.TRANSIT_FAB;

/**
 * Created by Harris on 2017/2/18.
 */

public class ExoPlayerMainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityExoplayerBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exoplayer);
        setSupportActionBar(binding.toolbar);

        binding.card1.setOnClickListener(this);
        binding.card2.setOnClickListener(this);
        binding.card3.setOnClickListener(this);
        ViewCompat.setTransitionName(binding.fab, TRANSIT_FAB);





    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.card1:
                intent = new Intent(this, SimpleExoPlayerViewActivity.class);
                String[] urlArray = new String[]{UriRepositories.Mp4uri};
                intent.putExtra(SimpleExoPlayerViewActivity.URI_LIST_EXTRA, urlArray);
                intent.setAction(SimpleExoPlayerViewActivity.ACTION_VIEW_LIST);
                break;
            case R.id.card2:
                intent = new Intent(this, SurfaceViewPlayerActivity.class);
               /* String[] urlArray = new String[]{uri};
                intent.putExtra(SimpleExoPlayerViewActivity.URI_LIST_EXTRA, urlArray);
                intent.setAction(SimpleExoPlayerViewActivity.ACTION_VIEW_LIST);*/
                break;
            case R.id.card3:
                intent = new Intent(this, TextureViewActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
