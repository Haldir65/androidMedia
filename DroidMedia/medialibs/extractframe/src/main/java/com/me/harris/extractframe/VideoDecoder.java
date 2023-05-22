package com.me.harris.extractframe;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoDecoder {
    private static String TAG = "VideoDecoder";

    public static final int COLOR_FORMAT_I420 = 1;

    public static final int COLOR_FORMAT_NV21 = 2;

    public static final int COLOR_FORMAT_NV12 = 3;


    private static final int DECODE_COLOR_FORMAT = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;

    private static final long DEFAULT_TIMEOUT_US = 10000;


    private int mOutputFormat = COLOR_FORMAT_NV21;

    private byte[] mYuvBuffer;

    private volatile boolean mStop = false;

    public void setOutputFormat(int outputFormat){
        mOutputFormat = outputFormat;
    }

    public int getOutputFormat (){
        return mOutputFormat;
    }

    public void stop(){
        mStop = true;
    }

    public interface DecodeCallback {
        void onDecode(byte[] yuv, int width, int height ,int formatCount, long presentationTimeUs,int format);


        void onFinish();

        void onStop();
    }

    public void decode(String videoFilePath,DecodeCallback decodeCallback){
        mStop = false;
        MediaExtractor extractor = null;
        MediaCodec decoder = null;;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(videoFilePath);
            int trackIndex = selectVideoTrack(extractor);
            if (trackIndex< 0 ){
                Log.e(TAG,"No video track found in " + videoFilePath);
                return;
            }
            MediaFormat mediaFormat = extractor.getTrackFormat(trackIndex);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(mime);
            if (isColorFormatSupported(DECODE_COLOR_FORMAT,decoder.getCodecInfo().getCapabilitiesForType(mime))){
                mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,DECODE_COLOR_FORMAT);
                Log.i(TAG,"set decode color format to type "+ DECODE_COLOR_FORMAT);
            }else {
                Log.i(TAG,"unable to set decode color format, color format type  "+ DECODE_COLOR_FORMAT + " not supported");
            }

            // [OMX.qcom.video.decoder.vp9] using color format 0x7fa30c04 in place of 0x7f420888
            //0x7f420888 = COLOR_FormatYUV420Flexible
            int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
            int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
            Log.d(TAG,"decode video width : "+ width + " , height: "+ height);

//            MediaFormat format = mediaFormat;
//             width = format.getInteger(MediaFormat.KEY_WIDTH);
//            if (format.containsKey("crop-left") && format.containsKey("crop-right")) {
//                width = format.getInteger("crop-right") + 1 - format.getInteger("crop-left");
//            }
//             height = format.getInteger(MediaFormat.KEY_HEIGHT);
//            if (format.containsKey("crop-top") && format.containsKey("crop-bottom")) {
//                height = format.getInteger("crop-bottom") + 1 - format.getInteger("crop-top");
//            }
//
//            Log.d(TAG,"decode video width : "+ width + " , height: "+ height);
            int yuvLength = width*height*3/2;
            // width * height * ImageFormat.getBitsPerPixel(format) / 8
            if (mYuvBuffer ==null || mYuvBuffer.length!= yuvLength){
                mYuvBuffer = new byte[yuvLength];
            }
            decoder.configure(mediaFormat,null,null,0);
            decoder.start();
            decodeFramesToImage(decoder,extractor,width,height,decodeCallback);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (decoder!=null){
                decoder.stop();
                decoder.release();
                decoder = null;
            }
            if (extractor!=null){
                extractor.release();
                extractor = null;
            }
        }
    }

    private static int selectVideoTrack(MediaExtractor extractor){
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")){
                extractor.selectTrack(i);
                return i;
            }
        }
        return -1;
    }

    private boolean isColorFormatSupported(int colorFormat,MediaCodecInfo.CodecCapabilities caps){
        for (int c: caps.colorFormats){
            if (c == colorFormat){
                return true;
            }
        }
        return false;
    }

    private void decodeFramesToImage(MediaCodec decoder,MediaExtractor extractor, int width , int height,DecodeCallback decodeCallback){
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        int outputFrameCount = 0;
        long startMs = System.currentTimeMillis();
        while (!mStop && !sawOutputEOS&&!Thread.currentThread().isInterrupted()){
            if (!sawInputEOS){
                int inputBufferId = decoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
                if(inputBufferId >=0){
                    ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferId);
                    int sampleSize = extractor.readSampleData(inputBuffer,0);
                    if (sampleSize <0 ){
                        decoder.queueInputBuffer(inputBufferId,0,0,0L,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        sawInputEOS = true;
                        Log.i(TAG,"sawInputEOS is true");
                    }else  {
                        decoder.queueInputBuffer(inputBufferId,0,sampleSize,extractor.getSampleTime(),0);
                        extractor.advance();
                    }
                }
            }
            int outputBufferId = decoder.dequeueOutputBuffer(info,DEFAULT_TIMEOUT_US);
            if (outputBufferId>=0 ){
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) !=0){
                    sawOutputEOS = true;
                    Log.i(TAG,"sawOutputEOS is true");
                }
                if (info.size>0){
                    outputFrameCount++;
                    int format = decoder.getOutputFormat().getInteger(MediaFormat.KEY_COLOR_FORMAT);
                    if (format ==  MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible) {
                        Log.w(TAG,"MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible found " + outputFrameCount + " format = " + format);
                    }
                    else  {
                        // format = 2141391876
                        Log.w(TAG,"MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible not found " + outputFrameCount + " format = " + format);
                    }
                    Image image = decoder.getOutputImage(outputBufferId);
                    int imageFormat = image.getFormat();
                    if (imageFormat != ImageFormat.YUV_420_888){
                        throw new IllegalStateException("unknown image Format!!!");
                    }
                    getDataFromImage(image,mOutputFormat,width,height);
                    image.close();
                    decoder.releaseOutputBuffer(outputBufferId,false);
                    //callback
//                    sleepRender(info,startMs);
                    if (decodeCallback!=null && (info.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME)!=0){
                        decodeCallback.onDecode(mYuvBuffer,width,height,outputFrameCount,info.presentationTimeUs,format);
                    }
                }
            }
        }
        if (decodeCallback!=null){
            if (mStop||Thread.currentThread().isInterrupted()){
                decodeCallback.onStop();
            }else {
                decodeCallback.onFinish();
            }
        }

    }

    private void getDataFromImage(Image image, int colorFormat, int width , int height){
        if (colorFormat != COLOR_FORMAT_I420 && colorFormat != COLOR_FORMAT_NV21 && colorFormat!= COLOR_FORMAT_NV12) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        Rect crop = image.getCropRect();
        Log.v(TAG,"crop width: " + crop.width() + " ,height: "+ crop.height() + " format = "+ image.getFormat() );
        // image.getFormat()  == ImageFormat.YUV_420_888
        Image.Plane[] planes = image.getPlanes();
        byte[] rowData = new byte[planes[0].getRowStride()];
        int channelOffset = 0;
        int outputStride = 1;
        for (int i =0 ;i<planes.length;i++){
            switch (i){
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FORMAT_I420){
                        channelOffset = width*height;
                        outputStride = 1;
                    }else if (colorFormat == COLOR_FORMAT_NV21){
                        channelOffset = width*height+1;
                        outputStride = 2;
                    }else if (colorFormat == COLOR_FORMAT_NV12){
                        channelOffset = width*height;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FORMAT_I420){
                        channelOffset = (int)(width*height*1.25);
                        outputStride = 1;
                    }else if (colorFormat == COLOR_FORMAT_NV21) {
                        channelOffset = width*height;
                        outputStride = 2;
                    }  else if (colorFormat == COLOR_FORMAT_NV12){
                        channelOffset = width*height+1;
                        outputStride = 2;
                    }
                    break;
                default:
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i==0)? 0:1;
            int w = width >>shift;
            int h = height >> shift;
            buffer.position(rowStride *(crop.top>>shift)+pixelStride*(crop.left>>shift));
            for(int row = 0; row<h; row++){
                int length;
                if (pixelStride == 1 && outputStride==1){
                    length = w;
                    buffer.get(mYuvBuffer,channelOffset,length);
                    channelOffset +=length;
                }else {
                    length = (w-1)*pixelStride+1;
                    buffer.get(rowData,0,length);
                    for(int col = 0; col<w ; col++){
                        mYuvBuffer[channelOffset] = rowData[col *pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h-1){
                    buffer.position(buffer.position()+rowStride - length);
                }
            }
        }
    }
    //2021-09-29 15:35:47.261 8565-8695/com.me.harris.viewmodelsample I/decoder: dequeueInputBuffer cost 0
    //2021-09-29 15:35:47.262 8565-8695/com.me.harris.viewmodelsample I/decoder: queueInputBuffer cost 1
    //2021-09-29 15:35:47.262 8565-8695/com.me.harris.viewmodelsample I/decoder: dequeueOutputBuffer cost 0
    //2021-09-29 15:35:47.262 8565-8695/com.me.harris.viewmodelsample I/decoder: getOutputImage cost 0


    private void sleepRender(MediaCodec.BufferInfo audioBufferInfo, long startMs) {
        // 这里的时间是 毫秒  presentationTimeUs 的时间是累加的 以微秒进行一帧一帧的累加
        // audioBufferInfo 是改变的
        while (audioBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
            try {
                // 10 毫秒
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }



}
