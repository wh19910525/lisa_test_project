package com.tt.push.model;

import com.tt.push.db.TaskInfoDBHelper;
import com.tt.push.model.protocol.ValidityCheckerProtocol;
import com.tt.push.net.SubmitTaskExecutionResultRequestBuilder;
import com.tt.push.store.InstallingApkInfoStore;

import java.io.Serializable;

public abstract class PushTask implements ValidityCheckerProtocol, Serializable {
    protected int mID;
    protected ApkInfo mApkInfo;

    public PushTask() {
    }

    @Override
    public boolean isValid() {
        return mApkInfo != null;
    }

    public ApkInfo apkInfo() {
        return mApkInfo;
    }

    public int id() {
        return mID;
    }

    public abstract int type();

    public abstract void execute();

    public void onAlreadyInstalled() {
        submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status.ALREADY_INSTALLED);
    }

    public void onDownloadSuccess() {
        submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status.DOWNLOAD_SUCCESS);

        InstallingApkInfoStore.shareStore().addInstallingApk(apkInfo());
    }

    public void onDownloadFail() {
        submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status.DOWNLOAD_FAIL);
    }

    public void onInstallSuccess() {
        submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status.INSTALL_SUCCESS);

        InstallingApkInfoStore.shareStore().removeInstallingApk(apkInfo());

        InstalledApkInfo apkInfo = new InstalledApkInfo().init(id(), type(), apkInfo().packageName());
        TaskInfoDBHelper.shareHelper().insertInstalledApkInfo(apkInfo);
    }

    public void onInstallFail() {
        submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status.INSTALL_FAIL);

        InstallingApkInfoStore.shareStore().removeInstallingApk(apkInfo());
    }

    public void onLaunchSuccess() {
        submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status.LAUNCH_SUCCESS);
    }

    public void onLaunchFail() {
        submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status.LAUNCH_FAIL);
    }

    private void submitTaskStatus(SubmitTaskExecutionResultRequestBuilder.Status status) {
        SubmitTaskExecutionResultRequestBuilder builder = SubmitTaskExecutionResultRequestBuilder.alloc();
        builder.init(id(), type(), status);
        builder.build();
    }
}
