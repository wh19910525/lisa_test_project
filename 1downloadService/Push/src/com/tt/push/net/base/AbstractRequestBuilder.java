package com.tt.push.net.base;

import co.lvdou.foundation.utils.extend.LDRequestHandle;
import co.lvdou.foundation.utils.extend.LDRequestParams;
import co.lvdou.foundation.utils.extend.Logout;
import co.lvdou.foundation.utils.net.LDResponseHandle;
import com.tt.push.store.BaseParamsStore;

public abstract class AbstractRequestBuilder {

    public LDRequestHandle build() {
        return build(LDResponseHandle.NULL);
    }

    public abstract LDRequestHandle build(LDResponseHandle responseHandle);

    protected void printRequestUrl(String url, LDRequestParams requestParams) {
        if (requestParams == null)
            Logout.out(String.format("[request_url: %s]", url));
        else
            Logout.out(String.format("[request_url: %s]", url + "?" + requestParams));
    }

    protected LDRequestParams getBaseParmas() {
        return BaseParamsStore.shareStore().getBaseParams();
    }
}
