/*
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
import android.os.SystemProperties;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.development.DevelopmentSettingsEnabler;

public class KeychordsPreferenceController extends ProtectedPreferenceController {

    private static final String PREFERENCE_KEY = "keychords";
    private static final String KEYCHORDS_PROPERTY = "persist.init.keychords";

    // Matches system/core/init/init.cpp handleKeychords()
    private static final String ADB_ENABLED_PROPERTY = "init.svc.adbd";
    private static final String ADB_ENABLED_VALUE = "running";

    public KeychordsPreferenceController(Context context, Activity activity,
            RestrictedDashboardFragment fragment) {
        super(context, activity, fragment, false /* handleClicks */);
    }

    @Override
    public String getPreferenceKey() {
        return PREFERENCE_KEY;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        updateStateInternal();
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);

        if (preference == mPreference) {
            updateStateInternal();
        }
    }

    @Override
    protected boolean isAuthorizationNeeded(int action, Object newValue) {
        // Authorization is only needed to toggle on.
        return newValue == Boolean.TRUE;
    }

    @Override
    protected void onAuthorizedAction(int action, Object newValue) {
        SystemProperties.set(KEYCHORDS_PROPERTY, newValue.toString());
        updateStateInternal();
    }

    private boolean isAdbEnabled() {
        return SystemProperties.get(ADB_ENABLED_PROPERTY).equals(ADB_ENABLED_VALUE);
    }

    protected boolean getValue() {
        return SystemProperties.getBoolean(KEYCHORDS_PROPERTY, false);
    }

    private void updateStateInternal() {
        SwitchPreference pref = (SwitchPreference) mPreference;
        if (isAdbEnabled()) {
            pref.setChecked(true);
            pref.setEnabled(false);
            pref.setSummary(R.string.keychords_summary_adb_enabled);
        } else {
            pref.setChecked(getValue());
            pref.setEnabled(true);
            pref.setSummary(R.string.keychords_summary);
        }
    }
}
