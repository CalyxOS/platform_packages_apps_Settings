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

import androidx.preference.SwitchPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AirplaneModeRadioPreference extends SwitchPreference {

    private final String mKey;

    private static final String def_airplane_mode_radios = String.join(",",
            Settings.Global.RADIO_CELL, Settings.Global.RADIO_BLUETOOTH, Settings.Global.RADIO_WIFI,
            Settings.Global.RADIO_NFC, Settings.Global.RADIO_WIMAX);


    public AirplaneModeRadioPreference(Context context, String key) {
        super(context);
        mKey = key;
        setKey(key);
        setTitle(key);
        setCheckedInternal(Settings.Global.getString(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_RADIOS).contains(key));
    }

    @Override
    public void setChecked(boolean isChecked) {
        setCheckedInternal(isChecked);
        String airplaneModeRadios = Settings.Global.getString(getContext().getContentResolver(),
                Settings.Global.AIRPLANE_MODE_RADIOS);
        if (airplaneModeRadios.contains(mKey) && !isChecked) {
            List<String> airplaneModeRadiosList = new ArrayList<>(Arrays.asList(
                    airplaneModeRadios.split(",")));
            airplaneModeRadiosList.remove(mKey);
            airplaneModeRadios = String.join(",", airplaneModeRadiosList);
        } else if (!airplaneModeRadios.contains(mKey) && isChecked) {
            airplaneModeRadios += "," + mKey;
        }
        Settings.Global.putString(getContext().getContentResolver(),
                Settings.Global.AIRPLANE_MODE_RADIOS, airplaneModeRadios);
    }

    private void setCheckedInternal(boolean isChecked) {
        super.setChecked(isChecked);
        setSummary(Boolean.toString(isChecked));
    }
}
