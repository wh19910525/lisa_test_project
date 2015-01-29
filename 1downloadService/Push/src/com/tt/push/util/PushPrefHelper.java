package com.tt.push.util;

import android.content.Context;
import android.content.SharedPreferences;
import co.lvdou.foundation.utils.extend.LDContextHelper;

public class PushPrefHelper {

    private static final String PREF_NAME = "push_preference";
    private static final String KEY_LAST_LOAD_PUSH_TASK_TIME = "last_load_push_task_time";
    private static final String KEY_LAST_CHECK_LOAD_PUSH_TASK_TIME = "last_check_load_push_task_time";
    private static PushPrefHelper mInstance;

    private SharedPreferences mPref;


    private PushPrefHelper() {
        mPref = LDContextHelper.getContext().getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
    }

    public static PushPrefHelper shareHelper() {
        if (mInstance == null)
            mInstance = new PushPrefHelper();

        return mInstance;
    }

    public void setLashLoadPushTaskTime(long timeInMills) {
        mPref.edit().putLong(KEY_LAST_LOAD_PUSH_TASK_TIME, timeInMills).commit();
    }

    public long lastLoadPushTaskTime() {
        return mPref.getLong(KEY_LAST_LOAD_PUSH_TASK_TIME, -1L);
    }

    public void setKeyLastCheckLoadPushTaskTime(long timeInMills) {
        mPref.edit().putLong(KEY_LAST_CHECK_LOAD_PUSH_TASK_TIME, timeInMills).commit();
    }

    public long lastCheckLoadPushTaskTime() {
        return mPref.getLong(KEY_LAST_CHECK_LOAD_PUSH_TASK_TIME, -1L);
    }
}
