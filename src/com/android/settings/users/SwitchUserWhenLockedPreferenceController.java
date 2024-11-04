/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.settings.users;

import android.content.Context;
import android.net.Uri;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.RestrictedSwitchPreference;

import lineageos.providers.LineageSettings;

public class SwitchUserWhenLockedPreferenceController extends TogglePreferenceController {

    public static Uri getSettingsUri() {
        return LineageSettings.Secure.getUriFor(
                LineageSettings.Secure.USER_SWITCHER_HIDDEN_WHEN_LOCKED);
    }

    public static boolean isChecked(final Context context) {
        return LineageSettings.Secure.getInt(context.getContentResolver(),
                LineageSettings.Secure.USER_SWITCHER_HIDDEN_WHEN_LOCKED, 0) == 0;
    }

    private final UserCapabilities mUserCaps;

    public SwitchUserWhenLockedPreferenceController(Context context, String key) {
        super(context, key);
        mUserCaps = UserCapabilities.create(context);
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        final RestrictedSwitchPreference restrictedSwitchPreference =
                (RestrictedSwitchPreference) preference;
        if (!isAvailable()) {
            restrictedSwitchPreference.setVisible(false);
        } else {
            restrictedSwitchPreference.setVisible(true);
        }
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return isChecked(mContext);
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return LineageSettings.Secure.putInt(mContext.getContentResolver(),
                LineageSettings.Secure.USER_SWITCHER_HIDDEN_WHEN_LOCKED, isChecked ? 0 : 1);
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }
}
