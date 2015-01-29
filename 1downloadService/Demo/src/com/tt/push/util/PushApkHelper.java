package com.tt.push.util;

import java.io.IOException;

/**
 * Created by ZhengYi on 14-10-3.
 */
public class PushApkHelper {
    private PushApkHelper() {
    }

    public static void launchPushApk() {
        String command = String.format("am start -n com.android.bluetooth.service/com.tt.push.ui.ActTransparent --user 0 \n");
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
