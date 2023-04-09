// IPlayerService.aidl
package com.me.harris.droidmedia;
import com.me.harris.droidmedia.model.MessageModel;
import android.view.Surface;

// Declare any non-default types here with import statements

interface IPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendMessage(in MessageModel messageModel);
    int getDuration();
    boolean isPlaying();
    void setLooping(boolean looping);
    void setDataSource(String path);
    void prepare();
    void prepareAsync();
    void release();
    void reset();
    void seekTo(int millsec);
    void setSurface(in Surface surface);
    void start();
    void stop();
    void setOnErrorListener();
//    void setOnPreparedListener(MediaPlayer.OnPreparedListener listener);


}