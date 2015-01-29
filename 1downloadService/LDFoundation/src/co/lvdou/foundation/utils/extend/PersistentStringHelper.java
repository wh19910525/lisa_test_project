package co.lvdou.foundation.utils.extend;

import android.text.TextUtils;

import java.io.*;

/**
 * 持久化字符串的工具类。
 *
 * @author 郑一
 */
public final class PersistentStringHelper {
    private PersistentStringHelper() {
    }

    /**
     * 把字符串内容存储至本地。
     *
     * @param content       待存储的字符串
     * @param path          文件保存的绝对路径
     * @param deleteIfExist 设置当文件存在时是否删除已存在文件
     */
    @SuppressWarnings({"UnusedDeclaration", "ResultOfMethodCallIgnored"})
    public static void saveToFile(String content, String path, boolean deleteIfExist) {
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(content)) {
            File f = new File(path);

            if (f.exists()) {
                if (f.isDirectory() || !deleteIfExist) {
                    return;
                }
                f.delete();
            }

            BufferedOutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(f));
                byte[] datas = content.getBytes();
                output.write(datas);
                output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LDStreamHelper.close(output);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String loadFromFile(String path) {
        String result = null;
        if (!TextUtils.isEmpty(path)) {
            final File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                BufferedInputStream input = null;
                try {
                    input = new BufferedInputStream(new FileInputStream(f));
                    final int size = input.available();
                    byte[] datas = new byte[size];
                    input.read(datas);
                    result = new String(datas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LDStreamHelper.close(input);
                }
            }
        }
        return result;
    }
}
