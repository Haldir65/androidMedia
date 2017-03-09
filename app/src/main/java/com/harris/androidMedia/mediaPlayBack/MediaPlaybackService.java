package com.harris.androidMedia.mediaPlayBack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.harris.androidMedia.R;

import java.util.List;

/**
 * Created by Harris on 2017/3/9.
 */

public class MediaPlaybackService extends MediaBrowserServiceCompat{

    private MediaSessionCompat mMediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        // Create your MediaSessionCompat.
        // You should already be doing this
        mMediaSession = new MediaSessionCompat(this,
                MediaPlaybackService.class.getSimpleName());
        // Make sure to configure your MediaSessionCompat as per
        // https://www.youtube.com/watch?v=FBC1FgWe5X4
        setSessionToken(mMediaSession.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // Returning null == no one can connect
        // so we’ll return something
        return new BrowserRoot(
                getString(R.string.app_name), // Name visible in Android Auto
                null); // Bundle of optional extras
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        // I promise we’ll get to browsing
        result.sendResult(null);
    }
}
