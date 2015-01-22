package co.lvdou.foundation.store;

import android.content.Context;
import co.lvdou.foundation.model.LDScreenStateModel;
import co.lvdou.foundation.receiver.LDScreenEventReceiver;
import co.lvdou.foundation.receiver.LDScreenEventReceiver.LDScreenEventReceiverDelegate;
import co.lvdou.foundation.utils.extend.LDContextHelper;

public final class LDScreenStateStore implements LDScreenEventReceiverDelegate {
    private static LDScreenStateStore _instance = null;

    private final Context _appCtx;
    private final LDScreenStateModel _latestScreenState;
    private LDScreenEventReceiver _screenEventReceiver = null;

    private LDScreenStateStore(Context ctx) {
        _appCtx = ctx;
        _latestScreenState = new LDScreenStateModel();
        registReceivers();
    }

    public static LDScreenStateStore getInstance() {
        if (_instance == null) {
            _instance = new LDScreenStateStore(LDContextHelper.getContext());
        }
        return _instance;
    }

    public void release() {
        unregistReceivers();
        _instance = null;
    }

    public LDScreenStateModel getLatestScreenState() {
        return _latestScreenState;
    }

    @Override
    public void onReceivedScreenOn() {
        _latestScreenState.setOffScreenNow(false);
    }

    @Override
    public void onReceivedScreenOff() {
        _latestScreenState.setOffScreenNow(true);
    }

    private void registReceivers() {
        if (_screenEventReceiver == null) {
            _screenEventReceiver = LDScreenEventReceiver.regist(_appCtx, this);
        }
    }

    private void unregistReceivers() {
        if (_screenEventReceiver != null) {
            LDScreenEventReceiver.unregist(_appCtx, _screenEventReceiver);
        }
    }
}
