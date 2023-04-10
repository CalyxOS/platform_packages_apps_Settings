/*
 * Copyright (C) 2018 The LineageOS Project
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

package com.android.settings.security.screenlock;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import lineageos.providers.LineageSettings;

public class QuickSettingsPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    static final String QUICK_SETTINGS_SHOWN_ON_SECURE_KEYGUARD =
            "quick_settings_shown_on_secure_keyguard";

    private final int mUserId;
    private final LockPatternUtils mLockPatternUtils;

    public QuickSettingsPreferenceController(Context context, int userId,
            LockPatternUtils lockPatternUtils) {
        super(context);
        mUserId = userId;
        mLockPatternUtils = lockPatternUtils;
    }

    @Override
    public boolean isAvailable() {
        return mLockPatternUtils.getCredentialTypeForUser(mUserId)
                != LockPatternUtils.CREDENTIAL_TYPE_NONE;
    }

    @Override
    public String getPreferenceKey() {
        return QUICK_SETTINGS_SHOWN_ON_SECURE_KEYGUARD;
    }

    @Override
    public void updateState(Preference preference) {
        ((TwoStatePreference) preference).setChecked(LineageSettings.Secure.getInt(
                mContext.getContentResolver(),
                LineageSettings.Secure.QS_TILES_TOGGLEABLE_ON_LOCK_SCREEN,
                1) == 1);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return LineageSettings.Secure.putInt(mContext.getContentResolver(),
                LineageSettings.Secure.QS_TILES_TOGGLEABLE_ON_LOCK_SCREEN,
                (Boolean) newValue ? 1 : 0);
    }
}
