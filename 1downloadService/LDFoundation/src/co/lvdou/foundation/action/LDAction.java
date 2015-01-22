package co.lvdou.foundation.action;

import co.lvdou.foundation.utils.extend.LDContextHelper;

/**
 * 封装了业务逻辑的操作类，异步操作的相关代码都应该封装成 {@link co.lvdou.foundation.action.LDAction} 的子类。
 *
 * @author 郑一
 */
public abstract class LDAction<T extends LDActionDelegate> {
    private LDActionResultDelegate mResultDelegate = LDActionResultDelegate.Null;

    /**
     * 实现具体的业务逻辑。
     * {@link co.lvdou.foundation.action.LDAction} 的子类必须覆写。
     */
    protected abstract void runCore();

    /**
     * 执行相关的业务逻辑代码。  <br/>
     */
    public final void execute() {
        runCore();
    }

    /**
     * 异步执行相关的业务逻辑代码。  <br/>
     */
    public final void executeAysnc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runCore();
            }
        }).start();
    }

    /**
     * 设置LDAction执行结果的委托者。  <br/>
     * 一般为 {@link co.lvdou.foundation.action.LDActionPool}，调用者不需要去设置。
     */
    public void setResultDelegate(LDActionResultDelegate delegate) {
        if (delegate == null) {
            mResultDelegate = LDActionResultDelegate.Null;
        } else {
            mResultDelegate = delegate;
        }
    }

    /**
     * 设置执行过程中委托者，具体委托者由泛型 {@link T} 决定。
     */
    public abstract LDAction setDelegate(T delegate);

    /**
     * 打断当前LDAction的具体执行代码。 <br/>
     * 要使其生效需要子类覆写方法 {@link #interrupt()},并且在方法 {@link #runCore()} 的代码块声明周期结束后调用方法 {@link #dispatchOnExecuteCompleteEvent()}
     */
    public void interrupt() {
        setDelegate(null);
    }

    /**
     * 分发执行完毕的事件。 <br/>
     * 由具体子类在方法 {@link #runCore()} 的代码块声明周期结束后调用。
     */
    public void dispatchOnExecuteCompleteEvent() {
        mResultDelegate.onActionExecuted();
        setDelegate(null);
    }

    @SuppressWarnings("EmptyCatchBlock")
    protected void sleepWithoutInterrupt(long timeInMills) {
        try {
            Thread.sleep(timeInMills);
        } catch (InterruptedException e) {
        }
    }

    protected String getString(int resId) {
        return LDContextHelper.getContext().getString(resId);
    }

    protected String getString(int resId, Object... formatedArgs) {
        return LDContextHelper.getContext().getString(resId, formatedArgs);
    }

    /**
     * {@link co.lvdou.foundation.action.LDAction} 执行结果的委托接口    <br/>
     * 由 {@link co.lvdou.foundation.action.LDActionPool} 去实现，{@link co.lvdou.foundation.action.LDAction} 的子类设计者和调用者无需理会。
     */
    public static interface LDActionResultDelegate {
        public static LDActionResultDelegate Null = new LDActionResultDelegate() {
            @Override
            public void onActionExecuted() {
            }
        };

        /**
         * {@link co.lvdou.foundation.action.LDAction} 执行完毕的回调
         */
        void onActionExecuted();
    }
}