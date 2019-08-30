package com.digo.platform.permission.bridge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.digo.platform.R;
import com.digo.platform.permission.LzPermission;
import com.digo.platform.permission.bridge.interfs.IRequestExecutorCallback;
import com.digo.platform.permission.bridge.interfs.IRequestSettingCallback;

import io.reactivex.annotations.Nullable;

import static com.digo.platform.permission.bridge.RequestType.OPERATION_RATIONALE;
import static com.digo.platform.permission.bridge.RequestType.OPERATION_SETTING;

/**
 * Author : Create by Linxinyuan on 2019/03/12
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class DialogActivity extends Activity {
    private static final int REQUEST_CODE_SETTING = 10000;

    public static final String EXTRA_OPERATION = "extraOperation";
    public static final String EXTRA_PERMISSION = "extraPermission";

    private int operator;
    private String[] permissions;

    private static IRequestSettingCallback requestSettingCallback;
    private static IRequestExecutorCallback requestExecutorCallback;

    public static void setRequestExecutorCallback(IRequestExecutorCallback callback) {
        requestExecutorCallback = callback;
    }

    public static void setRequestSettingCallback(IRequestSettingCallback callback) {
        requestSettingCallback = callback;
    }

    /**
     * Request for permissions.
     */
    public static void requestRationale(Context context, int operation, String[] pers, IRequestExecutorCallback callback) {
        setRequestExecutorCallback(callback);//设置弹窗按钮选择回调
        Intent intent = new Intent(context, DialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_OPERATION, operation);
        intent.putExtra(EXTRA_PERMISSION, pers);
        context.startActivity(intent);
    }

    /**
     * Request for permissions.
     */
    public static void requestSetting(Context context, int operation, String[] pers, IRequestSettingCallback callback) {
        setRequestSettingCallback(callback);//设置弹窗按钮选择回调
        Intent intent = new Intent(context, DialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_OPERATION, operation);
        intent.putExtra(EXTRA_PERMISSION, pers);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        operator = intent.getIntExtra(EXTRA_OPERATION, -1);
        permissions = intent.getStringArrayExtra(EXTRA_PERMISSION);
        switch (operator) {
            case OPERATION_RATIONALE:
                showRationaleDialog();
                break;
            case OPERATION_SETTING:
                showSettingDialog();
                break;
            default:
                break;
        }
    }

    public void showRationaleDialog() {
        String title = getResources().getString(R.string.title_dialog);
        String msg = getResources().getString(R.string.message_permission_rationale);
        String btnText = getResources().getString(R.string.resume);

        getDialog(title, msg, btnText, new Runnable() {
            @Override
            public void run() {
                if (requestExecutorCallback != null) {
                    requestExecutorCallback.onExecute();
                    requestExecutorCallback = null;
                }
                finish();
            }
        }).show();

    }

    public void showSettingDialog() {
        String title = getResources().getString(R.string.title_dialog);
        String msg = getResources().getString(R.string.message_permission_always_failed);
        String btnText = getResources().getString(R.string.setting);

        getDialog(title, msg, btnText, new Runnable() {
            @Override
            public void run() {
                LzPermission.with(DialogActivity.this).runtime().setting().start(REQUEST_CODE_SETTING);
            }
        }).show();

    }

    private Dialog getDialog(String title, String msg, String btnText, final Runnable runnable) {
        final Dialog dialog = new Dialog(this, R.style.Dialog_Permission);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.platform_permission_dialog);
        if (title != null && title.length() > 0) {
            ((TextView) dialog.findViewById(R.id.dialog_title)).setText(title);
        }
        if (msg == null || msg.length() == 0) {
            dialog.findViewById(R.id.dialog_message).setVisibility(View.GONE);
        } else {
            ((TextView) dialog.findViewById(R.id.dialog_message)).setText(msg);
        }
        if (btnText != null && btnText.length() > 0) {
            ((TextView) dialog.findViewById(R.id.dialog_ok)).setText(btnText);
        }
        dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (runnable != null)
                    runnable.run();
            }
        });

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        lp.width = (int) dialog.getContext().getResources().getDimension(R.dimen.dialog_permission_layout_width);
        dialogWindow.setAttributes(lp);

        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                //检查权限是否全部申请完毕，如果没有执行递归询问
                if (permissions != null && permissions.length > 0) {
                    if (LzPermission.hasPermissions(DialogActivity.this, permissions)) {
                        if (requestSettingCallback != null) {
                            requestSettingCallback.onAgree();
                            requestSettingCallback = null;
                        }
                        finish();
                    } else {
                        if (requestSettingCallback != null) {
                            requestSettingCallback.onForce();
                            requestSettingCallback = null;
                        }
                        finish();
                    }
                }
                break;
            }
        }
    }
}
