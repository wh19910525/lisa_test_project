package co.lvdou.foundation.store.base;

import co.lvdou.foundation.action.LDAction;
import co.lvdou.foundation.utils.extend.LDRequestHandle;

public abstract class LDNetworkSourceStore<T extends LDStoreDelegate> extends LDBaseStore<T> {

    protected LDRequestHandle mRequestHandle;
    protected LDAction mAction;

    @Override
    public void release() {
        super.release();
        cancelHistoryRequest();
        cancelHistoryAction();
    }

    protected void cancelHistoryRequest() {
        if (mRequestHandle != null) {
            mRequestHandle.cancel(true);
            mRequestHandle = null;
        }
    }

    protected void cancelHistoryAction() {
        if (mAction != null) {
            mAction.interrupt();
            mAction = null;
        }
    }
}
