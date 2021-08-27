/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;

import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

/**
 * A {@link BasePreferenceController} used in {@link AirplaneModeRadios}
 */
public class AirplaneModeRadiosPreferenceController extends BasePreferenceController {

    private PreferenceGroup mGroup;

    private static final String[] AIRPLANE_MODE_RADIOS = {Settings.Global.RADIO_BLUETOOTH,
            Settings.Global.RADIO_NFC, Settings.Global.RADIO_WIFI};

    public AirplaneModeRadiosPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mGroup = screen.findPreference(getPreferenceKey());
        mGroup.removeAll();
        for (String radio : AIRPLANE_MODE_RADIOS) {
            mGroup.addPreference(new AirplaneModeRadioPreference(mGroup.getContext(), radio));
        }
    }
}
