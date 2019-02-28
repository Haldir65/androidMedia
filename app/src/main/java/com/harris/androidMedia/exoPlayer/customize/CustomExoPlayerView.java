package com.harris.androidMedia.exoPlayer.customize;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.harris.androidMedia.R;
import com.harris.androidMedia.util.AudioUtil;
import com.harris.androidMedia.util.LogUtil;
import com.harris.androidMedia.util.Utils;

import java.util.List;


/**
 * Created by Harris on 2017/2/25.
 */

public class CustomExoPlayerView extends FrameLayout implements CustomPlaybackControlView.VisibilityListener, View.OnClickListener {

    private View surfaceView;
    private View shutterView;
    private SubtitleView subtitleLayout;
    private ImageView controlView;
    private AspectRatioFrameLayout layout;
    private CustomPlaybackControlView controller;
    private CustomExoPlayerView.ComponentListener componentListener;

    private SimpleExoPlayer player;
    private boolean useController = true;
    private int controllerShowTimeoutMs;
    int scaleTouchSlop;
    float currentX,currentY;
    float screenWidth,screenHeight;



    public CustomExoPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public CustomExoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomExoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        boolean useTextureView = false;
        int resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        int rewindMs = CustomPlaybackControlView.DEFAULT_REWIND_MS;
        int fastForwardMs = CustomPlaybackControlView.DEFAULT_FAST_FORWARD_MS;
        int controllerShowTimeoutMs = CustomPlaybackControlView.DEFAULT_SHOW_TIMEOUT_MS;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                   R.styleable.PlayerView, 0, 0);
            try {
                useController = a.getBoolean(R.styleable.PlayerView_use_controller, useController);
                /* useTextureView = a.getBoolean(R.styleable.SimpleExoPlayerView_use_texture_view,
                         useTextureView);*/
                resizeMode = a.getInt(R.styleable.PlayerView_resize_mode,
                        AspectRatioFrameLayout.RESIZE_MODE_FIT);
                rewindMs = a.getInt(R.styleable.PlayerView_rewind_increment, rewindMs);
                fastForwardMs = a.getInt(R.styleable.PlayerView_fastforward_increment,
                        fastForwardMs);
                controllerShowTimeoutMs = a.getInt(R.styleable.PlayerView_show_timeout,
                        controllerShowTimeoutMs);
            } finally {
                a.recycle();
            }
            LayoutInflater.from(context).inflate(R.layout.exo_simple_player_view, this);
            componentListener = new CustomExoPlayerView.ComponentListener();
            layout = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
            controlView = (ImageView) findViewById(R.id.play);
            layout.setResizeMode(resizeMode);
            shutterView = findViewById(R.id.shutter);
            subtitleLayout = (SubtitleView) findViewById(R.id.subtitles);
            subtitleLayout.setUserDefaultStyle();
            subtitleLayout.setUserDefaultTextSize();
            controller = (CustomPlaybackControlView) findViewById(R.id.control);
            controller.hide();
            controller.setRewindIncrementMs(rewindMs);
            controller.setFastForwardIncrementMs(fastForwardMs);
            this.controllerShowTimeoutMs = controllerShowTimeoutMs;
            controller.setVisibilityListener(this);
            controlView.setOnClickListener(this);
            View view = useTextureView ? new TextureView(context) : new SurfaceView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
            surfaceView = view;
            layout.addView(surfaceView, 0);
            scaleTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }
    }

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    /**
     * Set the {@link SimpleExoPlayer} to use. The {@link SimpleExoPlayer#setTextOutput} and
     * {@link SimpleExoPlayer#setVideoListener} method of the player will be called and previous
     * assignments are overridden.
     *
     * @param player The {@link SimpleExoPlayer} to use.
     */
    public void setPlayer(SimpleExoPlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.setTextOutput(null);
            this.player.setVideoListener(null);
            this.player.removeListener(componentListener);
            this.player.setVideoSurface(null);
        }
        this.player = player;
        if (useController) {
            controller.setPlayer(player);
        }
        if (player != null) {
            if (surfaceView instanceof TextureView) {
                player.setVideoTextureView((TextureView) surfaceView);
            } else if (surfaceView instanceof SurfaceView) {
                player.setVideoSurfaceView((SurfaceView) surfaceView);
            }
            player.setVideoListener(componentListener);
            player.addListener(componentListener);
            player.setTextOutput(componentListener);
            maybeShowController(false);
        } else {
            shutterView.setVisibility(VISIBLE);
            controller.hide();
        }
    }

    /**
     * Sets the resize mode which can be of value {@link AspectRatioFrameLayout#RESIZE_MODE_FIT},
     * {@link AspectRatioFrameLayout#RESIZE_MODE_FIXED_HEIGHT} or
     * {@link AspectRatioFrameLayout#RESIZE_MODE_FIXED_WIDTH}.
     *
     * @param resizeMode The resize mode.
     */
    public void setResizeMode(int resizeMode) {
        layout.setResizeMode(resizeMode);
    }

    /**
     * Returns whether the playback controls are enabled.
     */
    public boolean getUseController() {
        return useController;
    }

    /**
     * Sets whether playback controls are enabled. If set to {@code false} the playback controls are
     * never visible and are disconnected from the player.
     *
     * @param useController Whether playback controls should be enabled.
     */
    public void setUseController(boolean useController) {
        if (this.useController == useController) {
            return;
        }
        this.useController = useController;
        if (useController) {
            controller.setPlayer(player);
        } else {
            controller.hide();
            controller.setPlayer(null);
        }
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input and with playback or buffering in
     * progressbar.
     *
     * @return The timeout in milliseconds. A non-positive value will cause the controller to remain
     * visible indefinitely.
     */
    public int getControllerShowTimeoutMs() {
        return controllerShowTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input and with playback or buffering in progressbar.
     *
     * @param controllerShowTimeoutMs The timeout in milliseconds. A non-positive value will cause
     *                                the controller to remain visible indefinitely.
     */
    public void setControllerShowTimeoutMs(int controllerShowTimeoutMs) {
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
    }

    /**
     * Set the {@link CustomPlaybackControlView.VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void setControllerVisibilityListener(CustomPlaybackControlView.VisibilityListener listener) {
        controller.setVisibilityListener(listener);
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds.
     */
    public void setRewindIncrementMs(int rewindMs) {
        controller.setRewindIncrementMs(rewindMs);
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        controller.setFastForwardIncrementMs(fastForwardMs);
    }

    /**
     * Get the view onto which video is rendered. This is either a {@link SurfaceView} (default)
     * or a {@link TextureView} if the {@code use_texture_view} view attribute has been set to true.
     *
     * @return either a {@link SurfaceView} or a {@link TextureView}.
     */
    public View getVideoSurfaceView() {
        return surfaceView;
    }



    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (!useController || player == null) {
            return false;
        }
        maybeShowController(true);
        return true;
    }





    float currentVolumeY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (player != null) {
            /*int playbackState = player.getPlaybackState();
            boolean showIndefinitely = playbackState == ExoPlayer.STATE_IDLE
                    || playbackState == ExoPlayer.STATE_ENDED || !player.getPlayWhenReady();
            if (showIndefinitely) {
                controller.show();
            }*/
            Context context = getContext();
            if (screenWidth == 0) {
                screenWidth = Utils.getScreenWidth(context);
            }
            if (screenHeight == 0) {
                screenWidth = Utils.getScreenHeight(context);
            }

            ExoPlayer player = controller.getPlayer();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentX = ev.getX();
                    currentVolumeY = currentY = ev.getY();
                    if (!player.getPlayWhenReady()) {
                        controller.hide();
                        player.setPlayWhenReady(true);
                    } else {
                        controller.show();
                    }
                    controller.curPosition = player.getCurrentPosition();
                    return true; // critical , only in this way can we accept ongoing events
                case MotionEvent.ACTION_MOVE:
                    float x = ev.getX();
                    float y = ev.getY();
                    if (Math.abs(x - currentX) >= Math.abs(y - currentY)) {
                        if (x - currentX > 0) {
                            controller.fastFoward((long) ((x - currentX) * player.getDuration() / screenWidth));
                        } else {
                            controller.rewind((long) ((currentX - x) * player.getDuration() / screenWidth));
                        }
                        LogUtil.d("this is horizontal scroll"+(x - currentX));
                    } else {
                        // TODO: 2019/2/26 use simpleMexoplayer setVolume instead of framework api
                        LogUtil.d("this is vertical scroll"+(y - currentY));// y-currentY < 0 手指往上走
                        float VERTICAL_THRESHHOLD = screenHeight;
                        if (Math.abs(y - currentVolumeY) > 0) {
                            if (y < currentVolumeY) { //手指往上走，调大音量
                                int CurVolume = AudioUtil.getInstance(context).getMediaVolume();
                                int MaxVolume = AudioUtil.getInstance(context).getMediaMaxVolume();
                                if (CurVolume < MaxVolume) {
                                    AudioUtil.getInstance(context).setMediaVolume((CurVolume + 1) > MaxVolume ? MaxVolume : CurVolume + 1);
                                }
                                currentVolumeY+=VERTICAL_THRESHHOLD;
                                LogUtil.d("CurVolume " + CurVolume + " MaxV" + MaxVolume);
                            } else {
                                int CurVolume = AudioUtil.getInstance(context).getMediaVolume();
                                int MaxVolume = AudioUtil.getInstance(context).getMediaMaxVolume();
                                if (CurVolume > 0) {
                                    AudioUtil.getInstance(context).setMediaVolume((CurVolume - 1) > 0 ? CurVolume - 1 : 0);
                                }
                                currentVolumeY-=VERTICAL_THRESHHOLD;
                                LogUtil.d("CurVolume " + CurVolume);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    currentVolumeY= currentX = currentY = 0;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    currentVolumeY = currentX = currentY = 0;
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }




    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);//todo shift to horizontal and proceed playing
    }

/*    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return useController ? controller.dispatchKeyEvent(event) : super.dispatchKeyEvent(event);
    }*/

    private void maybeShowController(boolean isForced) {
        if (!useController || player == null) {
            return;
        }
        int playbackState = player.getPlaybackState();
        boolean showIndefinitely = playbackState == ExoPlayer.STATE_IDLE
                || playbackState == ExoPlayer.STATE_ENDED || !player.getPlayWhenReady();
        boolean wasShowingIndefinitely = controller.isVisible() && controller.getShowTimeoutMs() <= 0;
        controller.setShowTimeoutMs(showIndefinitely ? 0 : controllerShowTimeoutMs);
        if (isForced || showIndefinitely || wasShowingIndefinitely) {
            controller.show();
        }
    }

    @Override
    public void onVisibilityChange(int visibility) {
        if (controlView != null) {
            if (controlView.getVisibility() != visibility) {
                controlView.setVisibility(visibility);
                if (visibility == VISIBLE) {
                    if (controller.getPlayer().getPlayWhenReady()) {
                        controlView.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        controlView.setImageResource(android.R.drawable.ic_media_play);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (controller.getPlayer().getPlayWhenReady()) {
            controlView.setImageResource(android.R.drawable.ic_media_play);
        } else {
            controlView.setImageResource(android.R.drawable.ic_media_pause);
        }
        controller.getPlayer().setPlayWhenReady(!controller.getPlayer().getPlayWhenReady());
    }

    private final class ComponentListener implements SimpleExoPlayer.VideoListener,
            TextRenderer.Output, ExoPlayer.EventListener {
        // TextRenderer.Output implementation

        @Override
        public void onCues(List<Cue> cues) {
            subtitleLayout.onCues(cues);
        }
        // SimpleExoPlayer.VideoListener implementation

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                       float pixelWidthHeightRatio) {
            layout.setAspectRatio(height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
        }

        @Override
        public void onRenderedFirstFrame() {
            shutterView.setVisibility(GONE);
        }

     /*   @Override
        public void onVideoTracksDisabled() {
            shutterView.setVisibility(VISIBLE);
        }*/
        // ExoPlayer.EventListener implementation

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            maybeShowController(false);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            // Do nothing.
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }


        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Do nothing.
        }
    }
}
