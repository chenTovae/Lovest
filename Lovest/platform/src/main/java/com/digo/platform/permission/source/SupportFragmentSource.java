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
package com.digo.platform.permission.source;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.fragment.app.Fragment;

public class SupportFragmentSource extends Source {

    private Fragment mFragment;

    public SupportFragmentSource(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public Context getContext() {
        return mFragment.getActivity();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void startActivity(Intent intent) {
        mFragment.startActivity(intent);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void startActivityForResult(Intent intent, int requestCode) {
        mFragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean isShowRationalePermission(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        return mFragment.shouldShowRequestPermissionRationale(permission);
    }
}