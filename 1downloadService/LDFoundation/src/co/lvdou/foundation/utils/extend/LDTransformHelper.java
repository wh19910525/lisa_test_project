package co.lvdou.foundation.utils.extend;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

/**
 * 转换工具类。
 *
 * @author 郑一
 */
public final class LDTransformHelper {
    private final static int KB_UNIT = 1024;
    private final static int KB_MIN_UNIT = KB_UNIT / 10;
    private final static int MB_UNIT = 1024 * 1024;
    private final static int MB_MIN_UNIT = MB_UNIT / 10;
    private final static int SECOND_UNIT = 1000;
    private final static int MINUTE_UNIT = SECOND_UNIT * 60;
    private final static int HOUR_UNIT = MINUTE_UNIT * 60;
    private final static int DAY_UNIT = HOUR_UNIT * 24;

    private LDTransformHelper() {
    }

    /**
     * 把服务器地址装换为文件名，服务器地址为空时返回空。
     *
     * @param url 待转换的服务器地址
     */
    public static String transformUrl2FileName(String url) {
        return TextUtils.isEmpty(url) ? null : String.valueOf(url.hashCode());
    }

    /**
     * 拼接服务器地址和参数对。
     *
     * @param prefix 服务器地址
     * @param params 参数对
     */
    public static String transform2Url(String prefix, HashMap<String, String> params) {
        return appendUrlParam(prefix, params);
    }

    /**
     * 把以字节为单位的数字转换为其他单位。
     *
     * @param sizeInByte 以字节为单位的数字
     * @param unit       待转换的单位
     */
    public static double transform2PhysicUnit(long sizeInByte, SizeUnit unit) {
        double result = 0.0d;
        switch (unit) {
            case KB:
                result = transformSize2KBUnit(sizeInByte);
                break;
            case MB:
                result = transformSize2MBUnit(sizeInByte);
                break;
        }
        return result;
    }

    /**
     * 把以毫秒为单位的时间转换为中文格式显示的时间
     *
     * @param timeInMills 以毫秒为单位的时间
     */
    public static String transform2ReadableTime(long timeInMills) {
        return doTransform2ReadableTime(timeInMills);
    }

    /**
     * 转换百分比
     *
     * @param current 当前数值
     * @param total   最大值
     */
    public static int transform2Percentage(long current, long total) {
        return (int) ((total == 0 || current > total) ? 0 : current * 100 / total);
    }

    private static double transformSize2KBUnit(long sizeInByte) {
        double result = 0.0f;
        if (sizeInByte > KB_MIN_UNIT) {
            result = new BigDecimal(sizeInByte).divide(new BigDecimal(KB_UNIT)).setScale(1, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        } else if (sizeInByte != 0L) {
            result = 0.1d;
        }
        return result;
    }

    private static double transformSize2MBUnit(long sizeInByte) {
        double result = 0.0f;
        if (sizeInByte > MB_MIN_UNIT) {
            result = new BigDecimal(sizeInByte).divide(new BigDecimal(MB_UNIT)).setScale(1, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        } else if (sizeInByte != 0L) {
            result = 0.1d;
        }
        return result;
    }

    private static String doTransform2ReadableTime(long timeInMills) {
        StringBuilder sb = new StringBuilder();
        if (timeInMills < MINUTE_UNIT) {
            final int seconds = (int) (timeInMills / SECOND_UNIT);
            sb.append(seconds).append("秒");
        } else if (timeInMills < HOUR_UNIT) {
            final int minutes = (int) (timeInMills / MINUTE_UNIT);
            final int seconds = (int) ((timeInMills - minutes * MINUTE_UNIT) / SECOND_UNIT);
            sb.append(minutes).append("分钟").append(seconds).append("秒");
        } else if (timeInMills < DAY_UNIT) {
            final int hours = (int) (timeInMills / HOUR_UNIT);
            final int minutes = (int) ((timeInMills - hours * HOUR_UNIT) / MINUTE_UNIT);
            if (minutes > 0) {
                sb.append(hours).append("小时").append(minutes).append("分钟");
            } else {
                sb.append(hours).append("小时");
            }
        } else {
            final int days = (int) (timeInMills / DAY_UNIT);
            final int hours = (int) ((timeInMills - days * DAY_UNIT) / HOUR_UNIT);
            final int minutes = (int) ((timeInMills - days * DAY_UNIT - hours * HOUR_UNIT) / MINUTE_UNIT);
            if (hours > 0 && minutes > 0) {
                sb.append(days).append("天").append(hours).append("小时").append(minutes).append("分钟");
            } else {
                if (hours <= 0 && minutes > 0) {
                    sb.append(days).append("天").append(minutes).append("分钟");
                } else if (minutes <= 0 && hours > 0) {
                    sb.append(days).append("天").append(hours).append("小时");
                } else {
                    sb.append(days).append("天");
                }
            }
        }
        return sb.toString();
    }

    private static String appendUrlParam(String url, String key, HashMap<String, String> map, boolean delete) {
        String value = map.get(key);
        if (value != null) {
            String app = "?";
            if (url.contains("?")) {
                app = "&";
            }
            url = url + app + key + "=" + value;
            if (delete) {
                map.remove(key);
            }
        }
        return url;
    }

    private static String appendUrlParam(String url, HashMap<String, String> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            url = appendUrlParam(url, key, map, false);
        }
        return url;
    }

    /**
     * 大小单位的枚举类
     */
    public enum SizeUnit {
        KB, MB
    }

}
