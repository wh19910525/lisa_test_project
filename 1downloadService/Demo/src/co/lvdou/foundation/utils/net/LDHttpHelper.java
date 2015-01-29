package co.lvdou.foundation.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

final class LDHttpHelper {
    private LDHttpHelper() {
    }

    static HttpURLConnection openUrl(Context context, String urlStr) {
        URL urlURL;
        HttpURLConnection httpConn = null;
        try {
            urlURL = new URL(urlStr);
            // 需要android.permission.ACCESS_NETWORK_STATE
            // 在没有网络的情况下，返回值为null。
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            // 如果是使用的运营商网络
            if (networkInfo != null) {
                httpConn = (HttpURLConnection) urlURL.openConnection();
                httpConn.setConnectTimeout(Constants.NETWORK_OPEN_MAX_TIME_SHORT);
                httpConn.setReadTimeout(Constants.NETWORK_READ_MAX_TIME_SHORT);
                httpConn.setDoInput(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpConn;
    }

    static int connect(HttpURLConnection httpConn) {
        int code = -1;
        if (httpConn != null) {
            try {
                httpConn.connect();
                code = httpConn.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return code;
    }
}
