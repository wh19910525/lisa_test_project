package com.tt.push.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import co.lvdou.foundation.utils.extend.LDApkHelper;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import com.tt.push.model.ApkInfo;
import com.tt.push.model.PushTask;
import com.tt.push.service.PushService;
import com.tt.push.store.InstallingApkInfoStore;

public class NotificationHelper {

    private NotificationHelper() {
    }

    /**
     * 显示安装APK通知
     *
     * @param apkInfo  apk信息
     * @param apkPath  apk的本地路径
     * @param iconPath apk图标的本地路径
     */
    public static void showInstallAPKNotification(ApkInfo apkInfo, String apkPath, String iconPath) {

        int id = apkInfo.packageName().hashCode();
        String title = String.format("%s下载完毕", apkInfo.name());
        String content = "点击安装";

        Context context = LDContextHelper.getContext();
        Bitmap largeIcon = BitmapFactory.decodeFile(iconPath);
        PendingIntent installAPKIntent = LDApkHelper.generateInstallPendingIntent(apkPath);
        Notification notification = buildNotification(context, largeIcon, title, content, true, installAPKIntent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, notification);
    }

    /**
     * 显示免下载的apk通知
     *
     * @param titleSource 通知的标题，具体内容由接口返回数据确定
     * @param content     通知的内容，具体内容由接口返回数据确定
     * @param apkPath     apk的本地路径
     * @param iconPath    apk相关图标的本地路径
     * @param apkInfo     apk相关信息
     */
    public static void showFreeDownloadApkNotification(String titleSource, String content, String apkPath, String iconPath, ApkInfo apkInfo) {
        titleSource += "【免流量】";
        SpannableString title = new SpannableString(titleSource);
        int startIdx = titleSource.indexOf("【免流量】");
        int endIdx = startIdx + "【免流量】".length();
        title.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), startIdx, endIdx, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        int id = apkInfo.packageName().hashCode();

        Context context = LDContextHelper.getContext();
        Bitmap largeIcon = BitmapFactory.decodeFile(iconPath);
        PendingIntent installAPKIntent = LDApkHelper.generateInstallPendingIntent(apkPath);
        Notification notification = buildNotification(context, largeIcon, title, content, true, installAPKIntent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, notification);
    }

    /**
     * 显示下载失败的通知栏，点击通知后会重新开始下载
     *
     * @param pushTask 推送任务
     * @param iconPath 推送任务相关图标的本地地址
     */
    public static void showDownloadFailNotification(PushTask pushTask, String iconPath) {

        Context context = LDContextHelper.getContext();
        int id = pushTask.apkInfo().packageName().hashCode();
        String title = String.format("%s下载失败,点击重试", pushTask.apkInfo().name());
        String content = "点击下载";
        Bitmap largeIcon = BitmapFactory.decodeFile(iconPath);

        PendingIntent operation = newDownloadApkOperation(pushTask, iconPath);
        Notification notification = buildNotification(context, largeIcon, title, content, true, operation);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, notification);
    }

    /**
     * 显示下载apk的通知
     *
     * @param title    通知的标题，具体内容由接口返回数据确定
     * @param content  通知的内容，具体内容由接口的返回数据确定
     * @param pushTask 推送任务
     * @param iconPath 推送任务相关的图标本地路径
     */
    public static void showDownloadAPKNotification(String title, String content, PushTask pushTask, String iconPath) {

        Context context = LDContextHelper.getContext();
        int id = pushTask.apkInfo().packageName().hashCode();
        Bitmap largeIcon = BitmapFactory.decodeFile(iconPath);
        PendingIntent operation = newDownloadApkOperation(pushTask, iconPath);
        Notification notification = buildNotification(context, largeIcon, title, content, true, operation);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, notification);
    }

    /**
     * 显示apk下载进度的通知
     *
     * @param apkInfo  apk相关信息
     * @param progress apk的下载进度，取值区间在[0, 100]
     * @param iconPath apk相关图标的本地路径
     */
    public static void showDownloadingApkNotification(ApkInfo apkInfo, int progress, String iconPath) {
        Context context = LDContextHelper.getContext();
        int id = apkInfo.packageName().hashCode();
        String title = String.format("正在下载%s", apkInfo.name());
        Bitmap largeIcon = BitmapFactory.decodeFile(iconPath);

        Notification notification = buildNotification(context, largeIcon, title, progress, false, null, true);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, notification);
    }

    private static Notification buildNotification(Context context, Bitmap icon, String title, int progress, boolean autoCancel, PendingIntent contentIntent, boolean onGoing) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(autoCancel);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info);
        builder.setOngoing(onGoing);
        builder.setLargeIcon(icon);
        builder.setTicker(title);
        builder.setContentInfo(title);
        builder.setContentTitle(title);
        builder.setProgress(100, progress, false);
        if (contentIntent != null)
            builder.setContentIntent(contentIntent);
        return builder.build();
    }

    private static Notification buildNotification(Context context, Bitmap icon, CharSequence title, String content, boolean isAutoCancel, PendingIntent contentIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(isAutoCancel);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info);
        builder.setLargeIcon(icon);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(content);
        if (contentIntent != null)
            builder.setContentIntent(contentIntent);
        return builder.build();
    }

    private static PendingIntent newDownloadApkOperation(PushTask pushTask, String iconPath) {
        Context context = LDContextHelper.getContext();

        Intent intent = new Intent(PushService.ACTION_DOWNLOAD_APK);
        intent.setClass(context, PushService.class);
        intent.putExtra(PushService.EXTRA_PUSH_TASK, pushTask);
        intent.putExtra(PushService.EXTRA_APK_ICON_PATH, iconPath);

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
