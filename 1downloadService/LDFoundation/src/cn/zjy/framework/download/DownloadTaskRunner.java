package cn.zjy.framework.download;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class DownloadTaskRunner 
{
	private static DownloadTaskRunner _instance;
	private final ExecutorService _threadPool;
	
	static DownloadTaskRunner getInstance()
	{
		if(_instance == null)
		{
			_instance = new DownloadTaskRunner();
		}
		return _instance;
	}
	
	private DownloadTaskRunner()
	{
		_threadPool = Executors.newFixedThreadPool(3);
	}
	
	void submitTask(Runnable task)
	{
		_threadPool.execute(task);
	}
}
