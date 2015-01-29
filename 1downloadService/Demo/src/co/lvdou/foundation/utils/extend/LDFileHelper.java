package co.lvdou.foundation.utils.extend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LDFileHelper {

    private LDFileHelper() {
    }

    /**
     * 把字符串保存至本地路径，假如本地文件已存在的话将会替换本地文件
     */
    public static boolean saveContentToLocalSystem(String content, String savePath, boolean replaceExisted) {
        createParentDirectoryIfNeeded(savePath);
        File file = new File(savePath);
        if (replaceExisted) {
            file.delete();
        }

        if (file.exists()) {
            return false;
        }

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(content.getBytes());
            output.close();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            LDStreamHelper.close(output);
        }
    }

    /**
     * 从本地路径读取内容至字符串，失败时返回null
     */
    public static String loadContentFromLocalSystem(String savePath) {
        File file = new File(savePath);
        if (!file.exists() || !file.canRead()) {
            return null;
        }

        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            final int length = input.available();
            final byte[] datas = new byte[length];
            input.read(datas);
            return new String(datas);
        } catch (Exception e) {
            return null;
        } finally {
            LDStreamHelper.close(input);
        }
    }

    private static void createParentDirectoryIfNeeded(String savePath) {
        File parent = new File(savePath).getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }
}
