package cn.zjy.framework.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import cn.zjy.framework.db.DBManager;
import cn.zjy.framework.db.IDBItemObtainer;
import cn.zjy.framework.download.DownloadBean.State;
import cn.zjy.framework.net.DownloadFileEventListener;
import cn.zjy.framework.net.DownloadFileTask;

public final class DownloadManagerImpl implements IDownloadManager, DownloadFileEventListener
{
    private final Context _context;
    private static IDownloadManager _instance;
    private final List<DownloadFileTask> _downloadingTaskList;
    private List<DownloadBean> _beanList;
    private List<DownloadBean> _completeBeanList;
    private final List<DownloadFileEventListener> _obserberList;
    private final DownloadTaskRunner _taskRunner;
    private boolean _isObserberListEmpty = true;

    public static IDownloadManager getInstance(Context context)
    {
	if (_instance == null)
	{
	    _instance = new DownloadManagerImpl(context.getApplicationContext());
	}
	return _instance;
    }

    private DownloadManagerImpl(Context context)
    {
	_context = context.getApplicationContext();
	_downloadingTaskList = new CopyOnWriteArrayList<DownloadFileTask>();
	_obserberList = new CopyOnWriteArrayList<DownloadFileEventListener>();
	_taskRunner = DownloadTaskRunner.getInstance();
	getAllTask(new IDBItemObtainer<List<DownloadBean>>()
	{
	    @Override
	    public void obtainData(List<DownloadBean> datas)
	    {
		_beanList = new CopyOnWriteArrayList<DownloadBean>();
		for (DownloadBean b : datas)
		{
		    if (b._totalSize == 0)
		    {
			DBManager.getInstance(_context).deleteItem(b);
		    }
		    else
		    {
			if (b._state == State.Downloading || b._state == State.Waiting || b._state == State.Error)
			{
			    b._state = State.Pause;
			}
			else if (b._state == State.Complete)
			{
			    b._isNewTask = false;
			}
			_beanList.add(b);
		    }
		}
	    }
	});

	getAllCompleteTask(new IDBItemObtainer<List<DownloadBean>>()
	{
	    @Override
	    public void obtainData(List<DownloadBean> datas)
	    {
		_completeBeanList = new CopyOnWriteArrayList<DownloadBean>();
		for (DownloadBean b : datas)
		{
		    b._isNewTask = false;
		    _completeBeanList.add(b);
		}
	    }
	});
    }

    @Override
    public void addDownloadObserver(DownloadFileEventListener listener)
    {
	if (listener != null && _obserberList.contains(listener) == false)
	{
	    _obserberList.add(listener);
	    _isObserberListEmpty = false;
	}
    }

    @Override
    public void removeDownloadObserber(DownloadFileEventListener listener)
    {
	if (listener != null && _obserberList.contains(listener))
	{
	    _obserberList.remove(listener);
	    if (_obserberList.isEmpty())
	    {
		_isObserberListEmpty = true;
	    }
	}
    }

    @Override
    public List<DownloadBean> getAllDownloadTask()
    {
	return _beanList;
    }

    @Override
    public List<DownloadBean> getAllCompleteDownloadTask()
    {
	return _completeBeanList;
    }

    @Override
    public void pauseDownloadTask(long id, int type)
    {
	DownloadFileTask task = getDownloadTask(id, type);
	if (task != null)
	{
	    task.pauseTask();
	    removeDownloadTask(task);
	}
	else
	{
	    DownloadBean bean = getDownloadBean(id, type);
	    if (bean != null)
	    {
		bean._state = State.Pause;
		onDownloadPause(bean);
	    }
	}
    }

    @Override
    public void pauseAllDownloadTask()
    {
	for (DownloadFileTask task : _downloadingTaskList)
	{
	    task.pauseTask();
	}
	_downloadingTaskList.clear();
    }

    @Override
    public void startDownloadTask(long id, int type)
    {
	startDownloadTask(id, type, null);
    }

    @Override
    public void startDownloadTask(long id, int type, String md5)
    {
	DownloadBean bean = getDownloadBean(id, type);
	if (bean != null)
	{
	    if (bean._state == State.Pause || bean._state == State.Error || bean._state == State.Waiting)
	    {
		DownloadFileTask task = getDownloadTask(id, type);
		if (task == null)
		{
		    task = new DownloadFileTask(_context, bean, this, md5);
		    _downloadingTaskList.add(task);
		    _taskRunner.submitTask(task);
		}
	    }
	}
    }

    @Override
    public void addDownloadTask(DownloadBean bean)
    {
	if (_beanList != null)
	{
	    if (getDownloadBean(bean._id, bean._type) == null)
	    {
		_beanList.add(bean);
	    }
	}
    }

