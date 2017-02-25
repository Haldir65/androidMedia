package com.harris.androidMedia.exoPlayer.customize;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Harris on 2017/2/25.
 */

public class CustomExoPlayerView extends FrameLayout {
    public CustomExoPlayerView(@NonNull Context context) {
        super(context);
    }

    public CustomExoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomExoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
