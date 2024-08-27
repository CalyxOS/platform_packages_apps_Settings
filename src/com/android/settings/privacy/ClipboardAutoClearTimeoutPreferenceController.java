/*
 * Copyright (C) 2020-2021 The Calyx Institute
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

import android.content.Context;
import android.os.UserManager;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;

import lineageos.providers.LineageSettings;

public class ClipboardAutoClearTimeoutPreferenceController extends
        BasePreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private static final String TAG = "ClipboardAutoClearTimeoutPrefCtrl";

    // Copied from ClipboardService
    private static final long DEFAULT_CLIPBOARD_TIMEOUT_MILLIS = 3600000;

    private final String mClipboardAutoClearTimeoutKey;

    public ClipboardAutoClearTimeoutPreferenceController(Context context, String key) {
        super(context, key);
        mClipboardAutoClearTimeoutKey = key;
    }

    @Override
    public int getAvailabilityStatus() {
        return UserManager.get(mContext).isAdminUser() ? AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    public String getPreferenceKey() {
        return mClipboardAutoClearTimeoutKey;
    }

    @Override
    public void updateState(Preference preference) {
        final ListPreference timeoutListPreference = (ListPreference) preference;
        final long currentTimeout = LineageSettings.System.getLong(mContext.getContentResolver(),
                LineageSettings.System.AUTO_CLEAR_TIMEOUT, DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
        timeoutListPreference.setValue(String.valueOf(currentTimeout));
        updateTimeoutPreferenceDescription(timeoutListPreference,
                Long.parseLong(timeoutListPreference.getValue()));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            long value = Long.parseLong((String) newValue);
            LineageSettings.System.putLong(mContext.getContentResolver(),
                    LineageSettings.System.AUTO_CLEAR_TIMEOUT, value);
            updateTimeoutPreferenceDescription((ListPreference) preference, value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "could not persist clipboard auto clear timeout setting", e);
        }
        return true;
    }

    public static CharSequence getTimeoutDescription(
            long currentTimeout, CharSequence[] entries, CharSequence[] values) {
        if (currentTimeout < 0 || entries == null || values == null
                || values.length != entries.length) {
            return null;
        }

        for (int i = 0; i < values.length; i++) {
            long timeout = Long.parseLong(values[i].toString());
            if (currentTimeout == timeout) {
                return entries[i];
            }
        }
        return null;
    }

    private void updateTimeoutPreferenceDescription(ListPreference preference,
            long currentTimeout) {
        final CharSequence[] entries = preference.getEntries();
        final CharSequence[] values = preference.getEntryValues();
        final CharSequence timeoutDescription = getTimeoutDescription(
                currentTimeout, entries, values);
        String summary = "";
        if (timeoutDescription != null) {
            if (currentTimeout != 0) {
                summary = mContext.getString(R.string.clipboard_auto_clear_timeout_summary,
                        timeoutDescription);
            } else {
                summary = mContext.getString(R.string.clipboard_auto_clear_timeout_summary2);
            }
        }
        preference.setSummary(summary);
    }
}
