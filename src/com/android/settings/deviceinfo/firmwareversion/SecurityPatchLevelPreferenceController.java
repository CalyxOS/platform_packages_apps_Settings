/*
 * Copyright (C) 2019 The Android Open Source Project
 * Copyright (C) 2019 The LineageOS Project
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

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.R;
import com.android.settingslib.DeviceInfoUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecurityPatchLevelPreferenceController extends BasePreferenceController {

    private static final String TAG = "SecurityPatchCtrl";
    private static final Uri INTENT_URI_DATA = Uri.parse(
            "https://source.android.com/security/bulletin/");
    private static final String KEY_VENDOR_SECURITY_PATCH =
            "ro.vendor.build.security_patch";
    private static final String KEY_KERNEL_SECURITY_PATCH =
            "ro.vendor.kernel.security_patch";
    private static final String KEY_FIRMWARE_SECURITY_PATCH =
            "ro.vendor.firmware.security_patch";

    private final PackageManager mPackageManager;
    private final String mCurrentPatch;
    private String mCurrentKernelPatch;
    private String mCurrentVendorPatch;
    private String mCurrentFirmwarePatch;

    public SecurityPatchLevelPreferenceController(Context context, String key) {
        super(context, key);
        mPackageManager = mContext.getPackageManager();
        mCurrentPatch = DeviceInfoUtils.getSecurityPatch();
        mCurrentVendorPatch = parseDate(SystemProperties.get(KEY_VENDOR_SECURITY_PATCH,
                mCurrentPatch));
        mCurrentKernelPatch = parseDate(SystemProperties.get(KEY_KERNEL_SECURITY_PATCH,
                mCurrentVendorPatch));
        mCurrentFirmwarePatch = parseDate(SystemProperties.get(KEY_FIRMWARE_SECURITY_PATCH,
                mCurrentVendorPatch));
    }

    @Override
    public int getAvailabilityStatus() {
        return !TextUtils.isEmpty(mCurrentPatch)
                ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        String patchLevel;

        if (TextUtils.equals(mCurrentPatch, mCurrentVendorPatch)) {
            patchLevel = mCurrentPatch;
        } else {
            patchLevel = mContext.getString(R.string.detailed_security_patch,
                    mCurrentPatch, mCurrentKernelPatch,
                    mCurrentVendorPatch, mCurrentFirmwarePatch);
        }

        return patchLevel;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }

        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(INTENT_URI_DATA);
        if (mPackageManager.queryIntentActivities(intent, 0).isEmpty()) {
            // Don't send out the intent to stop crash
            Log.w(TAG, "queryIntentActivities() returns empty");
            return true;
        }

        mContext.startActivity(intent);
        return true;
    }

    private String parseDate(String date) {
        if (!date.isEmpty()) {
            try {
                SimpleDateFormat template = new SimpleDateFormat("yyyy-MM-dd");
                Date patchLevelDate = template.parse(date);
                String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy");
                date = DateFormat.format(format, patchLevelDate).toString();
            } catch (ParseException e) {
                // parsing failed, use raw string
            }
        }

        return date;
    }
}
