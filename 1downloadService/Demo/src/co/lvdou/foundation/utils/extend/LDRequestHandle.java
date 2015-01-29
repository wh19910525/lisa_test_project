package co.lvdou.foundation.utils.extend;

/**
 * 网络请求的处理器。
 *
 * @author 郑一
 */
public interface LDRequestHandle {

    /**
     * 打断当前正在执行的网络请求
     *
     * @param interruptIfRunning 是否在网络请求已经开始后打断
     */
    void cancel(boolean interruptIfRunning);
}
