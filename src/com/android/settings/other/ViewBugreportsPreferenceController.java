/*
 * Copyright (C) 2020 The Android Open Source Project
 * Copyright (C) 2023 The Calyx Institute
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

package com.android.settings.other;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.development.DevelopmentSettingsEnabler;

public class ViewBugreportsPreferenceController extends ProtectedPreferenceController {

    private static final String PREFERENCE_KEY = "view_bugreports";

    // see: com.android.server.am.ContentProviderHelper.DevelopmentSettingsObserver.onChange
    private final ComponentName mBugreportStorageProvider = new ComponentName(
            "com.android.shell", "com.android.shell.BugreportStorageProvider");

    // see: com.android.shell.BugreportStorageProvider
    private final String AUTHORITY = "com.android.shell.documents";
    private final String DOC_ID_ROOT = "bugreport";
    private final Uri mBugreportsUri = DocumentsContract.buildRootUri(AUTHORITY, DOC_ID_ROOT);

    private boolean mMustEnableStorageProvider;

    public ViewBugreportsPreferenceController(Context context, Activity activity,
            RestrictedDashboardFragment fragment) {
        super(context, activity, fragment, true /* handleClicks */);
    }

    @Override
    public String getPreferenceKey() {
        return PREFERENCE_KEY;
    }

    @Override
    protected boolean isAuthorizationNeeded(int action, Object newValue) {
        // The exact logic used to determine whether the Bug Reports section is accessible may be
        // found in com.android.server.am.ContentProviderHelper.DevelopmentSettingsObserver.
        // It also allows access for ENG builds. But this is good enough for us.

        mMustEnableStorageProvider = !isBugreportsStorageProviderEnabled();
        return mMustEnableStorageProvider;
        // return !DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(mContext);
    }

    @Override
    protected void onAuthorizedAction(int action, Object newValue) {
        // Enable the component.
        // TODO: Disable it at some point? When? If we don't, it will be available until reboot.
        if (mMustEnableStorageProvider) {
            setBugreportsStorageProviderEnabled(true);
            mMustEnableStorageProvider = false;
        }

        launchActivityWithUri(mBugreportsUri);
    }

    private boolean isBugreportsStorageProviderEnabled() {
        return mContext.getPackageManager().getComponentEnabledSetting(mBugreportStorageProvider)
                == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    private void setBugreportsStorageProviderEnabled(boolean enabled) {
        mContext.getPackageManager().setComponentEnabledSetting(
                    mBugreportStorageProvider,
                    enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            : PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    0);
    }

    // see: com.android.settings.deviceinfo.storage.StorageItemPreferenceController
    private void launchActivityWithUri(Uri dataUri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        // Uncomment to force launching with DocumentsUI instead of potentially prompting the
        // user with a chooser when multiple handlers are available.
        // StorageItemPreferenceController does not do this.
        //intent.setPackage("com.android.documentsui");

        intent.setData(dataUri);

        // NOTE: An existing DocumentsUI activity, started by us, but navigated to another
        // location, and left open (not returned to us with Back), will not open to our URI
        // for some reason. This same behavior is present for StorageItemPreferenceController
        // (Settings > Storage), so it is likely a DocumentsUI quirk or bug.
        mContext.startActivity(intent);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        updateStateInternal();
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);

        if (preference == mPreference) {
            updateStateInternal();
        }
    }

    private void updateStateInternal() {
        if (!DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(mContext)) {
            mPreference.setSummary(R.string.view_bugreports_summary_not_developer);
        } else {
            mPreference.setSummary("");
        }
    }
}
