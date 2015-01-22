package co.lvdou.foundation.utils.net;

import android.content.Context;
import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDDeviceInfoHelper;
import co.lvdou.foundation.utils.extend.LDMd5Helper;
import co.lvdou.foundation.utils.extend.LDTransformHelper;
import co.lvdou.foundation.utils.extend.LDTransformHelper.SizeUnit;

import java.io.*;
import java.net.HttpURLConnection;

public final class LDDownloadFileTask implements Runnable {
    private static final int MIN_NOTIFY_UNIT = (int) (1024 * 1024 * 0.5);
    private static final int MAX_NOTIFY_UNIT = 1024 * 1024 * 1;
    private final Context _context;
    private final File _path;
    private final String _url;
    private final String _md5;
    private LDDownloadFileDelegate _delegate;
    private boolean _isCancel = false;
    private boolean _isRetry = false;
    private int mHistoryContentLength = -1;

    public LDDownloadFileTask(Context context, String url, File path, LDDownloadFileDelegate delegate, String md5) {
        if (delegate == null) {
            delegate = LDDownloadFileDelegate.Null;
        }

        this._context = context.getApplicationContext();
        this._url = url;
        this._path = path;
        this._delegate = delegate;
        this._md5 = md5;
    }

    public LDDownloadFileTask(Context context, String url, File path, LDDownloadFileDelegate delegate) {
        this(context, url, path, delegate, null);
    }

    public synchronized void cancelTask() {
        _isCancel = true;
    }

    public synchronized void setDelegate(LDDownloadFileDelegate delegate) {
        if (delegate == null) {
            _delegate = LDDownloadFileDelegate.Null;
        } else {
            _delegate = delegate;
        }
    }

