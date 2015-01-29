package co.lvdou.foundation.utils.root;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDStreamHelper;

public final class LDRootManager {
	private static boolean _isObtainingRootPermission = false;
	private static boolean _isObtainedRootPermission = false;
	private static BufferedReader _input;
	private static BufferedOutputStream _output;

	public LDRootManager() {
	}

	@SuppressWarnings("UnusedDeclaration")
	public static boolean isMobileRooted() {
		boolean result = false;
		File su = new File("/system/bin/su");
		File su2 = new File("/system/xbin/su");

		if (su.exists() || su2.exists()) {
			result = true;
		}

		return result;
	}

	public static boolean isObtainedRootPermission() {
		return _isObtainedRootPermission;
	}

	public static boolean isInstalledBusyboxLibrary() {
		boolean result = false;

		File busybox = new File("/system/bin/busybox");
		File busybox2 = new File("/system/xbin/busybox");

		if (busybox.exists() || busybox2.exists()) {
			result = true;
		}

		return result;
	}

	public static boolean obtainRootPermission() {
		boolean result = false;

		if (_isObtainedRootPermission) {
			result = true;
		} else {
			if (!_isObtainingRootPermission) {
				_isObtainingRootPermission = true;
				try {
					Process process = Runtime.getRuntime().exec("su");
					_input = new BufferedReader(new InputStreamReader(
							process.getInputStream()));
					_output = new BufferedOutputStream(
							process.getOutputStream());
					_isObtainedRootPermission = isApplicationReallyObtainedRootPermission();
					if (_isObtainedRootPermission) {
						result = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					_isObtainedRootPermission = false;
				} finally {
					_isObtainingRootPermission = false;
				}
			}
		}

		return result;
	}

	public static void doCommand(String command) {
		if (_isObtainedRootPermission) {
			if (!TextUtils.isEmpty(command)) {
				command = command.endsWith("\n") ? command : command + "\n";
				try {
					_output.write(command.getBytes());
					_output.flush();
				} catch (IOException e) {
					e.printStackTrace();
					release();
				}
			}
		}
	}

	public static String readCommand() {
		String result = null;
		if (_isObtainedRootPermission) {
			try {
				result = _input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				release();
			}
		}
		return result;
	}

	public static boolean readSpecificFlag(String flag) {
		String line;
		while ((line = readCommand()) != null) {
			if (line.contains(flag)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isApplicationReallyObtainedRootPermission()
			throws Exception {
		boolean result = false;

		_output.write("id\n".getBytes());
		_output.flush();

		String line;
		while ((line = _input.readLine()) != null) {
			if (line.contains("uid=0") && line.contains("gid=0")) {
				result = true;
				break;
			}
		}

		return result;
	}

	private static void release() {
		LDStreamHelper.close(_input);
		LDStreamHelper.close(_output);
		_isObtainedRootPermission = false;
		_isObtainingRootPermission = false;
	}
}
