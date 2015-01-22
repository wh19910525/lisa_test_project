package co.lvdou.foundation.utils.extend;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 显示Toast的工具类。
 *
 * @author 郑一
 */
public final class LDToastHelper {
    private static Handler _handler;

    private LDToastHelper() {
    }

    /**
     * 显示Toast
     *
     * @param resId 待显示Toast内容相关字符串的资源ID
     */
    public static void show(int resId) {
        final Context context = LDContextHelper.getContext();
        show(context.getString(resId));
    }

    /**
     * 显示Toast
     *
     * @param msg 待显示Toast内容
     */
    public static void show(final String msg) {
        final Context context = LDContextHelper.getContext();
        if (!TextUtils.isEmpty(msg))
            initializeHandlerIfNeeded(context);
        if (_handler != null) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private static void initializeHandlerIfNeeded(Context context) {
        if (context != null) {
            _handler = new Handler(context.getApplicationContext().getMainLooper());
        }
    }
}
