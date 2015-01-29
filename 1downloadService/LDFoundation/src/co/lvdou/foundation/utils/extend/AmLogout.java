package co.lvdou.foundation.utils.extend;


import android.util.Log;

/**
 * 输出日志的工具类。
 * 
 * @author am
 */
public final class AmLogout {
	private static final String TAG = "ameng";

	private AmLogout() {
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
	public static void out(String tag, String msg) {
		Log.d(tag, msg);
	}
}
