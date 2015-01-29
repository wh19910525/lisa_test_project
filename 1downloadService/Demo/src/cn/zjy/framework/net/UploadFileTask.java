package cn.zjy.framework.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import cn.zjy.framework.global.AynsExcuteListener;
import co.lvdou.foundation.utils.extend.LDStreamHelper;

public abstract class UploadFileTask {
    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
    // 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    /**
     * 上传文件的最大长度 *
     */
    private static final long MAX_UPLOAD_FILE_SIZE = 1024 * 1024 * 10;
    private final Context _context;
    private boolean _isCanceled = false;
    private HttpURLConnection conn;

    protected UploadFileTask(Context context) {
        _context = context.getApplicationContext();
    }

    protected abstract String getRequestUrl();

    protected abstract String getUploadFilePath();

    protected HashMap<String, String> getParams() {
        return null;
    }

    public final void uploadAync(final AynsExcuteListener resultListener) {
        uploadAync(ProgressListener.NULL, resultListener);
    }

    /**
     * 异步上传文件 *
     */
    public final void uploadAync(final ProgressListener progressListener, final AynsExcuteListener resultListener) {
        new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (isValidFile() == false) {
                    resultListener.onReceiveResult(false, null);
                } else {
                    final INetworkManager manager = NetworkManagerImpl.getInstance(_context);
                    try {
                        conn = manager.openUrl(getRequestUrl(), "POST");
                        if (conn != null) {
                            final String uploadFilePath = getUploadFilePath();
                            setupProperty(conn);
                            conn.connect();
                            boolean isUploadSuccess = uploadFileToService(conn, uploadFilePath, getParams(),
                                    progressListener);
                            if (isUploadSuccess) {
                                final String uploadFileUrl = getUploadFileUrl(conn);
                                resultListener.onReceiveResult(true, uploadFileUrl);
                            } else {
                                resultListener.onReceiveResult(false, null);
                            }
                        } else {
                            resultListener.onReceiveResult(false, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        resultListener.onReceiveResult(false, null);
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 取消文件上传 *
     */
    public final void cancel() {
        _isCanceled = true;
        if (conn != null) {
            conn.disconnect();
        }
    }

    private boolean isValidFile() {
        boolean result = false;
        File file = new File(getUploadFilePath());
        if (file.exists() && file.isDirectory() == false) {
            if (file.length() < MAX_UPLOAD_FILE_SIZE) {
                result = true;
            }
        }
        return result;
    }

    private void setupProperty(HttpURLConnection conn) throws IOException {
        conn.setConnectTimeout(Constants.NETWORK_OPEN_MAXTIME_LONG);
        conn.setReadTimeout(Constants.NETWORK_READ_MAXTIME_LONG);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
    }

    // private boolean uploadFileToService(HttpURLConnection conn, String
    // uploadFilePath, ProgressListener listener)
    // throws Exception
    // {
    // return uploadFileToService(conn, uploadFilePath, null, listener);
    // }

    private boolean uploadFileToService(HttpURLConnection conn, String uploadFilePath, HashMap<String, String> params,
                                        ProgressListener listener) throws Exception {
        boolean result = false;

        BufferedOutputStream output = null;
        FileInputStream input = null;
        try {
            final File uploadFile = new File(uploadFilePath);

            final StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            if (params != null && params.size() > 0) {

                for (String key : params.keySet()) {
                    String value = params.get(key).toString();
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"" + LINE_END)
                            .append(LINE_END).append(value).append(LINE_END).append(PREFIX).append(BOUNDARY)
                            .append(LINE_END);
                }
            }

            sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + uploadFile.getName() + "\""
                    + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
            sb.append(LINE_END);

            output = new BufferedOutputStream(conn.getOutputStream());
            input = new FileInputStream(new File(uploadFilePath));

            output.write(sb.toString().getBytes());

            final int totalLength = (int) new File(uploadFilePath).length();
            int currentLength = 0;
            byte[] tmp = null;
            if (totalLength <= 50) {
                tmp = new byte[totalLength];
            } else {
                tmp = new byte[totalLength / 25];
            }
            int readLength = -1;
            while (_isCanceled == false && (readLength = input.read(tmp)) != -1) {
                output.write(tmp, 0, readLength);
                Thread.sleep(100L);
                output.flush();
                currentLength += readLength;
                listener.progress(totalLength, currentLength);
            }
            if (_isCanceled == false) {
                output.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                output.write(end_data);
                output.flush();
                int respondeCode = conn.getResponseCode();
                if (respondeCode == HttpURLConnection.HTTP_OK) {
                    listener.progress(totalLength, totalLength);
                    result = true;
                }
            }
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }

        return result;
    }

    private String getUploadFileUrl(HttpURLConnection conn) {
        String result = null;
        InputStream in = null;
        try {
            in = conn.getInputStream();
            // input = new BufferedInputStream(conn.getInputStream());
            // final byte[] datas = new byte[input.available()];
            // input.read(datas);
            // result = new String(datas);
            int i = -1;
            byte[] b = new byte[1024];
            StringBuffer sb = new StringBuffer();
            while ((i = in.read(b)) != -1) {
                sb.append(new String(b, 0, i));
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        } finally {
            LDStreamHelper.close(in);
        }
        return result;
    }
}
