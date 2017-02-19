package com.harris.androidMedia.exoPlayer;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivitySurfaceViewPlayerBinding;

/**
 * Created by Harris on 2017/2/19.
 */

public class SurfaceViewPlayerActivity extends AppCompatActivity {
    public static final String TAG = SurfaceViewPlayerActivity.class.getSimpleName();
    ActivitySurfaceViewPlayerBinding binding;
    private SimpleExoPlayer player;
    private Handler mHandler;
    private Uri mp4VideoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_surface_view_player);
        mp4VideoUri = Uri.parse("http://odzl05jxx.bkt.clouddn.com/%E6%9D%8E%E5%BF%97&quot;%E7%9C%8B%E8%A7%81&quot;2015%E5%B7%A1%E6%BC%94%E9%A2%84%E5%91%8A%E7%89%87.mp4");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        // 1. Create a default TrackSelector
        mHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        // 3. Create the player
        player =
                ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        // Attaching the player to a view
        player.setVideoSurfaceView(binding.surfaceview);
        player.setPlayWhenReady(true);

// Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandWidthMeter = new DefaultBandwidthMeter();
// Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "com.haris.me.androidMedia"),defaultBandWidthMeter);
// Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
                dataSourceFactory, extractorsFactory, null, null);
// Prepare the player with the source.
        player.prepare(videoSource);

    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
