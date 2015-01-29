package com.tt.push.ui;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by ZhengYi on 14-10-3.
 */
public class ActTransparent extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Panel);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        super.onCreate(savedInstanceState);
        finish();
    }
}
