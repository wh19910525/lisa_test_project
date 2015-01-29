package co.lvdou.foundation.utils.net;

import android.content.Context;
import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import org.apache.http.Header;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 网络请求客户端,封装了post和get方式的网络请求以及下载文件的方法。
 *
 * @author 郑一
 */
public final class LDHttpClient {
    private static final int MAX_CONNECTION_DEFAULT = 5;
    private static final int TIME_OUT_DEFAULT = Constants.NETWORK_READ_MAX_TIME_LONG;
    private static final Executor mThreadPool = Executors.newFixedThreadPool(3);
    private static AsyncHttpClient mDefaultClient = new AsyncHttpClient();
    private static boolean mIsInitialize = false;

    /**
     * 以get方式请求网络数据。
     *
     * @param url            服务器地址
     * @param params         请求参数对
     * @param responseHandle 网络数据回调的相关接口
     */
    public static LDRequestHandle get(String url, LDRequestParams params, LDResponseHandle responseHandle) {
        return get(null, url, params, responseHandle);
    }

    /**
     * 以get方式请求网络数据。
     *
     * @param context        程序上下文,传入程序上下文的话将可以调用方法
     *                       {@link #cancelRelatedRequest(android.content.Context)}
     *                       取消相关的网络请求
     * @param url            服务器地址
     * @param params         请求参数对
     * @param responseHandle 网络数据回调的相关接口
     */
    public static LDRequestHandle get(Context context, String url, LDRequestParams params, LDResponseHandle responseHandle) {
        ensureInitialized();

        if (responseHandle == null) {
            responseHandle = LDResponseHandle.NULL;
        }

        RequestHandle realHandle = null;
        if (isUrlInvalid(url) || !LDDeviceInfoHelper.defaultHelper().hasActiveNetwork()) {
            responseHandle.onFail();
        } else {
            final LDResponseHandleProxy responseHandleProxy = new LDResponseHandleProxy(responseHandle);
            realHandle = mDefaultClient.get(context, url, params, responseHandleProxy);
        }

        return new LDDefaultRequestHandle(realHandle);
    }

    /**
     * 以post方式请求网络数据。
     *
     * @param url            服务器地址
     * @param params         请求参数对
     * @param responseHandle 网络数据回调的相关接口
     */
    public static LDRequestHandle post(String url, LDRequestParams params, LDResponseHandle responseHandle) {
        return post(null, url, params, responseHandle);
    }

    /**
     * 以post方式请求网络数据。
     *
     * @param context        程序上下文,传入程序上下文的话将可以调用方法
     *                       {@link #cancelRelatedRequest(android.content.Context)}
     *                       取消相关的网络请求
     * @param url            服务器地址
     * @param params         请求参数对
     * @param responseHandle 网络数据回调的相关接口
     */
    public static LDRequestHandle post(Context context, String url, LDRequestParams params, LDResponseHandle responseHandle) {
        ensureInitialized();

        if (responseHandle == null) {
            responseHandle = LDResponseHandle.NULL;
        }

        RequestHandle realHandle = null;
        if (isUrlInvalid(url) || !LDDeviceInfoHelper.defaultHelper().hasActiveNetwork()) {
            responseHandle.onFail();
        } else {
            final LDResponseHandleProxy responseHandlerProxy = new LDResponseHandleProxy(responseHandle);
            realHandle = mDefaultClient.post(context, url, params, responseHandlerProxy);
        }

        return new LDDefaultRequestHandle(realHandle);
    }

    /**
     * 下载服务器地址相关文件至本地。
     *
     * @param url      待下载文件的服务器地址
     * @param path     本地的保存路径
     * @param listener 下载进度的回调接口
     * @return
     */
    public static LDRequestHandle download(String url, File path, LDDownloadFileDelegate listener) {
        final LDDownloadFileTask task = new LDDownloadFileTask(LDContextHelper.getContext(), url, path, listener);
        mThreadPool.execute(task);
        return new LDDownloadRequestHandle(task);
    }

    /**
     * 取消程序上下文相关的网络请求
     *
     * @param context 程序上下文
     */
    public static void cancelRelatedRequest(final Context context) {
        ensureInitialized();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDefaultClient.cancelRequests(context, true);
            }
        }).start();

    }

    private static boolean isUrlInvalid(String url) {
        return TextUtils.isEmpty(url);
    }

    private static void ensureInitialized() {
        if (!mIsInitialize) {
            mIsInitialize = true;
            mDefaultClient.setTimeout(TIME_OUT_DEFAULT);
        }
        // if (mDefaultClient == null) {
        // mDefaultClient = new AsyncHttpClient();
        // mDefaultClient.setMaxConnections(MAX_CONNECTION_DEFAULT);
        // mDefaultClient.setTimeout(TIME_OUT_DEFAULT);
        // }
    }

    private static class LDResponseHandleProxy extends AsyncHttpResponseHandler {
        private final LDResponseHandle _handle;

        public LDResponseHandleProxy(LDResponseHandle handle) {
            if (handle == null) {
                handle = LDResponseHandle.NULL;
            }

            _handle = handle;
        }

        @Override
        public boolean getUseSynchronousMode() {
            return false;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseData, Throwable throwable) {
            _handle.onFail();
        }

        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            super.onProgress(bytesWritten, totalSize);
            int percentage = (totalSize > bytesWritten && totalSize != 0) ? bytesWritten * 100 / totalSize : 0;
            _handle.onPregress(percentage);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            if (response != null && response.length > 0) {
                _handle.onCallback(new String(response));
            } else {
                _handle.onFail();
            }
        }
    }

    private static class LDDefaultRequestHandle implements LDRequestHandle {
        private final RequestHandle _realHandle;

        LDDefaultRequestHandle(RequestHandle handler) {
            _realHandle = handler;
        }

        @Override
        public void cancel(boolean interruptIfRunning) {
            if (_realHandle != null) {
                _realHandle.cancel(true);
            }
        }
    }

    private static class LDDownloadRequestHandle implements LDRequestHandle {
        private final LDDownloadFileTask _task;

        LDDownloadRequestHandle(final LDDownloadFileTask task) {
            _task = task;
        }

        @Override
        public void cancel(boolean interruptIfRunning) {
            if (_task != null) {
                _task.cancelTask();
            }
        }

    }
}
