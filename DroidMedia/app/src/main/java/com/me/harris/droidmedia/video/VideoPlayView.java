package com.me.harris.droidmedia.video;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;

/**
 * Created by xiaoqi on 2018/1/5.
 */

public class VideoPlayView extends SurfaceView implements SurfaceHolder.Callback {

	public static  String strVideo ;
//	static {
//		if (strVideo==null){
//			File dir = new File(Environment.getExternalStorageDirectory().getPath()+
//					File.separator+Environment.DIRECTORY_MOVIES);
//			strVideo = dir.listFiles()[1].getAbsolutePath();
//		}
////		private static final String strVideo = Environment.getExternalStorageDirectory().getPath()+
////				File.separator+Environment.DIRECTORY_MOVIES+File.separator + "/h265.mp4";
//	}


	public static void setUrl(){
		File dir = new File(Environment.getExternalStorageDirectory().getPath()+
				File.separator+Environment.DIRECTORY_MOVIES);
		File[] fs = dir.listFiles((dir1, name) -> name.endsWith(".mp4"));
//		File[] fs = dir.listFiles((dir1, name) -> name.endsWith(".mp4") || name.endsWith(".webm") || name.endsWith(".mkv"));
		assert fs != null;
		strVideo = fs[new Random().nextInt(fs.length)].getAbsolutePath();
		strVideo = fs[4].getAbsolutePath();
	}


	private VideoDecodeThread thread;
	private SoundDecodeThread soundDecodeThread;
	public static boolean isCreate = false;
	public VideoPlayView(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	public VideoPlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}

	public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e("VideoPlayView", "surfaceCreated");
		isCreate = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.e("VideoPlayView", "surfaceChanged");
		if (thread == null) {
			thread = new VideoDecodeThread(holder.getSurface(), strVideo);
			thread.start();
		}
		if (soundDecodeThread == null) {
			soundDecodeThread = new SoundDecodeThread(strVideo);
			soundDecodeThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e("VideoPlayView", "surfaceDestroyed");
		isCreate = false;
		if (thread != null) {
			thread.stop = true;
			thread.interrupt();
		}
		if (soundDecodeThread != null) {
			soundDecodeThread.stop = true;
			soundDecodeThread.interrupt();
		}
	}

	public void start(){
		Log.e("VideoPlayView", "start");
		thread = new VideoDecodeThread(getHolder().getSurface(), strVideo);
		soundDecodeThread = new SoundDecodeThread(strVideo);
		soundDecodeThread.start();
		thread.start();
	}

	public void stop(){
		thread.interrupt();
		thread.stop = true;
		soundDecodeThread.stop = true;
		soundDecodeThread.interrupt();
	}
}
