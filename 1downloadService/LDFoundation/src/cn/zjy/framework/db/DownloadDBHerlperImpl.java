package cn.zjy.framework.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.zjy.framework.download.DownloadBean;

class DownloadDBHerlperImpl extends SQLiteOpenHelper implements IDownloadDBHelper
{
    public static IDownloadDBHelper getInstance(Context context)
    {
	return new DownloadDBHerlperImpl(context.getApplicationContext(), DBSetting.DB_NAME, null, DBSetting.DB_VERSION);
    }

    private DownloadDBHerlperImpl(Context context, String name, CursorFactory factory, int version)
    {
	super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
	// 生成数据库表
	dropAllTable(db);
	createAllTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
	dropAllTable(db);
	createAllTable(db);
    }

    @Override
    public void addItem(DownloadBean bean)
    {
	deleteItem(bean);
	SQLiteDatabase db = getWritableDatabase();
	db.insert(DBSetting.TABLE, null, bean.toAddItemContentValues());
	db.close();
    }

    private final static String DeleteItemWhereClause = DBSetting.COLOUM_ID + "=?" + " AND " + DBSetting.COLOUM_TYPE
	    + "=?";

    @Override
    public void deleteItem(DownloadBean bean)
    {
	if (bean != null)
	{
	    SQLiteDatabase db = getWritableDatabase();
	    db.delete(DBSetting.TABLE, DeleteItemWhereClause,
		    new String[] { String.valueOf(bean._id), String.valueOf(bean._type) });
	    db.close();
	}
    }

    private final static String ModifyItemWhereClause = DeleteItemWhereClause;

    @Override
    public void modifyItem(DownloadBean bean)
    {
	SQLiteDatabase db = getWritableDatabase();
	db.update(DBSetting.TABLE, bean.toModifyItemContentValues(), ModifyItemWhereClause,
		new String[] { String.valueOf(bean._id), String.valueOf(bean._type) });
	db.close();
    }

    @Override
    public List<DownloadBean> getAllItem()
    {
	ArrayList<DownloadBean> result = null;
	SQLiteDatabase db = getReadableDatabase();
	Cursor cursor = null;
	try
	{
	    cursor = db.query(DBSetting.TABLE, null, null, null, null, null, null);
	    cursor.move(0);
	    result = new ArrayList<DownloadBean>(cursor.getCount());
	    DownloadBean tmp = null;
	    while (cursor.moveToNext())
	    {
		tmp = DownloadBean.restore(cursor);
		if (tmp != null)
		{
		    result.add(tmp);
		}
	    }
	}
	catch (Exception e)
	{}
	finally
	{
	    if (cursor != null)
	    {
		cursor.close();
	    }
	    db.close();
	}

	return result;
    }

    private static final String GetAllCompleteFontItemSelection = DBSetting.COLOUM_STATE + "="
	    + DBSetting.STATE_COMPLETE;

    @Override
    public List<DownloadBean> getAllCompleteItem()
    {
	ArrayList<DownloadBean> result = null;
	SQLiteDatabase db = getReadableDatabase();
	Cursor cursor = null;
	try
	{
	    cursor = db.query(DBSetting.TABLE, null, GetAllCompleteFontItemSelection, null, null, null, null);
	    cursor.move(0);
	    result = new ArrayList<DownloadBean>(cursor.getCount());
	    DownloadBean tmp = null;
	    while (cursor.moveToNext())
	    {
		tmp = DownloadBean.restore(cursor);
		if (tmp != null)
		{
		    result.add(tmp);
		}
	    }
	}
	catch (Exception e)
	{}
	finally
	{
	    if (cursor != null)
	    {
		cursor.close();
	    }
	    db.close();
	}
	return result;
    }

    private static final String GetUniqueItemSelection = DeleteItemWhereClause;

    @Override
    public DownloadBean getUniqueItem(long id, int type)
    {
	DownloadBean result = null;
	SQLiteDatabase db = getReadableDatabase();
	Cursor cursor = null;
	try
	{
	    cursor = db.query(DBSetting.TABLE, null, GetUniqueItemSelection,
		    new String[] { String.valueOf(id), String.valueOf(type) }, null, null, null);
	    cursor.move(0);
	    if (cursor.moveToNext())
	    {
		result = DownloadBean.restore(cursor);
	    }
	}
	catch (Exception e)
	{}
	finally
	{
	    if (cursor != null)
	    {
		cursor.close();
	    }
	    db.close();
	}
	return result;
    }

    private void createAllTable(SQLiteDatabase db)
    {
	String sql = createTableSql();
	db.execSQL(sql);
    }

    private void dropAllTable(SQLiteDatabase db)
    {
	String sql = "drop table if exists " + DBSetting.TABLE;
	db.execSQL(sql);
    }

    private String createTableSql()
    {
	String result = "create table if not exists " + DBSetting.TABLE// <br/>
		+ " (" + DBSetting.COLOUM_ID + " LONG NOT NULL,"// <br/>
		+ DBSetting.COLOUM_NAME + " TEXT,"// <br/>
		+ DBSetting.COLOUM_DOWN_URL + " TEXT,"// <br/>
		+ DBSetting.COLOUM_ICON_URL + " TEXT,"// <br/>
		+ DBSetting.COLOUM_PATH + " TEXT," // <br/>
		+ DBSetting.COLOUM_LAST_MODIFYED + " LONG,"// <br/>
		+ DBSetting.COLOUM_TOTAL_SIZE + " LONG,"// <br/>
		+ DBSetting.COLOUM_CURRENT_SIZE + " LONG,"// <br/>
		+ DBSetting.COLOUM_STATE + " INTEGER NOT NULL,"// <br/>
		+ DBSetting.COLOUM_TYPE + " INTEGER NOT NULL,"// <br/>
		+ DBSetting.COLOUM_EXTRA + " TEXT," // <br />
		+ DBSetting.COLOUM_EXTRA2 + " TEXT," // <br />
		+ DBSetting.COLOUM_EXTRA3 + " TEXT," // <br />
		+ "PRIMARY KEY(" + DBSetting.COLOUM_ID + ", " + DBSetting.COLOUM_TYPE + ")" // <br/>
		+ ");";
	return result;
    }
}
