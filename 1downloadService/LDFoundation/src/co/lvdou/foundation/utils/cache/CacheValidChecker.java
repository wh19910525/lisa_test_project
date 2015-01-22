package co.lvdou.foundation.utils.cache;

/**
 * 缓存校验者接口。
 *
 * @author 郑一
 */
public interface CacheValidChecker {

    /**
     * 判断缓存是否有效。
     *
     * @param jsonStr 缓存数据
     */
    boolean isCacheValid(String jsonStr);
}
