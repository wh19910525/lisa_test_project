package com.tt.push.store;

import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import co.lvdou.foundation.utils.extend.LDDeviceInfoHelper;
import co.lvdou.foundation.utils.net.LDDownloadFileDelegate;
import co.lvdou.foundation.utils.net.LDDownloadFileTask;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkImageStore {
    private static NetworkImageStore mInstance;
    private HashMap<Long, DownloadFileDelegateWrapper> mWrappers = new HashMap<Long, DownloadFileDelegateWrapper>();
    private Executor mThreadPool;
    private String mCacheDir;

    private NetworkImageStore() {
        mThreadPool = Executors.newFixedThreadPool(3);
        initCacheDir();
    }

    public static NetworkImageStore shareStore() {
        if (mInstance == null)
            mInstance = new NetworkImageStore();

        return mInstance;
    }

    public void loadImage(String imageURL, LoadImageResultHandler handler) {
        handler = handler == null ? LoadImageResultHandler.NULL : handler;

        if (TextUtils.isEmpty(imageURL)) {
            handler.onFinishLoadImage(null);
            return;
        }

        String savePath = transformToLocalPath(imageURL);
        if (new File(savePath).exists()) {
            handler.onFinishLoadImage(savePath);
        } else {
            long taskID = imageURL.hashCode();
            downloadImage(taskID, imageURL, savePath, new LDDownloadFileDelegateAdapter(savePath, handler));
        }
    }

    private void downloadImage(long taskID, String imageURL, String savePath, LDDownloadFileDelegate delegate) {

        DownloadFileDelegateWrapper wrapper = mWrappers.get(taskID);
        if (wrapper != null) {
            wrapper.addDelegate(delegate);
        } else {
            wrapper = new DownloadFileDelegateWrapper(taskID, delegate);
            LDDownloadFileTask task = new LDDownloadFileTask(LDContextHelper.getContext(), imageURL, new File(savePath), wrapper);

            mWrappers.put(taskID, wrapper);

            mThreadPool.execute(task);
        }
    }

    private String transformToLocalPath(String imageURL) {
        if (TextUtils.isEmpty(imageURL))
            return null;

        return mCacheDir + File.separator + imageURL.hashCode();
    }

    private void removeDownloadFileTask(long taskID) {
        mWrappers.remove(taskID);
    }

    @SuppressWarnings("ConstantConditions")
    private void initCacheDir() {
        if (LDDeviceInfoHelper.defaultHelper().isExternalMounted())
            mCacheDir = LDContextHelper.getContext().getExternalCacheDir().getAbsolutePath();
        else
            mCacheDir = LDContextHelper.getContext().getCacheDir().getAbsolutePath();
    }

    public static interface LoadImageResultHandler {
        public static LoadImageResultHandler NULL = new LoadImageResultHandler() {
            @Override
            public void onFinishLoadImage(String path) {
            }
        };

        void onFinishLoadImage(String path);
    }

    private static class LDDownloadFileDelegateAdapter implements LDDownloadFileDelegate {
        private final String mSavePath;
        private final LoadImageResultHandler mHandler;

        public LDDownloadFileDelegateAdapter(String savePath, LoadImageResultHandler handler) {
            mSavePath = savePath;
            mHandler = handler;
        }

        @Override
        public void onStart(String url) {
        }

        @Override
        public void onDownloading(long currentSizeInByte, long totalSizeInByte, int percentage, String speed, String remainTime) {
        }

        @Override
        public void onComplete() {
            mHandler.onFinishLoadImage(mSavePath);
        }

        @Override
        public void onFail() {
            mHandler.onFinishLoadImage(null);
        }

        @Override
        public void onCancel() {
            mHandler.onFinishLoadImage(null);
        }
    }

    private static class DownloadFileDelegateWrapper implements LDDownloadFileDelegate {

        private final long mTaskID;
        private final NetworkImageStore mStore = NetworkImageStore.shareStore();
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
