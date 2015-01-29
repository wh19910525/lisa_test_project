package com.tt.push.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDStreamHelper;
import co.lvdou.foundation.utils.extend.Logout;

public class ShellManager {
    private static final ShellManager mShareManager = new ShellManager();

    private BufferedReader mReader;
    private BufferedWriter mWriter;
    private boolean mIsPrepared = false;
    private boolean mIsObtainRootPermission = false;

    private ShellManager() {
    }

    /**
     * 鑾峰彇ShellManager鐨勫疄渚�
     */
    public static ShellManager shareManager() {
        synchronized (mShareManager) {
            if (!mShareManager.mIsPrepared) {
                try {
                    mShareManager.prepareShell();
                    mShareManager.checkIsObtainRootPermission();
                } catch (IOException e) {
                    e.printStackTrace();
                    mShareManager.mIsPrepared = false;
                    mShareManager.mIsObtainRootPermission = false;
                }
            }
        }

        return mShareManager;
    }

    /**
     * @return 鏄惁鑾峰彇浜哛OOT鏉冮檺
     */
    public synchronized boolean isObtainRootPermission() {
        return mIsObtainRootPermission;
    }

    /**
     * 鍐欏叆涓�鏉″懡浠�
     *
     * @param command 鍛戒护
     */
    public synchronized void writeCommand(String command) {
        writeCommand(command, false);
    }

    /**
     * 鍐欏叆涓�鏉″懡浠�
     *
     * @param command 鍛戒护
     * @param force   寮哄埗鍐欏叆涓�鏉″懡浠�
     */
    private synchronized void writeCommand(String command, boolean force) {
        if (!mIsPrepared && !force) return;

        command = appendLineBreak(command);
        Logout.out(String.format("attempt to write command: %s", command));
        try {
            mWriter.write(command);
            mWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeShell();
        }
    }

    /**
     * 璇诲彇涓嬩竴鏉″懡浠�
     *
     * @return 杩斿洖涓嬩竴鏉″懡浠ゆ垨鑰卬ull(shell鐢变簬寮傚父閫�鍑轰簡鎴栬�呮病鏈夋洿澶氭暟鎹�)
     */
    public synchronized String readNextCommand() {
        if (!mIsPrepared) return null;

        try {
            String nextCommand = mReader.readLine();
            Logout.out(String.format("read command: %s", nextCommand));
            return nextCommand;
        } catch (IOException e) {
            closeShell();
        }

        return null;
    }

    /**
     * 璇诲彇褰撳墠鐨勮緭鍏ョ紦瀛樼洿鍒板叆鍒版煇涓爣蹇�
     *
     * @param tag
     * @return 鏄惁鍦ㄨ緭鍏ョ紦瀛樹腑璇诲彇鍒版煇涓爣蹇�
     */
    public synchronized boolean readCommandUntilMatchTag(String tag) {
        if (TextUtils.isEmpty(tag)) return false;

        String line;
        try {
            while ((line = mReader.readLine()) != null) {
                Logout.out(String.format("read command: %s", line));
                if (line.contains(tag) && !line.contains("echo"))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeShell();
        }

        return false;
    }

    private String appendLineBreak(String line) {
        return line.endsWith("\n") ? line : line + "\n";
    }

    private void prepareShell() throws IOException {
        String rootCommand = "su\n";
        Process process = Runtime.getRuntime().exec(rootCommand);
        mWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        mReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        mIsPrepared = true;
    }

    private void closeShell() {
        LDStreamHelper.close(mWriter);
        LDStreamHelper.close(mReader);
        mIsPrepared = false;
        mIsObtainRootPermission = false;
    }

    private void checkIsObtainRootPermission() {
        writeCommand("id", true);
        mIsObtainRootPermission = readCommandUntilMatchTag("uid=0");
    }
}
