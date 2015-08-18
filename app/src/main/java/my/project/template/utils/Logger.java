package my.project.template.utils;

import android.util.Log;

/**
 * @author Devishankar
 */
public class Logger {
    public static int d(String tag, String msg) {
        if (AppConstants.DEVELOPER_MODE)
            if (msg != null) return Log.d(tag, msg);
            else return Log.d(tag, "null");
        else
            return 0;
    }

    public static int i(String tag, String msg) {
        if (AppConstants.DEVELOPER_MODE)
            return Log.i(tag, msg);
        else
            return 0;
    }

    public static int e(String tag, String msg) {
        if (AppConstants.DEVELOPER_MODE)
            return Log.e(tag, msg);
        else
            return 0;
    }

    public static int w(String tag, String msg) {
        if (AppConstants.DEVELOPER_MODE)
            return Log.w(tag, msg);
        else
            return 0;
    }

    public static void w(String logTag, String s, Exception e) {
        if (AppConstants.DEVELOPER_MODE)
            Log.w(logTag, s, e);
    }

    public static void v(String tag, String s) {
        if (AppConstants.DEVELOPER_MODE)
            Log.w(tag, s);

    }

    public static void e(String tag, String s, Exception e) {
        if (AppConstants.DEVELOPER_MODE)
            Log.e(tag, s, e);
    }
}
