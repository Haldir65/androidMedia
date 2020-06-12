package com.me.harris.droidmedia.video;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.me.harris.droidmedia.R;

import java.util.HashMap;

public class MediaCodecVideoMainActivity extends AppCompatActivity {
	VideoPlayView playView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_codec_video_player);
		playView = findViewById(R.id.player);
		//获取所支持的编码信息的方法
		HashMap<String, MediaCodecInfo.CodecCapabilities> mEncoderInfos = new HashMap<>();
		for(int i = MediaCodecList.getCodecCount() - 1; i >= 0; i--){
			MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
			if(codecInfo.isEncoder()){
				for(String t : codecInfo.getSupportedTypes()){
					try{
						mEncoderInfos.put(t, codecInfo.getCapabilitiesForType(t));
					} catch(IllegalArgumentException e){
						e.printStackTrace();
					}
				}
			}
		}
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
}
