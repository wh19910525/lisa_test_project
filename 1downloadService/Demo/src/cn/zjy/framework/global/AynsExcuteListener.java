package cn.zjy.framework.global;

@Deprecated
public interface AynsExcuteListener
{
    /**
     * 当接收到异步执行的结果时触发该事件
     */
    void onReceiveResult(boolean result, String extra);

    public static AynsExcuteListener Null = new AynsExcuteListener()
    {
	@Override
	public void onReceiveResult(boolean result, String extra)
	{}
    };
}
