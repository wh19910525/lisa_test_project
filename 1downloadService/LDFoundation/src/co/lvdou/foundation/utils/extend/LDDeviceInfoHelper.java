package co.lvdou.foundation.utils.extend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

@SuppressWarnings("SpellCheckingInspection")
public final class LDDeviceInfoHelper {
	private static LDDeviceInfoHelper _instance;
	private final Context _context;

	public static LDDeviceInfoHelper defaultHelper() {
		if (_instance == null) {
			_instance = new LDDeviceInfoHelper(LDContextHelper.getContext());
		}
		return _instance;
	}

	private LDDeviceInfoHelper(Context context) {
		this._context = context.getApplicationContext();
	}

	/**
	 * 获取Imei码 无法获取则返回unknown
	 */
	public String getImei() {
		String result;
		TelephonyManager tm = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
		result = tm.getDeviceId();
		if (result == null || result.length() == 0) {
			result = "unknown";
		}
		return result;
	}

	/**
	 * 获取Imsi码 无法获取则返回unknown
	 */
	public String getImsi() {
		String result;
		TelephonyManager tm = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
		result = tm.getSubscriberId();
		if (result == null || result.length() == 0) {
			result = "unknown";
		}
		return result;
	}

	/**
	 * 获取手机厂商 *
	 */
	public String getManufacturer() {
		return Build.BRAND.replaceAll(" ", "_");
	}

	/**
	 * 获取手机型号 *
	 */
	public String getMobileType() {
		return Build.MODEL.replaceAll(" ", "_");
	}

	/**
	 * 获取手机系统版本
	 */
	public String getMobileVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取手机系统SDK版本
	 */
	public int getMobileSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 判断手机版本是否低于2.2
	 */
	public boolean isMobileVersionTooLower() {
		boolean result = false;
		String mobileVersion = Build.VERSION.RELEASE;
		if (mobileVersion.startsWith("2.1") || mobileVersion.startsWith("1.5") || mobileVersion.startsWith("1.6")) {
			result = true;
		}
		return result;
	}

	public String getAppPackageName() {
		return _context.getPackageName();
	}

	/**
	 * 获取应用的版本号 获取失败时返回-1
	 */
	public int getAppVersionCode() {
		int result;
		PackageManager pm = _context.getPackageManager();
		if (pm != null) {
			try {
				PackageInfo info = pm.getPackageInfo(_context.getPackageName(), 0);
				result = info.versionCode;
			} catch (NameNotFoundException e) {
				result = -1;
			}
		} else {
			result = -1;
		}
		return result;
	}

