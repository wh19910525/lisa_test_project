package co.lvdou.foundation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public final class LDScreenEventReceiver extends BroadcastReceiver {
    private LDScreenEventReceiverDelegate _delegate = LDScreenEventReceiverDelegate.Null;

    public static LDScreenEventReceiver regist(Context ctx, LDScreenEventReceiverDelegate delegate) {
        if (ctx == null) {
            return null;
        }

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.setPriority(100000);
        final LDScreenEventReceiver receiver = new LDScreenEventReceiver();
        receiver.setDelegate(delegate);
        ctx.registerReceiver(receiver, filter);

        return receiver;
    }

    public static void unregist(Context ctx, LDScreenEventReceiver receiver) {
        if (ctx != null && receiver != null) {
            receiver.setDelegate(null);
            ctx.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (ctx != null && intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
                    _delegate.onReceivedScreenOn();
                } else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
                    _delegate.onReceivedScreenOff();
                }
            }
        }
    }

    void setDelegate(LDScreenEventReceiverDelegate delegate) {
        if (delegate == null) {
            _delegate = LDScreenEventReceiverDelegate.Null;
        } else {
            _delegate = delegate;
        }
    }

    public static interface LDScreenEventReceiverDelegate {
        public static LDScreenEventReceiverDelegate Null = new LDScreenEventReceiverDelegate() {
            @Override
            public void onReceivedScreenOn() {
            }

            @Override
            public void onReceivedScreenOff() {
            }
        };

        void onReceivedScreenOn();

        void onReceivedScreenOff();
    }
}
