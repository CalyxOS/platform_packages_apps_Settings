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

package com.android.settings.development.featureflags;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.BasePreferenceController;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link BasePreferenceController} used in {@link AirplaneModeRadios}
 */
public class AirplaneModeRadiosPreferenceController extends BasePreferenceController {

    private PreferenceGroup mGroup;

    private static final Map<String, String> DEFAULT_FLAGS;

    static {
        DEFAULT_FLAGS = new HashMap<>();
        DEFAULT_FLAGS.put(Settings.Global.RADIO_BLUETOOTH, "true");
        DEFAULT_FLAGS.put(Settings.Global.RADIO_WIFI, "true");
        DEFAULT_FLAGS.put(Settings.Global.RADIO_NFC, "true");
    }

    public AirplaneModeRadiosPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return Build.IS_DEBUGGABLE ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mGroup = screen.findPreference(getPreferenceKey());
        final Map<String, String> radioMap = DEFAULT_FLAGS;
        if (radioMap == null) {
            return;
        }
        mGroup.removeAll();
        final Context prefContext = mGroup.getContext();
        radioMap.keySet().stream().sorted().forEach(feature ->
                mGroup.addPreference(new AirplaneModeRadioPreference(prefContext, feature)));
    }
}
