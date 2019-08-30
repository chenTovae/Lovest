package com.digo.push.domestic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digo.push.R;
import com.digo.push.domestic.event.PushConnectionStatusEvent;
import com.digo.push.domestic.event.PushMessageNotificationClickedEvent;
import com.digo.push.domestic.event.PushMessageNotificationDeletedEvent;
import com.digo.push.domestic.event.PushMessageReceiveEvent;
import com.digo.push.domestic.event.PushTokenEvent;
import com.digo.push.domestic.interfaces.PushUpdateTokenToServerInterface;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.model.UpdatePushToken;
import com.digo.push.domestic.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.app.AppCompatActivity;

public class PushSdkTestActivity extends AppCompatActivity implements View.OnClickListener, PushUpdateTokenToServerInterface {
    private TextView mTvLog;
    public final String TAG = "PushSdkTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_sdk_test);
        EventBus.getDefault().register(this);

        initViewComponent();//初始化组件调试界面控件
    }

    private void initViewComponent() {
        mTvLog = (TextView) findViewById(R.id.tv_log);
        mTvLog.setTextIsSelectable(true);
        findViewById(R.id.btn_register_meizu).setOnClickListener(this);
        findViewById(R.id.btn_register_huwei).setOnClickListener(this);
        findViewById(R.id.btn_register_xiaomi).setOnClickListener(this);
        findViewById(R.id.btn_register_xinge).setOnClickListener(this);
        findViewById(R.id.btn_register_oppo).setOnClickListener(this);
        findViewById(R.id.btn_register_vivo).setOnClickListener(this);
        findViewById(R.id.btn_register_getui).setOnClickListener(this);
        findViewById(R.id.btn_register_zidong).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        SystemUtil.toLogString();
        if (view.getId() == R.id.btn_register_meizu) {
            PushSdkManager.getInstance().setEnablePush(true).initAssignPush(this, PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU, new int[]{});
        } else if (view.getId() == R.id.btn_register_huwei) {
            PushSdkManager.getInstance().initAssignPush(this, PushSdkManagerConfig.PushType.PUSH_TYPE_HUAWEI, new int[]{});
        } else if (view.getId() == R.id.btn_register_xiaomi) {
            PushSdkManager.getInstance().initAssignPush(this, PushSdkManagerConfig.PushType.PUSH_TYPE_XIAOMI, new int[]{});
        } else if (view.getId() == R.id.btn_register_xinge) {
            PushSdkManager.getInstance().initAssignPush(this, PushSdkManagerConfig.PushType.PUSH_TYPE_XINGE, new int[]{});
        } else if (view.getId() == R.id.btn_register_oppo) {
            PushSdkManager.getInstance().initAssignPush(this, PushSdkManagerConfig.PushType.PUSH_TYPE_OPPO, new int[]{});
        } else if (view.getId() == R.id.btn_register_vivo) {
            PushSdkManager.getInstance().initAssignPush(this, PushSdkManagerConfig.PushType.PUSH_TYPE_VIVO, new int[]{});
        } else if (view.getId() == R.id.btn_register_getui) {
            PushSdkManager.getInstance().initAssignPush(this, PushSdkManagerConfig.PushType.PUSH_TYPE_GETUI);
        } else if (view.getId() == R.id.btn_register_zidong) {
            Toast.makeText(PushSdkTestActivity.this, "不支持自动通道配置接口~", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadPushTokenEvent(PushTokenEvent event) {
        Ln.d(TAG + "UploadPushTokenEvent" + event.mToken);
        mTvLog.setText(mTvLog.getText() + "\n ------获取Token：" + event.mToken);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPushStatusEvent(PushConnectionStatusEvent event) {
        Ln.d(TAG + "UploadPushTokenEvent" + event.mStatus + "状态码：" + event.mResultCode);
        mTvLog.setText(mTvLog.getText() + "\n ------连接状态：" + event.mStatus + "    状态码：" + event.mResultCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPushMessageReceiveEvent(PushMessageReceiveEvent event) {
        Ln.d(TAG + "PushMessageReceiveEvent" + event.mMessage);
        mTvLog.setText(mTvLog.getText() + "\n ------收到通知：" + event.mMessage);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPushMessageNotificationClickedEvent(PushMessageNotificationClickedEvent event) {
        Ln.d(TAG + "PushMessageReceiveEvent" + event.mMessage);
        mTvLog.setText(mTvLog.getText() + "\n ------通知被点击：" + event.mMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPushMessageNotificationDeletedEvent(PushMessageNotificationDeletedEvent event) {
        Ln.d(TAG + "PushMessageReceiveEvent" + event.mMessage);
        mTvLog.setText(mTvLog.getText() + "\n ------通知被删除：" + event.mMessage);
    }

    @Override
    public void updateTokenToServerCallBack(UpdatePushToken updatePushToken) {
        Ln.d(TAG + updatePushToken.toString());
    }
}
