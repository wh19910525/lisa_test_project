package co.lvdou.foundation.utils.extend;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

/**
 * 显示通知的工具类。
 *
 * @author 郑一
 */
public final class LDNotificationHelper {

    private LDNotificationHelper() {
    }

    private static Context getContext() {
        return LDContextHelper.getContext();
    }

    /**
     * 显示系统通知
     *
     * @param id    通知的ID
     * @param title 通知的标题
     * @param icon  通知的图标
     */
    public static void show(int id, CharSequence title, int icon) {
        show(id, title, null, icon);
    }

    /**
     * 显示系统通知
     *
     * @param id     通知的ID
     * @param title  通知的标题
     * @param icon   通知的图标
     * @param virate 设置通知显示时是否震动
     */
    public static void show(int id, CharSequence title, int icon, boolean virate) {
        show(id, title, null, icon, null, virate);
    }

    /**
     * 显示系统通知
     *
     * @param id      通知的ID
     * @param title   通知的标题
     * @param content 通知的内容
     * @param icon    通知的图标
     */
    public static void show(int id, CharSequence title, CharSequence content, int icon) {
        show(id, title, content, icon, null, false);
    }

    /**
     * 显示系统通知
     *
     * @param id      通知的ID
     * @param title   通知的标题
     * @param content 通知的内容
     * @param icon    通知的图标
     * @param virate  设置通知显示时是否震动
     */
    public static void show(int id, CharSequence title, CharSequence content, int icon, boolean virate) {
        show(id, title, content, icon, null, virate);
    }

    /**
     * 显示系统通知
     *
     * @param id      通知的ID
     * @param title   通知的标题
     * @param content 通知的内容
     * @param icon    通知的图标
     * @param pIntent 通知附带的 {@link android.app.PendingIntent}
     */
    public static void show(int id, CharSequence title, CharSequence content, int icon, PendingIntent pIntent) {
        show(id, title, content, icon, pIntent, false);
    }

    /**
     * 显示系统通知
     *
     * @param id      通知的ID
     * @param title   通知的标题
     * @param content 通知的内容
     * @param icon    通知的图标
     * @param pIntent 通知附带的 {@link android.app.PendingIntent}
     * @param virate  设置通知显示时是否震动
     */
    public static void show(int id, CharSequence title, CharSequence content, int icon, PendingIntent pIntent, boolean virate) {
        show(id, title, content, icon, pIntent, virate, true);
    }

    /**
     * 显示系统通知
     *
     * @param id        通知的ID
     * @param title     通知的标题
     * @param content   通知的内容
     * @param icon      通知的图标
     * @param pIntent   通知附带的 {@link android.app.PendingIntent}
     * @param virate    设置通知显示时是否震动
     * @param clearable 设置通知是否可手动清除
     */
    public static void show(int id, CharSequence title, CharSequence content, int icon, PendingIntent pIntent, boolean virate,
                            boolean clearable) {
        show(id, title, title, content, icon, pIntent, virate, clearable);
    }

    /**
     * 显示系统通知
     *
     * @param id        通知的ID
     * @param ticker    通知的简介
     * @param title     通知的标题
     * @param content   通知的内容
     * @param icon      通知的图标
     * @param pIntent   通知附带的 {@link android.app.PendingIntent}
     * @param virate    设置通知显示时是否震动
     * @param clearable 设置通知是否可手动清除
     */
    public static void show(int id, CharSequence ticker, CharSequence title, CharSequence content, int icon,
                            PendingIntent pIntent, boolean virate, boolean clearable) {
        final NotificationCompat.Builder builder = new Builder(getContext());

        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setSmallIcon(icon);
        builder.setAutoCancel(clearable);
        if (virate) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        if (content != null) {
            builder.setContentText(content);
        }
        if (pIntent != null) {
            builder.setContentIntent(pIntent);
        } else {
            builder.setContentIntent(PendingIntent.getBroadcast(getContext(), id, new Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        final NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, builder.build());
    }

    /**
     * 显示系统通知
     *
     * @param id       通知的ID
     * @param title    通知的标题
     * @param content  通知的内容
     * @param progress 通知的进度
     * @param icon     通知的图标
     */
    public static void show(int id, CharSequence title, CharSequence content, int progress, int icon) {
        show(id, title, content, progress, icon, null);
    }

    /**
     * 显示系统通知
     *
     * @param id       通知的ID
     * @param title    通知的标题
     * @param content  通知的内容
     * @param progress 通知的进度
     * @param icon     通知的图标
     * @param pIntent  通知附带的 {@link android.app.PendingIntent}
     */
    public static void show(int id, CharSequence title, CharSequence content, int progress, int icon, PendingIntent pIntent) {
        if (!isMobileVersionTooLower()) {
            final NotificationManager nm = (NotificationManager) getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder builder = new Builder(getContext());
            builder.setTicker(title);
            builder.setSmallIcon(icon);
            builder.setContentTitle(title);
            builder.setContentText(content);
            builder.setProgress(100, progress, false);
            builder.setWhen(id);
            if (pIntent != null) {
                builder.setContentIntent(pIntent);
            } else {
                builder.setContentIntent(PendingIntent.getBroadcast(getContext(), id, new Intent(),
                        PendingIntent.FLAG_UPDATE_CURRENT));
            }
            nm.notify(id, builder.build());
        } else {
            show(id, title, content, icon, pIntent);
        }
    }

    public static void clean(int id) {
        final NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(id);
    }

    public static void cleanAll() {
        final NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    private static boolean isMobileVersionTooLower() {
        boolean result = false;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < 14) {
            result = true;
        }
        return result;
    }
}
