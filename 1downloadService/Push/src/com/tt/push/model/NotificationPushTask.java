package com.tt.push.model;

import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDApkHelper;
import co.lvdou.foundation.utils.extend.LDDeviceInfoHelper;
import co.lvdou.foundation.utils.extend.Logout;
import com.tt.push.store.NetworkApkStore;
import com.tt.push.store.NetworkImageStore;
import com.tt.push.util.NotificationHelper;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URLDecoder;

public final class NotificationPushTask extends PushTask implements Serializable {

    private static final int COUNT_MAX_RETRY = 2;
    private String mTitle;
    private String mContent;
    private String mIconURL;
    private int mTaskExecutionCount = 0;

    private NotificationPushTask() {
    }

    public static NotificationPushTask alloc() {
        return new NotificationPushTask();
    }

    public NotificationPushTask init(ApkInfo apkInfo, String title, String content, String iconURL) {
        mApkInfo = apkInfo;
        mApkInfo.setRelatedTask(this);

        mTitle = title;
        mContent = content;
        mIconURL = iconURL;

        return this;
    }

    public NotificationPushTask initWithJsonMap(JSONObject jsonMap) throws Exception {
        String CHARSET = "UTF-8";
        JSONObject rootMap = jsonMap;
        JSONObject resultDateMap = rootMap.getJSONObject("resultDate");
        JSONObject commonMap = resultDateMap.getJSONObject("common");
        JSONObject displayMap = resultDateMap.getJSONObject("display");

        String domain = TextUtils.isEmpty(rootMap.getString("mainDomain")) ?
                rootMap.getString("backDomain") : rootMap.getString("mainDomain");
        String appName = URLDecoder.decode(commonMap.getString("appName"), CHARSET);
        String packageName = commonMap.getString("packageName");
        String url = String.format("%s%s", domain, commonMap.getString("url"));
        String title = URLDecoder.decode(displayMap.getString("title"), CHARSET);
        String iconURL = String.format("%s%s", domain, displayMap.getString("icon"));
        String content = URLDecoder.decode(displayMap.getString("content"), CHARSET);

        return init(ApkInfo.alloc().init(appName, packageName, url), title, content, iconURL);
    }

    @Override
    public int type() {
        return 2;
    }

    public String title() {
        return mTitle;
    }

    public String content() {
        return mContent;
    }

    public String iconURL() {
        return mIconURL;
    }

    public void execute() {
        if (!isValid()) return;

        mTaskExecutionCount = 0;
        executeCore();
    }

    private void executeCore() {
        mTaskExecutionCount++;

        if (LDApkHelper.isInstalled(mApkInfo.packageName())) {
            Logout.out("用户已经安装过该应用");
            onAlreadyInstalled();
            return;
        }

        if (!TextUtils.isEmpty(mIconURL)) {
            Logout.out(String.format("[action: download_notification_icon][image_url: %s]", mIconURL));
            NetworkImageStore.shareStore().loadImage(iconURL(), new NetworkImageStore.LoadImageResultHandler() {

                @Override
                public void onFinishLoadImage(String path) {
                    Logout.out(String.format("[action: on_finish_load_image][image_path: %s]", path));
                    if (path == null) {
                        retryIfNeeded();
                        return;
                    }

                    if (LDDeviceInfoHelper.defaultHelper().hasActiveWifi()) {
                        Logout.out("检测到用户打开了WIFI，先下载，再通知");
                        downloadAPKAndShowNotification(path);
                    } else {
                        Logout.out("检测到用户打开了2G/3G/4G网络，先通知，再下载");
                        showNotificationOfDownload(path);
                    }
                }
            });
        }
    }

    private void retryIfNeeded() {
        if (mTaskExecutionCount < COUNT_MAX_RETRY) {
            Logout.out("任务失败,尝试重新执行任务");
            mTaskExecutionCount++;
            executeCore();
        } else {
            onDownloadFail();
            Logout.out("任务失败，超过了限定的重试次数，不再重试");
        }
    }

    private void downloadAPKAndShowNotification(final String iconPath) {
        NetworkApkStore.shareStore().download(apkInfo(), new NetworkApkStore.LoadApkResultHandler() {
            @Override
            public void onLoadingApk(ApkInfo apkInfo, int progress) {
            }

            @Override
            public void onFinishLoadApk(boolean isDownloadSuccess, ApkInfo apkInfo, String apkPath) {
                if (!isDownloadSuccess) {
                    retryIfNeeded();
                    return;
                }

                onDownloadSuccess();
                NotificationHelper.showFreeDownloadApkNotification(title(), content(), apkPath, iconPath, apkInfo());
            }
        });
    }

    private void showNotificationOfDownload(String imagePath) {
        Logout.out("显示下载通知栏");
        NotificationHelper.showDownloadAPKNotification(title(), content(), this, imagePath);
    }
}
