package co.lvdou.foundation.store.base;

public abstract class LDBaseStore<T extends LDStoreDelegate> {

    public abstract LDBaseStore setDelegate(T delegate);

    public void release() {
        setDelegate(null);
    }

    @SuppressWarnings("EmptyCatchBlock")
    protected void sleepWithoutIntterrupt(long timeInMills) {
        try {
            Thread.sleep(timeInMills);
        } catch (InterruptedException e) {
        }
    }
}
