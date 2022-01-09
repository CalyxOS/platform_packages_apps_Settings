/*
 * Copyright (C) 2020 The Android Open Source Project
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
package com.android.settings.network.telephony;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

/**
 * Preference controller for "Enable 3G"
 */
public class Enable3gPreferenceController extends TelephonyTogglePreferenceController {

    private static final String LOG_TAG = "Enable3gPreferenceController";
    private static final long BITMASK_3G =  TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_0
                | TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_A
                | TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_B
                | TelephonyManager.NETWORK_TYPE_BITMASK_EHRPD
                | TelephonyManager.NETWORK_TYPE_BITMASK_HSUPA
                | TelephonyManager.NETWORK_TYPE_BITMASK_HSDPA
                | TelephonyManager.NETWORK_TYPE_BITMASK_HSPA
                | TelephonyManager.NETWORK_TYPE_BITMASK_HSPAP
                | TelephonyManager.NETWORK_TYPE_BITMASK_UMTS
                | TelephonyManager.NETWORK_TYPE_BITMASK_TD_SCDMA;

    private final MetricsFeatureProvider mMetricsFeatureProvider;

    private TelephonyManager mTelephonyManager;

    /**
     * Class constructor of "Enable 3G" toggle.
     *
     * @param context of settings
     * @param key assigned within UI entry of XML file
     */
    public Enable3gPreferenceController(Context context, String key) {
        super(context, key);
        mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    /**
     * Initialization based on a given subscription id.
     *
     * @param subId is the subscription id
     * @return this instance after initialization
     */
    public Enable3gPreferenceController init(int subId) {
        mSubId = subId;
        mTelephonyManager = mContext.getSystemService(TelephonyManager.class)
              .createForSubscriptionId(mSubId);
        return this;
    }

    @Override
    public int getAvailabilityStatus(int subId) {
        final PersistableBundle carrierConfig = mCarrierConfigManager.getConfigForSubId(subId);
        if (mTelephonyManager == null) {
            Log.w(LOG_TAG, "Telephony manager not yet initialized");
            mTelephonyManager = mContext.getSystemService(TelephonyManager.class);
        }
        boolean visible =
                SubscriptionManager.isUsableSubscriptionId(subId)
                && mTelephonyManager.isRadioInterfaceCapabilitySupported(
                    mTelephonyManager.CAPABILITY_USES_ALLOWED_NETWORK_TYPES_BITMASK);
        return visible ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }

    @Override
    public boolean isChecked() {
        long currentlyAllowedNetworkTypes = mTelephonyManager.getAllowedNetworkTypesForReason(
                mTelephonyManager.ALLOWED_NETWORK_TYPES_REASON_ENABLE_3G);
        return (currentlyAllowedNetworkTypes & BITMASK_3G) != 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        if (!SubscriptionManager.isUsableSubscriptionId(mSubId)) {
            return false;
        }
        long currentlyAllowedNetworkTypes = mTelephonyManager.getAllowedNetworkTypesForReason(
                mTelephonyManager.ALLOWED_NETWORK_TYPES_REASON_ENABLE_3G);
        boolean enabled = (currentlyAllowedNetworkTypes & BITMASK_3G) != 0;
        if (enabled == isChecked) {
            return false;
        }
        long newAllowedNetworkTypes = currentlyAllowedNetworkTypes;
        if (isChecked) {
            newAllowedNetworkTypes = currentlyAllowedNetworkTypes | BITMASK_3G;
            Log.i(LOG_TAG, "Enabling 3g. Allowed network types: " + newAllowedNetworkTypes);
        } else {
            newAllowedNetworkTypes = currentlyAllowedNetworkTypes & ~BITMASK_3G;
            Log.i(LOG_TAG, "Disabling 3g. Allowed network types: " + newAllowedNetworkTypes);
        }
        mTelephonyManager.setAllowedNetworkTypesForReason(
                mTelephonyManager.ALLOWED_NETWORK_TYPES_REASON_ENABLE_3G, newAllowedNetworkTypes);
        mMetricsFeatureProvider.action(
                mContext, SettingsEnums.ACTION_3G_ENABLED, isChecked);
        return true;
    }
}
