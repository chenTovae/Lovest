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
package com.digo.platform.permission.notify;

import com.digo.platform.permission.Action;
import com.digo.platform.permission.Rationale;

public interface PermissionRequest {

    /**
     * Set request rationale.
     */
    PermissionRequest rationale(Rationale<Void> rationale);

    /**
     * Action to be taken when all permissions are granted.
     */
    PermissionRequest onGranted(Action<Void> granted);

    /**
     * Action to be taken when all permissions are denied.
     */
    PermissionRequest onDenied(Action<Void> denied);

    /**
     * Start install.
     */
    void start();

}