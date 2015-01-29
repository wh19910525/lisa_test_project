package co.lvdou.foundation.protocol;

public abstract class LDSimpleAsyncResultDelegate implements LDAsyncResultDelegate<String> {
    @Override
    public final void didCallback(boolean isSuccess, String extra) {
        didCallback(isSuccess);
    }

    public abstract void didCallback(boolean isSuccess);

    public static LDSimpleAsyncResultDelegate Null = new LDSimpleAsyncResultDelegate() {
        @Override
        public void didCallback(boolean isSuccess) {

        }
    };
}
