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
package com.digo.platform.permission.checker;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public final class StandardChecker implements PermissionChecker {

    private static final int MODE_ASK = 4;

    public StandardChecker() {
    }

    @Override
    public boolean hasPermission(Context context, String... permissions) {
        return hasPermission(context, Arrays.asList(permissions));
    }

    @Override
    public boolean hasPermission(Context context, List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        if (context == null) return false;

        AppOpsManager opsManager = null;
        for (String permission : permissions) {
            int result = context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            String op = AppOpsManager.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) {
                continue;
            }

            if (opsManager == null) opsManager = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
            result = opsManager.checkOpNoThrow(op, android.os.Process.myUid(), context.getPackageName());
            if (result != AppOpsManager.MODE_ALLOWED && result != MODE_ASK) {
                return false;
            }
        }
        return true;
    }
}