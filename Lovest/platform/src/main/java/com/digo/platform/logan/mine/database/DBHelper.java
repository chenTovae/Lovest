package com.digo.platform.logan.mine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.digo.platform.logan.mine.database.tables.LoganUFileStorage;

/**
 * Author : Create by Linxinyuan on 2018/10/19
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class DBHelper {
    private static DBHelper dbhelper = null;
    private DatabaseHelper databasehelper = null;

    private DBHelper(Context context) {
        this.databasehelper = new DatabaseHelper(context);
    }

    public synchronized static DBHelper getInstance(Context context) {
        if (dbhelper == null) {
            dbhelper = new DBHelper(context);
        }
        return dbhelper;
    }

    public SQLiteDatabase getWritableDB() {
        return databasehelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDB() {
        return databasehelper.getReadableDatabase();
    }

    public void closeDb() {
        databasehelper.close();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        private static final int DB_VERSION = 1;
        private static final String DATABASE_NAME = "lzlogan.db";

        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DatabaseHelper(Context context) {
            this(context, DATABASE_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(LoganUFileStorage.CREAT_TABLE());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
