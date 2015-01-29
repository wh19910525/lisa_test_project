package co.lvdou.foundation.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public interface LDJsonInitializer<T> {
	T initWith(JSONObject rootMap) throws JSONException;

	boolean isModelValid();
}
