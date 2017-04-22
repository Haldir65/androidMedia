package com.harris.androidMedia.album;

import android.graphics.Bitmap;
import android.os.Process;
import android.util.Log;

/**
 * 图片加载的任务单元
 * @author carrey
 *
 */
public class ThreadPoolTaskBitmap extends ThreadPoolTask {

	private static final String TAG = "ThreadPoolTaskBitmap";

	private CallBack callBack;

	private int position;

	public ThreadPoolTaskBitmap(String url, CallBack callBack, int position) {
		super(url);
		this.callBack = callBack;
		this.position = position;
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

		Bitmap bitmap = ImageHelper.loadBitmapFromNet(url);

		Log.i(TAG, "loaded: " + url);

		if (callBack != null) {
			callBack.onReady( bitmap, this.position);
		}
	}


}
