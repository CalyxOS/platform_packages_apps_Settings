/*
 * Copyright (C) 2016 The Android Open Source Project
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.development.OnActivityResultListener;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.development.DevelopmentSettingsEnabler;

public class ProtectedBugReportPreferenceController extends OtherOptionsPreferenceController
        implements PreferenceControllerMixin, OnActivityResultListener {

    private static final String KEY_BUGREPORT = "bugreport";
    // Arbitrary request code for ChooseLockSettingsHelper when requesting authorization
    private static final int KEYGUARD_REQUEST = 1924;

    private final Activity mActivity;
    private final RestrictedDashboardFragment mFragment;
    private ProtectedBugreportPreference mPreference;

    public ProtectedBugReportPreferenceController(Context context, Activity activity,
            RestrictedDashboardFragment fragment) {
        super(context);

        mActivity = activity;
        mFragment = fragment;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_BUGREPORT;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        mPreference = (ProtectedBugreportPreference) screen.findPreference(getPreferenceKey());
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference.equals(mPreference)) {
            if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(mContext)
                    || !showKeyguardConfirmation(mContext.getResources())) {
                // Development Options is enabled, or the user has no credential established.
                mPreference.showDialog();
            }
            // The user has a credential established. Eat the click while waiting for auth result.
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KEYGUARD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // Show the bug report dialog.
                mPreference.showDialog();
            }
            return true;
        }
        return false;
    }

    /** Obtain keyguard confirmation from the user. Returns false if the user has no credential. */
    private boolean showKeyguardConfirmation(Resources resources) {
        final ChooseLockSettingsHelper.Builder builder =
                new ChooseLockSettingsHelper.Builder(mActivity, mFragment);
        return builder.setRequestCode(KEYGUARD_REQUEST)
                .setTitle(resources.getString(com.android.internal.R.string.bugreport_title))
                .show();
    }
}
