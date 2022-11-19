/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright (C) 2023 The Calyx Institute
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

package com.android.settings.other;

import android.app.Activity;
import android.content.Context;

import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.development.DevelopmentSettingsEnabler;

public class ProtectedBugReportPreferenceController extends ProtectedPreferenceController {

    private static final String KEY_BUGREPORT = "bugreport";

    public ProtectedBugReportPreferenceController(Context context, Activity activity,
            RestrictedDashboardFragment fragment) {
        super(context, activity, fragment, true /* handleClicks */);
    }

    @Override
    public String getPreferenceKey() {
        return KEY_BUGREPORT;
    }

    @Override
    protected boolean isAuthorizationNeeded(int action, Object newValue) {
        // Authorization is needed if Developer Options is not enabled.
        return !DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(mContext);
    }

    @Override
    protected void onAuthorizedAction(int action, Object newValue) {
        ((ProtectedBugreportPreference) mPreference).showDialog();
    }
}
