package com.sv.tasklist.activity;

import android.util.Log;

public class Loggi {

    private final static String TAG = "LOGGI";

    public static void text(String s) {
        Log.i(TAG, s);
    }

    public static void text(long l) {
        Log.i(TAG, String.valueOf(l));
    }

    public static void text(int i) {
        Log.i(TAG, String.valueOf(i));
    }
}
