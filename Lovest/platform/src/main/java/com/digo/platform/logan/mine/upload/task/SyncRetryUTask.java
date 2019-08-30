//package com.digo.platform.logz.mine.upload.task;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//
//import com.digo.platform.logz.mine.Logz;
//import com.digo.platform.logz.mine.upload.HttpPostRunnable;
//import com.digo.platform.logz.mine.upload.HttpPostUrlBuilder;
//import com.digo.platform.logz.mine.upload.RealSendRunnable;
//import com.digo.platform.logz.mine.upload.bean.HttpPostSyncModel;
//import com.digo.platform.logz.mine.upload.http.OnPostSyncHttpRequest;
//import com.digo.platform.logz.mine.upload.task.base.interfaces.IUTask;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import io.reactivex.Observable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//
//import static com.digo.platform.logz.mine.common.LogzConstant.LOGAN_TAG;
//import static com.digo.platform.logz.mine.common.LogzConstant.MODE_4G;
//
///**
// * Author : Create by Linxinyuan on 2019/01/18
// * Email : linxinyuan@lizhi.fm
// * Desc : android dev
// */
//public class SyncRetryUTask implements IUTask {
//    private long recentStamp;
//    private long minStart = 0;//最小的一个时间起点
//    private long maxEnd = 0;//最大的一个时间结点
//    private long serverStamp = 0;//服务器下发的最近时间戳
//
//    public SyncRetryUTask() {
//        //Class Default Constructor
//    }
//
//    public void setRecentStamp(long recentStamp) {
//        this.recentStamp = recentStamp;
//    }
//
//    public static final class Builder {
//        private long recentStamp;
//
//        public Builder setRecentStamp(long recentStamp) {
//            this.recentStamp = recentStamp;
//            return this;
//        }
//
//        public SyncRetryUTask build() {
//            SyncRetryUTask task = new SyncRetryUTask();
//            task.setRecentStamp(recentStamp);
//            return task;
//        }
//    }
//
//    @Override
//    @SuppressLint("CheckResult")
//    public void runTask(final Context context, final RealSendRunnable runnable) {
//        final HttpPostSyncModel httpPostSyncModel = new HttpPostSyncModel.Builder()
//                .setHttpUrl(HttpPostUrlBuilder.buildOffLineUploadUrl(recentStamp))
//                .build();
//        Logz.tag(LOGAN_TAG).i("查询Sync回捞历史推送记录url：%s", httpPostSyncModel.getHttpUrl());
//        Observable.just(true)
//                .observeOn(Schedulers.io())
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        runHttpGetRequest(httpPostSyncModel);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                });
//    }
//
//    private void runHttpGetRequest(HttpPostSyncModel httpPostSyncModel) {
//        HttpPostRunnable.getInstance().doPostRequest(httpPostSyncModel, new OnPostSyncHttpRequest() {
//            @Override
//            @SuppressLint("CheckResult")
//            public void onPostSyncHttpSuccess(byte[] backData) {
//                if (backData != null) {
//                    Logz.tag(LOGAN_TAG).i("查询Sync回捞历史推送记录json : %s",
//                            new String(backData));
//                    try {
//                        //最外层JSON解析rcode与cmd
//                        JSONObject backDataJson = new JSONObject(new String(backData));
//                        if (!backDataJson.has("rcode") || !backDataJson.has("cmd")) {
//                            return;
//                        }
//                        if (backDataJson.optInt("rcode") != 0) {
//                            return;
//                        }
//                        //json-cmd字段拆分并根据类型操作
//                        JSONArray jsonArray = new JSONArray(backDataJson.getString("cmd"));
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject baseCmdJson = jsonArray.getJSONObject(i);
//                            Logz.tag(LOGAN_TAG).i("单个记录json : %s", baseCmdJson);
//                            //拆分单个cmd类型区分
//                            if (baseCmdJson.has("type") && baseCmdJson.has("param")) {
//                                if (baseCmdJson.optInt("type") == 1) {
//                                    doLogFileUpload(baseCmdJson.optString("param"));
//                                } else {
//                                    JSONObject jo = new JSONObject(baseCmdJson.optString("param"));
//                                    if (jo.has("config")) {
//                                        doZipLogUpload(jo.optString("config"));
//                                    }
//                                }
//                            }
//                            //保存最新的一个时间戳到文件
//                            if (baseCmdJson.has("timestamp") && baseCmdJson.optLong("timestamp") > serverStamp) {
//                                serverStamp = baseCmdJson.optLong("timestamp");
//                            }
//                        }
//                        if (minStart != 0 && maxEnd != 0) {
//                            Logz.tag(LOGAN_TAG).i("Sync批量指令上传时间起点：%d, 上传时间结点：%d", minStart, maxEnd);
//                            Logz.send(minStart, maxEnd, MODE_4G, false);
//                        }
//                        if (serverStamp != 0) {
//                            Logz.saveSyncRecentTimeStamp(serverStamp);
//                        }
//                    } catch (Exception e) {
//                        Logz.e(e);
//                    }
//                }
//            }
//
//            /**
//             * 上传阶段日志文件
//             * @param jsonString
//             * @throws JSONException
//             */
//            private void doLogFileUpload(String jsonString) throws JSONException {
//                JSONObject jsonObject = new JSONObject(jsonString);
//                if (jsonObject.has("start") && jsonObject.has("end")
//                        && jsonObject.has("net") && jsonObject.has("force")) {
//                    Logz.tag(LOGAN_TAG).i("json解析完成，开始时间：%d, 结束时间：%d, 模式限制：%d, 强制上传：%b",
//                            jsonObject.optLong("start"), jsonObject.optLong("end"), jsonObject.optInt("net"), jsonObject.optBoolean("force"));
//                    if (minStart == 0)
//                        minStart = jsonObject.optLong("start");
//                    if (maxEnd == 0) {
//                        maxEnd = jsonObject.optLong("end");
//                    }
//                    if (jsonObject.optLong("start") < minStart) {
//                        minStart = jsonObject.optLong("start");
//                    }
//                    if (jsonObject.optLong("end") > maxEnd) {
//                        maxEnd = jsonObject.optLong("end");
//                    }
//                }
//            }
//
//            /**
//             * 上传第三方日志文件
//             * @param jsonString
//             * @throws JSONException
//             */
//            private void doZipLogUpload(String jsonString) throws JSONException {
//                JSONObject jsonObject = new JSONObject(jsonString);
//                if (jsonObject.has("tag") && jsonObject.has("Android")) {
//                    String tag = jsonObject.optString("tag");
//                    JSONObject androidConfig = jsonObject.getJSONObject("Android");
//                    if (androidConfig.has("path") && androidConfig.has("rule")) {
//                        String path = androidConfig.optString("path");
//                        String rule = androidConfig.optString("rule");
//                        Logz.tag(LOGAN_TAG).i("json解析完成开始上传zip，tag：%s, 路径：%s, 正则：%s", tag, path, rule);
//                        Logz.send(tag, path, rule);//Sync指令下发上传Sdk日志
//                    }
//                }
//            }
//
//            @Override
//            public void onPostSyncHttpFail(Exception e) {
//                if (e != null) {
//                    Logz.e(e);
//                }
//            }
//        });
//    }
//}
