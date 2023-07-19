/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.android.settings.accounts;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.UserHandle;
import android.text.format.DateFormat;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import java.util.Calendar;

import lineageos.providers.LineageSettings;

public class WorkHoursPreferenceController extends TogglePreferenceController {

    private static final String TAG = "WorkHoursPreferenceController";

    private Preference mPreference;
    private UserHandle mManagedUser;

    public WorkHoursPreferenceController(Context context, String key) {
        super(context, key);
    }

    public void setManagedUser(UserHandle managedUser) {
        mManagedUser = managedUser;
    }

    @Override
    public int getAvailabilityStatus() {
        return (mManagedUser != null) ? AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = screen.findPreference(getPreferenceKey());
    }

    @Override
    public CharSequence getSummary() {
        long time = LineageSettings.Secure.getLongForUser(mContext.getContentResolver(),
                LineageSettings.Secure.USER_ACTIVITY_END_TIME, -1, mManagedUser.getIdentifier());
        return time != -1 ? mContext.getString(R.string.work_hours_summary,
                DateFormat.getTimeFormat(mContext).format(time)) : null;
    }

    @Override
    public boolean isChecked() {
        return LineageSettings.Secure.getLongForUser(mContext.getContentResolver(),
                LineageSettings.Secure.USER_ACTIVITY_END_TIME, -1, mManagedUser.getIdentifier())
                != -1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        if (isChecked) {
            new TimePickerDialog(mContext,
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        LineageSettings.Secure.putLongForUser(mContext.getContentResolver(),
                                LineageSettings.Secure.USER_ACTIVITY_END_TIME,
                                calendar.getTimeInMillis(), mManagedUser.getIdentifier());
                        updateState(mPreference);
                    }, 17, 0,
                    DateFormat.is24HourFormat(mContext, mManagedUser.getIdentifier())).show();
        } else {
            LineageSettings.Secure.putLongForUser(mContext.getContentResolver(),
                    LineageSettings.Secure.USER_ACTIVITY_END_TIME, -1,
                    mManagedUser.getIdentifier());
            updateState(mPreference);
        }
        return !isChecked;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(preference);
    }

    @Override
    protected void refreshSummary(Preference preference) {
        if (preference != null) {
            preference.setSummary(getSummary());
        }
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return 0;
    }
}
