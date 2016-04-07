package com.harris.camera2training.tasks;

import android.media.Image;

import com.harris.camera2training.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Harris on 2016/4/1.
 */
public class ImageSaver implements Runnable {
    /**
     * The JPEG image
     */
    private final Image mImage;
    /**
     * The file we save the image into.
     */
    private final File mFile;

    public ImageSaver(Image image, String FolderPath) {
        mImage = image;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        String fileName = dateFormat.format(new Date())+".jpg";
        File parentFolder = new File(FolderPath);
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
        mFile = new File(FolderPath + File.separator + fileName);

        LogUtil.d("文件不存在并创建" + (mFile.exists() ? "成功" : " 失败 "));
        LogUtil.d("文件保存在 " + mFile.getAbsolutePath());
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
