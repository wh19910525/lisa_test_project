package cn.zjy.framework.download;

import java.util.List;

import cn.zjy.framework.net.DownloadFileEventListener;

public interface IDownloadManager
{
	/** 获取当前类型的下载完成数据项 **/
	List<DownloadBean> getAllCompleteDownloadTaskByType(int type);
	
    /** 获取所有类型的下载数据项 **/
    List<DownloadBean> getAllDownloadTask();

    /** 获取所有类型且下载完成的数据项 **/
    List<DownloadBean> getAllCompleteDownloadTask();

    /** 暂停下载任务 **/
    void pauseDownloadTask(long id, int type);

    /** 暂停所有下载任务 **/
    void pauseAllDownloadTask();

    /**
     * 开始下载任务 前置条件:该下载任务已经添加进数据库
     **/
    void startDownloadTask(long id, int type);

    /**
     * 开始下载任务,如果md5检验码相同，则不重新下载 前置条件:该下载任务已经添加进数据库
     **/
    void startDownloadTask(long id, int type, String md5);

    /** 添加下载任务 **/
    void addDownloadTask(DownloadBean bean);

    /** 根据DownloadBean的id和类型获取最新的DownloadBean **/
    DownloadBean getDownloadBean(long id, int type);

    /** 删除下载任务 **/
    void deleteDownloadTask(long id, int type, boolean isDeleteFile);

    /** 判断下载任务是否已经存在 **/
    boolean isTaskExist(long id, int type);

    /** 添加下载进度观察者 **/
    void addDownloadObserver(DownloadFileEventListener listener);

    /** 移除下载进度观察者 **/
    void removeDownloadObserber(DownloadFileEventListener listener);
}
