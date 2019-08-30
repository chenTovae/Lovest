package com.digo.platform.logan.mine.database.tables;

/**
 * Author : Create by Linxinyuan on 2018/10/19
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class LoganUFileStorage {
    //static name table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_RETRY_LIMIT = "retry";
    public static final String COLUMN_STATUS = "status";

    public static String TABLE_NAME() {
        return "logan_up";
    }

    public static final String[] COLUMN_ARRAY = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PATH,
            COLUMN_RETRY_LIMIT,
            COLUMN_STATUS,
    };

    public static String CREAT_TABLE() {
        return new StringBuffer().
                append("CREATE TABLE IF NOT EXISTS ").
                append(TABLE_NAME()).
                append("(").
                append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").
                append(COLUMN_NAME).append(" TEXT NOT NULL UNIQUE,").
                append(COLUMN_PATH).append(" TEXT NOT NULL UNIQUE,").
                append(COLUMN_RETRY_LIMIT).append(" INTEGER DEFAULT 0,").
                append(COLUMN_STATUS).append(" INTEGER DEFAULT 0 ").
                append(");").
                toString();
    }

    private static String DROP_TABLE() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME();
    }
}
