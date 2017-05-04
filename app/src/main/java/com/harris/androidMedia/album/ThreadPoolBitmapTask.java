package com.harris.androidMedia.album;

import android.graphics.Bitmap;
import android.os.Process;
import android.util.Log;

import com.harris.androidMedia.App;
import com.harris.androidMedia.util.Utils;

/**
 * 图片加载的任务单元
 * @author carrey
 *
 */
public class ThreadPoolBitmapTask extends ThreadPoolTask {

	private static final String TAG = "ThreadPoolTaskBitmap";

	private CallBack callBack;

	private int position;

	int width,height;


	public ThreadPoolBitmapTask(String url, CallBack callBack, int position) {
		super(url);
		this.callBack = callBack;
		this.position = position;
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
		int width = ((AlbumImageHolder) callBack).itemView.getWidth()==0? Utils.getScreenWidth(App.getContext())/4:((AlbumImageHolder) callBack).itemView.getWidth();
		int height = ((AlbumImageHolder) callBack).itemView.getHeight() == 0 ? Utils.dip2px(App.getContext(), 200) : ((AlbumImageHolder) callBack).itemView.getHeight();
		Bitmap bitmap = ImageHelper.getResultBitmapWithExactSize(url,width,height);
		Log.i(TAG, "loaded: " + url);
		if (callBack != null) {
			callBack.onReady( bitmap, this.position);
		}
	}


}
