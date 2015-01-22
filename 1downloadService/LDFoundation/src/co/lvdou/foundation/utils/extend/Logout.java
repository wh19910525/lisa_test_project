package co.lvdou.foundation.utils.extend;

import android.util.Log;

/**
 * 输出日志的工具类。
 * 
 * @author 郑一
 */
public final class Logout {
	private static final String TAG = "Default";

	private Logout() {
	}

	/**
	 * 输出日志。
	 * 
	 * @param msg
	 *            日志内容
	 */
	public static void out(String msg) {
		Log.d(TAG, msg);
	}

	/**
	 * 输出日志。
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public static void out(Object tag, String msg) {
		out(tag.getClass().getSimpleName(), msg);
	}

	/**
	 * 输出日志。
	 * 
	 * @param tag
	 *            日志标签
	 * @param msg
	 *            日志内容
	 */
	public static void out(String tag, String msg) {
		Log.d(tag, msg);
	}
}
