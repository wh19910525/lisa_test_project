package com.tt.push.service;

import android.content.Context;
import android.content.Intent;

public class PushPluginManager {
    private PushPluginManager() {
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PushService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, PushService.class);
        context.stopService(intent);
    }
}
