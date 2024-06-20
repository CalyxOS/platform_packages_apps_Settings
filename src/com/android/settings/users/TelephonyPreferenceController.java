/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.settings.users;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IUserManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

/**
 * Controls the preference on the user settings screen which determines whether the guest user
 * should have access to telephony or not.
 */
public class TelephonyPreferenceController extends TogglePreferenceController {

    private final IUserManager mUserManager;
    private final UserCapabilities mUserCaps;

    public TelephonyPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mUserManager = IUserManager.Stub.asInterface(
                ServiceManager.getService(Context.USER_SERVICE));
        mUserCaps = UserCapabilities.create(context);
    }

    @Override
    public int getAvailabilityStatus() {
        return (mUserCaps.isAdmin()) ? AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    public boolean isChecked() {
        try {
            return !mUserManager.hasUserRestriction(UserManager.DISALLOW_OUTGOING_CALLS,
                    mContext.getUserId()) && !mUserManager.hasUserRestriction(
                    UserManager.DISALLOW_SMS,
                    mContext.getUserId());
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        try {
            mUserManager.setUserRestriction(UserManager.DISALLOW_OUTGOING_CALLS, !isChecked,
                    mContext.getUserId());
            mUserManager.setUserRestriction(UserManager.DISALLOW_SMS, !isChecked,
                    mContext.getUserId());
        } catch (RemoteException e) {
            return false;
        }
        return true;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        mUserCaps.updateAddUserCapabilities(mContext);
        preference.setVisible(isAvailable() && mContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_TELEPHONY));
    }
}
