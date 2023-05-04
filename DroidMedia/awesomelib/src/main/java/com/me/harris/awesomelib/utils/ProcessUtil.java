package com.me.harris.awesomelib.utils;

import android.app.ActivityManager;
import android.content.Context;

public class ProcessUtil {
    public static String getCurrentProcessName(Context context) {
        // Log.d(TAG, "getCurrentProcessName");
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses())
        {
            // Log.d(TAG, processInfo.processName);
            if (processInfo.pid == pid)
                return processInfo.processName;
        }
        return "";
    }
}

//droidmedia             E  FATAL EXCEPTION: main
//                                                                                                    Process: com.me.harris.droidmedia:playerService, PID: 8849
//                                                                                                    java.lang.NoClassDefFoundError: Failed resolution of: Lcom/me/harris/ipc/ProcessUtil;
//                                                                                                    	at com.me.harris.ipc.RemoteMediaPlayerBackEndService.onCreate(RemoteMediaPlayerBackEndService.kt:20)
//                                                                                                    	at android.app.ActivityThread.handleCreateService(ActivityThread.java:3378)
//                                                                                                    	at android.app.ActivityThread.-wrap4(Unknown Source:0)
//                                                                                                    	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1706)