    @Override
    public void deleteDownloadTask(long id, int type, boolean isDeleteFile)
    {
	DownloadBean bean = getDownloadBean(id, type);
	if (bean != null)
	{
	    onDownloadCancel(bean);
	    _beanList.remove(bean);
	    _completeBeanList.remove(bean);
	    DownloadFileTask task = getDownloadTask(id, type);
	    if (task != null)
	    {
		task.cancelTask();
	    }
	    DBManager.getInstance(_context).deleteItem(bean);
	    if (isDeleteFile)
	    {
		new File(bean._filePath).delete();
		new File(bean._filePath + ".tmp").delete();
	    }
	}
    }

    @Override
    public boolean isTaskExist(long id, int type)
    {
	boolean result = false;
	if (getDownloadTask(id, type) != null)
	{
	    result = true;
	}
	return result;
    }

    @Override
    public void onStartDownload(final DownloadBean bean)
    {
	if (_isObserberListEmpty == false)
	{
	    for (DownloadFileEventListener l : _obserberList)
	    {
		l.onStartDownload(bean);
	    }
	}
    }

    @Override
    public void onDownloading(final DownloadBean bean)
    {
	if (_isObserberListEmpty == false)
	{
	    for (DownloadFileEventListener l : _obserberList)
	    {
		l.onDownloading(bean);
	    }
	}
    }

    @Override
    public void onDownloadComplete(DownloadBean bean)
    {
	if (_isObserberListEmpty == false)
	{
	    for (DownloadFileEventListener l : _obserberList)
	    {
		l.onDownloadComplete(bean);
	    }
	}

	if (_completeBeanList != null)
	{
	    _completeBeanList.add(bean);
	}

	removeDownloadTask(bean._id, bean._type);
    }

    @Override
    public void onDownloadFail(DownloadBean bean)
    {
	if (_isObserberListEmpty == false)
	{
	    for (DownloadFileEventListener l : _obserberList)
	    {
		l.onDownloadFail(bean);
	    }
	}

	if (_beanList != null)
	{
	    _beanList.remove(getDownloadBean(bean._id, bean._type));
	}
	removeDownloadTask(bean._id, bean._type);
    }

    @Override
    public void onDownloadPause(DownloadBean bean)
    {
	if (_isObserberListEmpty == false)
	{
	    for (DownloadFileEventListener l : _obserberList)
	    {
		l.onDownloadPause(bean);
	    }
	}
	removeDownloadTask(bean._id, bean._type);
    }

    @Override
    public void onDownloadCancel(DownloadBean bean)
    {
	if (_isObserberListEmpty == false)
	{
	    for (DownloadFileEventListener l : _obserberList)
	    {
		l.onDownloadCancel(bean);
	    }
	}
    }

    @Override
    public DownloadBean getDownloadBean(long id, int type)
    {
	if (_completeBeanList != null)
	{
	    for (DownloadBean tmp : _completeBeanList)
	    {
		if (tmp._id == id && tmp._type == type)
		{
		    return tmp;
		}
	    }
	}

	if (_beanList != null)
	{
	    for (DownloadBean tmp : _beanList)
	    {
		if (tmp._id == id && tmp._type == type)
		{
		    return tmp;
		}
	    }
	}

	return null;
    }

    private DownloadFileTask getDownloadTask(long id, int type)
    {
	DownloadFileTask result = null;
	for (DownloadFileTask task : _downloadingTaskList)
	{
	    if (task.isRelated(id, type))
	    {
		result = task;
		break;
	    }
	}
	return result;
    }

    private void removeDownloadTask(long id, int type)
    {
	removeDownloadTask(getDownloadTask(id, type));
    }

    private void removeDownloadTask(DownloadFileTask task)
    {
	if (task != null)
	{
	    _downloadingTaskList.remove(task);
	}
    }

    private void getAllTask(IDBItemObtainer<List<DownloadBean>> obtainer)
    {
	DBManager.getInstance(_context).getAllItem(obtainer);
    }

    private void getAllCompleteTask(IDBItemObtainer<List<DownloadBean>> obtainer)
    {
	DBManager.getInstance(_context).getAllCompleteItem(obtainer);
    }

    @Override
    public List<DownloadBean> getAllCompleteDownloadTaskByType(int type)
    {
	List<DownloadBean> list = new ArrayList<DownloadBean>();
	for (DownloadBean bean : _completeBeanList)
	{
	    if (bean._type == type)
	    {
		list.add(bean);
	    }
	}
	return list;
    }
}
