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

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import lineageos.providers.LineageSettings;

public class EnableQuickSettingsWhenLockedPreferenceController extends TogglePreferenceController {

    public EnableQuickSettingsWhenLockedPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return LineageSettings.Secure.getInt(mContext.getContentResolver(),
                LineageSettings.Secure.DISABLE_QUICK_SETTINGS_ON_LOCK_SCREEN, 0) == 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return LineageSettings.Secure.putInt(mContext.getContentResolver(),
                LineageSettings.Secure.DISABLE_QUICK_SETTINGS_ON_LOCK_SCREEN, isChecked ? 0 : 1);
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }
}
