package cn.zjy.framework.net;

import android.content.Context;
import cn.zjy.framework.db.DBManager;
import cn.zjy.framework.download.DownloadBean;
import cn.zjy.framework.download.DownloadBean.State;
import co.lvdou.foundation.utils.extend.LDDeviceInfoHelper;
import co.lvdou.foundation.utils.extend.LDMd5Helper;

import java.io.*;
import java.net.HttpURLConnection;

public final class DownloadFileTask implements Runnable {
    private final Context _context;
    private final DownloadBean _bean;
    private final DownloadFileEventListener _listener;
    private final String _md5;
    private boolean _isPause = false;
    private boolean _isCancel = false;

    public DownloadFileTask(Context context, DownloadBean downloadBean, DownloadFileEventListener listener, String md5) {
        this._context = context.getApplicationContext();
        this._bean = downloadBean;
        this._listener = listener;
        this._md5 = md5;
    }

    public DownloadFileTask(Context context, DownloadBean downloadBean, DownloadFileEventListener listener) {
        this(context, downloadBean, listener, null);
    }

    public boolean isRelated(long id, int type) {
        return _bean._id == id && _bean._type == type;
    }

    public synchronized void pauseTask() {
        _isPause = true;
    }

    public synchronized void cancelTask() {
        _isCancel = true;
    }

    @Override
    public void run() {
        if (_context != null) {
            boolean hasActiveNetwork = LDDeviceInfoHelper.defaultHelper().hasActiveNetwork();
            if (hasActiveNetwork) {
                download2File(_listener);
            } else {
                if (_bean._currentSize > 0) {
                    _bean._state = State.Pause;
                    DBManager.getInstance(_context).modifyItem(_bean);
                    _listener.onDownloadPause(_bean);
                } else {
                    _bean._state = State.Error;
                    DBManager.getInstance(_context).deleteItem(_bean);
                    _listener.onDownloadFail(_bean);
                }
            }
        }
    }

