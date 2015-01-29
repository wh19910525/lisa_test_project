package com.tt.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tt.push.service.PushPluginManager;

public class DaemonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PushPluginManager.start(context);
    }
}
