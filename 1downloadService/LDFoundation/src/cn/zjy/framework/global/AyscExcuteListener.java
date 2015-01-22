package cn.zjy.framework.global;

public interface AyscExcuteListener<T>
{
    void onReceiveResult(boolean result, T extra);
    
    public static AyscExcuteListener<String> Null = new AyscExcuteListener<String>()
    {

	@Override
	public void onReceiveResult(boolean result, String extra)
	{
	    // TODO Auto-generated method stub

	}};
}
