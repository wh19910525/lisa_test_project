package co.lvdou.foundation.utils.extend;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import co.lvdou.foundation.receiver.LDTimeEventReceiver;

public class LDTimerHelper {
    private LDTimerHelper() {
    }

    public static void sendTimerBroadcast(String sender, long triggerDelayMills, long intervalMills) {
        final Context context = LDContextHelper.getContext();
        final Intent intent = new Intent(LDTimeEventReceiver.ACTION_TIME_UP);
        intent.putExtra(LDTimeEventReceiver.EXTRA_SENDER, sender);
        intent.putExtra(LDTimeEventReceiver.EXTRA_INTERVAL, intervalMills);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + triggerDelayMills,
                intervalMills, pi);
    }

    public static void cancelTimerBroadcast(String sender, long intervalMills) {
        final Context context = LDContextHelper.getContext();
        final Intent intent = new Intent(LDTimeEventReceiver.ACTION_TIME_UP);
        intent.putExtra(LDTimeEventReceiver.EXTRA_SENDER, sender);
        intent.putExtra(LDTimeEventReceiver.EXTRA_INTERVAL, intervalMills);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }
}
