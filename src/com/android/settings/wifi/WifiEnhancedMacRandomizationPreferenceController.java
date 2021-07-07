/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.TogglePreferenceController;

/**
 * Controller for enhanced MAC randomization.
 */
public class WifiEnhancedMacRandomizationPreferenceController
        extends TogglePreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private static final String WIFI_ENHANCED_MAC_RANDOMIZATION_KEY =
            "wifi_enhanced_mac_randomization";
    private static final String ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG =
            "enhanced_mac_randomization_force_enabled";

    public WifiEnhancedMacRandomizationPreferenceController(Context context) {
        super(context, WIFI_ENHANCED_MAC_RANDOMIZATION_KEY);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return Settings.Global.getInt(mContext.getContentResolver(),
                ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG, 0) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.Global.putInt(mContext.getContentResolver(),
                ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG, isChecked ? 1 : 0);
    }
}
