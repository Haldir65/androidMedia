package com.harris.androidMedia.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.harris.androidMedia.exoPlayer.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 工具类，用于获得要加载的图片资源
 * @author carrey
 *
 */
public class ImageHelper {

	private static final String TAG = "ImageHelper";

	public static String getImageUrl(String webServerStr, int position) {
		switch (position % 5) {
			case 0:
				return Constants.IMG_URL_1;
			case 1:
				return Constants.IMG_URL_2;
			case 2:
				return Constants.IMG_URL_3;
			case 3:
				return Constants.IMG_URL_4;
			case 4:
				return Constants.IMG_URL_5;
		}
		return Constants.IMG_URL_4;
	}



	/**
	 * 获得网络图片Bitmap
	 * @param
	 * @return
	 */
	public static Bitmap loadBitmapFromNet(String imageUrlStr) {
		Bitmap bitmap = null;
		URL imageUrl = null;

		if (imageUrlStr == null || imageUrlStr.length() == 0) {
			return null;
		}

		try {
			imageUrl = new URL(imageUrlStr);
			URLConnection conn = imageUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			int length = conn.getContentLength();
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) != -1) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}
				bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
			}
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return null;
		}

		return bitmap;
	}
}
