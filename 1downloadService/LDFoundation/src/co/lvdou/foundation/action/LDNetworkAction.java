package co.lvdou.foundation.action;

import co.lvdou.foundation.utils.extend.LDRequestHandle;

/**
 * 封装了网络相关的业务逻辑的操作类。    <br/>
 * 用于重用网络相关的 {@link co.lvdou.foundation.action.LDAction} 子类的相关代码
 *
 * @author 郑一
 */
public abstract class LDNetworkAction<T extends LDActionDelegate> extends LDAction<T> {

    protected LDRequestHandle mRequestHandle;

    @Override
    public void dispatchOnExecuteCompleteEvent() {
        super.dispatchOnExecuteCompleteEvent();
        cancelRequestHandle();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        cancelRequestHandle();
    }

    protected final void cancelRequestHandle() {
        if (mRequestHandle != null) {
            mRequestHandle.cancel(false);
            mRequestHandle = null;
        }
    }
}
