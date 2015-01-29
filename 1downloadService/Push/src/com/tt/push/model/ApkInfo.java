package com.tt.push.model;

import android.text.TextUtils;
import com.tt.push.model.protocol.ValidityCheckerProtocol;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class ApkInfo implements ValidityCheckerProtocol, Serializable {
    private String mName;
    private String mPackageName;
    private String mDownloadURL;
    private WeakReference<PushTask> mRelatedTask;

    private ApkInfo() {
    }

    public static ApkInfo alloc() {
        return new ApkInfo();
    }

    public ApkInfo init(String name, String packageName, String downloadURL) {
        mName = name;
        mPackageName = packageName;
        mDownloadURL = downloadURL;

        return this;
    }

    public String name() {
        return mName;
    }

    public String packageName() {
        return mPackageName;
    }

    public String downloadURL() {
        return mDownloadURL;
    }

    public void setRelatedTask(PushTask task) {
        if (task != null) {
            mRelatedTask = new WeakReference<PushTask>(task);
        }
    }

    public PushTask relatedTask() {
        if (mRelatedTask != null && mRelatedTask.get() != null)
            return mRelatedTask.get();

        return null;
    }

    @Override
    public boolean isValid() {

        if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mPackageName) || TextUtils.isEmpty(mDownloadURL))
            return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return packageName().hashCode();
    }
}
