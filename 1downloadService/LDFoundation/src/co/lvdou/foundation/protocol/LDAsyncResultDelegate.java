package co.lvdou.foundation.protocol;

interface LDAsyncResultDelegate<T>
{
    void didCallback(boolean isSuccess, T extra);
}
