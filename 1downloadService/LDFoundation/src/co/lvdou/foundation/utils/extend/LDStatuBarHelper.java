package co.lvdou.foundation.utils.extend;

import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;

public class LDStatuBarHelper {
	private static LDStatuBarHelper instance;
	private final Context context;

	private LDStatuBarHelper(Context context) {
		super();
		this.context = context;
	}

	public static LDStatuBarHelper getInstance(Context context) {
		if (instance == null) {
			instance = new LDStatuBarHelper(context);
		}
		return instance;
	}

	public void collapseStatusBar() {
		try {
			Object statusBarManager = context.getSystemService("statusbar");
			Method collapse;
			if (Build.VERSION.SDK_INT <= 16) {
				collapse = statusBarManager.getClass().getMethod("collapse");
			} else {
				collapse = statusBarManager.getClass().getMethod("collapsePanels");
			}
			collapse.invoke(statusBarManager);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
}
