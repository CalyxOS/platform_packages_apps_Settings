/*
 * Copyright (C) 2017-2019 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.statusbar;

import android.app.settings.SettingsEnums;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NetworkTrafficSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "NetworkTrafficSettings";

    private DropDownPreference mNetTrafficMode;
    private SwitchPreference mNetTrafficAutohide;
    private DropDownPreference mNetTrafficUnits;
    private SwitchPreference mNetTrafficShowUnits;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.network_traffic_settings);
        final ContentResolver resolver = getActivity().getContentResolver();

        mNetTrafficMode = findPreference(Settings.Secure.NETWORK_TRAFFIC_MODE);
        mNetTrafficMode.setOnPreferenceChangeListener(this);
        int mode = Settings.Secure.getInt(resolver,
                Settings.Secure.NETWORK_TRAFFIC_MODE, 0);
        mNetTrafficMode.setValue(String.valueOf(mode));

        mNetTrafficAutohide = findPreference(Settings.Secure.NETWORK_TRAFFIC_AUTOHIDE);
        mNetTrafficAutohide.setOnPreferenceChangeListener(this);

        mNetTrafficUnits = findPreference(Settings.Secure.NETWORK_TRAFFIC_UNITS);
        mNetTrafficUnits.setOnPreferenceChangeListener(this);
        int units = Settings.Secure.getInt(resolver,
                Settings.Secure.NETWORK_TRAFFIC_UNITS, /* Mbps */ 1);
        mNetTrafficUnits.setValue(String.valueOf(units));

        mNetTrafficShowUnits = findPreference(Settings.Secure.NETWORK_TRAFFIC_SHOW_UNITS);
        mNetTrafficShowUnits.setOnPreferenceChangeListener(this);

        updateEnabledStates(mode);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetTrafficMode) {
            int mode = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.NETWORK_TRAFFIC_MODE, mode);
            updateEnabledStates(mode);
        } else if (preference == mNetTrafficUnits) {
            int units = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.NETWORK_TRAFFIC_UNITS, units);
        } else if (preference == mNetTrafficAutohide) {
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.NETWORK_TRAFFIC_AUTOHIDE, (boolean) newValue ? 1 : 0);
        } else if (preference == mNetTrafficShowUnits) {
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.NETWORK_TRAFFIC_SHOW_UNITS, (boolean) newValue ? 1 : 0);
        }
        return true;
    }

    private void updateEnabledStates(int mode) {
        final boolean enabled = mode != 0;
        mNetTrafficAutohide.setEnabled(enabled);
        mNetTrafficUnits.setEnabled(enabled);
        mNetTrafficShowUnits.setEnabled(enabled);
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PAGE_UNKNOWN;
    }
}
