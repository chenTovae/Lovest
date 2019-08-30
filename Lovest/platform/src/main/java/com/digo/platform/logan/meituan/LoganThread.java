/*
 * Copyright (c) 2018-present, 美团点评
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.digo.platform.logan.meituan;

import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.meituan.action.ArrangeAction;
import com.digo.platform.logan.meituan.action.LoganModel;
import com.digo.platform.logan.meituan.action.ReOpenAction;
import com.digo.platform.logan.meituan.action.SendLogAction;
import com.digo.platform.logan.meituan.action.WriteLogAction;
import com.digo.platform.logan.meituan.protocol.LoganProtocol;
import com.digo.platform.logan.meituan.protocol.OnLoganProtocolStatus;
import com.digo.platform.logan.meituan.send.SendLogRunnable;
import com.digo.platform.logan.meituan.util.LoganUtil;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoganThread extends Thread {

    private static final String TAG = "LoganThread";
    private static final int MINUTE = 60 * 1000;
    private static final long LONG = 24 * 60 * 60 * 1000;

    private final Object sync = new Object();
    private final Object sendSync = new Object();
    private volatile boolean mIsRun = true;

    private long mCurrentDay;
    private boolean mIsWorking;
    private File mFileDirectory;
    private boolean mIsSDCard;
    private long mLastTime;
    private LoganProtocol mLoganProtocol;
    private ConcurrentLinkedQueue<LoganModel> mCacheLogQueue;
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private long mSaveTime; //存储时间
    private long mMaxLogFile;//最大文件大小
    private long mMinSDCard;//最小不写入磁盘大小
    private long mSimpleSize;//单个分片的最大限制
    private int mDepotFileNum;//单天仓库的日志文件数量
    private boolean mIsMainProcess;//是否是主进程
    private String mEncryptKey16;
    private String mEncryptIv16;
    private int mSendLogStatusCode;
    //正则校验规则
    private Pattern dirPattern;
    private Pattern filePattern;
    // 发送缓存队列
    private ConcurrentLinkedQueue<LoganModel> mCacheSendQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();

    public LoganThread(
            ConcurrentLinkedQueue<LoganModel> cacheLogQueue, String cachePath,
            String path, long saveTime, long maxLogFile, long minSDCard, long simpleSize, int depotFileNum,
            String encryptKey16, String encryptIv16, boolean isMainProcess) {
        mCacheLogQueue = cacheLogQueue;
        mCachePath = cachePath;
        mPath = path;
        mSaveTime = saveTime;
        mMaxLogFile = maxLogFile;
        mMinSDCard = minSDCard;
        mEncryptKey16 = encryptKey16;
        mEncryptIv16 = encryptIv16;
        mSimpleSize = simpleSize;
        mDepotFileNum = depotFileNum;
        mIsMainProcess = isMainProcess;

        dirPattern = Pattern.compile(LogzConstant.DEPOT_RULE);
        filePattern = Pattern.compile(LogzConstant.WRITING_RULE);
    }

    public void notifyRun() {
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    public void quit() {
        mIsRun = false;
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        while (mIsRun) {
            synchronized (sync) {
                mIsWorking = true;
                try {
                    LoganModel model = mCacheLogQueue.poll();
                    if (model == null) {
                        mIsWorking = false;
                        sync.wait();
                        mIsWorking = true;
                    } else {
                        doNetworkLog(model);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mIsWorking = false;
                }
            }
        }
    }

    private void doNetworkLog(LoganModel model) {
        if (mLoganProtocol == null) {
            mLoganProtocol = LoganProtocol.newInstance();
            mLoganProtocol.setOnLoganProtocolStatus(new OnLoganProtocolStatus() {
                @Override
                public void loganProtocolStatus(String cmd, int code) {
                    Logan.onListenerLogWriteStatus(cmd, code);
                }
            });
            mLoganProtocol.logan_init(mCachePath, mPath, (int) mMaxLogFile, mEncryptKey16, mEncryptIv16);
            mLoganProtocol.logan_debug(Logan.sDebug);
        }

        if (model == null || !model.isValid()) {
            return;
        }

        if (model.action == LoganModel.Action.WRITE) {
            doWriteLog2File(model.writeLogAction);
        } else if (model.action == LoganModel.Action.SEND) {
            if (model.sendLogAction.sendLogRunnable != null) {
                // 是否正在发送
                synchronized (sendSync) {
                    if (mSendLogStatusCode == SendLogRunnable.SENDING) {
                        mCacheSendQueue.add(model);
                    } else {
                        doSendLog2Net(model.sendLogAction);
                    }
                }
            }
        } else if (model.action == LoganModel.Action.FLUSH) {
            doFlushLog2File();
        } else if (model.action == LoganModel.Action.REOPEN) {
            doReOpenLogFile(model.reOpenAction);
        } else if (model.action == LoganModel.Action.ARRANGE) {
            doArrangeFile(model.arrangeAction);
        }
    }

    private void doReOpenLogFile(ReOpenAction action) {
        //进行文件移动
        File loging = new File(mPath + File.separator + String.valueOf(LoganUtil.getCurrentTime()));
        if (loging.exists()) {
            moveCuttedFileToDir(loging);
            //进行LoganFile的重新打开
            mLoganProtocol.logan_open(String.valueOf(LoganUtil.getCurrentTime()));
            //回调处理
            action.callback.onReOpenFile();
        }
    }

    private void doArrangeFile(ArrangeAction arrangeAction) {
        mLoganProtocol.logan_flush();//调用进程的flush操作
        doCleanUseLessDir();//过期文件清除逻辑
        moveRestLogFileDir();//整理非当天的剩余文件
        if (arrangeAction != null && arrangeAction.callback != null) {
            arrangeAction.callback.onArrangeFile();
        }
    }

    private void doFlushLog2File() {
        if (Logan.sDebug) {
            Log.d(TAG, "Logan flush start");
        }
        if (mLoganProtocol != null) {
            mLoganProtocol.logan_flush();
        }
    }

    private boolean isDay() {
        long currentTime = System.currentTimeMillis();
        return mCurrentDay < currentTime && mCurrentDay + LONG > currentTime;
    }

    private void doWriteLog2File(WriteLogAction action) {
        if (mFileDirectory == null) {
            mFileDirectory = new File(mPath);
        }

        //当天创建新文件（只会在新的一天才会执行该逻辑一次）
        if (!isDay()) {
            long tempCurDay = LoganUtil.getCurrentTime();
            mCurrentDay = tempCurDay;//2018-10-24的时间戳
            createNewDayDir();//建立2018-10-24文件夹
            mLoganProtocol.logan_open(String.valueOf(mCurrentDay));
        }

        //TODO 只可以在主线程进行的大小切片动作，避免数据库并发
        //文件切片处理（文件大小大于切片自定义大小要做一次文件到仓库的移动）
        File loging = new File(mPath + File.separator + String.valueOf(LoganUtil.getCurrentTime()));
        if (loging.exists() && loging.length() >= mSimpleSize && mIsMainProcess) {
            try {
                moveCuttedFileToDir(loging);
                mLoganProtocol.logan_open(String.valueOf(LoganUtil.getCurrentTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long currentTime = System.currentTimeMillis(); //每隔1分钟判断一次
        if (currentTime - mLastTime > MINUTE) {
            mIsSDCard = isCanWriteSDCard();
        }
        mLastTime = System.currentTimeMillis();

        if (!mIsSDCard) {
            return;
        }
        mLoganProtocol.logan_write(action.flag, action.log, action.localTime, action.threadName,
                action.threadId, action.isMainThread);
    }

    //定期清理日志文件，清除粒度为文件夹+文件
    private void doCleanUseLessDir() {
        /**
         * Logan生成日志文件的整理删除
         */
        File dir = new File(mPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    try {
                        if (f.isDirectory()) {
                            Matcher m = dirPattern.matcher(f.getName());
                            //2018-10-30格式时间转化为时间戳再进行判断
                            if (m.find() && LoganUtil.getDateLong(f.getName()) < LoganUtil.getCurrentTime() - mSaveTime) {
                                //遍历文件并进行单个删除
                                File[] dayLogan = f.listFiles();
                                for (File simple : dayLogan) {
                                    deleteDaySimpleLog(simple);
                                }
                                f.delete();//删除文件夹
                            }
                        } else {
                            Matcher m = filePattern.matcher(f.getName());
                            //直接以文件命名进行判断,文件名为当天年月日时间戳
                            if (m.find() && Long.valueOf(f.getName()) < LoganUtil.getCurrentTime() - mSaveTime) {
                                deleteDaySimpleLog(f);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //创建日志文件对应的文件夹(2018-12-25格式)
    private void createNewDayDir() {
        //创建对应文件夹,命名规则类似于2018-10-25
        File file = new File(mPath + File.separator + LoganUtil.getDateStr(LoganUtil.getCurrentTime()));
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    //针对：不满足最大切片大小而滞留在根目录下的文件
    private void moveRestLogFileDir() {
        //根目录下非文件夹文件
        File rootPath = new File(mPath);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
            return;
        }
        File[] fileList = rootPath.listFiles();
        if (fileList != null && fileList.length > 0) {
            for (File f : fileList) {
                //非文件夹文件且不是当天的文件才继续执行
                if (!f.isDirectory() && !f.getName().equals(String.valueOf(LoganUtil.getCurrentTime()))) {
                    //0B文件的处理直接删除
                    if (f.length() == 0L) {
                        f.delete();
                        return;
                    }
                    Matcher m = filePattern.matcher(f.getName());
                    if (m.find()) {
                        try {
                            //检测移动的目标文件夹
                            File desDir = new File(mPath + File.separator
                                    + LoganUtil.getDateStr(Long.parseLong(f.getName())));
                            if (!desDir.exists())
                                desDir.mkdirs();
                            //如果达到10片的最大容量则丢弃最早的一份日志并放入新的日志文件
                            if (desDir.listFiles().length >= mDepotFileNum) {
                                File oldestfile = getOldestFile(desDir);
                                if (oldestfile != null && oldestfile.exists()) {
                                    //删除准备移动的仓库最老的一份日志文件
                                    deleteDaySimpleLog(oldestfile);
                                }
                            }
                            String tFileName = LoganUtil.getDateStr(Long.parseLong(f.getName())) + "_" + getLastestIndex(desDir);//移动后的文件名
                            String desFile = desDir.getAbsolutePath() + File.separator + tFileName;
                            //完成切片文件进行移动,根据完成日期进行日志切片命名
                            //可能存在2018-10-10中存在文件名为2018-10-12的某个时间戳的日志文件的情况
                            f.renameTo(new File(desFile));
                        } catch (Exception e) {
                            Logz.e(e);
                        }
                    }
                }
            }
        }
    }

    //针对：连续写入情况下且满足切片最大情况的处理
    private void moveCuttedFileToDir(File loging) {
        mLoganProtocol.logan_flush();
        //0B文件的处理直接删除
        if (loging.length() == 0L) {
            loging.delete();
            return;
        }
        //如果达到10片的最大容量则丢弃最早的一份日志并放入新的日志文件
        File dateDir = new File(mPath + File.separator + LoganUtil.getDateStr(LoganUtil.getCurrentTime()));
        if (!dateDir.exists()) {
            dateDir.mkdirs();//如果直接删除了文件夹切片达到最大会自动生成日期为名字的文件夹
        }
        if (dateDir.listFiles().length >= mDepotFileNum) {
            File oldestfile = getOldestFile(dateDir);
            if (oldestfile != null && oldestfile.exists()) {
                //删除准备移动的仓库最老的一份日志文件
                deleteDaySimpleLog(oldestfile);
            }
        }
        String tFileName = LoganUtil.getDateStr(LoganUtil.getCurrentTime()) + "_" + getLastestIndex(dateDir);//移动后的文件名
        String destinationFile = mPath + File.separator + LoganUtil.getDateStr(LoganUtil.getCurrentTime())
                + File.separator + tFileName;
        //完成切片文件进行移动,根据完成日期进行日志切片命名
        loging.renameTo(new File(destinationFile));
    }

    //单个文件删除并清理数据库
    private void deleteDaySimpleLog(File simple) {
        String fn = simple.getName();
        String fp = simple.getAbsolutePath();
        simple.delete();//执行过期文件删除逻辑
        Logan.onSyncFileDeleteCall(fn, fp);
    }

    //根据下标判断最早的文件,切片仓库大于最大限制丢弃最旧的日志
    private File getOldestFile(File parentFile) {
        File[] childFile = parentFile.listFiles();
        int[] indexArray = new int[childFile.length];
        try {
            for (int i = 0; i < childFile.length; i++) {
                String[] splite = childFile[i].getName().split("_");
                indexArray[i] = Integer.parseInt(splite[splite.length - 1]);
            }
            //快速排序(从小到大)
            Arrays.sort(indexArray);
            return new File(parentFile.getAbsolutePath() + File.separator
                    + parentFile.getName() + "_" + String.valueOf(indexArray[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //下标递增
    private String getLastestIndex(File dir) {
        int index = 0;
        try {
            String[] childNames = dir.list();
            if (childNames.length == 0)
                return String.valueOf(1);
            for (int i = 0; i < childNames.length; i++) {
                String[] splite = childNames[i].split("_");
                int temp = Integer.parseInt(splite[splite.length - 1]);
                if (temp >= index)
                    index = temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(index + 1);
    }

    private void doSendLog2Net(SendLogAction action) {
        if (Logan.sDebug) {
            Log.d(TAG, "Logan send start");
        }
        if (TextUtils.isEmpty(mPath) || action == null || !action.isValid()) {
            return;
        }
        if (!isFileExist(action.uploadPath)) {
            if (Logan.sDebug) {
                Log.d(TAG, "Logan prepare log file failed, can't find log file");
            }
            return;
        }
        action.sendLogRunnable.setSendLogAction(action);
        action.sendLogRunnable.setCallBackListener(
                new SendLogRunnable.OnSendLogCallBackListener() {
                    @Override
                    public void onCallBack(int statusCode) {
                        synchronized (sendSync) {
                            mSendLogStatusCode = statusCode;
                            if (statusCode == SendLogRunnable.FINISH) {
                                mCacheLogQueue.addAll(mCacheSendQueue);
                                mCacheSendQueue.clear();
                                notifyRun();
                            }
                        }
                    }
                });
        mSendLogStatusCode = SendLogRunnable.SENDING;
        mSingleThreadExecutor.execute(action.sendLogRunnable);
    }

    /**
     * 判断sd卡是否可写，用于磁盘写入阀值限制
     *
     * @return
     */
    private boolean isCanWriteSDCard() {
        boolean item = false;
        try {
            StatFs stat = new StatFs(mPath);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long total = availableBlocks * blockSize;
            if (total > mMinSDCard) {
                item = true;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 文件是否存在工具类方法
     *
     * @param filePaht
     * @return
     */
    private boolean isFileExist(String filePaht) {
        boolean isExist = false;
        File file = new File(filePaht);
        if (file.exists() && file.isFile()) {
            isExist = true;
        }
        return isExist;
    }
}
