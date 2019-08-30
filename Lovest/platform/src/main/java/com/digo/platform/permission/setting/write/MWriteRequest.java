/*
 * Copyright 2019 Zhenjie Yan
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
package com.digo.platform.permission.setting.write;

import com.digo.platform.permission.RequestExecutor;
import com.digo.platform.permission.bridge.BridgeRequest;
import com.digo.platform.permission.bridge.RequestManager;
import com.digo.platform.permission.source.Source;

public class MWriteRequest extends BaseRequest implements RequestExecutor, BridgeRequest.Callback {

    private Source mSource;

    public MWriteRequest(Source source) {
        super(source);
        this.mSource = source;
    }

    @Override
    public void start() {
        if (mSource.canWriteSetting()) {
            callbackSucceed();
        } else {
            showRationale(this);
        }
    }

    @Override
    public void execute() {
        BridgeRequest request = new BridgeRequest(mSource);
        request.setType(BridgeRequest.TYPE_WRITE_SETTING);
        request.setCallback(this);
        RequestManager.get().add(request);
    }

    @Override
    public void cancel() {
        callbackFailed();
    }

    @Override
    public void onCallback() {
        if (mSource.canWriteSetting()) {
            callbackSucceed();
        } else {
            callbackFailed();
        }
    }
}