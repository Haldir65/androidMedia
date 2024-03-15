package com.me.harris.simdjson;

import android.util.Log;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Java11Support {


    public static void showJava11Support(){
        Log.i("=A=","java 11 language feature are supported");
        Log.i("=A=","https://developer.android.com/build/jdks#compileSdk");
        Instant now = Instant.now();
        Instant eightHoursLater = now.plus(8, ChronoUnit.HOURS);
        Instant parsedTime = Instant.parse( now.toString());
        Log.i("=A=","utc time now is "+ now.toString() + "  eight hours later is " + eightHoursLater);
        Log.i("=A=","https://developer.android.com/studio/write/java11-default-support-table");
    }
}
