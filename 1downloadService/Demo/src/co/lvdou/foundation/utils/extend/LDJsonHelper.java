package co.lvdou.foundation.utils.extend;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 处理JSON的工具类。
 *
 * @author 郑一
 */
public final class LDJsonHelper {
    private LDJsonHelper() {
    }

    /**
     * 格式化特殊格式的JSON数据,服务器某些接口会返回包含很多双引号的JSON数据。
     *
     * @param jsonData 特殊格式的JSON数据
     */
    public static String formatSpecialJson(String jsonData) {
        if (TextUtils.isEmpty(jsonData) || jsonData.length() <= 5) {
            return jsonData;
        } else {
            jsonData = jsonData.replace("\\\"", "\"");
            jsonData = jsonData.substring(1, jsonData.length() - 1);
            return jsonData;
        }
    }

    /**
     * 判断JSON数据是否有效。
     *
     * @param jsonData JSON数据
     */
    public static boolean isValid(String jsonData) {

        boolean result = false;

        if (jsonData != null) {

            try {

                final JSONObject jsonObject = new JSONObject(jsonData);

                if (jsonObject.has("code") && jsonObject.getInt("code") == 1) {

                    result = true;
                }
            } catch (JSONException e) {

                result = false;
            }

        }
        return result;
    }

    /**
     * 判断当前JSON数据是否包含errortype字段且为-2。
     *
     * @param jsonData JSON数据
     */
    public static boolean isErrorType(String jsonData) {
        boolean result = false;

        if (jsonData != null) {
            try {

                final JSONObject jsonObject = new JSONObject(jsonData);

                if (jsonObject.has("errortype") && jsonObject.getInt("errortype") == -2) {

                    result = true;
                }
            } catch (JSONException e) {

                result = false;
            }
        }

        return result;
    }

    /**
     * 获取code值。
     *
     * @param dataStr JSON数据
     */
    public static int getCode(String dataStr) {

        int code = -1;

        if (dataStr != null) {

            try {
                JSONObject jsObject = new JSONObject(dataStr);

                if (jsObject.has("code")) {

                    code = jsObject.getInt("code");
                } else {

                    code = -1;
                }

            } catch (Exception e) {
                code = -1;
                e.printStackTrace();
            }
        }

        return code;
    }

    /**
     * 获取失败原因。
     *
     * @param dataStr JSON数据
     */
    public static String getFailMsg(String dataStr) {

        if (dataStr == null) {

            return "网络异常";
        }

        try {

            final JSONObject jsonObject = new JSONObject(dataStr);
            return jsonObject.getString("msg");
        } catch (JSONException e) {

            return "网络异常";
        }

    }
}
