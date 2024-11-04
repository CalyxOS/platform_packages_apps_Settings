/*
 * Copyright (C) 2016 The Android Open Source Project
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

import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static androidx.lifecycle.Lifecycle.Event.ON_STOP;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.RestrictedSwitchPreference;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AddUserWhenLockedPreferenceController
        extends TogglePreferenceController
        implements LifecycleObserver {

    private final UserCapabilities mUserCaps;
    private final ContentObserver mSettingsContentObserver;
    private RestrictedSwitchPreference mPreference;

    public AddUserWhenLockedPreferenceController(Context context, String key) {
        super(context, key);
        mUserCaps = UserCapabilities.create(context);
        mSettingsContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                android.util.Log.i("debuggy", "mPreference = " + mPreference);
                if (mPreference != null) {
                    updateState(mPreference);
                }
            }
        };
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        mUserCaps.updateAddUserCapabilities(mContext);
        final RestrictedSwitchPreference restrictedSwitchPreference =
                (RestrictedSwitchPreference) preference;
        updateStateViaSwitchUserSetting(restrictedSwitchPreference);
        if (!isAvailable()) {
            restrictedSwitchPreference.setVisible(false);
        } else {
            if (android.multiuser.Flags.newMultiuserSettingsUx()) {
                restrictedSwitchPreference.setVisible(true);
                if (mUserCaps.mDisallowAddUserSetByAdmin) {
                    restrictedSwitchPreference.setDisabledByAdmin(mUserCaps.mEnforcedAdmin);
                } else if (mUserCaps.mDisallowAddUser) {
                    restrictedSwitchPreference.setVisible(false);
                }
            } else {
                restrictedSwitchPreference.setDisabledByAdmin(
                        mUserCaps.disallowAddUser() ? mUserCaps.getEnforcedAdmin() : null);
                restrictedSwitchPreference.setVisible(mUserCaps.mUserSwitcherEnabled);
            }
        }
    }

    private void updateStateViaSwitchUserSetting(final RestrictedSwitchPreference preference) {
        final boolean canSwitchUserWhenLocked =
                SwitchUserWhenLockedPreferenceController.isChecked(mContext);
        android.util.Log.i("debuggy", "canSwitchUserWhenLocked = " + canSwitchUserWhenLocked);
        if (canSwitchUserWhenLocked) {
            preference.setEnabled(true);
            preference.setSummary(null);
        } else {
            preference.setEnabled(false);
            preference.setSummary(R.string.user_add_on_lockscreen_menu_summary_no_switcher);
        }
    }

    @Override
    public int getAvailabilityStatus() {
        if (!mUserCaps.isAdmin()) {
            return DISABLED_FOR_USER;
        } else if (android.multiuser.Flags.newMultiuserSettingsUx()) {
            return AVAILABLE;
        } else if (mUserCaps.disallowAddUser() || mUserCaps.disallowAddUserSetByAdmin()) {
            return DISABLED_FOR_USER;
        } else {
            return mUserCaps.mUserSwitcherEnabled ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
        }
    }

    @Override
    public boolean isChecked() {
        return Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.ADD_USERS_WHEN_LOCKED, 0) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.ADD_USERS_WHEN_LOCKED, isChecked ? 1 : 0);
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = (RestrictedSwitchPreference) screen.findPreference(getPreferenceKey());
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        mContext.getContentResolver().registerContentObserver(
                SwitchUserWhenLockedPreferenceController.getSettingsUri(),
                /* notifyForDescendants= */ false, mSettingsContentObserver);
    }

    @OnLifecycleEvent(ON_STOP)
    public void onStop() {
        mContext.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }
}
