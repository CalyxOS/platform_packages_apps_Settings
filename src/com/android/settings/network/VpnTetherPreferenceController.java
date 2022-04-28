/*
 * Copyright (C) 2019 The Android Open Source Project
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

import static android.net.TetheringManager.TETHERING_INVALID;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.android.settingslib.RestrictedLockUtilsInternal;

import com.google.common.annotations.VisibleForTesting;

/**
 * This controller helps to manage the switch state and visibility of VPN tether switch
 * preference.
 */
public final class VpnTetherPreferenceController extends TetherBasePreferenceController {

    public VpnTetherPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public boolean isChecked() {
        return shouldEnable();
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.TETHERING_ALLOW_VPN_UPSTREAMS, isChecked ? 1 : 0);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean shouldEnable() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.TETHERING_ALLOW_VPN_UPSTREAMS, 1) != 0;
    }

    @Override
    public boolean shouldShow() {
        return true;
    }

    @Override
    public int getTetherType() {
        return TETHERING_INVALID;
    }
}
