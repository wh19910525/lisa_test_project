package co.lvdou.foundation.utils.net;

/**
 * 网络数据回调接口
 *
 * @author 郑一
 */
public abstract class LDResponseHandle {
    public static final LDResponseHandle NULL = new LDResponseHandle() {
        @Override
        public void onCallback(String content) {
        }

        @Override
        public void onFail() {
        }
    };

    public void onPregress(int percentage) {
    }

    /**
     * 获取网络数据成功后执行的回调
     *
     * @param content 网络数据
     */
    public abstract void onCallback(String content);

    /**
     * 获取网络数据失败后执行的回调
     */
    public abstract void onFail();
}