    private void download2File(DownloadFileEventListener listener) {
        _bean._state = State.Downloading;
        listener.onStartDownload(_bean);

        DBManager dbManager = DBManager.getInstance(_context);
        dbManager.deleteItem(_bean);
        dbManager.addItem(_bean);

        File file = new File(_bean._filePath);
        new File(file.getParent()).mkdirs();
        if (file.exists()) {
            // 校验文件的Md5码，如果相同则不再次下载
            if (isFileHasSameMd5()) {
                doStatisticNotDownload();
                _bean._state = State.Complete;
                _bean._totalSize = file.length();
                _bean._currentSize = _bean._totalSize;
                dbManager.modifyItem(_bean);
                listener.onDownloadComplete(_bean);
                return;
            } else {
                file.delete();
            }
        }

        BufferedOutputStream fos = null;
        RandomAccessFile randomFile = null;
        BufferedInputStream is = null;
        byte[] data = new byte[100 * 1024];
        int readLength = -1;
        File tmpFile = new File(_bean._filePath + ".tmp");
        try {
            INetworkManager networkManager = NetworkManagerImpl.getInstance(_context);
            // 采用普通的下载方式
            if (_bean._state == State.Error || _bean._currentSize == 0 || tmpFile.exists() == false) {
                _bean._currentSize = 0;
                // 采用普通的下载方式
                fos = new BufferedOutputStream(new FileOutputStream(tmpFile));
                HttpURLConnection httpConn = networkManager.openUrl(_bean._downurl);
                int respondCode = networkManager.connect(httpConn);
                if (respondCode == HttpURLConnection.HTTP_OK) {
                    _bean._state = State.Downloading;
                    _bean._totalSize = httpConn.getContentLength();
                    _listener.onDownloading(_bean);
                    dbManager.modifyItem(_bean);

                    // int notifyUnit = (int) ((_bean._totalSize < 100) ?
                    // _bean._totalSize : (_bean._totalSize / 25));
                    final int notifyUnit = (int) computeNotifyUnit(_bean._totalSize);

                    int loadedSize = 0;
                    is = new BufferedInputStream(httpConn.getInputStream());
                    while ((readLength = is.read(data)) != -1) {
                        if (_isPause || _isCancel) {
                            _bean._state = State.Pause;
                            listener.onDownloadPause(_bean);
                            break;
                        } else {
                            fos.write(data, 0, readLength);
                            fos.flush();
                            _bean._currentSize += readLength;
                            loadedSize += readLength;

                            if (loadedSize > notifyUnit) {
                                listener.onDownloading(_bean);
                                loadedSize = 0;
                                // dbManager.modifyItem(_bean);
                            }
                        }
                    }
                    fos.flush();
                    if (_bean._currentSize == _bean._totalSize) {
                        _bean._state = State.Complete;
                        file.delete();
                        tmpFile.renameTo(file);
                        listener.onDownloadComplete(_bean);
                    } else if (_isCancel) {
                        _bean._state = State.Error;
                        listener.onDownloadCancel(_bean);
                    } else if (_isPause && _bean._currentSize > 0) {
                        listener.onDownloadPause(_bean);
                    } else {
                        _bean._state = State.Error;
                        tmpFile.delete();
                        file.delete();
                        listener.onDownloadFail(_bean);
                    }
                    dbManager.modifyItem(_bean);
                } else {
                    _bean._state = State.Error;
                    tmpFile.delete();
                    file.delete();
                    listener.onDownloadFail(_bean);
                }
            } else {
                // 采用断点续传方式
                randomFile = new RandomAccessFile(tmpFile, "rw");
                randomFile.setLength(_bean._totalSize);
                HttpURLConnection httpConn = networkManager.openUrl(_bean._downurl);
                // httpConn.setRequestProperty("Referer",
                // "http://ishuaji.com/");
                httpConn.setRequestProperty("Range", "bytes=" + _bean._currentSize + "-" + (_bean._totalSize - 1));
                int respondCode = networkManager.connect(httpConn);
                // final int notifyUnit = (int) ((_bean._totalSize < 100) ?
                // _bean._totalSize : (_bean._totalSize / 25));
                final int notifyUnit = (int) computeNotifyUnit(_bean._totalSize);
                int loadedSize = 0;

                if (respondCode == HttpURLConnection.HTTP_PARTIAL) {
                    _bean._state = State.Downloading;
                    _listener.onDownloading(_bean);
                    dbManager.modifyItem(_bean);
                    is = new BufferedInputStream(httpConn.getInputStream());
                    while ((readLength = is.read(data)) != -1) {
                        if (_isCancel) {
                            _bean._state = State.Error;
                            listener.onDownloadCancel(_bean);
                            break;
                        } else if (_isPause) {
                            _bean._state = State.Pause;
                            listener.onDownloadPause(_bean);
                            break;
                        } else {
                            randomFile.seek(_bean._currentSize);
                            randomFile.write(data, 0, readLength);
                            _bean._currentSize += readLength;
                            loadedSize += readLength;
                            if (loadedSize > notifyUnit) {
                                listener.onDownloading(_bean);
                                loadedSize = 0;
                                // dbManager.modifyItem(_bean);
                            }
                        }
                    }
                } else {
                    tmpFile.delete();
                    _bean._currentSize = -1;
                    _bean._state = State.Error;
                    listener.onDownloadFail(_bean);
                }

                if (_bean._currentSize == _bean._totalSize) {
                    _bean._state = State.Complete;
                    tmpFile.renameTo(file);
                    listener.onDownloadComplete(_bean);
                }
                dbManager.modifyItem(_bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (_bean._currentSize > 0L) {
                _bean._state = State.Pause;
                listener.onDownloadPause(_bean);
            } else {
                _bean._state = State.Error;
                listener.onDownloadFail(_bean);
            }
        } finally {
            dbManager.modifyItem(_bean);
            closeOutputStream(fos);
            closeInputStream(is);
            closeRandomAccessFile(randomFile);
        }
    }

    private boolean isFileHasSameMd5() {
        boolean result = false;
        File file = new File(_bean._filePath);
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
        INetworkManager manager = NetworkManagerImpl.getInstance(_context);
        HttpURLConnection conn = null;
        try {
            conn = manager.openUrl(_bean._downurl);
            conn.connect();
            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private long computeNotifyUnit(long totalSize) {
        long notifyUnit = totalSize;
        if (totalSize < 1024 * 1024 * 2) {
            notifyUnit = (int) ((totalSize < 100) ? totalSize : (totalSize / 5));
        } else {
            notifyUnit = (int) ((totalSize < 100) ? totalSize : (totalSize / 25));
        }

        return notifyUnit;
    }

    private void closeInputStream(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
            }
        }
    }

    private void closeOutputStream(OutputStream output) {
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
            }
        }
    }

    private void closeRandomAccessFile(RandomAccessFile file) {
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
            }
        }
    }
}
