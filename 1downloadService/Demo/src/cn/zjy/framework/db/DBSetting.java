package cn.zjy.framework.db;

public final class DBSetting 
{
	/** 数据库名称 **/
	public final static String DB_NAME = "download";
	
	/** 当前数据库版本  **/
	public final static int DB_VERSION = 1;
	
	/** 记录下载到本地的文件。 */
	public static final String TABLE = "download_file";
	
	/**
	 * 下载的文件的ID。<br/>
	 * 数据库文件表的主键。<br/>
	 */
	public static final String COLOUM_ID = "id";
	
	/**
	 * 文件类型
	 */
	public static final String COLOUM_TYPE = "type";
	
	/**
	 * 下载的文件名称。<br/>
	 */
	public static final String COLOUM_NAME = "name";
	
	/**
	 * 文件的下载链接。<br/>
	 */
	public static final String COLOUM_DOWN_URL = "file_url";
	
	/**
	 * 文件图标所代表的下载链接。<br/>
	 */
	public static final String COLOUM_ICON_URL = "icon_url";
	
	/**
	 * 文件完整的下载字节数。<br/>
	 */
	public static final String COLOUM_TOTAL_SIZE = "size";
	
	/**
	 * 文件已下载的字节数。<br/>
	 */
	public static final String COLOUM_CURRENT_SIZE = "size_loaded";
	
	/**
	 * 文件的存放路径。<br/>
	 */
	public static final String COLOUM_PATH = "save_path";
	
	/**
	 * 下载文件的时间。<br/>
	 */
	public static final String COLOUM_LAST_MODIFYED = "time";
	
	/**
	 * 附加标签
	 */
	public static final String COLOUM_EXTRA = "extra";
	
	/**
	 * 附加标签2
	 */
	public static final String COLOUM_EXTRA2 = "extra2";
	
	/**
	 * 附加标签3
	 */
	public static final String COLOUM_EXTRA3 = "extra3";
	
	/**
	 * 当前下载任务的下载状态。<br/>
	 */
	public static final String COLOUM_STATE = "state";
	/** 下载任务中不存在该包名。与下载状态无关。 */
	public static final int STATE_ERROR = 1;
	/** 正在下载数据。Button应显示“暂停”。 */
	public static final int STATE_DOWNLOADING = 2;
	/** 数据全部下载完成。Button应显示“安装”。 */
	public static final int STATE_COMPLETE = 3;
	
	public static final int STATE_PAUSE = 4;
	
	public static final int STATE_WAITING = 5;
	
	public static final int STATE_DEFAULT = STATE_ERROR;
}
