package com.tt.push.model;

public class InstalledApkInfo {
    private int mTaskID;
    private int mTaskType;
    private String mPackageName;

    public InstalledApkInfo() {
    }

    public InstalledApkInfo init(int taskID, int taskType, String packageName) {
        mTaskID = taskID;
        mTaskType = taskType;
        mPackageName = packageName;

        return this;
    }

    public int taskID() {
        return mTaskID;
    }

    public int taskType() {
        return mTaskType;
    }

    public String packageName() {
        return mPackageName;
    }
}
