package com.harris.androidMedia.camera2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.harris.androidMedia.R;
import com.harris.androidMedia.camera2.album.AlbumActivity;
import com.harris.androidMedia.camera2.basic.project.Camera2Activity;
import com.harris.androidMedia.camera2.raw.Camera2RawActivity;
import com.harris.androidMedia.camera2.video.Camera2VideoActivity;

/**
 * Created by Fermi on 2017/3/31.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2MainActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    CardView card1;
    CardView card2;
    CardView card3;
    CardView card4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2_mian);
        toolbar = findViewById(R.id.toolbar);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
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
