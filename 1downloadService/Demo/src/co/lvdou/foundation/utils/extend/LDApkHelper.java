package co.lvdou.foundation.utils.extend;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import co.lvdou.foundation.utils.root.LDCommandBuilder;
import co.lvdou.foundation.utils.root.LDRootManager;

import java.io.File;

/**
 * APK文件的工具类，包含获取APK信息、安装APK、卸载APK等API。
 *
 * @author 郑一
 */
public final class LDApkHelper {
    private LDApkHelper() {
    }

    /**
     * 安装APK。
     *
     * @param path apk文件的绝对路径
     */
    public static void install(String path) {
        final Context context = LDContextHelper.getContext();
        final Intent intent = generateInstallIntent(path);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 获取应用的版本号。
     *
     * @param packageName 相关应用的包名
     */
    public static int getVersionCode(String packageName) {
        int result;
        try {
            final Context context = LDContextHelper.getContext();
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
                result = info.versionCode;
            } else {
                result = -1;
            }
        } catch (NameNotFoundException e) {
            result = -1;
        }
        return result;

    }

    /**
     * 静默安装APK。
     *
     * @param pkg  待安装APK的包名
     * @param path APK文件的绝对路径
     */
    @SuppressWarnings("UnusedDeclaration")
    public static boolean installSilence(String pkg, String path) {
        boolean result = false;

        Context context = LDContextHelper.getContext();
        if (LDRootManager.isObtainedRootPermission()) {
            LDCommandBuilder builder = new LDCommandBuilder();
            final String mark = String.format("gamecenter:package %s install complete", pkg);
            builder.addSilenceInstallCommand(pkg, path, mark);
            LDRootManager.doCommand(builder.build());

            String line;
            while ((line = LDRootManager.readCommand()) != null) {
                if (line.contains(mark)) {
                    if (LDApkHelper.isInstalled(pkg)) {
                        result = true;
                    }
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 卸载应用。
     *
     * @param pkg 待卸载应用的相关包名
     */
    public static void uninstall(String pkg) {
        Context context = LDContextHelper.getContext();
        if (context != null && !TextUtils.isEmpty(pkg)) {
            final Uri uri = Uri.parse("package:" + pkg);
            final Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 判断应用是否已经安装。
     *
     * @param pkg 应用相关的包名
     */
    public static boolean isInstalled(String pkg) {
        boolean result = false;
        if (!TextUtils.isEmpty(pkg)) {
            Context context = LDContextHelper.getContext();
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                try {
                    final PackageInfo info = pm.getPackageInfo(pkg, 0);
                    if (info != null) {
                        result = true;
                    }
                } catch (Exception e) {
                    result = false;
                }
            }

        }
        return result;
    }

    /**
     * 获取App安装路径，假如App尚未安装将返回空
     *
     * @param packageName App包名
     */
    @SuppressWarnings("EmptyCatchBlock")
    public static String getAppInstallPath(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }

        final Context context = LDContextHelper.getContext();
        final PackageManager pm = context.getPackageManager();
        try {
            if (pm != null) {
                final ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                return ai.sourceDir;
            }
        } catch (NameNotFoundException e) {
        }

        return null;
    }

    /**
     * 启动已安装应用，未安装将不执行任何操作。
     *
     * @param pkg 待启动的应用相关的包名
     */
    public static void launch(String pkg) {
        final Intent intent = generateLaunchIntent(pkg);
        if (intent != null) {

            LDContextHelper.getContext().startActivity(intent);
        }
    }

    /**
     * 生成启动已安装应用的 {@link android.app.PendingIntent}，未安装将返回空。
     *
     * @param pkg 待启动应用的相关包名
     */
    public static PendingIntent generateLaunchPendingIntent(String pkg) {
        PendingIntent pi = null;

        final Intent intent = generateLaunchIntent(pkg);
        if (intent != null) {
            pi = PendingIntent.getActivity(LDContextHelper.getContext(), 0, intent, 0);
        }

        return pi;
    }

    /**
     * 生成安装APK的 {@link android.app.PendingIntent}，APK文件不存在或无效将返回空。
     *
     * @param path 待安装APK的绝对路径
     */
    public static PendingIntent generateInstallPendingIntent(String path) {
        PendingIntent pi = null;
        final Context context = LDContextHelper.getContext();
        final Intent intent = generateInstallIntent(path);
        if (intent != null) {
            pi = PendingIntent.getActivity(context, 0, intent, 0);
        }

        return pi;
    }

    private static Intent generateLaunchIntent(String pkg) {
        Intent intent = null;
        final Context context = LDContextHelper.getContext();
        if (context != null && !TextUtils.isEmpty(pkg)) {
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                intent = pm.getLaunchIntentForPackage(pkg);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }

        }

        return intent;
    }

    private static Intent generateInstallIntent(String path) {
        Intent intent = null;
        final Context context = LDContextHelper.getContext();
        if (context != null && !TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                intent = new Intent(Intent.ACTION_VIEW);
                final Uri uri = Uri.fromFile(f);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }

        return intent;
    }
}
