/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.privacy;

import static android.provider.Settings.Secure.STRICT_LOCATION_REDACTION;

import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;

import com.android.settings.core.TogglePreferenceController;

public class StrictLocationRedactionPreferenceController extends TogglePreferenceController {

    private final UserManager mUserManager;

    public StrictLocationRedactionPreferenceController(Context context, String key) {
        super(context, key);
        mUserManager = context.getSystemService(UserManager.class);
    }

    @Override
    public int getAvailabilityStatus() {
        return mUserManager.isProfile() ? DISABLED_FOR_USER : AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                STRICT_LOCATION_REDACTION, /* def */ 1) != 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        Settings.Secure.putInt(mContext.getContentResolver(),
                STRICT_LOCATION_REDACTION, isChecked ? 1 : 0);
        return true;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return 0; // TODO Actual res
    }
}
