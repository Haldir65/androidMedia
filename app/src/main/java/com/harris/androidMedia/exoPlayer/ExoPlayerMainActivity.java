package com.harris.androidMedia.exoPlayer;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityExoplayerBinding;
import com.harris.androidMedia.exoPlayer.customize.ChooseLocalVideoActivity;
import com.harris.androidMedia.util.Constants;

import static com.harris.androidMedia.MainActivity.TRANSIT_FAB;

/**
 * Created by Harris on 2017/2/18.
 */

public class ExoPlayerMainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    CardView card1;
    CardView card2;
    CardView card3;
    CardView card4;
    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);
        toolbar = findViewById(R.id.toolbar);
        toolbar = findViewById(R.id.toolbar);
        toolbar = findViewById(R.id.toolbar);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);
        ViewCompat.setTransitionName(fab, TRANSIT_FAB);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.card1:
                intent = new Intent(this, SimpleExoPlayerViewActivity.class);
                String[] urlArray = new String[]{Constants.Mp4uri};
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
            case R.id.card4:
                intent = new Intent(this, ChooseLocalVideoActivity.class);
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
