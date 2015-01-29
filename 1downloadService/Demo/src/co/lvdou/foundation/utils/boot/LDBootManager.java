package co.lvdou.foundation.utils.boot;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import co.lvdou.foundation.model.BootAutoAppModel;
import co.lvdou.foundation.protocol.LDSimpleAsyncResultDelegate;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import co.lvdou.foundation.utils.root.LDCommandBuilder;
import co.lvdou.foundation.utils.root.LDRootManager;

import java.util.List;

public final class LDBootManager {
    private static LDBootManager _instance;

    private final Context _context;

    private LDBootManager(Context context) {
        _context = context;
    }

    public static LDBootManager defaultManager() {
        if (_instance == null) {
            _instance = new LDBootManager(LDContextHelper.getContext());
        }
        return _instance;
    }


    public BootAutoAppModel getBootAutoPackage(String pkg) {
        return BootAutoAppModel.getInstance(pkg);
    }


    public List<BootAutoAppModel> getAllBootAutoPackage() {
        return BootAutoAppModel.getInstance();
    }


    public List<BootAutoAppModel> getAllBootAutoPackage(List<String> filters) {
        return BootAutoAppModel.getInstance(filters);
    }


    public void enableBootAutoPackage(final BootAutoAppModel bean, final LDSimpleAsyncResultDelegate delegate) {
        final Runnable action = new Runnable() {

            public void run() {
                if (!LDRootManager.isObtainedRootPermission()) {
                    delegate.didCallback(false);
                    return;
                }

                final LDCommandBuilder cb = new LDCommandBuilder();
                try {
                    PackageManager pm = _context.getPackageManager();
                    if (pm != null) {
                        final String packageName = bean.getPkg();
                        PackageInfo pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
                        ActivityInfo[] receivers = pkgInfo.receivers;
                        if (receivers != null) {
                            for (ActivityInfo receiver : receivers) {
                                int componentState = pm.getComponentEnabledSetting(new ComponentName(packageName, receiver.name));
                                if (componentState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                                    cb.addEnableComponentCommand(packageName, receiver.name);
                                }
                            }
                        }
                        final String flag = "EnableBootAutoPackageComplete";
                        cb.addPrintCommand(flag);
                        LDRootManager.doCommand(cb.build());
                        if (LDRootManager.readSpecificFlag(flag)) {
                            delegate.didCallback(true);
                        } else {
                            delegate.didCallback(false);
                        }
                    } else {
                        delegate.didCallback(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    delegate.didCallback(false);
                }
            }
        };
        new Thread(action).start();
    }


    public void enableBootAutoPackage(final List<BootAutoAppModel> beans, final LDBootEventDelegate delegate) {
        final Runnable action = new Runnable() {

            public void run() {
                if (!LDRootManager.isObtainedRootPermission()) {
                    delegate.onOperateFail();
                    return;
                }

                final LDCommandBuilder cb = new LDCommandBuilder();
                final String enableSinglePackageFlag = "enable a package";
                final String enableAllPackageFlag = "enable all package";

                try {
                    PackageManager pm = _context.getPackageManager();
                    if (pm != null) {
                        for (BootAutoAppModel bean : beans) {
                            final String packageName = bean.getPkg();
                            PackageInfo pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
                            if (pkgInfo != null) {
                                ActivityInfo[] receivers = pkgInfo.receivers;
                                if (receivers != null) {
                                    for (ActivityInfo receiver : receivers) {
                                        int componentState = pm.getComponentEnabledSetting(new ComponentName(packageName, receiver.name));
                                        if (componentState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                                            cb.addEnableComponentCommand(packageName, receiver.name);
                                        }
                                    }
                                }
                                cb.addPrintCommand(enableSinglePackageFlag);
                            }
                        }
                        cb.addPrintCommand(enableAllPackageFlag);
                        LDRootManager.doCommand(cb.build());

                        final int total = beans.size();
                        int count = 0;
                        String line;
                        while ((line = LDRootManager.readCommand()) != null) {
                            if (line.contains(enableSinglePackageFlag)) {
                                count++;
                                delegate.onOperating(count, total);
                            } else if (line.contains(enableAllPackageFlag)) {
                                delegate.onOperateComplete();
                                return;
                            }
                        }

                        delegate.onOperateFail();
                    } else {
                        delegate.onOperateFail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    delegate.onOperateFail();
                }
            }
        };
        new Thread(action).start();
    }


    public void disableBootAutoPackage(final BootAutoAppModel bean, final LDSimpleAsyncResultDelegate delegate) {
        final Runnable action = new Runnable() {

            public void run() {
                if (!LDRootManager.isObtainedRootPermission()) {
                    delegate.didCallback(false);
                    return;
                }

                try {
                    final LDCommandBuilder cb = new LDCommandBuilder();
                    final List<String> allBootComponents = bean.getAllBootComponent();
                    final String packageName = bean.getPkg();
                    bean.obtainBootAutoComponent(_context);
                    for (String bootAutoComponent : allBootComponents) {
                        cb.addDisableComponentCommand(packageName, bootAutoComponent);
                    }

                    final String flag = "Disable BootAutoPackageComplete";
                    cb.addPrintCommand(flag);
                    LDRootManager.doCommand(cb.build());

                    if (LDRootManager.readSpecificFlag(flag)) {
                        delegate.didCallback(true);
                    } else {
                        delegate.didCallback(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    delegate.didCallback(false);
                }
            }
        };
        new Thread(action).start();
    }


    public void disableBootAutoPackage(final List<BootAutoAppModel> beans, final LDBootEventDelegate delegate) {
        final Runnable action = new Runnable() {

            public void run() {
                if (!LDRootManager.isObtainedRootPermission()) {
                    delegate.onOperateFail();
                    return;
                }

                try {
                    final LDCommandBuilder cb = new LDCommandBuilder();
                    final String disableSinglePackageFlag = "disable a package";
                    final String disableAllPackageFlag = "disable all package";

                    for (BootAutoAppModel bean : beans) {
                        bean.obtainBootAutoComponent(_context);
                        final List<String> allBootComponents = bean.getAllBootComponent();
                        for (String bootAutoComponent : allBootComponents) {
                            cb.addDisableComponentCommand(bean.getPkg(), bootAutoComponent);
                        }
                        cb.addPrintCommand(disableSinglePackageFlag);
                    }
                    cb.addPrintCommand(disableAllPackageFlag);
                    LDRootManager.doCommand(cb.build());

                    final int total = beans.size();
                    int current = 0;
                    String line;
                    while ((line = LDRootManager.readCommand()) != null) {
                        if (line.contains(disableSinglePackageFlag)) {
                            current++;
                            delegate.onOperating(total, current);
                        } else if (line.contains(disableAllPackageFlag)) {
                            delegate.onOperateComplete();
                            return;
                        }
                    }

                    delegate.onOperateFail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(action).start();
    }

    public static interface LDBootEventDelegate {
        public static LDBootEventDelegate Null = new LDBootEventDelegate() {
            @Override
            public void onOperating(int current, int total) {
            }

            @Override
            public void onOperateComplete() {
            }

            @Override
            public void onOperateFail() {
            }
        };

        void onOperating(int current, int total);

        void onOperateComplete();

        void onOperateFail();
    }

}
