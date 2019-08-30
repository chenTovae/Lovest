package com.digo.platform.logan.mine.database.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.digo.platform.logan.mine.database.DBHelper;
import com.digo.platform.logan.mine.database.tables.LoganUFileStorage;
import com.digo.platform.logan.mine.upload.RealSendRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.digo.platform.logan.mine.database.tables.LoganUFileModel.DEFAULT_RETRY;
import static com.digo.platform.logan.mine.database.tables.LoganUFileModel.DEFAULT_STATUS;

/**
 * Author : Create by Linxinyuan on 2018/10/19
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class LoganUFileDao {
    private DBHelper mSqlDb = null;
    private static LoganUFileDao dao = null;

    private LoganUFileDao(Context context) {
        mSqlDb = DBHelper.getInstance(context);
    }

    public synchronized static LoganUFileDao getInstance(Context context) {
        if (dao == null) {
            dao = new LoganUFileDao(context);
        }
        return dao;
    }

    /**
     * 查询需要上传的文件
     * 文件过滤：retry<=3
     *
     * @param arrPath 待上传的日志文件路径数组
     * @param force   是否启用强制上传
     * @return
     */
    public String[] queryUploadFilter(String[] arrPath, boolean force) {
        List<String> filterAfter = new ArrayList<>();
        if (arrPath != null && arrPath.length > 0) {
            try {
                String placeHolder = makePlaceholders(arrPath.length);
                String sql = "select * from " + LoganUFileStorage.TABLE_NAME() + " where path in (" + placeHolder + ")";
                Cursor cursor = mSqlDb.getWritableDB().rawQuery(sql, arrPath);//根据path查询出符合条件的上传条目
                while (cursor.moveToNext()) {
                    //日志文件没有上传过且重试次数小于等于3次
                    if (cursor.getInt(cursor.getColumnIndex("retry")) <= 3) {
                        if (force) {
                            //不考虑是否已上传标志位，force表示已经上传过的仍然要上传
                            filterAfter.add(cursor.getString(cursor.getColumnIndex("path")));
                        } else {
                            if (cursor.getInt(cursor.getColumnIndex("status")) == 0) {
                                //需要考虑是否已上传标志位，只上传没有上传过的文件
                                filterAfter.add(cursor.getString(cursor.getColumnIndex("path")));
                            }
                        }
                    }
                }
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                //do not show exception msg
            } finally {
                mSqlDb.closeDb();
            }
        }
        return (filterAfter.size() > 0) ? filterAfter.toArray(new String[filterAfter.size()]) : null;
    }

    /**
     * 重传文件检测并顺便删除retry次数大于3的条目
     *
     * @return
     */
    public String[] queryNeedRetry() {
        List<String> filterAfter = new ArrayList<>();
        try {
            //删除retry最大次数的文件,不删除源文件等待7天自动被清理
            mSqlDb.getWritableDB().delete(LoganUFileStorage.TABLE_NAME(), "retry>3", null);
            //数据库检索需要重新上传的条目
            String sql = "select * from " + LoganUFileStorage.TABLE_NAME() + " where retry<=3 and status!=1";
            Cursor cursor = mSqlDb.getWritableDB().rawQuery(sql, null);//根据path查询出符合条件的重新上传条目
            while (cursor.moveToNext()) {
                filterAfter.add(cursor.getString(cursor.getColumnIndex("path")));
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            //do not show exception msg
        } finally {
            mSqlDb.closeDb();
        }
        return (filterAfter.size() > 0) ? filterAfter.toArray(new String[filterAfter.size()]) : null;
    }

    /**
     * 插入新待上传项
     *
     * @param paths 文件路径
     */
    public void insertNew(String[] paths) {
        if (paths != null && paths.length > 0) {
            for (String path : paths) {
                ContentValues cv = new ContentValues();
                try {
                    cv.put(LoganUFileStorage.COLUMN_NAME, splitFileName(path));//unique key
                    cv.put(LoganUFileStorage.COLUMN_PATH, path);//unique key
                    cv.put(LoganUFileStorage.COLUMN_RETRY_LIMIT, DEFAULT_RETRY);
                    cv.put(LoganUFileStorage.COLUMN_STATUS, DEFAULT_STATUS);
                    //数据库不存在该记录则插入
                    if (!checkRecordIsExist(splitFileName(path), path)) {
                        mSqlDb.getWritableDB().insert(LoganUFileStorage.TABLE_NAME(), null, cv);
                    }
                } catch (Exception e) {
                    //do not show exception msg
                } finally {
                    cv.clear();
                    mSqlDb.closeDb();
                }
            }
        }
    }

    /**
     * 判断数据库记录是否存在
     *
     * @param name
     * @param path
     */
    private boolean checkRecordIsExist(String name, String path) {
        try {
            String sql = "select * from " + LoganUFileStorage.TABLE_NAME() + " where name=? and path=?";
            Cursor cursor = mSqlDb.getWritableDB().rawQuery(sql, new String[]{name, path});
            if (cursor == null || cursor.getCount() == 0) {
                if (cursor != null)
                    cursor.close();
                return false;//不存在
            } else {
                if (cursor != null)
                    cursor.close();
                return true;//存在
            }
        } catch (Exception e) {
            //do not show exception msg
        } finally {
            mSqlDb.closeDb();
        }
        return false;//默认不存在
    }

    /**
     * 数据库与文件系统删除文件同步
     *
     * @param fn 文件名
     * @param fp 文件路径
     * @return
     */
    public int syncDelete(String fn, String fp) {
        if (!TextUtils.isEmpty(fp) && !TextUtils.isEmpty(fn)) {
            try {
                return mSqlDb.getWritableDB().
                        delete(LoganUFileStorage.TABLE_NAME(), "name=? and path=?", new String[]{fn, fp});
            } catch (Exception e) {
                //do not show exception msg
            } finally {
                mSqlDb.closeDb();
            }
        }
        return -1;
    }

    /**
     * 数据库与文件系统删除文件同步
     *
     * @param fp 文件路径
     * @return
     */
    public int syncDeleteOnlyPath(String fp) {
        if (!TextUtils.isEmpty(fp)) {
            try {
                return mSqlDb.getWritableDB().
                        delete(LoganUFileStorage.TABLE_NAME(), "path=?", new String[]{fp});
            } catch (Exception e) {
                //do not show exception msg
            } finally {
                mSqlDb.closeDb();
            }
        }
        return -1;
    }

    /**
     * 更新上传状态
     *
     * @param fn    文件名
     * @param fp    文件路径
     * @param state 上传状态
     * @return
     */
    public int updateStatus(String fn, String fp, int state) {
        if (!TextUtils.isEmpty(fn) && !TextUtils.isEmpty(fp)) {
            ContentValues cv = new ContentValues();
            try {
                cv.put(LoganUFileStorage.COLUMN_STATUS, state);
                return mSqlDb.getWritableDB().update(LoganUFileStorage.TABLE_NAME(), cv,
                        "name=? and path=?", new String[]{fn, fp});
            } catch (Exception e) {
                //do not show exception msg
            } finally {
                cv.clear();
                mSqlDb.closeDb();
            }
        }
        return -1;
    }

    /**
     * 更新重试状态(重试+1)
     *
     * @param fn 文件名
     * @param fp 文件路径
     * @return
     */
    public int updateRetry(String fn, String fp) {
        if (!TextUtils.isEmpty(fn) && !TextUtils.isEmpty(fp)) {
            int retry = queryRetryColumn(fn, fp);
            if (retry != -1) {
                ContentValues cv = new ContentValues();
                try {
                    cv.put(LoganUFileStorage.COLUMN_RETRY_LIMIT, retry + 1);
                    return mSqlDb.getWritableDB().update(LoganUFileStorage.TABLE_NAME(), cv,
                            "name=? and path=?", new String[]{fn, fp});
                } catch (Exception e) {
                    //do not show exception msg
                } finally {
                    cv.clear();
                    mSqlDb.closeDb();
                }
            }
        }
        return -1;
    }

    /**
     * 启动重传的时候重置非法的上传状态，正在进行中改为未上传
     * 备注:上次使用过程中正在上传的文件标志位为2，上传器无法再次上传
     */
    public void refreshIllegalStatus() {
        try {
            String sql = "select * from " + LoganUFileStorage.TABLE_NAME() + " where status=2";
            Cursor cursor = mSqlDb.getWritableDB().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String fn = cursor.getString(cursor.getColumnIndex("name"));
                String fp = cursor.getString(cursor.getColumnIndex("path"));
                int status = RealSendRunnable.STATUS_UPLOAD_INITIAL;
                updateStatus(fn, fp, status);
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            //do not show exception msg
        } finally {
            mSqlDb.closeDb();
        }
    }

    /**
     * 查询某一条数据的retry当前值
     *
     * @param fn 文件名
     * @param fp 文件路径
     * @return
     */
    public int queryRetryColumn(String fn, String fp) {
        if (TextUtils.isEmpty(fp) || TextUtils.isEmpty(fn))
            return -1;
        try {
            Cursor cursor = mSqlDb.getWritableDB().query(LoganUFileStorage.TABLE_NAME(),
                    LoganUFileStorage.COLUMN_ARRAY, "name=? and path=?",
                    new String[]{fn, fp}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int retry = cursor.getInt(cursor.getColumnIndex("retry"));
                if (cursor != null)
                    cursor.close();
                return retry;
            }
        } catch (Exception e) {
            //do not show exception msg
        } finally {
            mSqlDb.closeDb();
        }
        return -1;
    }

    /**
     * 查询某一条数据的status当前值
     *
     * @param fn 文件名
     * @param fp 文件路径
     * @return
     */
    public int queryUploadStatus(String fn, String fp) {
        if (TextUtils.isEmpty(fp) || TextUtils.isEmpty(fn))
            return -1;
        try {
            Cursor cursor = mSqlDb.getWritableDB().query(LoganUFileStorage.TABLE_NAME(),
                    LoganUFileStorage.COLUMN_ARRAY, "name=? and path=?",
                    new String[]{fn, fp}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                if (cursor != null)
                    cursor.close();
                return status;
            }
        } catch (Exception e) {
            //do not show exception msg
        } finally {
            mSqlDb.closeDb();
        }
        return -1;
    }

    /**
     * 根据路径切割出文件名的辅助方法
     *
     * @param path 文件路径
     * @return
     */
    private String splitFileName(String path) {
        String[] sourceStrArray = path.split("/");
        return sourceStrArray[sourceStrArray.length - 1];
    }

    /**
     * where in 拼接语句
     *
     * @param len 参数长度
     * @return
     */
    private String makePlaceholders(int len) {
        if (len < 1) {
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
