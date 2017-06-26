package com.example.gcg.teacherend;

import android.util.Log;

/**
 * Created by GCG on 2017/5/5.
 */

public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int debug = 2;

    public static final int current = 1;

    public static void v(String tag,String info) {
        if (current <= VERBOSE) {
            Log.v(tag, info);
        }
    }

}
