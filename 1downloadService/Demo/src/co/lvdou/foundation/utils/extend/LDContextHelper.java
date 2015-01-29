package co.lvdou.foundation.utils.extend;

import android.content.Context;

/**
 * 获取程序上下文的工具类。
 *
 * @author 郑一
 */
public class LDContextHelper {
    private static Context mApplicationContext;

    private LDContextHelper() {
    }

    /**
     * 设置程序上下文。
     *
     * @param context 程序上下文
     */
    public static void init(Context context) {
        if (mApplicationContext == null && context != null) {
            mApplicationContext = context.getApplicationContext();
        }
    }

    /**
     * 获取程序上下文，未设置将返回空。
     */
    public static Context getContext() {
        return mApplicationContext;
    }

    public static int getLayout(String name) {
        return mApplicationContext.getResources().getIdentifier(name, "layout", mApplicationContext.getPackageName());
    }

    public static int getId(String name) {
        return mApplicationContext.getResources().getIdentifier(name, "id", mApplicationContext.getPackageName());
    }

    public static int getStyle(String name) {
        return mApplicationContext.getResources().getIdentifier(name, "style", mApplicationContext.getPackageName());
    }

    public static int getString(String name) {
        return mApplicationContext.getResources().getIdentifier(name, "string", mApplicationContext.getPackageName());
    }

    public static int getAnim(String name) {
        return mApplicationContext.getResources().getIdentifier(name, "anim", mApplicationContext.getPackageName());
    }

    public static int getColor(String name) {
        return mApplicationContext.getResources().getIdentifier(name, "color", mApplicationContext.getPackageName());
    }

    public static int getDrawable(String name) {
        return mApplicationContext.getResources().getIdentifier(name, "drawable", mApplicationContext.getPackageName());
    }
}
