package com.harris.androidMedia.mediaPlayBack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.harris.androidMedia.R;
import com.harris.androidMedia.exoPlayer.Constants;

import java.io.IOException;

/**
 * Created by Harris on 2017/3/25.
 */

public class MediaPlayerService extends Service {
    MediaSession mediaSession;
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_forward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    MediaPlayer mediaPlayer;
    MediaSessionManager mManager;
    MediaController mControler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @RequiresApi(21)
    void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        if (action.equalsIgnoreCase(ACTION_PLAY)) {
                mControler.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mControler.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(ACTION_FAST_FORWARD)) {
            mControler.getTransportControls().fastForward();
        }else if (action.equalsIgnoreCase(ACTION_REWIND)){
            mControler.getTransportControls().rewind();
        }else if (action.equalsIgnoreCase(ACTION_PREVIOUS)){
            mControler.getTransportControls().skipToPrevious();
        }else if (action.equalsIgnoreCase(ACTION_NEXT)){
            mControler.getTransportControls().skipToNext();
        }else if (action.equalsIgnoreCase(ACTION_STOP)){
            mControler.getTransportControls().stop();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    Notification.Action generateAction(@DrawableRes int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon,title,pendingIntent).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void buildNotification(Notification.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.camera)
                .setContentTitle("Lock Screen Media")
                .setContentText("Artist Name")
                .setDeleteIntent(pendingIntent)
                .setStyle(style);
        builder.addAction(generateAction(android.R.drawable.ic_media_previous,"Previous",ACTION_PREVIOUS));
        builder.addAction(generateAction(android.R.drawable.ic_media_rew,"Rewind",ACTION_REWIND));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_ff,"fast Forward",ACTION_FAST_FORWARD));
        builder.addAction(generateAction(android.R.drawable.ic_media_next,"Next",ACTION_NEXT));
        style.setShowActionsInCompactView(0, 1, 2, 3, 4);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        if (mManager == null) {
            initMediaSession();
        }
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSession() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(Constants.mp3Url);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (IOException e) {
        }
        mediaSession = new MediaSession(getApplicationContext(), "Player Session");
        mControler = new MediaController(getApplicationContext(), mediaSession.getSessionToken());
        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                if (!mediaPlayer.isPlaying()) {
                   mediaPlayer.start();
                }
                super.onPlay();
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));

            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                buildNotification(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));

            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                buildNotification(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));

            }

            @Override
            public void onRewind() {
                super.onRewind();
                buildNotification(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND));

            }

            @Override
            public void onStop() {
                super.onStop();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                stopService(intent);

            }
        });

    }
}
