package com.harris.androidMedia.mediaPlayBack;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityLockScreenControlBinding;

/**
 * Created by Harris on 2017/3/25.
 */

public class LockScreenNotificationControl extends AppCompatActivity implements View.OnClickListener {
    ActivityLockScreenControlBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen_control);
        binding.button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            // do stuff
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(MediaPlayerService.ACTION_PLAY);
            startService(intent);
        }
    }
}
