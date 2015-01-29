package co.lvdou.foundation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public final class LDTimeEventReceiver extends BroadcastReceiver {
    public static final String ACTION_TIME_UP = "co.lvdou.mockactive.ACTION.TIME_UP";
    public static final String EXTRA_INTERVAL = "co.lvdou.mockactive.EXTRA.INTERVAL";
    public static final String EXTRA_SENDER = "co.lvdou.mockactive.EXTRA.SENDER";
    private static final long DEFAULT_DURATION = -1L;

    private LDTimeEventReceiverDelegate _delegate = LDTimeEventReceiverDelegate.Null;

    public static LDTimeEventReceiver regist(Context context, LDTimeEventReceiverDelegate delegate) {
        LDTimeEventReceiver receiver = null;

        if (context != null) {
            receiver = new LDTimeEventReceiver();
            receiver.setDelegate(delegate);
            final IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_TIME_UP);
            context.registerReceiver(receiver, filter);
        }
        return receiver;
    }

    public static void unregist(Context context, LDTimeEventReceiver receiver) {
        if (context != null && receiver != null) {
            receiver.setDelegate(null);
            context.unregisterReceiver(receiver);
        }
    }

    void setDelegate(LDTimeEventReceiverDelegate delegate) {
        if (delegate == null) {
            _delegate = LDTimeEventReceiverDelegate.Null;
        } else {
            _delegate = delegate;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null && intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(ACTION_TIME_UP)) {
                    final String sender = intent.getStringExtra(EXTRA_SENDER);
                    final long duration = intent.getLongExtra(EXTRA_INTERVAL, DEFAULT_DURATION);
                    _delegate.onTimeUp(sender, duration);
                }
            }
        }
    }

    public static interface LDTimeEventReceiverDelegate {
        public static LDTimeEventReceiverDelegate Null = new LDTimeEventReceiverDelegate() {
            @Override
            public void onTimeUp(String sender, long interval) {
            }
        };

        void onTimeUp(String sender, long interval);
    }
}
