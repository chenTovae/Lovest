/*
 * Copyright © Zhenjie Yan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digo.platform.permission.bridge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import com.digo.platform.permission.overlay.setting.LSettingPage;
import com.digo.platform.permission.overlay.setting.MSettingPage;
import com.digo.platform.permission.source.ActivitySource;
import com.digo.platform.permission.source.Source;

import io.reactivex.annotations.NonNull;

public final class BridgeActivity extends Activity {

    private static final String KEY_TYPE = "KEY_TYPE";
    private static final String KEY_PERMISSIONS = "KEY_PERMISSIONS";

    /**
     * Request for permissions.
     */
    static void requestAppDetails(Source source) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_APP_DETAILS);
        source.startActivity(intent);
    }

    /**
     * Request for permissions.
     */
    static void requestPermission(Source source, String[] permissions) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_PERMISSION);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        source.startActivity(intent);
    }

    /**
     * Request for package install.
     */
    static void requestInstall(Source source) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_INSTALL);
        source.startActivity(intent);
    }

    /**
     * Request for overlay.
     */
    static void requestOverlay(Source source) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_OVERLAY);
        source.startActivity(intent);
    }

    /**
     * Request for alert window.
     */
    static void requestAlertWindow(Source source) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_ALERT_WINDOW);
        source.startActivity(intent);
    }

    /**
     * Request for notify.
     */
    static void requestNotify(Source source) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_NOTIFY);
        source.startActivity(intent);
    }

    /**
     * Request for notification listener.
     */
    static void requestNotificationListener(Source source) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_NOTIFY_LISTENER);
        source.startActivity(intent);
    }

    /**
     * Request for write system setting.
     */
    static void requestWriteSetting(Source source) {
        Intent intent = new Intent(source.getContext(), BridgeActivity.class);
        intent.putExtra(KEY_TYPE, BridgeRequest.TYPE_WRITE_SETTING);
        source.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) return;

        Intent intent = getIntent();
        int operation = intent.getIntExtra(KEY_TYPE, -1);
        switch (operation) {
            case BridgeRequest.TYPE_APP_DETAILS: {
                Intent appDetailsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                appDetailsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(appDetailsIntent, BridgeRequest.TYPE_APP_DETAILS);
                break;
            }
            case BridgeRequest.TYPE_PERMISSION: {
                String[] permissions = intent.getStringArrayExtra(KEY_PERMISSIONS);
                requestPermissions(permissions, BridgeRequest.TYPE_PERMISSION);
                break;
            }
            case BridgeRequest.TYPE_INSTALL: {
                Intent manageIntent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                manageIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(manageIntent, BridgeRequest.TYPE_INSTALL);
                break;
            }
            case BridgeRequest.TYPE_OVERLAY: {
                MSettingPage settingPage = new MSettingPage(new ActivitySource(this));
                settingPage.start(BridgeRequest.TYPE_OVERLAY);
                break;
            }
            case BridgeRequest.TYPE_ALERT_WINDOW: {
                LSettingPage settingPage = new LSettingPage(new ActivitySource(this));
                settingPage.start(BridgeRequest.TYPE_ALERT_WINDOW);
                break;
            }
            case BridgeRequest.TYPE_NOTIFY: {
                Intent settingIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                settingIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivityForResult(settingIntent, BridgeRequest.TYPE_NOTIFY);
                break;
            }
            case BridgeRequest.TYPE_NOTIFY_LISTENER: {
                Intent settingIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(settingIntent, BridgeRequest.TYPE_NOTIFY_LISTENER);
                break;
            }
            case BridgeRequest.TYPE_WRITE_SETTING: {
                Intent settingIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                settingIntent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(settingIntent, BridgeRequest.TYPE_WRITE_SETTING);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        Messenger.send(this);
        finish();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Messenger.send(this);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}