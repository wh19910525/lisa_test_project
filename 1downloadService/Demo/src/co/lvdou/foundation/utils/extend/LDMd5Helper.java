package co.lvdou.foundation.utils.extend;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * MD5码的工具类。
 *
 * @author 郑一
 */
final public class LDMd5Helper {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    private LDMd5Helper() {
    }

    /**
     * 根据文件内容生成md5校验码，文件不存在将返回空。
     *
     * @param file 待生成MD5吗的文件绝对路径
     */
    public static String generateMD5(File file) {
        String result = "";
        if (file.exists()) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                FileInputStream input = new FileInputStream(file);
                byte[] datas = new byte[1024];
                int length;
                while ((length = input.read(datas)) != -1) {
                    md5.update(datas, 0, length);
                }
                input.close();
                result = toHexString(md5.digest());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 判断该md5校验码是否合法。
     */
    public static boolean isMd5Valid(String md5) {
        boolean result = false;
        if (md5 != null && md5.length() == 32) {
            result = true;
        }
        return result;
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(HEX_DIGITS[(aB & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[aB & 0x0f]);
        }
        return sb.toString();
    }
}
