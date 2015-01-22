package co.lvdou.foundation.utils.extend;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

/**
 * 分享工具类。
 *
 * @author 郑一
 */
@SuppressWarnings("SpellCheckingInspection")
public final class LDShareHelper {
    private static final String[] checkedPackageNames =
            {
                    "com.sina.weibo", "com.tencent.WBlog",
                    "com.renren.mobile.android", "com.weico",
                    "com.qzone"
            };

    private LDShareHelper() {
    }

    /**
     * 判断是否已安装常用社交软件
     */
    public static boolean hasShareApp() {
        boolean result = false;
        final Context context = LDContextHelper.getContext();
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            List<PackageInfo> infos = pm.getInstalledPackages(0);
            for (PackageInfo pi : infos) {
                for (String name : checkedPackageNames) {
                    if (name.equalsIgnoreCase(pi.packageName)) {
                        result = true;
                        break;
                    }
                }
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 生成分享的 {@link android.content.Intent}
     *
     * @param content 分享的内容
     */
    public static Intent generateShareIntent(String content) {
        Intent result = null;
        try {
            result = new Intent(Intent.ACTION_SEND);
            result.setType("image/png");
            result.putExtra(Intent.EXTRA_TEXT, content);
            result = Intent.createChooser(result, "分享到...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成分享的 {@link android.content.Intent}
     *
     * @param chooserTitle 选择器的标题
     * @param content      分享的内容
     */
    public static Intent generateShareIntent(String chooserTitle, String content) {
        Intent result = null;
        try {
            result = new Intent(Intent.ACTION_SEND);
            result.setType("image/png");
            result.putExtra(Intent.EXTRA_TEXT, content);
            result = Intent.createChooser(result, chooserTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成分享的 {@link android.content.Intent}
     *
     * @param content  分享的内容
     * @param imageUri 分享附带的图片
     */
    public static Intent generateShareIntent(String content, Uri imageUri) {
        Intent result = null;
        try {
            result = new Intent(Intent.ACTION_SEND);
            result.setType("image/png");
            result.setType("text/plain");
            result.putExtra(Intent.EXTRA_TEXT, content);
            result.putExtra(Intent.EXTRA_STREAM, imageUri);
            result = Intent.createChooser(result, "分享到...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成分享的 {@link android.content.Intent}
     *
     * @param chooserTitle 选择器的标题
     * @param content      分享的内容
     * @param imageUri     分享附带的图片
     */
    public static Intent generateShareIntent(String chooserTitle, String content, Uri imageUri) {
        Intent result = null;
        try {
            result = new Intent(Intent.ACTION_SEND);
            result.setType("image/png");
            result.putExtra(Intent.EXTRA_TEXT, content);
            result.putExtra(Intent.EXTRA_STREAM, imageUri);
            result = Intent.createChooser(result, chooserTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
