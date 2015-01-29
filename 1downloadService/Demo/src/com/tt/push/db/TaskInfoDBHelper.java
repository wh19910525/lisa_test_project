package com.tt.push.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import co.lvdou.foundation.utils.extend.LDContextHelper;
import co.lvdou.foundation.utils.extend.Logout;
import com.tt.push.model.InstalledApkInfo;

public class TaskInfoDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "task_infos";
    private static final int DB_VERSION = 1;
    private static TaskInfoDBHelper mShareHelper;

    private TaskInfoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static TaskInfoDBHelper shareHelper() {
        if (mShareHelper == null) {
            Context context = LDContextHelper.getContext();
            mShareHelper = new TaskInfoDBHelper(context);
        }

        return mShareHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_INSTALL_APK.SQL_CREATE_TABLE());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 插入已安装的apk信息
     *
     * @param info
     */
    public void insertInstalledApkInfo(InstalledApkInfo info) {
        if (info == null) return;

        SQLiteDatabase db = getWritableDatabase();
        try {
            String sql = TABLE_INSTALL_APK.SQL_INSERT_ROW(info.taskID(), info.taskType(), info.packageName());
            db.execSQL(sql);
        } catch (Exception exception) {
            if (!(exception instanceof SQLiteConstraintException)) {
                exception.printStackTrace();
            }
        } finally {
            db.close();
        }
    }

    /**
     * 根据包名查询已安装的apk信息
     *
     * @param packageName apk信息相关的包名
     */
    public InstalledApkInfo queryInstalledApkInfo(String packageName) {
        if (TextUtils.isEmpty(packageName)) return null;

        InstalledApkInfo result = null;

        String sql = TABLE_INSTALL_APK.SQL_QUERY_ROW(packageName);
        Logout.out("待执行的sql语句: " + sql);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = TABLE_INSTALL_APK.TO_INSTALLED_APK_INFO(cursor);
            }
        } finally {
            closeCursor(cursor);
            db.close();
        }

        return result;
    }

    public void deleteInstalledApkInfo(String packageName) {
        if (TextUtils.isEmpty(packageName)) return;

        SQLiteDatabase db = getWritableDatabase();
        try {
            String sql = TABLE_INSTALL_APK.SQL_DELETE_ROW(packageName);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    private static class TABLE_INSTALL_APK {
        private TABLE_INSTALL_APK() {
        }

        //表名
        public static final String NAME = "installed_apk";
        //任务ID，字段类型 INTEGER NOT NULL
        public static final String COLUMN_TASK_ID = "task_id";
        //任务类型，字段类型 INTEGER NOT NULL
        public static final String COLUMN_TASK_TYPE = "task_type";
        //任务相关的apk包名，字段类型 TEXT NOT_NULL
        public static final String COLUMN_PACKAGE_NAME = "package_name";

        /**
         * 创建表的语句
         */
        public static String SQL_CREATE_TABLE() {
            return "CREATE TABLE installed_apk (" +
                    "task_id INTEGER NOT_NULL, " +
                    "task_type INTEGER NOT_NULL, package_name TEXT NOT_NULL UNIQUE);";
        }

        /**
         * 插入新行的语句
         *
         * @param task_id      任务id
         * @param task_type    任务类型
         * @param package_name 任务相关包名
         */
        public static String SQL_INSERT_ROW(int task_id, int task_type, String package_name) {
            return String.format("INSERT INTO installed_apk (task_id, task_type, package_name) VALUES(%d, %d, '%s')", task_id, task_type, package_name);
        }

        /**
         * 根据包名查询相关行的语句
         *
         * @param package_name 包名
         * @return
         */
        public static String SQL_QUERY_ROW(String package_name) {
            return String.format("SELECT * FROM installed_apk WHERE package_name='%s';", package_name);
        }

        /**
         * 根据包名删除行
         *
         * @param package_name 包名
         * @return
         */
        public static String SQL_DELETE_ROW(String package_name) {
            return String.format("DELETE FROM installed_apk WHERE package_name='%s';", package_name);
        }

        /**
         * 从Cursor对象中获取InstalledApkInfo的实例，存在返回相应实例，失败返回null
         */
        public static InstalledApkInfo TO_INSTALLED_APK_INFO(Cursor cursor) {
            int task_id = cursor.getInt(cursor.getColumnIndex(COLUMN_TASK_ID));
            int task_type = cursor.getInt(cursor.getColumnIndex(COLUMN_TASK_TYPE));
            String package_name = cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE_NAME));

            return new InstalledApkInfo().init(task_id, task_type, package_name);
        }
    }
}
