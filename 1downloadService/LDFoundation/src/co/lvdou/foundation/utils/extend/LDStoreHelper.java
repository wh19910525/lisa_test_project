package co.lvdou.foundation.utils.extend;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * RAW目录下的文件获取工具类。
 *
 * @author 郑一
 */
public class LDStoreHelper {
    private LDStoreHelper() {
    }

    /**
     * 存储RAW目录下的文件到应用的缓存控件
     *
     * @param resId    RAW目录下的资源ID
     * @param filename 待存储的文件名
     * @throws Exception 当存储失败时抛出该异常
     */
    public static void saveIncludedFileIntoFilesFolder(int resId, String filename) throws Exception {
        final Context context = LDContextHelper.getContext();
        InputStream is = context.getResources().openRawResource(resId);
        @SuppressLint("WorldReadableFiles") FileOutputStream fos = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
        if (fos != null) {
            byte[] bytebuf = new byte[1024];
            int read;
            while ((read = is.read(bytebuf)) >= 0) {
                fos.write(bytebuf, 0, read);
            }
            is.close();
            fos.getChannel().force(true);
            fos.flush();
            fos.close();
        }
    }

    /**
     * 存储RAW目录下以ZIP格式压缩的文件到应用的缓存空间
     *
     * @param resId    RAW目录下的资源ID
     * @param filename 待存储的文件名
     * @throws Exception 当存储失败时抛出该异常
     */
    public static void saveIncludedZippedFileIntoFilesFolder(int resId, String filename) throws Exception {
        final Context context = LDContextHelper.getContext();
        InputStream is = context.getResources().openRawResource(resId);
        @SuppressLint("WorldReadableFiles") FileOutputStream fos = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
        if (fos != null) {
            GZIPInputStream gzis = new GZIPInputStream(is);
            byte[] bytebuf = new byte[1024];
            int read;
            while ((read = gzis.read(bytebuf)) >= 0) {
                fos.write(bytebuf, 0, read);
            }
            gzis.close();
            fos.getChannel().force(true);
            fos.flush();
            fos.close();
        }
    }

    /**
     * 存储RAW目录下用 {@link co.lvdou.foundation.utils.extend.ObfuseTableBase64} 加密的文件到应用的缓存空间
     *
     * @param resId    RAW目录下的资源ID
     * @param filename 待存储的文件名
     * @throws Exception 当存储失败时抛出该异常
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveIncludedEncodedFileIntoFilesFolder(int resId, String filename) throws Exception {
        final Context context = LDContextHelper.getContext();
        final InputStream is = context.getResources().openRawResource(resId);
        @SuppressLint("WorldReadableFiles") FileOutputStream fos = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
        if (fos != null) {
            byte[] bytebuf = new byte[is.available()];
            is.read(bytebuf);
            bytebuf = ObfuseTableBase64.decode(bytebuf);
            fos.write(bytebuf);
            is.close();
            fos.getChannel().force(true);
            fos.flush();
            fos.close();
        }
    }

    /**
     * 存储RAW目录下用 {@link co.lvdou.foundation.utils.extend.ObfuseTableBase64} 加密且以ZIP格式压缩的文件到应用的缓存空间
     *
     * @param resId    RAW目录下的资源ID
     * @param filename 待存储的文件名
     * @throws Exception 当存储失败时抛出该异常
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveIncludedEncodedZippedFileIntoFilesFolder(int resId, String filename) throws Exception {
        final Context appCtx = LDContextHelper.getContext();
        InputStream is = appCtx.getResources().openRawResource(resId);
        @SuppressLint("WorldReadableFiles") FileOutputStream fos = appCtx.openFileOutput(filename, Context.MODE_WORLD_READABLE);
        if (fos != null) {
            GZIPInputStream gzis = new GZIPInputStream(is);
            byte[] bytebuf = new byte[is.available()];
            gzis.read(bytebuf);
            bytebuf = ObfuseTableBase64.decode(bytebuf);
            fos.write(bytebuf);
            gzis.close();
            fos.getChannel().force(true);
            fos.flush();
            fos.close();
        }

    }
}
