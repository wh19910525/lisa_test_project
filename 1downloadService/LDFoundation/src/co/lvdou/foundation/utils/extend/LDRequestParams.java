package co.lvdou.foundation.utils.extend;

import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 网络请求的参数对
 *
 * @author 郑一
 */
public final class LDRequestParams extends RequestParams {
    public LDRequestParams() {
        super();
    }

    public LDRequestParams(HashMap<String, String> maps) {
        this();
        put(maps);
    }

    @Override
    public void put(String key, Object value) {
        super.put(key, value.toString());
    }

    void put(HashMap<String, String> maps) {
        if (maps != null && maps.size() > 0) {
            Set<Entry<String, String>> entries = maps.entrySet();
            Iterator<Entry<String, String>> iterator = entries.iterator();
            Entry<String, String> tmp;
            while (iterator.hasNext()) {
                tmp = iterator.next();
                put(tmp.getKey(), tmp.getValue());
            }
        }
    }
}
