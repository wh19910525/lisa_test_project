package com.tt.push.store;

import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.Logout;
import com.tt.push.model.ApkInfo;

import java.util.HashSet;

public class InstallingApkInfoStore {
    private static final InstallingApkInfoStore mShareStore = new InstallingApkInfoStore();
    private HashSet<ApkInfo> mInstallingApkInfo = new HashSet<ApkInfo>();

    private InstallingApkInfoStore() {
    }

    public static InstallingApkInfoStore shareStore() {
        return mShareStore;
    }

    public void addInstallingApk(ApkInfo apkInfo) {
        Logout.out("尝试添加正在安装的应用: " + apkInfo.packageName());
        if (!mInstallingApkInfo.contains(apkInfo) && apkInfo.isValid()) {
            Logout.out("成功添加正在安装的应用: " + apkInfo.packageName());
            mInstallingApkInfo.add(apkInfo);
        }
    }

    public void removeInstallingApk(ApkInfo apkInfo) {
        mInstallingApkInfo.remove(apkInfo);
    }

    public ApkInfo getInstallingApk(String packageName) {
        if (TextUtils.isEmpty(packageName)) return null;

        Logout.out(String.format("尝试寻找与包名:%s相关的任务", packageName));

        for (ApkInfo info : mInstallingApkInfo) {
            if (info.packageName().equalsIgnoreCase(packageName)) {

                return info;
            }
        }

        return null;
    }
}
