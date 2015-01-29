package co.lvdou.foundation.utils.root;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDApkHelper;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import co.lvdou.foundation.utils.extend.LDDeviceInfoHelper;

import java.io.File;
import java.util.List;

/**
 * 内核相关命令的构造器，用于快速创建各种经常使用的内核命令。
 *
 * @author 郑一
 */
public final class LDCommandBuilder {
    private final StringBuilder mBuilder = new StringBuilder();

    /**
     * 添加安装busybox的命令。
     *
     * @param busyboxPath busybox文件的绝对路径
     */
    public LDCommandBuilder addSetupBusyboxCommand(String busyboxPath) {
        if (TextUtils.isEmpty(busyboxPath) || !new File(busyboxPath).exists()) {
            return this;
        }

        if (new File("/system/bin/busybox").exists() || new File("/system/xbin/busybox").exists()) {
            return this;
        }

        appendCommand("chmod 755 " + busyboxPath + "\n");
        appendCommand(String.format("alias busybox=%s\n", busyboxPath));

        return this;
    }

    /**
     * 前提:  已经获取了ROOT权限。 <br/>
     * 添加静默安装apk的命令。
     *
     * @param pkg          apk的相关包名
     * @param path         apk文件的绝对路径
     * @param executedMark 命令执行完毕后的打印标记
     */
    public LDCommandBuilder addSilenceInstallCommand(String pkg, String path, String executedMark) {

        if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(path) || !new File(path).exists()) {
            return this;
        }

        if (!LDApkHelper.isInstalled(pkg)) {
            appendCommand(String.format("pm install %s\n", path));
        }
        addPrintCommand(executedMark);

