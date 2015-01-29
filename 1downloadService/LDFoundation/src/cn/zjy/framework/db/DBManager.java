package cn.zjy.framework.db;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import cn.zjy.framework.download.DownloadBean;

public final class DBManager
{
    private static DBManager _instance;
    private final IDownloadDBHelper _downloadDBHelper;
    private final ExecutorService _threadPool;

    /** 获取DBManager的一个实例 **/
    public static DBManager getInstance(Context context)
    {
	if (_instance == null)
	{
	    _instance = new DBManager(context.getApplicationContext());
	}
	return _instance;
    }

    private DBManager(Context context)
    {
	_downloadDBHelper = DownloadDBHerlperImpl.getInstance(context.getApplicationContext());
	_threadPool = Executors.newSingleThreadExecutor();
    }

    /** 添加一个数据项 **/
    public void addItem(final DownloadBean bean)
    {
	if (bean._save2database)
	{
	    _threadPool.execute(new Runnable()
	    {
		@Override
		public void run()
		{
		    _downloadDBHelper.addItem(bean);
		    sleepWithoutInterrupt();
		}
	    });
	}
    }

    /** 删除一个数据项 **/
    public void deleteItem(final DownloadBean bean)
    {
	if (bean._save2database)
	{
	    _threadPool.execute(new Runnable()
	    {
		@Override
		public void run()
		{
		    _downloadDBHelper.deleteItem(bean);
		    sleepWithoutInterrupt();
		}
	    });
	}
    }

    /** 修改一个数据项 **/
    public void modifyItem(final DownloadBean bean)
    {
	if (bean._save2database)
	{
	    _threadPool.execute(new Runnable()
	    {
		@Override
		public void run()
		{
		    _downloadDBHelper.modifyItem(bean);
		    sleepWithoutInterrupt();
		}
	    });
	}
    }

    /**
     * 获取所有类型的数据项
     * 
     * @param obtainer
     *            在方法中返回类List<DownloadBean>的实例
     */
    public void getAllItem(final IDBItemObtainer<List<DownloadBean>> obtainer)
    {
	_threadPool.execute(new Runnable()
	{
	    @Override
	    public void run()
	    {
		List<DownloadBean> itemList = _downloadDBHelper.getAllItem();
		obtainer.obtainData(itemList);
		sleepWithoutInterrupt();
	    }
	});
    }

    /**
     * 获取所有类型且下载完成的数据项
     * 
     * @param obtainer
     *            在方法中返回类List<DownloadBean>的实例
     */
    public void getAllCompleteItem(final IDBItemObtainer<List<DownloadBean>> obtainer)
    {
	_threadPool.execute(new Runnable()
	{
	    @Override
	    public void run()
	    {
		List<DownloadBean> itemList = _downloadDBHelper.getAllCompleteItem();
		obtainer.obtainData(itemList);
		sleepWithoutInterrupt();
	    }
	});
    }

    /**
     * 获取id对应的数据项
     * 
     * @param id
     *            数据项的唯一标示符
     * @param obtainer
     *            在方法中返回类DownloadBean的实例，不存在时为null
     */
    public void getUniqueItem(final long id, final int type, final IDBItemObtainer<DownloadBean> obtainer)
    {
	_threadPool.execute(new Runnable()
	{
	    @Override
	    public void run()
	    {
		DownloadBean item = _downloadDBHelper.getUniqueItem(id, type);
		obtainer.obtainData(item);
		sleepWithoutInterrupt();
	    }
	});
    }

    private void sleepWithoutInterrupt()
    {
	try
	{
	    Thread.sleep(10L);
	}
	catch (InterruptedException e)
	{}
    }
}
