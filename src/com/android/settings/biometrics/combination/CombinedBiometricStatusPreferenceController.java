/*
 * Copyright (C) 2021 The Android Open Source Project
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
package com.android.settings.biometrics.combination;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricStatusPreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;

/**
 * Preference controller for biometrics settings page controlling the ability to unlock the phone
 * with face and fingerprint.
 */
public class CombinedBiometricStatusPreferenceController extends
        BiometricStatusPreferenceController implements LifecycleObserver {
    private static final String KEY_BIOMETRIC_SETTINGS = "biometric_settings";

    @VisibleForTesting
    RestrictedPreference mPreference;
    protected final CombinedBiometricStatusUtils mCombinedBiometricStatusUtils;

    public CombinedBiometricStatusPreferenceController(Context context) {
        this(context, KEY_BIOMETRIC_SETTINGS, null /* lifecycle */);
    }

    public CombinedBiometricStatusPreferenceController(Context context, String key) {
        this(context, key, null /* lifecycle */);
    }

    public CombinedBiometricStatusPreferenceController(Context context, Lifecycle lifecycle) {
        this(context, KEY_BIOMETRIC_SETTINGS, lifecycle);
    }

    public CombinedBiometricStatusPreferenceController(
            Context context, String key, Lifecycle lifecycle) {
        super(context, key);
        mCombinedBiometricStatusUtils = new CombinedBiometricStatusUtils(context, getUserId());

        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        updateStateInternal();
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = screen.findPreference(mPreferenceKey);

        if (mPreference != null) {
            boolean hasFingerprints = Utils.hasFingerprintHardware(mContext);
            boolean hasFace = Utils.hasFaceHardware(mContext);

            if (hasFingerprints != hasFace) {
                if (hasFingerprints) {
                    mPreference.setTitle(mContext.getResources().getString(
                            R.string.security_settings_fingerprint_preference_title));
                } else {
                    mPreference.setTitle(mContext.getResources().getString(
                            R.string.security_settings_face_preference_title));
                }
            }
        }
    }

    @Override
    protected boolean isDeviceSupported() {
        return mCombinedBiometricStatusUtils.isAvailable();
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        updateStateInternal();
    }

    private void updateStateInternal() {
        final RestrictedLockUtils.EnforcedAdmin admin =
                mCombinedBiometricStatusUtils.getDisablingAdmin();

        updateStateInternal(admin);
    }

    /**
     *   Disables the preference and shows the consent flow only if consent is required for all
     *   modalities.
     *
     *   <p>Otherwise, users will not be able to enter and modify settings for modalities which have
     *   already been consented. In any case, the controllers for the modalities which have not yet
     *   been consented will be disabled in the combined page anyway - users can go through the
     *   consent+enrollment flow from there.
     */
    @VisibleForTesting
    void updateStateInternal(@Nullable RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (mPreference != null) {
            mPreference.setDisabledByAdmin(enforcedAdmin);
        }
    }

    @Override
    protected String getSummaryText() {
        return mCombinedBiometricStatusUtils.getSummary();
    }

    @Override
    protected String getSettingsClassName() {
        return mCombinedBiometricStatusUtils.getSettingsClassName();
    }
}
