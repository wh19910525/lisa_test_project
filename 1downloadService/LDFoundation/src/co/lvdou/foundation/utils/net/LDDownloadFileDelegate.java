package co.lvdou.foundation.utils.net;

/**
 * 下载文件的回调接口。
 *
 * @author 郑一
 */
public interface LDDownloadFileDelegate {
    public static LDDownloadFileDelegate Null = new LDDownloadFileDelegate() {
        @Override
        public void onStart(String url) {
        }

        @Override
        public void onDownloading(long currentSizeInByte, long totalSizeInByte, int percentage, String speed, String remainTime) {
        }

        @Override
        public void onFail() {
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void onCancel() {
        }
    };

    /**
     * 文件开始下载时执行的回调。
     *
     * @param url 文件相关的下载地址
     */
    void onStart(String url);

    /**
     * 文件下载中执行的回调。
     *
     * @param currentSizeInByte 文件的当前大小，以字节为单位
     * @param totalSizeInByte   文件的总大小，以字节为单位
     * @param percentage        文件的下载进度
     * @param speed             当前的下载速度
     * @param remainTime        预计下载剩余时间
     */
    void onDownloading(long currentSizeInByte, long totalSizeInByte, int percentage, String speed, String remainTime);

    /**
     * 文件下载完毕时执行的回调。
     */
    void onComplete();

    /**
     * 文件下载失败时执行的回调。
     */
    void onFail();

    /**
     * 文件下载被取消时执行的回调。
     */
    void onCancel();
}
