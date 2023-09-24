package com.me.harris.playerLibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.me.harris.playerLibrary.video.AvTimeSynchronizer;
import com.me.harris.playerLibrary.video.SoundDecodeThread2;
import com.me.harris.playerLibrary.video.VideoDecodeThread2;

/**
 * Created by xiaoqi on 2018/1/5.
 */

public class VideoPlayView extends SurfaceView implements SurfaceHolder.Callback {

	public  String strVideo ;


	private VideoDecodeThread2 thread;
	private SoundDecodeThread2 soundDecodeThread;
	public  boolean isCreate = false;
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
        if (isCreate){
            start();
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e("VideoPlayView", "surfaceDestroyed");
		isCreate = false;
        stop();
	}

	public void start(){
        if (isCreate){
            stop();
            Log.e("VideoPlayView", "start");
            AvTimeSynchronizer sync = new AvTimeSynchronizer(0,0);
            thread = new VideoDecodeThread2(getHolder().getSurface(), strVideo,this);
            soundDecodeThread = new SoundDecodeThread2(strVideo);
            soundDecodeThread.start();
            thread.start();
        }

	}

	public void stop(){
        Log.e("VideoPlayView", "stop");
        if (thread != null) {
            thread.setStop(true);
            thread.interrupt();
        }
        if (soundDecodeThread != null) {
            soundDecodeThread.setStop(true);
            soundDecodeThread.interrupt();
        }
	}

	public long getCurrentPosition(){
		if (thread!=null) return thread.currentPosition();
		else  return 0;
	}

	public long getDuration(){
		if (thread!=null) return thread.getVideoDuration();
		return 0;
	}

    public void seek(long position){
        Log.e("=A=","Seeking to " + position + "  or " + position/1_000_000 + " s");
        if (thread!=null) thread.seek(position);
        if (soundDecodeThread!=null) soundDecodeThread.seek(position);
    }

}
