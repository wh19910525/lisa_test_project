package co.lvdou.foundation.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.net.Uri;
import co.lvdou.foundation.utils.boot.LDBootManagerDictionary;
import co.lvdou.foundation.utils.extend.LDContextHelper;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class BootAutoAppModel implements Serializable {
    private static final long serialVersionUID = 1693947618568645660L;
    private final int _uid;
    private final String _pkg;
    private final String _name;
    private final boolean _isAllowBoot;
    private final LinkedList<String> _bootComponents = new LinkedList<String>();

    private BootAutoAppModel(int uid, String pkg, String name, boolean isAllowBoot) {
        _uid = uid;
        _pkg = pkg;
        _name = name;
        _isAllowBoot = isAllowBoot;
    }

    public static List<BootAutoAppModel> getInstance() {
        final Context context = LDContextHelper.getContext();
        LinkedList<BootAutoAppModel> result = new LinkedList<BootAutoAppModel>();
        BootAutoAppModel tmpBean;

        // 获取包含自启动Action的Receiver列表
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            List<PackageInfo> pkgInfos = pm.getInstalledPackages(0);
            for (PackageInfo pkgInfo : pkgInfos) {
                tmpBean = getInstance(pkgInfo.packageName);
                if (tmpBean != null) {
                    result.add(tmpBean);
                }
            }
        }
        return result;
    }


    public static List<BootAutoAppModel> getInstance(List<String> filters) {
        Context context = LDContextHelper.getContext();
        LinkedList<BootAutoAppModel> result = new LinkedList<BootAutoAppModel>();
        BootAutoAppModel tmpBean;

        // 获取包含自启动Action的Receiver列表
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            List<PackageInfo> pkgInfos = pm.getInstalledPackages(0);
            for (PackageInfo pkgInfo : pkgInfos) {
                if (!filters.contains(pkgInfo.packageName)) {
                    tmpBean = getInstance(pkgInfo.packageName);
                    if (tmpBean != null) {
                        result.add(tmpBean);
                    }
                }
            }
        }
        return result;
    }

    public static BootAutoAppModel getInstance(String pkg) {
        BootAutoAppModel result = null;
        try {
            Context context = LDContextHelper.getContext();
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                final PackageInfo packageInfo = pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS |
                        PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES);
                if (packageInfo.applicationInfo != null) {
                    final CharSequence label = packageInfo.applicationInfo.loadLabel(pm);
                    if (label != null) {
                        if (!isAppInstalledOnSystem(packageInfo)) {
                            final String name = label.toString();
                            if (name != null && name.trim().length() > 0) {
                                final int uid = packageInfo.applicationInfo.uid;
                                if (!isAllComponentEnable(pm, packageInfo)) {
                                    result = new BootAutoAppModel(uid, pkg, name, false);
                                } else if (isAllowBootAuto(context, pkg)) {
                                    result = new BootAutoAppModel(uid, pkg, name, true);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    private static boolean isAllowBootAuto(Context context, String pkg) {
        boolean result = false;
        if (isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_RECEIVE_BOOT_COMPLETE) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_USER_PRESENT) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_SCREEN_ON) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_SCREEN_OFF) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_BATTERY_CHANGED) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_SIM_STATE_CHANGED) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_CONNECTION_CHANGED) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_DOWNLOAD_COMPLETE) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_DOWNLOAD_NOTIFICATION_CLICKED) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_MEDIA_MOUNTED, Uri.parse("file:")) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_PACKAGE_ADDED, Uri.parse("package:")) ||
                isPkgReceiveTargetAction(context, pkg, LDBootManagerDictionary.ACTION_PACKAGE_REMOVED, Uri.parse("package:"))) {
            result = true;
        }
        return result;
    }

    private static boolean isPkgReceiveTargetAction(Context context, String pkg, String targetAction) {
        boolean result = false;
        if (isPkgReceiveTargetActionReceiver(context, pkg, targetAction)) {
            result = true;
        }
        return result;
    }

    private static boolean isPkgReceiveTargetAction(Context context, String pkg, String targetAction, Uri schema) {
        boolean result = false;
        if (isPkgReceiveTargetActionReceiver(context, pkg, targetAction, schema)) {
            result = true;
        } else if (isPkgReceiveTargetActionService(context, pkg, targetAction, schema)) {
            result = true;
        }
        return result;
    }

    private static boolean isPkgReceiveTargetActionReceiver(Context context, String pkg, String targetAction) {
        boolean result = false;
        Intent intent = new Intent(targetAction);
        intent.setPackage(pkg);
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            List<ResolveInfo> infos = pm.queryBroadcastReceivers(intent, 0);
            if (infos != null && infos.size() > 0) {
                result = true;
            }
        }
        return result;
    }

    private static boolean isPkgReceiveTargetActionReceiver(Context context, String pkg, String targetAction, Uri schema) {
        boolean result = false;
        Intent intent = new Intent(targetAction);
        intent.setPackage(pkg);
        intent.setData(schema);
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            List<ResolveInfo> infos = pm.queryBroadcastReceivers(intent, 0);
            if (infos != null && infos.size() > 0) {
                result = true;
            }
        }
        return result;
    }

    private static boolean isPkgReceiveTargetActionService(Context context, String pkg, String targetAction, Uri schema) {
        boolean result = false;
        Intent intent = new Intent(targetAction);
        intent.setPackage(pkg);
        intent.setData(schema);
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            List<ResolveInfo> infos = pm.queryIntentServices(intent, 0);
            if (infos != null && infos.size() > 0) {
                result = true;
            }
        }
        return result;
    }

    private static void addReceiveMediaMountedComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_MEDIA_MOUNTED, Uri.parse("file:"));
    }

    private static void addReceiveDownloadCompleteComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_DOWNLOAD_COMPLETE);
    }

    private static void addReceiveDownloadNotificationCompleteComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_DOWNLOAD_NOTIFICATION_CLICKED);
    }

    private static void addReceiveConnectionChangedComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_CONNECTION_CHANGED);
    }

    private static void addReceiveSimStateChangedComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_SIM_STATE_CHANGED);
    }

    private static void addReceivePackageAddedComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_PACKAGE_ADDED, Uri.parse("package:"));
    }

    private static void addReceivePackageRemovedComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_PACKAGE_REMOVED, Uri.parse("package:"));
    }

    private static void addReceiveScreenOnComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_SCREEN_ON);
    }

    private static void addReceiveScreenOffComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_SCREEN_OFF);
    }

    private static void addReceiveBatteryChangedComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_BATTERY_CHANGED);
    }

    private static void addReceiveBootComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_RECEIVE_BOOT_COMPLETE);
    }

    private static void addReceiveUserPresentComponent(PackageManager pm, PackageInfo packageInfo, List<String> componentList) {
        addReceiveTargetActionReceiver(pm, packageInfo, componentList, LDBootManagerDictionary.ACTION_USER_PRESENT);
    }

    private static void addReceiveTargetActionReceiver(PackageManager pm,
                                                       PackageInfo packageInfo, List<String> componentList, String targetAction) {
        final String pkg = packageInfo.packageName;
        Intent intent = new Intent(targetAction);
        intent.setPackage(pkg);
        List<ResolveInfo> resolves = pm.queryBroadcastReceivers(intent, 0);
        for (ResolveInfo resolve : resolves) {
            if (resolve.activityInfo != null) {
                componentList.add(resolve.activityInfo.name);
            }
        }
    }

    private static void addReceiveTargetActionReceiver(PackageManager pm,
                                                       PackageInfo packageInfo, List<String> componentList, String targetAction, Uri schema) {
        final String pkg = packageInfo.packageName;
        Intent intent = new Intent(targetAction);
        intent.setData(schema);
        intent.setPackage(pkg);
        List<ResolveInfo> resolves = pm.queryBroadcastReceivers(intent, 0);
        for (ResolveInfo resolve : resolves) {
            if (resolve.activityInfo != null) {
                componentList.add(resolve.activityInfo.name);
            }
        }
    }

    private static boolean isAllComponentEnable(PackageManager pm, PackageInfo packageInfo) {
        boolean result = true;
        final String pkg = packageInfo.packageName;
        int componentState;
        final ActivityInfo[] receivers = packageInfo.receivers;
        if (receivers != null) {
            for (ActivityInfo receiver : receivers) {
                componentState = pm.getComponentEnabledSetting(new ComponentName(pkg, receiver.name));
                if (componentState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    result = false;
                }
            }
        }
        final ServiceInfo[] services = packageInfo.services;
        if (services != null) {
            for (ServiceInfo service : services) {
                componentState = pm.getComponentEnabledSetting(new ComponentName(pkg, service.name));
                if (componentState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    result = false;
                }
            }
        }
        return result;
    }

    private static boolean isAppInstalledOnSystem(PackageInfo packageInfo) {
        boolean result = false;
        if (packageInfo.applicationInfo != null) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                result = true;
            }
        }
        return result;
    }

    public int getUid() {
        return _uid;
    }

    public String getPkg() {
        return _pkg;
    }

    public String getName() {
        return _name;
    }

    public boolean isAllowBoot() {
        return _isAllowBoot;
    }

    public List<String> getAllBootComponent() {
        return _bootComponents;
    }

    public void obtainBootAutoComponent(Context context) {

        final PackageManager pm = context.getPackageManager();
        try {
            if (pm != null) {
                final PackageInfo pi = pm.getPackageInfo(_pkg, PackageManager.GET_RECEIVERS);
                if (_isAllowBoot && pi != null) {
                    addReceiveMediaMountedComponent(pm, pi, _bootComponents);
                    addReceiveDownloadCompleteComponent(pm, pi, _bootComponents);
                    addReceiveDownloadNotificationCompleteComponent(pm, pi, _bootComponents);
                    addReceiveConnectionChangedComponent(pm, pi, _bootComponents);
                    addReceivePackageAddedComponent(pm, pi, _bootComponents);
                    addReceivePackageRemovedComponent(pm, pi, _bootComponents);
                    addReceiveScreenOnComponent(pm, pi, _bootComponents);
                    addReceiveScreenOffComponent(pm, pi, _bootComponents);
                    addReceiveBatteryChangedComponent(pm, pi, _bootComponents);
                    addReceiveBootComponent(pm, pi, _bootComponents);
                    addReceiveUserPresentComponent(pm, pi, _bootComponents);
                    addReceiveSimStateChangedComponent(pm, pi, _bootComponents);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        return _pkg.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }
}
