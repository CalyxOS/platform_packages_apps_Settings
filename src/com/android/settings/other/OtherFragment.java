/*
 * Copyright (C) 2022 The Calyx Institute
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

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.Intent;
import android.os.UserManager;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.development.OnActivityResultListener;
import com.android.settings.development.OemUnlockDialogHost;
import com.android.settingslib.core.AbstractPreferenceController;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that shows several toggle controls moved outside from
 * development settings
 */
public class OtherFragment extends RestrictedDashboardFragment
        implements OemUnlockDialogHost {

    private static final String TAG = "OtherFrag";
    private List<AbstractPreferenceController> mPreferenceControllers = new ArrayList<>();

    public OtherFragment() {
        super(UserManager.DISALLOW_DEBUGGING_FEATURES);
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DEVELOPMENT;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.other_settings;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (Utils.isMonkeyRunning()) {
            mPreferenceControllers = new ArrayList<>();
            return null;
        }
        mPreferenceControllers.add(
                new ProtectedBugReportPreferenceController(context, getActivity(), this));
        mPreferenceControllers.add(new OemUnlockPreferenceController(context, getActivity(), this));
        return mPreferenceControllers;
    }

    @Override
    public void onOemUnlockDialogConfirmed() {
        final OemUnlockPreferenceController controller = use(
                OemUnlockPreferenceController.class);
        controller.onOemUnlockConfirmed();
    }

    @Override
    public void onOemUnlockDialogDismissed() {
        final OemUnlockPreferenceController controller = use(
                OemUnlockPreferenceController.class);
        controller.onOemUnlockDismissed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean handledResult = false;
        for (AbstractPreferenceController controller : mPreferenceControllers) {
            if (controller instanceof OnActivityResultListener) {
                // We do not break early because it is possible for multiple controllers to
                // handle the same result code.
                handledResult |=
                        ((OnActivityResultListener) controller).onActivityResult(
                                requestCode, resultCode, data);
            }
        }
        if (!handledResult) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
