package com.me.harris.awesomelib.videoutil;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.util.Log;

public class VideoInfoHelper {

    public static int[] queryVideoInfo(String videoPath){
        int width = -1;
        int height = -1;
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(videoPath);
        }catch (Exception e){
            e.printStackTrace();
        }
        int trackCount = extractor.getTrackCount();
        String mimeTYpe = "video/";
        MediaFormat format = null;
        //New format {crop-right=1279, color-format=2141391878,
        // slice-height=736, mime=video/raw,
        // hdr-static-info=java.nio.HeapByteBuffer[pos=0 lim=25 cap=25], stride=1280, color-standard=1,
        // color-transfer=3, crop-bottom=719, crop-left=0, width=1280,
        // color-range=2, crop-top=0, height=720}
        int trackId = -1;

        for (int i = 0; i < trackCount; i++) {
            format = extractor.getTrackFormat(i);
            if (format.getString(MediaFormat.KEY_MIME).startsWith(mimeTYpe)){
                trackId = i;
                width = format.getInteger(MediaFormat.KEY_WIDTH);
                height = format.getInteger(MediaFormat.KEY_HEIGHT);
                break;
            }

        }
        extractor.release();
//        if (trackId!=-1){
//            extractor.selectTrack(trackId);
//        }
        Log.e("=A=","width = "+ width+" height = "+height);
        return new int[]{width,height};
    }
}
