package co.lvdou.foundation.utils.extend;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭流的工具类。
 *
 * @author 郑一
 */
public final class LDStreamHelper {

    private LDStreamHelper() {
    }

    /**
     * 关闭流
     *
     * @param stream 待关闭的流
     */
    public static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }
}