        return this;
    }

    /**
     * 前提:  已经获取了ROOT权限。 <br/>
     * 添加激活应用的命令。
     *
     * @param pkg 待激活应用的相关包名
     */
    public LDCommandBuilder addEnablePackageCommand(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return this;
        }

        appendCommand(String.format("pm enable %s\n", pkg));
        return this;
    }

    /**
     * 前提:  已经获取了ROOT权限。 <br/>
     * 添加冻结应用的命令。
     *
     * @param pkg 待冻结应用的相关包名
     */
    public LDCommandBuilder addDisablePackageCommand(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return this;
        }

        appendCommand(String.format("pm disable %s\n", pkg));

        return this;
    }

    /**
     * 前提:  已经获取了ROOT权限。 <br/>
     * 添加卸载应用的命令。
     *
     * @param pkg 待卸载应用的相关包名
     */
    public LDCommandBuilder addUninstallPackageCommand(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return this;
        }

        appendCommand(String.format("pm uninstall %s", pkg));
        return this;
    }

    /**
     * 前提:  已经获取了ROOT权限。 <br/>
     * 添加激活应用特定组件的命令。
     *
     * @param packageName   相关应用的包名
     * @param componentName 待激活组件的全称
     */
    public LDCommandBuilder addEnableComponentCommand(String packageName, String componentName) {
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(componentName)) {
            return this;
        }

        appendCommand(String.format("pm enable %s/%s\n", packageName, componentName));

        return this;
    }

    /**
     * 前提:  已经获取了ROOT权限。 <br/>
     * 添加冻结应用特定组件的命令。
     *
     * @param packageName   相关应用的包名
     * @param componentName 待冻结组件的全称
     */
    public LDCommandBuilder addDisableComponentCommand(String packageName, String componentName) {
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(componentName)) {
            return this;
        }

        appendCommand(String.format("pm disable %s/%s\n", packageName, componentName));

        return this;
    }

    /**
     * 添加打印文本的命令。
     *
     * @param text 待打印的文本
     */
    public LDCommandBuilder addPrintCommand(String text) {
        if (!TextUtils.isEmpty(text)) {
            appendCommand(String.format("echo '%s'\n", text));
        }

        return this;
    }

    /**
     * 添加运行应用的命令。
     *
     * @param pkg 待运行应用的相关包名
     */
    public LDCommandBuilder addLaunchApplicationCommand(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return this;
        }

        final Context context = LDContextHelper.getContext();
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            final Intent queryIntent = new Intent();
            queryIntent.setAction(Intent.ACTION_MAIN);
            queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            queryIntent.setPackage(pkg);
            List<ResolveInfo> infos = pm.queryIntentActivities(queryIntent, 0);
            if (infos != null && infos.size() > 0) {
                final ResolveInfo resolveInfo = infos.get(0);
                if (resolveInfo.activityInfo != null) {
                    final String launcherActivityName = resolveInfo.activityInfo.name;
                    appendCommand(String.format("am start -n %s/%s\n", pkg, launcherActivityName));
                }
            }
        }
        return this;
    }

    /**
     * 前提:  已经获取了ROOT权限且安装了busybox。 <br/>
     * 添加停止正在运行应用的命令
     *
     * @param pkg 正在运行应用的相关包名
     */
    public LDCommandBuilder addKillRunningApplicationCommand(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return this;
        }

        appendCommand(String.format("busybox killall %s\n", pkg));
        return this;
    }

    /**
     * 前提:  已经获取ROOT权限。 <br/>
     * 挂载system分区
     */
    public LDCommandBuilder addMountSystemPartitionCommand(String flag) {
        appendCommand(String.format("busybox mount -o remount,rw /system && echo '%s'", flag));
        appendCommand(String.format("mount -o remount,rw /system && echo '%s'", flag));
        return this;
    }

    /**
     * 前提:  已经挂载了system分区。   <br/>
     * 添加移除system分区中所有授权软件的命令
     */
    public LDCommandBuilder addRemoveAllSuperUserInSystemPartitionCommand() {
        appendCommand("rm -r /system/app/Super*");
        appendCommand("rm -r /system/app/super*");
        appendCommand("rm -r /system/app/su360*");
        appendCommand("rm -r /system/app/King*");
        appendCommand("rm -r /system/app/king*");
        return this;
    }

    /**
     * 前提:  已经挂载了system分区。  <br/>
     * 添加移除su的命令
     */
    public LDCommandBuilder addRemoveAllSuCommand() {
        appendCommand("rm -r /system/xbin/su");
        appendCommand("rm -r /system/bin/su");
        appendCommand("rm -r /system/xbin/ku.sud");
        appendCommand("rm -r /system/bin/.ext/.su");
        appendCommand("rm -r /system/xbin/ksud");
        appendCommand("rm -r /system/xbin/daemonsu");
        return this;
    }

    /**
     * 前提:  已经挂载了system分区。  <br/>
     *
     * @param suPath su文件的绝对路径
     */
    public LDCommandBuilder addInstallSuCommand(String suPath, String recoveryScriptPath) {
        addRemoveAllSuCommand();
        addCopyCommand(suPath, "/system/xbin/su");
        appendCommand("chown 0:0 /system/xbin/su");
        appendCommand("chmod 6755 /system/xbin/su");
        appendCommand("busybox ln -s /system/xbin/su /system/bin/su");
        appendCommand("ln -s /system/xbin/su /system/bin/su");
        appendCommand("chattr -i /system/xbin/su");
        appendCommand("busybox chattr -i /system/xbin/su");
        appendCommand("chattr -i /system/bin/su");
        appendCommand("busybox chattr -i /system/bin/su");
        if (LDDeviceInfoHelper.defaultHelper().getMobileSDKVersion() >= 18) {
            File recoveryScript = new File(recoveryScriptPath);
            if (recoveryScript.exists() && !recoveryScript.isDirectory()) {
                final String destScriptPath = "/system/etc/install-recovery.sh";
                addCopyCommand(recoveryScriptPath, destScriptPath);
                appendCommand("chattr -i " + destScriptPath);
                appendCommand("busybox chattr -i " + destScriptPath);
                appendCommand("chmod 755 " + destScriptPath);
            }
        } else {
            appendCommand("rm -r /system/etc/install-recovery.sh");
        }
        return this;
    }

    /**
     * 添加自定义命令.
     *
     * @param cmd 自定义命令
     */
    public LDCommandBuilder addCustomizeCommand(String cmd) {
        if (!TextUtils.isEmpty(cmd)) {
            appendCommand(cmd);
        }

        return this;
    }

    /**
     * 添加复制命令
     *
     * @param targetPath 目标文件的绝对绝对路径
     * @param destPath   希望复制到的绝对路径
     */
    public LDCommandBuilder addCopyCommand(String targetPath, String destPath) {
//        if (new File(targetPath).exists()) {
//            if (new File(destPath).exists()) {
//                appendCommand("rm -f " + destPath);
//            }
//            appendCommand(String.format("dd if=%s of=%s", targetPath, destPath));
//        }

        appendCommand(String.format("dd if=%s of=%s", targetPath, destPath));
        return this;
    }

    /**
     * 构建已添加命令的相关字符串.
     */
    public String build() {
        return mBuilder.toString();
    }

    /**
     * 清除已添加的命令。
     */
    public void clear() {
        mBuilder.setLength(0);
    }

    private void appendCommand(String cmd) {
        cmd = cmd.endsWith("?") ? cmd : cmd + "\n";
        mBuilder.append(cmd);
    }
}
