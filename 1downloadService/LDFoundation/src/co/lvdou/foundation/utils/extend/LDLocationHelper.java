package co.lvdou.foundation.utils.extend;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * 获取用户地理位置的工具类
 */
public class LDLocationHelper {
    private LDLocationHelper() {
    }

    /**
     * 获取用户最新的地理位置
     */
    public static Location fetchLatestLocation() {

        final Context context = LDContextHelper.getContext();
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final Location locationFromGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final Location locationFromNetwork = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (locationFromGps == null && locationFromNetwork == null) {
            return null;
        } else if (locationFromGps == null) {
            return locationFromNetwork;
        } else if (locationFromNetwork == null) {
            return locationFromGps;
        } else {
            return (locationFromGps.getTime() > locationFromNetwork.getTime()) ? locationFromGps : locationFromNetwork;
        }
    }
}
