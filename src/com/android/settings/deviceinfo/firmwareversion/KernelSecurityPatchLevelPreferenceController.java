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

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.text.TextUtils;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KernelSecurityPatchLevelPreferenceController extends BasePreferenceController {

    private static final String TAG = "KernelSecurityPatchCtrl";

    private static final String KEY_KERNEL_SECURITY_PATCH =
            "ro.vendor.kernel.security_patch";

    private String mCurrentPatch;

    public KernelSecurityPatchLevelPreferenceController(Context context, String key) {
        super(context, key);
        mCurrentPatch = SystemProperties.get(KEY_KERNEL_SECURITY_PATCH);
    }

    @Override
    public int getAvailabilityStatus() {
        return !TextUtils.isEmpty(mCurrentPatch)
                ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        try {
            SimpleDateFormat template = new SimpleDateFormat("yyyy-MM-dd");
            Date patchLevelDate = template.parse(mCurrentPatch);
            String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy");
            mCurrentPatch = DateFormat.format(format, patchLevelDate).toString();
        } catch (ParseException e) {
            // parsing failed, use raw string
        }

        return mCurrentPatch;
    }
}
