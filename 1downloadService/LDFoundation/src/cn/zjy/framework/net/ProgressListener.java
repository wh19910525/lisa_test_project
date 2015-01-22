package cn.zjy.framework.net;

public interface ProgressListener 
{
	void progress(int total, int current);
	
	public static ProgressListener NULL = new ProgressListener()
	{
		@Override
		public void progress(int total, int current) 
		{}
	};
}
