/*
 * Copyright 2018 Zhenjie Yan
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
package com.digo.platform.permission.notify.listener;

import android.os.Build;

import com.digo.platform.permission.RequestExecutor;
import com.digo.platform.permission.bridge.BridgeRequest;
import com.digo.platform.permission.bridge.RequestManager;
import com.digo.platform.permission.source.Source;

import androidx.annotation.RequiresApi;

class J2Request extends BaseRequest implements RequestExecutor, BridgeRequest.Callback {

    private Source mSource;

    J2Request(Source source) {
        super(source);
        this.mSource = source;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void start() {
        if (mSource.canListenerNotification()) {
            callbackSucceed();
        } else {
            showRationale(this);
        }
    }

    @Override
    public void execute() {
        BridgeRequest request = new BridgeRequest(mSource);
        request.setType(BridgeRequest.TYPE_NOTIFY_LISTENER);
        request.setCallback(this);
        RequestManager.get().add(request);
    }

    @Override
    public void cancel() {
        callbackFailed();
    }

    @Override
    public void onCallback() {
        if (mSource.canListenerNotification()) {
            callbackSucceed();
        } else {
            callbackFailed();
        }
    }
}