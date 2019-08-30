//package com.digo.platform.logz.mine.upload.task;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.digo.platform.logz.mine.Logz;
//import com.digo.platform.logz.mine.common.LogzConstant;
//import com.digo.platform.logz.mine.database.daos.LoganUFileDao;
//import com.digo.platform.logz.mine.upload.RealSendRunnable;
//import com.digo.platform.logz.mine.upload.task.base.interfaces.IUTask;
//import com.digo.platform.logz.mine.utils.FileDisposeUtils;
//import com.digo.platform.logz.meituan.Logan;
//import com.digo.platform.logz.meituan.action.SendLogAction;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * 第三方Sdk-Log文件上传任务
// * zego:sdcard + /Android/data/com.yibasan.lizhifm/files/zegoavlog.*.txt
// * agora:sdcard + /183/agora.log
// * <p>
// * tag  SDK类型
// * path 文件路径(根路径)
// * rule 正则匹配规则
// */
//public class ThirdSdkUTask implements IUTask {
//    private String tag;//SDK类型
//    private String path;//SDK根路径
//    private String rule;//SDK正则匹配
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public void setRule(String rule) {
//        this.rule = rule;
//    }
//
//    public ThirdSdkUTask() {
//        //Class Default Constructor
//    }
//
//    public static final class Builder {
//        private String tag;//SDK类型
//        private String path;//SDK根路径
//        private String rule;//SDK正则匹配
//
//        public Builder setTag(String tag) {
//            this.tag = tag;
//            return this;
//        }
//
//        public Builder setPath(String path) {
//            this.path = path;
//            return this;
//        }
//
//        public Builder setRule(String rule) {
//            this.rule = rule;
//            return this;
//        }
//
//        public ThirdSdkUTask build() {
//            ThirdSdkUTask task = new ThirdSdkUTask();
//            task.setTag(tag);
//            task.setPath(path);
//            task.setRule(rule);
//            return task;
//        }
//    }
//
//    @Override
//    @SuppressLint("CheckResult")
//    public void runTask(final Context context, final RealSendRunnable runnable) {
//        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(path) || TextUtils.isEmpty(rule)) {
//            Logz.tag(LogzConstant.LOGAN_TAG).e("Some Improtant params can not be null");
//            return;
//        }
//        //外部相对SdCard的相对路径转化为文件绝对路径
//        path = LogzConstant.SD_ROOT_PATH + File.separator + path;
//        if (!FileDisposeUtils.isFileExist(path)) {
//            Logz.tag(LogzConstant.LOGAN_TAG).e("ThirdPartySdkUpload >> tag=%s, path=%s, rule=%s" +
//                    " >> file with target path is not exist", tag, path, rule);
//            return;
//        } else {
//            Logz.tag(LogzConstant.LOGAN_TAG).i("ThirdPartySdkUpload >> tag=%s, path=%s, rule=%s", tag, path, rule);
//        }
//        //是否可上传sdk日志总开关,预埋了AppConfig字段接口
//        if (!Logz.getLogConfiger().getAppConfigSdkUpload()) {
//            Logz.tag(LogzConstant.LOGAN_TAG).e("ThirdPartySdkUpload >> AppConfig not allow upload sdk zip");
//            return;
//        }
//
//        final Pattern pattern = Pattern.compile(rule);//初始化正则匹配规则
//        List<String> arrPath = new ArrayList<String>();//上传路径检索归并集合
//
//        if (FileDisposeUtils.isDirectory(path)) {
//            //如果是文件夹，遍历所有子文件并匹配正则
//            File file = new File(path);
//            File[] child = file.listFiles();
//            for (File depotMember : child) {
//                //只进行单文件匹配, 不进行文件夹的递归操作
//                if (FileDisposeUtils.isDirectory(depotMember.getAbsolutePath()))
//                    continue;
//
//                Matcher m = pattern.matcher(depotMember.getName());
//                if (m.find()) {
//                    //匹配成功添加到上传列表
//                    arrPath.add(depotMember.getAbsolutePath());
//                    Logz.tag(LogzConstant.LOGAN_TAG).i("File match and add to path >> name=%s, path=%s",
//                            depotMember.getName(), depotMember.getAbsolutePath());
//                }
//            }
//        } else {
//            File depotMember = new File(path);
//            //如果是单个文件，匹配正则成功则加入
//            Matcher m = pattern.matcher(depotMember.getName());
//            if (m.find()) {
//                //匹配成功添加到上传列表
//                arrPath.add(depotMember.getAbsolutePath());
//                Logz.tag(LogzConstant.LOGAN_TAG).i("File match and add to path >> name=%s, path=%s",
//                        depotMember.getName(), depotMember.getAbsolutePath());
//            }
//        }
//
//        if (arrPath.size() == 0) {
//            Logz.tag(LogzConstant.LOGAN_TAG).i("在路径: %s 下没有找到任何匹配的文件添加到压缩任务，任务结束!", path);
//            return;
//        }
//
//        final List<String> resultArrPath = arrPath;
//        final String zipFileName = tag + "." + String.valueOf(System.currentTimeMillis()) + ".zip";
//        final String zipFilePath = Logz.getLogConfiger().getSdkZipRPath() + File.separator + zipFileName;
//        Logz.tag(LogzConstant.LOGAN_TAG).i("ThirdPartySdkUpload will build zip in path : %s", zipFilePath);
//        Observable.create(new ObservableOnSubscribe<Boolean>() {
//            @Override
//            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
//                e.onNext(FileDisposeUtils.zipSDKLogFile(zipFilePath, resultArrPath));
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
//            @Override
//            public void accept(Boolean aBoolean) throws Exception {
//                if (aBoolean && FileDisposeUtils.isFileExist(zipFilePath)) {
//                    if (FileDisposeUtils.reachZipConfigMax(zipFilePath, Logz.getLogConfiger().getSdkZipMax())) {
//                        Logz.tag(LogzConstant.LOGAN_TAG).i("生成Zip文件：%s, 已超出最大上传zip配置大小(Default:20M)，放弃此次任务并删除zip包!", zipFileName);
//                        FileDisposeUtils.deleteFileByPath(zipFilePath);//进行文件删除并且不入库
//                    } else {
//                        LoganUFileDao.getInstance(context).insertNew(new String[]{zipFilePath});//文件记录数据库
//                        Logan.s(SendLogAction.TYPE_SDK_LOG, tag, new String[]{zipFilePath}, runnable);//Sdk-Zip包上传
//                    }
//                }
//            }
//        });
//    }
//}
