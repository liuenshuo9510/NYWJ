package com.nanyue.app.nywj;

import android.util.Log;


public class MyLog {

    private static String APP = "com.nanyue.app.nywj";

    public static void e(String x, String y) {
        Log.e(APP, x + "+" + y);
    }

    public static void i(String x, String y) {
        Log.i(APP, x + "+" + y);
    }

    public static void d(String x, String y) {
        Log.d(APP, x + "+" + y);
    }

    public static void v(String x, String y) {
        Log.v(APP, x + "+" + y);
    }
}