    @Override
    public void run() {
        if (_context == null || _path == null || TextUtils.isEmpty(_url)) {
            _delegate.onFail();
            return;
        }

        boolean hasActiveNetwork = LDDeviceInfoHelper.defaultHelper().hasActiveNetwork();
        if (hasActiveNetwork) {
            download2File(_delegate);
        } else {
            _delegate.onFail();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void download2File(LDDownloadFileDelegate listener) {
        File file = _path;
        new File(file.getParent()).mkdirs();
        if (file.exists()) {
            // 校验文件的Md5码，如果相同则不再次下载
            if (isFileHasSameMd5()) {
                doStatisticNotDownload();
                listener.onDownloading(file.length(), file.length(), 100, null, null);
                listener.onComplete();
                return;
            } else {
                final int netLength = getContentLength(_url);
                if (netLength > 0 && file.length() == netLength) {
                	if (_url!=null&&_url.equals("http://app.down.ishuaji.cn/lvdouapp/config/su.conf")) {
                		 file.delete();
					}else {
						listener.onDownloading(file.length(), file.length(), 100, null, null);
						listener.onComplete();
						return;
					}
                } else {
                    file.delete();
                }
            }
        }

        BufferedOutputStream fos = null;
        RandomAccessFile randomFile = null;
        BufferedInputStream is = null;
        HttpURLConnection httpConn = null;
        byte[] data = new byte[100 * 1024];
        int readLength;
        File tmpFile = new File(file.getAbsoluteFile() + ".tmp");
        try {
            // 采用普通的下载方式
            if (!tmpFile.exists()) {
                // 采用普通的下载方式
                fos = new BufferedOutputStream(new FileOutputStream(tmpFile));
                httpConn = LDHttpHelper.openUrl(_context, _url);
                int respondCode = LDHttpHelper.connect(httpConn);
                if (respondCode == HttpURLConnection.HTTP_OK) {
                    _delegate.onStart(httpConn.getURL().toString());
                    final int totalSize = httpConn.getContentLength();
                    final int notifyUnit = computeNotifyUnit(totalSize);
                    int loadedSize = 0;
                    long currentSize = 0;
                    long startTime = System.currentTimeMillis();

                    _delegate.onDownloading(0, totalSize, 0, null, null);

                    is = new BufferedInputStream(httpConn.getInputStream());
                    while ((readLength = is.read(data)) != -1) {
                        if (_isCancel) {
                            break;
                        } else {
                            fos.write(data, 0, readLength);
                            fos.flush();
                            currentSize += readLength;
                            loadedSize += readLength;

                            if (loadedSize > notifyUnit) {
                                long duration = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();
                                String speed = computeSpeed(loadedSize, duration);
                                String remainTime = computeRemainTime(totalSize, currentSize, loadedSize, duration);

                                int percentage = (int) ((totalSize == 0) ? 0 : (currentSize * 100 / totalSize));
                                listener.onDownloading(currentSize, totalSize, percentage, speed, remainTime);
                                loadedSize = 0;
                            }
                        }
                    }
                    fos.flush();
                    if (currentSize == totalSize) {
                        file.delete();
                        tmpFile.renameTo(file);
                        listener.onComplete();
                    } else if (_isCancel) {
                        listener.onCancel();
                    } else {
                        tmpFile.delete();
                        file.delete();
                        listener.onFail();
                    }
                } else {
                    tmpFile.delete();
                    file.delete();
                    listener.onFail();
                }
            } else {
                // 采用断点续传方式

//                final int totalSize = getContentLength(_url);
//                if (totalSize < 0) {
//                    tmpFile.delete();
//                    file.delete();
//                    _delegate.onFail();
//                    return;
//                }

                long currentSize = tmpFile.length();
                int loadedSize = 0;

                httpConn = LDHttpHelper.openUrl(_context, _url);
                randomFile = new RandomAccessFile(tmpFile, "rw");
//                httpConn.setRequestProperty("Range", "bytes=" + currentSize + "-" + (totalSize - 1));
                httpConn.setRequestProperty("Range", "bytes=" + currentSize + "-");
                int respondCode = LDHttpHelper.connect(httpConn);
                if (respondCode == HttpURLConnection.HTTP_PARTIAL) {
                    _delegate.onStart(httpConn.getURL().toString());
                    int totalSize = (int) (httpConn.getContentLength() + currentSize);
                    final int notifyUnit = computeNotifyUnit(totalSize);
                    int percentage = (int) ((totalSize == 0) ? 0 : currentSize * 100 / totalSize);
                    _delegate.onDownloading(currentSize, totalSize, percentage, null, null);
                    long startTime = System.currentTimeMillis();
                    is = new BufferedInputStream(httpConn.getInputStream());
                    while ((readLength = is.read(data)) != -1) {
                        if (_isCancel) {
                            listener.onCancel();
                            break;
                        } else {
                            randomFile.seek(currentSize);
                            randomFile.write(data, 0, readLength);
                            currentSize += readLength;
                            loadedSize += readLength;
                            if (loadedSize > notifyUnit) {
                                long duration = System.currentTimeMillis() - startTime;
                                startTime = System.currentTimeMillis();

                                String speed = computeSpeed(loadedSize, duration);
                                String remainTime = computeRemainTime(totalSize, currentSize, loadedSize, duration);

                                percentage = (int) ((totalSize == 0) ? 0 : currentSize * 100 / totalSize);

                                listener.onDownloading(currentSize, totalSize, percentage, speed, remainTime);
                                loadedSize = 0;
                            }
                        }
                    }

                    if (currentSize == totalSize) {
                        tmpFile.renameTo(file);
                        listener.onComplete();
                    }

                } else {
                    tmpFile.delete();
                    file.delete();
                    listener.onFail();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LDDeviceInfoHelper.defaultHelper().hasActiveNetwork()) {
                if (!_isRetry) {
                    _isRetry = true;
                    download2File(_delegate);
                } else {
                    listener.onFail();
                }
            } else {
                listener.onCancel();
            }
        } finally {
            closeOutputStream(fos);
            closeInputStream(is);
            closeRandomAccessFile(randomFile);

            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }

    private boolean isFileHasSameMd5() {
        boolean result = false;
        File file = _path;
        if (file.exists()) {
            if (LDMd5Helper.isMd5Valid(_md5)) {
                final String fileMd5 = LDMd5Helper.generateMD5(file);
                if (fileMd5.equalsIgnoreCase(_md5)) {
                    result = true;
                }
            }
        }
        return result;
    }

    private void doStatisticNotDownload() {
        HttpURLConnection conn = null;
        try {
            conn = LDHttpHelper.openUrl(_context, _url);
            conn.connect();
            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void closeInputStream(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeOutputStream(OutputStream output) {
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeRandomAccessFile(RandomAccessFile file) {
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getContentLength(String url) {
        if (mHistoryContentLength != -1) {
            return mHistoryContentLength;
        }

        try {
            final HttpURLConnection conn = LDHttpHelper.openUrl(_context, url);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                mHistoryContentLength = conn.getContentLength();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }).start();
        } catch (Exception e) {
            mHistoryContentLength = -1;
        }

        return mHistoryContentLength;
    }

    private int computeNotifyUnit(int totalSize) {
        int notifyUnit = (totalSize < 100) ? totalSize : (totalSize / 25);
        if (notifyUnit < MIN_NOTIFY_UNIT) {
            notifyUnit = MIN_NOTIFY_UNIT;
        } else if (notifyUnit > MAX_NOTIFY_UNIT) {
            notifyUnit = MAX_NOTIFY_UNIT;
        }
        return notifyUnit;
    }

    private String computeSpeed(int loadedSize, long duration) {
        if (duration > 0) {
            long bytesPerSecond = loadedSize * 1000 / duration;
            return LDTransformHelper.transform2PhysicUnit(bytesPerSecond, SizeUnit.KB) + "KB/s";
        } else {
            return "未知";
        }
    }

    private String computeRemainTime(long totalSize, long currentSize, long loadedSize, long duration) {
        if (duration > 0 && loadedSize > duration) {
            long bytesPerMills = loadedSize / duration;

            return LDTransformHelper.transform2ReadableTime((totalSize - currentSize) / bytesPerMills);
        } else {
            return "未知";
        }
    }
}
