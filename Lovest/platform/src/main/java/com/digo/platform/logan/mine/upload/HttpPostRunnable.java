package com.digo.platform.logan.mine.upload;

import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.upload.bean.HttpPostFileModel;
import com.digo.platform.logan.mine.upload.bean.HttpPostSyncModel;
import com.digo.platform.logan.mine.upload.http.OnPostFileHttpRequest;
import com.digo.platform.logan.mine.upload.http.OnPostSyncHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import static com.digo.platform.logan.mine.common.LogzConstant.LOGAN_TAG;

/**
 * Author : Create by Linxinyuan on 2018/11/27
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class HttpPostRunnable {
    private static volatile HttpPostRunnable mInstance;
    private static int TIME_OUT = 30000;//设置连接与读写超时时间

    private HttpPostRunnable() {
        //Class Default Constructor
    }

    public static HttpPostRunnable getInstance() {
        if (mInstance == null) {
            synchronized (HttpPostRunnable.class) {
                if (mInstance == null) {
                    mInstance = new HttpPostRunnable();
                }
            }
        }
        return mInstance;
    }

    public byte[] doPostRequest(HttpPostSyncModel httpPostSyncModel, OnPostSyncHttpRequest callback) {
        HttpURLConnection c = null;
        InputStream inputStream = null;

        byte[] data = null;
        ByteArrayOutputStream back;
        byte[] Buffer = new byte[2048];

        try {
            java.net.URL u = new URL(httpPostSyncModel.getHttpUrl());
            c = (HttpURLConnection) u.openConnection();
            if (c instanceof HttpsURLConnection) {
                ((HttpsURLConnection) c).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            Set<Map.Entry<String, String>> entrySet = getActionGetHeader().entrySet();
            for (Map.Entry<String, String> tempEntry : entrySet) {
                c.addRequestProperty(tempEntry.getKey(), tempEntry.getValue());
            }

            c.setReadTimeout(TIME_OUT);
            c.setConnectTimeout(TIME_OUT);
            c.setRequestMethod("GET");
            int res = c.getResponseCode();
            Logz.tag(LOGAN_TAG).i("Sync根据时间戳请求http返回码为：%d", res);
            if (res == 200) {
                int i;
                back = new ByteArrayOutputStream();
                inputStream = c.getInputStream();
                while ((i = inputStream.read(Buffer)) != -1) {
                    back.write(Buffer, 0, i);
                }
                data = back.toByteArray();
                if (callback != null) {
                    callback.onPostSyncHttpSuccess(data);
                }
            } else {
                if (callback != null) {
                    callback.onPostSyncHttpFail(new Exception("Error Http resp code : " + String.valueOf(res)));
                }
            }
        } catch (ProtocolException e) {
            Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
            if (callback != null) {
                callback.onPostSyncHttpFail(e);
            }
        } catch (MalformedURLException e) {
            Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
            if (callback != null) {
                callback.onPostSyncHttpFail(e);
            }
        } catch (IOException e) {
            Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
            if (callback != null) {
                callback.onPostSyncHttpFail(e);
            }
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
        return data;
    }

    public byte[] doPostFileRequest(HttpPostFileModel httpPostFileModel, OnPostFileHttpRequest callBack) {
        byte[] data = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        FileInputStream inputData = null;
        HttpURLConnection c = null;
        ByteArrayOutputStream back;
        byte[] Buffer = new byte[2048];
        try {
            java.net.URL u = new URL(httpPostFileModel.getHttpUrl());
            c = (HttpURLConnection) u.openConnection();
            if (c instanceof HttpsURLConnection) {
                ((HttpsURLConnection) c).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
            Set<Map.Entry<String, String>> entrySet = getActionPostHeader().entrySet();
            for (Map.Entry<String, String> tempEntry : entrySet) {
                c.addRequestProperty(tempEntry.getKey(), tempEntry.getValue());
            }
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setReadTimeout(TIME_OUT);
            c.setConnectTimeout(TIME_OUT);
            c.setRequestMethod("POST");
            outputStream = c.getOutputStream();
            inputData = new FileInputStream(httpPostFileModel.getLogFile());
            int i;
            while ((i = inputData.read(Buffer)) != -1) {
                outputStream.write(Buffer, 0, i);
            }
            outputStream.flush();
            int res = c.getResponseCode();
            if (res == 200) {
                back = new ByteArrayOutputStream();
                inputStream = c.getInputStream();
                while ((i = inputStream.read(Buffer)) != -1) {
                    back.write(Buffer, 0, i);
                }
                data = back.toByteArray();
                handleSendLogBackData(httpPostFileModel, data, callBack);
            } else {
                dueWithUploadFail(callBack, httpPostFileModel, "Error http code : " + String.valueOf(res));
            }
        } catch (ProtocolException e) {
            dueWithUploadFail(callBack, httpPostFileModel, e.toString());
            Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
        } catch (MalformedURLException e) {
            dueWithUploadFail(callBack, httpPostFileModel, e.toString());
            Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
        } catch (IOException e) {
            dueWithUploadFail(callBack, httpPostFileModel, e.toString());
            Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
        } catch (JSONException e) {
            dueWithUploadFail(callBack, httpPostFileModel, e.toString());
            Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
                }
            }
            if (inputData != null) {
                try {
                    inputData.close();
                } catch (IOException e) {
                    Logz.tag(LogzConstant.LOGAN_TAG).e(e.toString());
                }
            }
            if (c != null) {
                c.disconnect();
            }
        }
        return data;
    }

    private HashMap<String, String> getActionPostHeader() throws NullPointerException {
        HashMap<String, String> map = new HashMap<>();
        map.put("client", "android");
        map.put("Content-Type", "application/octet-stream; charset=utf-8");
        return map;
    }

    private HashMap<String, String> getActionGetHeader() throws NullPointerException {
        HashMap<String, String> map = new HashMap<>();
        map.put("client", "android");
        map.put("Content-Type", "application/json; charset=utf-8");
        return map;
    }

    /**
     * 处理上传日志接口返回的数据
     */
    private void handleSendLogBackData(HttpPostFileModel httpPostFileModel, byte[] backData, OnPostFileHttpRequest callBack) throws JSONException {
        if (backData != null) {
            String data = new String(backData);
            if (!TextUtils.isEmpty(data)) {
                JSONObject jsonObj = new JSONObject(data);
                if (jsonObj.has("rcode")) {
                    Logz.tag(LOGAN_TAG).i("Http-Post方式请求上传服务器返回Rcode字段: %d", jsonObj.getInt("rcode"));
                }
                if (jsonObj.has("msg")) {
                    Logz.tag(LOGAN_TAG).i("Http-Post方式请求上传服务器返回Msg字段: %s", jsonObj.getString("msg"));
                }
                if (jsonObj.getInt("rcode") == 0) {
                    dueWithUploadSuccess(callBack, httpPostFileModel);
                } else {
                    dueWithUploadFail(callBack, httpPostFileModel, "Error back params rcode : " + jsonObj.getInt("rcode"));
                }
            }
        } else {
            dueWithUploadFail(callBack, httpPostFileModel, "Error null http backData");
        }
    }

    /**
     * 上传成功回调
     *
     * @param onPostFileHttpRequest
     * @param httpPostFileModel
     */
    private void dueWithUploadSuccess(OnPostFileHttpRequest onPostFileHttpRequest, HttpPostFileModel httpPostFileModel) {
        if (onPostFileHttpRequest != null)
            onPostFileHttpRequest.onPostSuccess(httpPostFileModel);
    }

    /**
     * 上传失败回调
     *
     * @param onPostFileHttpRequest
     * @param httpPostFileModel
     */
    private void dueWithUploadFail(OnPostFileHttpRequest onPostFileHttpRequest, HttpPostFileModel httpPostFileModel, String exception) {
        if (onPostFileHttpRequest != null)
            onPostFileHttpRequest.onPostFail(httpPostFileModel, exception);
    }
}
