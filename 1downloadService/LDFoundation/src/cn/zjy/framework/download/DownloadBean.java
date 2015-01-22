package cn.zjy.framework.download;

import java.net.URLDecoder;
import java.net.URLEncoder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.zjy.framework.db.DBManager;
import cn.zjy.framework.db.DBSetting;

public final class DownloadBean
{
    public enum State
    {
	Downloading, Complete, Error, Pause, Waiting
    };

    public long _id;
    public String _name;
    public String _downurl;
    public String _imgUrl;
    public long _lastModify;
    public long _currentSize;
    public long _totalSize;
    public String _filePath;
    public State _state;
    public int _type;
    public String _extra;
    public String _extra2;
    public String _extra3;
    public boolean _isNewTask = true;
    public boolean _notify = true;
    public boolean _save2database = true;

    public DownloadBean(long id, String name, String savePath, String downUrl, String iconUrl, int type, String extra,
	    String extra2, String extra3)
    {
	this._id = id;
	this._name = (name == null) ? "" : name;
	this._downurl = (downUrl == null) ? "" : downUrl;
	this._imgUrl = (iconUrl == null) ? "" : iconUrl;
	this._type = type;
	this._filePath = (savePath == null) ? "" : savePath;
	this._lastModify = System.currentTimeMillis();
	this._currentSize = 0L;
	this._totalSize = 0L;
	this._state = State.Error;
	this._extra = extra;
	this._extra2 = extra2;
	this._extra3 = extra3;
    }

    public DownloadBean()
    {}

    @Override
    public int hashCode()
    {
	return (int) _id * 10000 + _type;
    }

    @Override
    public boolean equals(Object object)
    {
	return hashCode() == object.hashCode();
    }

    @SuppressWarnings("deprecation")
    public ContentValues toAddItemContentValues()
    {
	ContentValues cv = new ContentValues();
	cv.put(DBSetting.COLOUM_ID, _id);
	cv.put(DBSetting.COLOUM_NAME, URLEncoder.encode(_name));
	cv.put(DBSetting.COLOUM_DOWN_URL, URLEncoder.encode(_downurl));
	if (_imgUrl != null)
	{
	    cv.put(DBSetting.COLOUM_ICON_URL, URLEncoder.encode(_imgUrl));
	}
	cv.put(DBSetting.COLOUM_LAST_MODIFYED, _lastModify);
	cv.put(DBSetting.COLOUM_CURRENT_SIZE, _currentSize);
	cv.put(DBSetting.COLOUM_TOTAL_SIZE, _totalSize);
	cv.put(DBSetting.COLOUM_PATH, URLEncoder.encode(_filePath));
	cv.put(DBSetting.COLOUM_TYPE, _type);
	if (_extra != null)
	{
	    cv.put(DBSetting.COLOUM_EXTRA, URLEncoder.encode(_extra));
	}
	if (_extra2 != null)
	{
	    cv.put(DBSetting.COLOUM_EXTRA2, URLEncoder.encode(_extra2));
	}
	if (_extra3 != null)
	{
	    cv.put(DBSetting.COLOUM_EXTRA3, URLEncoder.encode(_extra3));
	}
	switch (_state)
	{
	case Downloading:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_DOWNLOADING);
	    break;
	case Error:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_ERROR);
	    break;
	case Pause:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_PAUSE);
	    break;
	case Complete:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_COMPLETE);
	    break;
	case Waiting:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_WAITING);
	    break;
	default:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_DEFAULT);
	    break;
	}
	return cv;
    }

    public ContentValues toModifyItemContentValues()
    {
	ContentValues cv = new ContentValues();
	cv.put(DBSetting.COLOUM_LAST_MODIFYED, _lastModify);
	cv.put(DBSetting.COLOUM_CURRENT_SIZE, _currentSize);
	cv.put(DBSetting.COLOUM_TOTAL_SIZE, _totalSize);
	switch (_state)
	{
	case Downloading:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_DOWNLOADING);
	    break;
	case Error:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_ERROR);
	    break;
	case Pause:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_PAUSE);
	    break;
	case Complete:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_COMPLETE);
	    break;
	case Waiting:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_WAITING);
	    break;
	default:
	    cv.put(DBSetting.COLOUM_STATE, DBSetting.STATE_DEFAULT);
	    break;
	}
	return cv;
    }

    public void modify(Context context)
    {
	DBManager.getInstance(context).modifyItem(this);
    }

    public static DownloadBean restore(Cursor cursor)
    {
	try
	{
	    DownloadBean bean = new DownloadBean();
	    bean._id = cursor.getLong(cursor.getColumnIndex(DBSetting.COLOUM_ID));
	    bean._name = URLDecoder.decode(cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_NAME)));
	    bean._downurl = URLDecoder.decode(cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_DOWN_URL)));
	    bean._totalSize = cursor.getLong(cursor.getColumnIndex(DBSetting.COLOUM_TOTAL_SIZE));
	    bean._lastModify = cursor.getLong(cursor.getColumnIndex(DBSetting.COLOUM_LAST_MODIFYED));
	    bean._currentSize = cursor.getLong(cursor.getColumnIndex(DBSetting.COLOUM_CURRENT_SIZE));
	    bean._filePath = URLDecoder.decode(cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_PATH)));
	    bean._extra = cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_EXTRA));
	    if (bean._extra == null)
	    {
		bean._extra = "";
	    }
	    else
	    {
		bean._extra = URLDecoder.decode(bean._extra);
	    }
	    bean._extra2 = cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_EXTRA2));
	    if (bean._extra2 == null)
	    {
		bean._extra2 = "";
	    }
	    else
	    {
		bean._extra2 = URLDecoder.decode(bean._extra2);
	    }
	    bean._extra3 = cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_EXTRA3));
	    if (bean._extra3 == null)
	    {
		bean._extra3 = "";
	    }
	    else
	    {
		bean._extra3 = URLDecoder.decode(bean._extra);
	    }
	    bean._type = cursor.getInt(cursor.getColumnIndex(DBSetting.COLOUM_TYPE));
	    final String imgUrl = cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_ICON_URL));
	    if (imgUrl != null)
	    {
		bean._imgUrl = URLDecoder.decode(cursor.getString(cursor.getColumnIndex(DBSetting.COLOUM_ICON_URL)));
	    }
	    switch (cursor.getInt(cursor.getColumnIndex(DBSetting.COLOUM_STATE)))
	    {
	    case DBSetting.STATE_ERROR:
		bean._state = State.Error;
		break;
	    case DBSetting.STATE_PAUSE:
		bean._state = State.Pause;
		break;
	    case DBSetting.STATE_DOWNLOADING:
		bean._state = State.Downloading;
		break;
	    case DBSetting.STATE_COMPLETE:
		bean._state = State.Complete;
		break;
	    case DBSetting.STATE_WAITING:
		bean._state = State.Waiting;
		break;
	    default:
		bean._state = State.Error;
		break;
	    }
	    return bean;
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    return null;
	}
    }
}
