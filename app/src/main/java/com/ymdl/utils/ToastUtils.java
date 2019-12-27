package com.ymdl.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by LENOVO on 2019/9/1.
 * Toast提示框
 */

public class ToastUtils {
    // 自定义view的toast
    private static Toast customToast = null;
    // 系统默认样式的toast
    private static Toast defaultToast = null;

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }

    public static void showToast(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int resId, int duration) {
        show(context, context.getResources().getString(resId), duration);
    }

    public static void showToast(Context context, String text, int duration, Object... args) {
        show(context, String.format(text, args), duration);
    }

    /**
     * 自定义Toast
     *
     * @param context  上下文对象
     * @param msg      toast显示的内容
     * @param duration toast显示的时长
     */
    private static void show(Context context, CharSequence msg, int duration) {
        if (context != null && msg != null) {
            showDefault(context, msg, duration);
        }
    }

    /**
     * 弹出系统默认样式的Toast，展示错误
     *
     * @param context  上下文对象
     * @param msg      toast显示的内容
     * @param duration toast显示的时长
     */
    @SuppressLint("ShowToast")
    private static void showDefault(Context context, CharSequence msg, int duration) {
        if (defaultToast == null) {
            defaultToast = Toast.makeText(context.getApplicationContext(), msg, duration);
        } else {
            defaultToast.setText(msg);
            defaultToast.setDuration(duration);
        }
        defaultToast.show();
    }
}
