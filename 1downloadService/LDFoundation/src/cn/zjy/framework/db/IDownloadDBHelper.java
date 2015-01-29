package cn.zjy.framework.db;

import java.util.List;

import cn.zjy.framework.download.DownloadBean;

interface IDownloadDBHelper
{
    /** 添加数据项 **/
    void addItem(DownloadBean bean);

    /** 删除数据项 **/
    void deleteItem(DownloadBean bean);

    /** 修改数据项 **/
    void modifyItem(DownloadBean bean);

    /** 获取所有类型为铃声的数据项 **/
    List<DownloadBean> getAllItem();

    /** 获取所有类型为铃声且下载完成的数据项 **/
    List<DownloadBean> getAllCompleteItem();

    /**
     * 根据Id值获取唯一的数据项 不存在时返回null
     **/
    DownloadBean getUniqueItem(long id, int type);
}