	/**
	 * 获取应用的版本名 获取失败时返回unknown
	 */
	public String getAppVersionName() {
		String result = "unknown";
		PackageManager pm = _context.getPackageManager();
		if (pm != null) {
			try {
				PackageInfo info = pm.getPackageInfo(_context.getPackageName(), 0);
				result = info.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取已开机时间 *
	 */
	public String getBootUpTime() {
		long bootUpTime = SystemClock.elapsedRealtime();
		return LDTransformHelper.transform2ReadableTime(bootUpTime);
	}

	private static String currentRomVersion = null;

	/**
	 * 获取Rom版本 .<br />
	 * 示例:<br/>
	 * (每个手机都存在的字段)1.[ro.build.display.id]: [IML74K - Ice Cream Sandwich] <br />
	 * (CM9Mod特有的字段)2.[ro.modversion]: [9-NIGHTLY-120301-Defy] <br />
	 * (魔趣Mod特有的字段)3.[ro.build.version.full]: [GT4786] <br />
	 * *
	 */
	public String getRomVersion() {
		String result = currentRomVersion;
		if (result == null) {
			try {
				java.lang.Process process = Runtime.getRuntime().exec("getprop");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains("ro.modversion") || line.contains("ro.build.version.full")) {
						result = line;
						break;
					} else if (line.contains("ro.build.display.id")) {
						result = line;
						break;
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (result == null) {
				result = "unknown";
				currentRomVersion = result;
			} else {
				int startIdx = result.indexOf(":");
				result = result.substring(startIdx + 3, result.length() - 1);
				currentRomVersion = result;
			}
		}
		return result;
	}

	/**
	 * 是否打开Wifi *
	 */
	public boolean isWifiEnable() {
		WifiManager manager = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
		int state = manager.getWifiState();
		return state == WifiManager.WIFI_STATE_ENABLED;
	}

	/**
	 * 是否有可用网络
	 */
	public boolean hasActiveNetwork() {
		boolean result = false;
		ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			result = true;
		}
		return result;
	}

	public boolean isThreeGNetwork() {
		boolean result = false;
		ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
				final int subType = ni.getSubtype();
				if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
					result = true;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("SpellCheckingInspection")
	public boolean isSupportHttps() {
		boolean result = true;
		ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
				if (!TextUtils.isEmpty(ni.getExtraInfo()) && ni.getExtraInfo().equalsIgnoreCase("cmwap")) {
					result = false;
				}
			}
		}
		return result;
	}

	/**
	 * 是否存在可用Wifi *
	 */
	public boolean hasActiveWifi() {
		boolean result = false;

		if (_context != null) {
			final ConnectivityManager connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			result = (wifi != null && wifi.isAvailable());
		}
		return result;
	}

	/**
	 * 是否插入USB *
	 */
	public boolean isUSBMounted(Intent intent) {
		final String action = intent.getAction();
		if (action != null) {
			if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
				int pluggedType = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
				if (pluggedType == BatteryManager.BATTERY_PLUGGED_USB) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 获取已用内存 *
	 */
	public long getUsedMemory(long totalMemory) {
		ActivityManager am = (ActivityManager) _context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		am.getMemoryInfo(info);
		return totalMemory - info.availMem;
	}

	/**
	 * 获取可用内存 *
	 */
	public long getAvailMemory() {
		ActivityManager am = (ActivityManager) _context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		am.getMemoryInfo(info);
		return info.availMem;
	}

	/**
	 * 获取Data分区的总大小 *
	 */
	@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
	public long getDataPartitionTotalSize() {
		StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
		return (long) fs.getBlockSize() * fs.getBlockCount();
	}

	/**
	 * 获取Data分区的已用空间 *
	 */
	@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
	public long getDataPartitionUsedSize() {
		StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
		return (long) (fs.getBlockCount() - fs.getAvailableBlocks()) * fs.getBlockSize();
	}

	/**
	 * 获取Data分区的可用空间 *
	 */
	@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
	public long getDataPartitionAvailableSize() {
		StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
		return (long) fs.getAvailableBlocks() * fs.getBlockSize();
	}

	private static final String SYSTEM_PATH = "/system";

	/**
	 * 获取System分区总大小 *
	 */
	@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
	public long getSystemPartitionTotalSize() {
		StatFs fs = new StatFs(SYSTEM_PATH);
		return (long) fs.getBlockSize() * fs.getBlockCount();
	}

	/**
	 * 获取System分区的已用空间 *
	 */
	@SuppressWarnings({ "deprecation", "UnusedDeclaration" })
	public long getSystemPartitionUsedSize() {
		StatFs fs = new StatFs(SYSTEM_PATH);
		return (long) (fs.getBlockCount() - fs.getAvailableBlocks()) * fs.getBlockSize();
	}

	/**
	 * 获取System分区的可用空间 *
	 */
	@SuppressWarnings("deprecation")
	public long getSystemPartionAvailableSize() {
		StatFs fs = new StatFs(SYSTEM_PATH);
		return (long) fs.getAvailableBlocks() * fs.getBlockSize();
	}

	/**
	 * 是否插入SDCard *
	 */
	public boolean isExternalMounted() {
		return Environment.getExternalStorageState() != null && Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);

	}

	/**
	 * 获取SD卡的总空间 *
	 */
	@SuppressWarnings("deprecation")
	public long getExternalTotalSize() {
		StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		return (long) fs.getBlockSize() * fs.getBlockCount();
	}

	/**
	 * 获取SD卡的已用空间 *
	 */
	@SuppressWarnings("deprecation")
	public long getExternalUsedSize() {
		StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		return (long) (fs.getBlockCount() - fs.getAvailableBlocks()) * fs.getBlockSize();
	}

	/**
	 * 获取SD卡的可用空间 *
	 */
	@SuppressWarnings("deprecation")
	public long getExternalAvailSize() {
		StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		return (long) fs.getAvailableBlocks() * fs.getBlockSize();
	}

	/**
	 * 获取外置SD卡路径，不存在则返回null *
	 */
	@SuppressWarnings("SpellCheckingInspection")
	String getExtraExternalPath() {
		String result = null;
		// if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED))
		// {
		// File[] list = new File("/mnt/").listFiles();
		// if(list != null && list.length > 0)
		// {
		// String name = null;
		// for(File tmp : list)
		// {
		// name = tmp.getName().trim().toLowerCase();
		// if(name.equalsIgnoreCase("emmc") || name.contains("sd"))
		// {
		// if(tmp.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())
		// == false)
		// {
		// result = tmp.getAbsolutePath();
		// break;
		// }
		// }
		// }
		// }
		// }
		final List<String> allExternalPath = getAllExternalPath();
		if (allExternalPath.size() >= 2) {
			File tmp;
			for (String externalPath : allExternalPath) {
				tmp = new File(externalPath);
				if (!tmp.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
					result = tmp.getAbsolutePath();
					break;
				}
			}
		}
		return result;
	}

	List<String> getAllExternalPath() {
		List<String> result = new LinkedList<String>();
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			File[] list = new File("/mnt/").listFiles();
			if (list != null && list.length > 0) {
				String name;
				for (File tmp : list) {
					name = tmp.getName().trim().toLowerCase();
					if (name.equalsIgnoreCase("emmc") || name.contains("sd")) {
						result.add(tmp.getAbsolutePath());
					}
				}
			}
		}
		return result;
	}

	// /** 用于判断是否所有的SD卡都挂载 **/
	// public boolean isAllExternalMounted()
	// {
	// boolean result = false;
	// if(hasExtraExternal())
	// {
	// result = isExternalMounted() & isExtraExternalMounted();
	// }
	// else
	// {
	// result = isExternalMounted();
	// }
	// return result;
	// }

	/**
	 * 用于判断是否存在外置SD卡 *
	 */
	boolean hasExtraExternal() {
		return getExtraExternalPath() != null;
	}

	/**
	 * 用于判断外置SD卡是否挂载 *
	 */
	public boolean isExtraExternalMounted() {
		boolean result = false;
		if (hasExtraExternal() && getExtraExternalTotalSize() > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * 获取外置SD卡的总空间 *
	 */
	@SuppressWarnings("deprecation")
	long getExtraExternalTotalSize() {
		long result = 0;
		final String path = getExtraExternalPath();
		if (path != null) {
			StatFs fs = new StatFs(path);
			result = (long) fs.getBlockSize() * fs.getBlockCount();
		}
		return result;
	}

	/**
	 * 获取外置SD卡的已用空间 *
	 */
	@SuppressWarnings("deprecation")
	public long getExtraExternalUsedSize() {
		long result = 0;
		final String path = getExtraExternalPath();
		if (path != null) {
			StatFs fs = new StatFs(path);
			result = (long) (fs.getBlockCount() - fs.getAvailableBlocks()) * fs.getBlockSize();
		}
		return result;
	}

	/**
	 * 获取外置SD卡的可利用空间 *
	 */
	@SuppressWarnings("deprecation")
	public long getExtraExternalAvailSize() {
		long result = 0;
		final String path = getExtraExternalPath();
		if (path != null) {
			StatFs fs = new StatFs(path);
			result = (long) fs.getAvailableBlocks() * fs.getBlockSize();
		}
		return result;
	}

	/**
	 * 获取总内存 *
	 */
	public long getTotalMemory() {
		long result = 0;
		String str1 = "/proc/meminfo";
		String str2;
		BufferedReader bufferedRead = null;
		try {
			FileReader r = new FileReader(str1);
			bufferedRead = new BufferedReader(r, 8192);
			str2 = bufferedRead.readLine();
			String str4 = str2.substring(str2.length() - 9, str2.length() - 3);
			// 该文件中总内存的单位为KB，应转换成byte
			result = Long.parseLong(str4) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedRead != null) {
				try {
					bufferedRead.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	// 是否开启gpws
	public boolean isOpenGPS() {
		boolean gps = false;
		LocationManager alm = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			gps = true;
		}
		// Intent myIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
		// startActivity(myIntent);
		return gps;
	}
	
	
	
	//清除默认启动的launcher
	public void clearDefaultBoot() {
	    PackageManager manager = _context.getPackageManager();
	    Intent intent = new Intent(Intent.ACTION_MAIN, null);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    intent.addCategory(Intent.CATEGORY_DEFAULT);
	    List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
	    for (int i = 0; i < list.size(); i++) {
	        // 作用是： 清除之前选择的Homescreen,比如即使你手动设置了Launcher作为你的     
	    	//Homescreen，执行以下方法之后，Launcher就不再是默认的Homescreen了。
	       manager.clearPackagePreferredActivities(list.get(i).activityInfo.packageName);
	    }
	}	
	
	//如果输入法在窗口上已经显示，则隐藏，反之则显示
		public void hideAndShowKeyboard(Context context){
		    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
		}
}
