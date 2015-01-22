package com.tt.push.store;

import co.lvdou.foundation.utils.extend.LDRequestParams;

import java.util.HashMap;

public class BaseParamsStore {

    private static BaseParamsStore mInstance;
    private HashMap<String, String> mBaseParams = new HashMap<String, String>();

    private BaseParamsStore() {
    }

    public static BaseParamsStore shareStore() {
        if (mInstance == null)
            mInstance = new BaseParamsStore();

        return mInstance;
    }

    public void setBaseParams(HashMap<String, String> params) {
        if (params != null) {
            mBaseParams = params;
        }
    }

    public LDRequestParams getBaseParams() {
        return new LDRequestParams(mBaseParams);
    }
}
