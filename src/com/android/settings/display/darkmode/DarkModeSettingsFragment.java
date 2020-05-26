/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.settings.display.darkmode;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.app.settings.SettingsEnums;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

/**
 * Settings screen for Dark UI Mode
 */
@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class DarkModeSettingsFragment extends DashboardFragment {

    private static final String TAG = "DarkModeSettingsFragment";
    private static final String KEY_BERRY_BLACK_THEME = "berry_black_theme";
    private DarkModeObserver mContentObserver;
    private Runnable mCallback = () -> {
        updatePreferenceStates();
    };
    private SwitchPreference mBlackTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getContext();
        mContentObserver = new DarkModeObserver(context);
        mBlackTheme = (SwitchPreference) findPreference(KEY_BERRY_BLACK_THEME);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Listen for changes only while visible.
        mContentObserver.subscribe(mCallback);
        updateBlackThemeState();
    }

    private void updateBlackThemeState() {
        mBlackTheme.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.BERRY_BLACK_THEME, 0) == 1 ? true : false);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop listening for state changes.
        mContentObserver.unsubscribe();
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.dark_mode_settings;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mBlackTheme) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BERRY_BLACK_THEME,
                    mBlackTheme.isChecked() ? 1 : 0);
        }

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public int getHelpResource() {
        return R.string.help_url_dark_theme;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DARK_UI_SETTINGS;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider();
}
