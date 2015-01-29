package com.tt.push.store;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import co.lvdou.foundation.utils.extend.LDDeviceInfoHelper;
import co.lvdou.foundation.utils.net.LDDownloadFileDelegate;
import co.lvdou.foundation.utils.net.LDDownloadFileTask;

import com.tt.push.model.ApkInfo;

public class NetworkApkStore {
    private static NetworkApkStore mInstance;
    private final String mSaveDir;
    private HashMap<Long, DownloadFileDelegateWrapper> mWrapples = new HashMap<Long, DownloadFileDelegateWrapper>();

    private NetworkApkStore() {
        mSaveDir = initSaveDir();
    }

    public static NetworkApkStore shareStore() {
        if (mInstance == null)
            mInstance = new NetworkApkStore();

        return mInstance;
    }

    public void download(ApkInfo apkInfo, LoadApkResultHandler handler) {
        if (handler == null)
            handler = LoadApkResultHandler.NULL;

        if (apkInfo == null || !apkInfo.isValid()) {
            handler.onFinishLoadApk(false, null, null);
            return;
        }

        long taskID = getTaskId(apkInfo);
        String savePath = generateSavePath(apkInfo);
        LDDownloadFileDelegate delegate = new DownloadFileDelegateAdapter(apkInfo, savePath, handler);
        if (mWrapples.containsKey(taskID)) {
            mWrapples.get(taskID).addDelegate(delegate);
        } else {
            Context context = LDContextHelper.getContext();
            String url = apkInfo.downloadURL();
            DownloadFileDelegateWrapper delegateWrapper = new DownloadFileDelegateWrapper(taskID, delegate);
            LDDownloadFileTask task = new LDDownloadFileTask(context, url, new File(savePath), delegateWrapper);
            mWrapples.put(taskID, delegateWrapper);

            new Thread(task).start();
        }
    }

    private void removeDownloadFileTask(long taskID) {
        mWrapples.remove(taskID);
    }

    private long getTaskId(ApkInfo apkInfo) {
        return apkInfo.packageName().hashCode();
    }

    private String generateSavePath(ApkInfo info) {
        return mSaveDir + File.separator + info.packageName().hashCode() + ".apk";
    }

    private String initSaveDir() {
        if (LDDeviceInfoHelper.defaultHelper().isExternalMounted()) {
            return LDContextHelper.getContext().getExternalCacheDir() + File.separator + "apk";
        } else {
            return LDContextHelper.getContext().getCacheDir() + File.separator + "apk";
        }
    }

    public static interface LoadApkResultHandler {

        public static LoadApkResultHandler NULL = new LoadApkResultHandler() {
            @Override
            public void onLoadingApk(ApkInfo apkInfo, int progress) {
            }

            @Override
            public void onFinishLoadApk(boolean isDownloadSuccess, ApkInfo apkInfo, String savePath) {
            }
        };

        void onLoadingApk(ApkInfo apkInfo, int progress);

        void onFinishLoadApk(boolean isDownloadSuccess, ApkInfo apkInfo, String savePath);
    }

    private static class DownloadFileDelegateAdapter implements LDDownloadFileDelegate {

        private final ApkInfo mApkInfo;
        private final String mSavePath;
        private final LoadApkResultHandler mHandler;

        public DownloadFileDelegateAdapter(ApkInfo apkInfo, String savePath, LoadApkResultHandler handler) {
            mApkInfo = apkInfo;
            mSavePath = savePath;
            mHandler = handler;
        }

        @Override
        public void onStart(String url) {

        }

        @Override
        public void onDownloading(long currentSizeInByte, long totalSizeInByte, int percentage, String speed, String remainTime) {
            mHandler.onLoadingApk(mApkInfo, percentage);
        }

        @Override
        public void onComplete() {
            mHandler.onFinishLoadApk(true, mApkInfo, mSavePath);
        }

        @Override
        public void onFail() {
            mHandler.onFinishLoadApk(false, mApkInfo, null);
        }

        @Override
        public void onCancel() {
            mHandler.onFinishLoadApk(false, mApkInfo, null);
        }
    }

    private static class DownloadFileDelegateWrapper implements LDDownloadFileDelegate {

        private final long mTaskID;
        private final NetworkApkStore mStore = NetworkApkStore.shareStore();
        private final LinkedList<LDDownloadFileDelegate> mDelegates = new LinkedList<LDDownloadFileDelegate>();

        public DownloadFileDelegateWrapper(long taskID, LDDownloadFileDelegate delegate) {
            mTaskID = taskID;
            mDelegates.add(delegate);
        }

        @Override
        public void onStart(String url) {
            dispatchOnStartEvent(url);
        }

        @Override
        public void onDownloading(long currentSizeInByte, long totalSizeInByte, int percentage, String speed, String remainTime) {
            dispatchOnDownloadingEvent(currentSizeInByte, totalSizeInByte, percentage, speed, remainTime);
        }

        @Override
        public void onComplete() {
            mStore.removeDownloadFileTask(mTaskID);
            dispatchOnCompleteEvent();
        }

        @Override
        public void onFail() {
            mStore.removeDownloadFileTask(mTaskID);
            dispatchOnFailEvent();
        }

        @Override
        public void onCancel() {
            mStore.removeDownloadFileTask(mTaskID);
            dispatchOnCancelEvent();
        }

        public void addDelegate(LDDownloadFileDelegate delegate) {
            mDelegates.add(delegate);
        }

        private void dispatchOnStartEvent(String url) {
            for (LDDownloadFileDelegate delegate : mDelegates) {
                delegate.onStart(url);
            }
        }

        private void dispatchOnDownloadingEvent(long currentSizeInByte, long totalSizeInByte, int percentage, String speed, String remainTime) {
            for (LDDownloadFileDelegate delegate : mDelegates) {
                delegate.onDownloading(currentSizeInByte, totalSizeInByte, percentage, speed, remainTime);
            }
        }

        private void dispatchOnCompleteEvent() {
            for (LDDownloadFileDelegate delegate : mDelegates) {
                delegate.onComplete();
            }
        }

        private void dispatchOnFailEvent() {
            for (LDDownloadFileDelegate delegate : mDelegates) {
                delegate.onFail();
            }
        }

        private void dispatchOnCancelEvent() {
            for (LDDownloadFileDelegate delegate : mDelegates) {
                delegate.onCancel();
            }
        }
    }
}
