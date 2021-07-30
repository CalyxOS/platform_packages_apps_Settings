/*
 * Copyright (C) 2021 The Calyx Institute
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

package com.android.settings.network;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;
import com.android.settingslib.RestrictedSwitchPreference;

/**
 * Preference of Airplane Mode UI entry
 */
public class AirplaneModePreference extends RestrictedSwitchPreference {
    final static String TAG = "AirplaneModePreference";

    public AirplaneModePreference(Context context, AttributeSet attrs, int defStyleAttr,
                                  int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.restricted_switch_widget_with_gear);
        setWidgetLayoutResource(R.layout.preference_widget_gear);
    }

    public AirplaneModePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AirplaneModePreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.switchPreferenceStyle,
                android.R.attr.switchPreferenceStyle));
    }

    public AirplaneModePreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final View gear = holder.findViewById(android.R.id.widget_frame)
                .findViewById(R.id.settings_button);
        gear.setOnClickListener(new View.OnClickListener() {
            @java.lang.Override
            public void onClick(View v) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(24,16,24,16);
                Switch bluetooth = new Switch(getContext());
                bluetooth.setPadding(0,0,0,16);
                Switch wifi = new Switch(getContext());
                wifi.setPadding(0,0,0,16);
                Switch nfc = new Switch(getContext());
                bluetooth.setText(R.string.bluetooth);
                wifi.setText(R.string.wifi);
                nfc.setText(R.string.nfc_quick_toggle_title);
                linearLayout.addView(bluetooth);
                linearLayout.addView(wifi);
                linearLayout.addView(nfc);
                String airplaneModeRadios = Settings.Global.getString(
                        getContext().getContentResolver(), Settings.Global.AIRPLANE_MODE_RADIOS);
                bluetooth.setChecked(airplaneModeRadios.contains(Settings.Global.RADIO_BLUETOOTH));
                wifi.setChecked(airplaneModeRadios.contains(Settings.Global.RADIO_WIFI));
                nfc.setChecked(airplaneModeRadios.contains(Settings.Global.RADIO_NFC));
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.airplane_mode)
                        .setView(linearLayout)
                        .setOnDismissListener(dialogInterface -> {
                            Settings.Global.putString(getContext().getContentResolver(),
                                    Settings.Global.AIRPLANE_MODE_RADIOS,
                                    Settings.Global.RADIO_CELL +
                                            "," +
                                            (bluetooth.isChecked() ? Settings.Global.RADIO_BLUETOOTH
                                                    + "," : "") +
                                            (wifi.isChecked() ? Settings.Global.RADIO_WIFI + ","
                                                    : "") +
                                            (nfc.isChecked() ? Settings.Global.RADIO_NFC + "," : "")
                                            + Settings.Global.RADIO_WIMAX
                            );
                        })
                        .show();
            }
        });
    }
}
