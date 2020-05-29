package com.harris.androidMedia.exoPlayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
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
import com.harris.androidMedia.util.Constants;
import com.harris.androidMedia.util.ToastUtil;
import com.harris.androidMedia.util.Utils;

import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Harris on 2017/2/19.
 */

public class SurfaceViewPlayerActivity extends AppCompatActivity {
    public static final String TAG = SurfaceViewPlayerActivity.class.getSimpleName();
    private SimpleExoPlayer player;
    private Handler mHandler;
    private Uri resourceUri;
    private List<String> fileList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_player);
//        resourceUri = Uri.parse(UriRepositories.flvString);
        checkPermissions();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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

    public static final int REQUEST_READ_EXTERNAL_STORAGE = 10;

    @TargetApi(23)
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                show_EXTERNAL_STORAGE_PermissionRequestRationale();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            //已经拥有permission
            fileList = Utils.getVideoFileAbsolutePathList();
            if (fileList != null && fileList.size() > 0) {
                for (int i = 0; i < fileList.size(); i++) {
//                        resourceUri = Uri.fromFile(new File(fileList.get(0)));
                    resourceUri = Uri.parse(Constants.Mp4uri);
                }
            }
        }
    }

    private void show_EXTERNAL_STORAGE_PermissionRequestRationale() {
        //do something tell the user why you need this permission
        ToastUtil.showTextLong(this, "Please Grant permission!!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            // Empty Permission and result array means a cancellation
            if (grantResults.length == 0) {
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something with this permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fileList = Utils.getVideoFileAbsolutePathList();
                if (fileList != null && fileList.size() > 0) {
                    if ((Util.SDK_INT <= 23 || player == null)) {
                        initializePlayer();
                    }
                }
            }
        }
    }

    private void initializePlayer() {
        // 1. Create a default TrackSelector
        mHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
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
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "com.haris.me.androidMedia"), defaultBandWidthMeter);
// Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(resourceUri,
                dataSourceFactory, extractorsFactory, null, null);
        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
// Prepare the player with the source.
        player.prepare(loopingSource);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
