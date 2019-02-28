package com.harris.androidMedia.mediaCodec;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.harris.androidMedia.R;
import com.harris.androidMedia.util.LogUtil;

import java.io.File;
import java.util.HashMap;

import static android.media.MediaCodecInfo.CodecCapabilities.FEATURE_AdaptivePlayback;
import static com.harris.androidMedia.exoPlayer.customize.CustomPlayerViewActivity.CUSTOM_PLAYER_VIEW_URL_STRING;

// https://github.com/JavaNoober/MedioDecode
public class H256VideoPlayerActivity extends AppCompatActivity {
    VideoPlayView playView;
    private static final String TAG = "H256VideoPlayerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h265_play);
        playView = findViewById(R.id.player);
        String localVideoPath = getIntent().getStringExtra(CUSTOM_PLAYER_VIEW_URL_STRING);
        VideoPlayView.strVideo = localVideoPath;
        hideSystemUI();
//        //获取所支持的编码信息的方法
//        HashMap<String, MediaCodecInfo.CodecCapabilities> mEncoderInfos = new HashMap<>();
//        for(int i = MediaCodecList.getCodecCount() - 1; i >= 0; i--){
//            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
//            if(codecInfo.isEncoder()){
//                for(String t : codecInfo.getSupportedTypes()){
//                    try{
//                        mEncoderInfos.put(t, codecInfo.getCapabilitiesForType(t));
//                    } catch(IllegalArgumentException e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        LogUtil.d("H256VideoPlayerActivity",mEncoderInfos.toString());
        displayDecoders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playView.stop();
    }

    private void displayDecoders() {
        MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);//REGULAR_CODECS参考api说明
        MediaCodecInfo[] codecs = list.getCodecInfos();
        for (MediaCodecInfo codec : codecs) {
            if (codec.isEncoder())
                continue;
            Log.i(TAG, "displayDecoders: "+codec.getName());
        }
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
