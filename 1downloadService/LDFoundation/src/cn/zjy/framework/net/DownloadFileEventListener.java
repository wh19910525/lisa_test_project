package cn.zjy.framework.net;

import cn.zjy.framework.download.DownloadBean;

public interface DownloadFileEventListener 
{
	void onStartDownload(DownloadBean bean);
	void onDownloading(DownloadBean bean);
	void onDownloadComplete(DownloadBean bean);
	void onDownloadFail(DownloadBean bean);
	void onDownloadPause(DownloadBean bean);
	void onDownloadCancel(DownloadBean bean);
	
	public static DownloadFileEventListener Null = new DownloadFileEventListener() 
	{
		@Override
		public void onStartDownload(DownloadBean bean) 
		{
		}
		
		@Override
		public void onDownloading(DownloadBean bean) 
		{
		}
		
		@Override
		public void onDownloadPause(DownloadBean bean) 
		{
		}
		
		@Override
		public void onDownloadFail(DownloadBean bean) 
		{
		}
		
		@Override
		public void onDownloadComplete(DownloadBean bean) 
		{
		}

		@Override
		public void onDownloadCancel(DownloadBean bean) {
			// TODO Auto-generated method stub
			
		}
	};
}
