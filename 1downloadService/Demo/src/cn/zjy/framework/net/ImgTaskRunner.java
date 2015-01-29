package cn.zjy.framework.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class ImgTaskRunner
{
    private static ImgTaskRunner _instance;
    private final ExecutorService _threadPool;

    static ImgTaskRunner getInstance()
    {
	if (_instance == null)
	{
	    _instance = new ImgTaskRunner();
	}
	return _instance;
    }

    private ImgTaskRunner()
    {
	_threadPool = Executors.newSingleThreadExecutor();
    }

    void submitTask(Runnable task)
    {
	_threadPool.execute(task);
    }
}
