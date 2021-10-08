package com.me.harris.droidmedia.video;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.me.harris.droidmedia.R;
import com.me.harris.droidmedia.utils.VideoUtil;

import java.util.HashMap;

public class MediaCodecVideoMainActivity extends AppCompatActivity {
	VideoPlayView playView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_codec_video_player);
		playView = findViewById(R.id.player);
		VideoUtil.setUrl();
		VideoPlayView.strVideo = VideoUtil.strVideo;
		int[] arr = VideoInfoHelper.queryVideoInfo(VideoUtil.strVideo);
		ViewGroup.LayoutParams params = playView.getLayoutParams();


		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		float height = displayMetrics.heightPixels;
		float width = displayMetrics.widthPixels;

		params.width = (int) width;
		params.height = (int) (width*((float) arr[1]/(float) arr[0]));

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
