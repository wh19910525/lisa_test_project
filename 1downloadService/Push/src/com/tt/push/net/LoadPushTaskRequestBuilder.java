package com.tt.push.net;

import co.lvdou.foundation.utils.extend.LDRequestHandle;
import co.lvdou.foundation.utils.extend.LDRequestParams;
import co.lvdou.foundation.utils.net.LDHttpClient;
import co.lvdou.foundation.utils.net.LDResponseHandle;
import com.tt.push.net.base.AbstractRequestBuilder;
import com.tt.push.util.ShellManager;

public class LoadPushTaskRequestBuilder extends AbstractRequestBuilder {

    private LoadPushTaskRequestBuilder() {
    }

    public static AbstractRequestBuilder alloc() {
        return new LoadPushTaskRequestBuilder();
    }

    @Override
    public LDRequestHandle build(LDResponseHandle responseHandle) {
        String url = "http://silent.beself.net/silent/maketask/gettask";
        LDRequestParams params = getBaseParmas();
        params.put("installDir", ShellManager.shareManager().isObtainRootPermission() ? "system" : "data");
        printRequestUrl(url, params);
        return LDHttpClient.post(url, params, responseHandle);
    }
}
