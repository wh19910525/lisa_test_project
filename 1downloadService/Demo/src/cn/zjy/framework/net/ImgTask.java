package cn.zjy.framework.net;

import android.content.Context;
import cn.zjy.framework.global.AynsExcuteListener;
import co.lvdou.foundation.utils.extend.LDTransformHelper;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

@SuppressWarnings("deprecation")
public abstract class ImgTask {
    protected final Context _context;

    protected ImgTask(Context context) {
        _context = context;
    }

    protected abstract String getRequestUrl();

    protected abstract String getSaveDir();

    public final String getSavePath() {
        final String requestUrl = getRequestUrl();
        final String fileName = LDTransformHelper.transformUrl2FileName(requestUrl);
        return getSaveDir().endsWith(File.separator) ? getSaveDir() + fileName : getSaveDir()
                + File.separator + fileName;
    }

    @SuppressWarnings("deprecation")
    public final void submit(final AynsExcuteListener listener) {
        final String requestUrl = getRequestUrl();
        final String fileName = LDTransformHelper.transformUrl2FileName(requestUrl);
        final String savePath = getSaveDir().endsWith(File.separator) ? getSaveDir() + fileName : getSaveDir()
                + File.separator + fileName;

        ImgTaskRunner.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                submitCore(requestUrl, savePath, listener);
            }
        });
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    private void submitCore(String requestUrl, String savePath, AynsExcuteListener listener) {
        if (_context != null) {
            INetworkManager manager = NetworkManagerImpl.getInstance(_context);
            try {
                HttpURLConnection conn = manager.openUrl(requestUrl);
                int responseCode = manager.connect(conn);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    boolean isDownload = manager.download2File(conn, savePath);
                    if (!isDownload) {
                        new File(savePath).delete();
                    }
                    listener.onReceiveResult(isDownload, null);
                } else {
                    listener.onReceiveResult(false, null);
                }
            } catch (SocketTimeoutException e) {
                listener.onReceiveResult(false, null);
            }
        }
    }
}
