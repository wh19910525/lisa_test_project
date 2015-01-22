package co.lvdou.foundation.utils.cache;

import android.content.Context;
import co.lvdou.foundation.utils.extend.LDContextHelper;

import java.io.*;
import java.util.HashMap;

/**
 * 用于存放网络请求返回数据的缓存管理器。
 *
 * @author 郑一
 */
public final class LDCacheManager {
    private final static int MAX_CACHE = 12;
    private final HashMap<String, String> mMemoryCaches = new HashMap<String, String>(MAX_CACHE);
    private final static long DEFAULT_CACHE_AVAIL_TIME = 1800 * 1000;
    private final static String SEPERATE_CHARS = "@@@";
    private static LDCacheManager mInstance;
    private final String mSaveDir;

    private LDCacheManager(String saveDir) {
        mSaveDir = saveDir;
    }

    /**
     * 获取缓存管理器的公共实例。
     */
    public static LDCacheManager shareManager() {
        if (mInstance == null) {
            final Context context = LDContextHelper.getContext();
            final String saveDir = context.getCacheDir() + File.separator + "n_cache" + File.separator;
            new File(saveDir).mkdirs();
            mInstance = new LDCacheManager(saveDir);
        }
        return mInstance;
    }

    /**
     * 清除内存中的缓存数据。
     */
    public void clearCacheInMemory() {
        synchronized (mMemoryCaches) {
            mMemoryCaches.clear();
        }
    }

    /**
     * 保存服务器地址相关的数据缓存。
     *
     * @param url          服务器地址
     * @param data         缓存数据
     * @param validChecker 缓存检验者，用于校验缓存是否有效，无效的话将不保存到本地
     */
    public boolean saveCache(String url, String data, CacheValidChecker validChecker) {
        return saveCache(url, data, validChecker, DEFAULT_CACHE_AVAIL_TIME);
    }

    /**
     * 保存服务器地址相关的数据缓存。
     *
     * @param url          服务器地址
     * @param data         缓存数据
     * @param validChecker 缓存检验者，用于校验缓存是否有效，无效的话将不保存到本地
     * @param aliveTime    缓存的有效期，超过该有效期的缓存将会自动删除
     */

    public boolean saveCache(String url, String data, CacheValidChecker validChecker, long aliveTime) {
        boolean result = false;
        if (validChecker.isCacheValid(data)) {
            String savePath = generateSavePath(url, aliveTime);
            File file = new File(savePath);
            file.getParentFile().mkdirs();
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                writer.write(data);
                writer.flush();
                writer.close();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            } finally {
                try {
                	if(writer!=null)
                	{
                		writer.close();
                	}
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    /**
     * 获取服务器地址相关的缓存数据，并且不把缓存保存在内存当中。
     *
     * @param url          服务器地址
     * @param validChecker 缓存校验者，假如有缓存在本地但是检验不通过的话将会自动删除该缓存并返回空
     */
    public String getCacheData(String url, CacheValidChecker validChecker) {
        return getCacheData(url, validChecker, false);
    }

    /**
     * 获取服务器地址相关的缓存数据。
     *
     * @param url                服务器地址
     * @param validChecker       缓存校验者，假如有缓存在本地但是检验不通过的话将会自动删除该缓存并返回空
     * @param saveCacheInMemorys 是否把缓存保存在内存当中，保存缓存在内存中将会加快相关缓存的获取速度
     */
    public String getCacheData(String url, CacheValidChecker validChecker, boolean saveCacheInMemorys) {
        String result = null;
        if (hasCacheInMemory(url)) {
            result = getCacheInMemory(url);
        } else {
            final String savePath = getSavePath(url);
            if (savePath != null) {
                if (isCacheTimeout(savePath, true) == false) {
                    FileReader reader = null;
                    try {
                        reader = new FileReader(new File(savePath));
                        final char[] datas = new char[50480];
                        int length = reader.read(datas);
                        final String dataStr = String.valueOf(datas, 0, length);
                        if (validChecker.isCacheValid(dataStr)) {
                            result = dataStr;
                            if (saveCacheInMemorys) {
                                addCacheInMemory(url, dataStr);
                            }
                        } else {
                            new File(savePath).delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = null;
                    } finally {
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断是否存在服务器地址相关的缓存。
     *
     * @param url          服务器地址
     * @param validChecker 缓存检验者，假如本地有缓存但是校验不通过的话将会自动删除该缓存并且返回 false
     */
    public boolean hasCache(String url, CacheValidChecker validChecker) {
        boolean result = false;
        if (getCacheData(url, validChecker) != null) {
            result = true;
        }
        return result;
    }

    private String generateSavePath(String url, long aliveTime) {
        return mSaveDir + url.hashCode() + SEPERATE_CHARS + aliveTime;
    }

    private String getSavePath(final String url) {
        String result = null;
        File file = new File(mSaveDir);
        final String prefix = "" + url.hashCode();
        if (file.exists()) {
            String[] fileNameList = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    boolean result = false;
                    if (filename.startsWith(prefix)) {
                        result = true;
                    }
                    return result;
                }
            });
            if (fileNameList != null && fileNameList.length > 0) {
                result = mSaveDir + fileNameList[0];
            }
        }
        return result;
    }

    private String getCacheInMemory(String key) {
        String result = null;
        synchronized (mMemoryCaches) {
            result = mMemoryCaches.get(key);
        }
        return result;
    }

    private void addCacheInMemory(String key, String value) {
        synchronized (mMemoryCaches) {
            if (mMemoryCaches.size() >= MAX_CACHE) {
                final String firstKey = mMemoryCaches.keySet().iterator().next();
                removeCacheInMemory(firstKey);
            }
            mMemoryCaches.put(key, value);
        }
    }

    private void removeCacheInMemory(String key) {
        synchronized (mMemoryCaches) {
            mMemoryCaches.remove(key);
        }
    }

    private boolean hasCacheInMemory(String key) {
        boolean result = false;
        synchronized (mMemoryCaches) {
            if (mMemoryCaches.containsKey(key)) {
                result = true;
            }
        }
        return result;
    }

    private boolean isCacheTimeout(String filePath, boolean autoClean) {
        boolean result = true;
        File file = new File(filePath);
        if (file.exists()) {
            final String[] subStrList = file.getName().split(SEPERATE_CHARS);
            if (subStrList != null && subStrList.length >= 2) {
                final long aliveTime = Long.parseLong(subStrList[subStrList.length - 1]);
                final long createTime = file.lastModified();
                final long currentTime = System.currentTimeMillis();
                if ((currentTime - createTime) < aliveTime) {
                    result = false;
                } else if (autoClean) {
                    file.delete();
                }
            }
        }
        return result;
    }
}
