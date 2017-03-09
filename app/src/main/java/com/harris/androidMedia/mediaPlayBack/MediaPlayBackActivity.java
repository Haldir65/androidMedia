package com.harris.androidMedia.mediaPlayBack;

import android.content.ComponentName;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.harris.androidMedia.MainActivity;
import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityMediaPlaybackBinding;

import static com.harris.androidMedia.MainActivity.TRANSIT_FAB;

/**
 * Created by Harris on 2017/2/18.
 */

public class MediaPlayBackActivity extends AppCompatActivity {
    ActivityMediaPlaybackBinding binding;

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private MediaBrowserCompat mMediaBrowser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_playback);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setTransitionName(binding.fab, TRANSIT_FAB);
        initializePlayer();
    }

     void initializePlayer() {
        // The usual setContentView, etc
        // Now create the MediaBrowserCompat
        mMediaBrowser = new MediaBrowserCompat(
                this, // a Context
                new ComponentName(this, MediaPlaybackService.class),
                // Which MediaBrowserService
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        try {
                            // Ah, here’s our Token again
                            MediaSessionCompat.Token token =
                                    mMediaBrowser.getSessionToken();
                            // This is what gives us access to everything
                            MediaControllerCompat controller =
                                    new MediaControllerCompat(MediaPlayBackActivity.this, token);
                            // Convenience method of FragmentActivity to allow you to use
                            // getSupportMediaController() anywhere
                            MediaControllerCompat.setMediaController(MediaPlayBackActivity.this, controller);
                        } catch (RemoteException e) {
                            Log.e(MainActivity.class.getSimpleName(),
                                    "Error creating controller", e);
                        }
                    }
                    @Override
                    public void onConnectionSuspended() {
                        // We were connected, but no longer :-(
                    }
                    @Override
                    public void onConnectionFailed() {
                        // The attempt to connect failed completely.
                        // Check the ComponentName!
                    }
                },
                null); // optional Bundle
        mMediaBrowser.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaBrowser.disconnect();
    }
}
