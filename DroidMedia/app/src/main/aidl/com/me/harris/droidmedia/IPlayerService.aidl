// IPlayerService.aidl
package com.me.harris.droidmedia;
import com.me.harris.droidmedia.model.MessageModel;

// Declare any non-default types here with import statements

interface IPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendMessage(in MessageModel messageModel);
}