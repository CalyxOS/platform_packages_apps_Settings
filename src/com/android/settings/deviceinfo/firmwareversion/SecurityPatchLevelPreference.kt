/*
 * Copyright (C) 2024 The Android Open Source Project
 * Copyright (C) 2019-2025 The LineageOS Project
 * Copyright (C) 2021-2026 The Calyx Institute
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

package com.android.settings.deviceinfo.firmwareversion

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemProperties
import android.text.TextUtils
import android.text.format.DateFormat
import androidx.preference.Preference
import com.android.settings.R
import com.android.settings.utils.getLocale
import com.android.settingslib.DeviceInfoUtils
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.metadata.PersistentPreference
import com.android.settingslib.metadata.PreferenceAvailabilityProvider
import com.android.settingslib.metadata.preferencesapi.preconditions.PreconditionStability
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.preference.PreferenceBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// LINT.IfChange
class SecurityPatchLevelPreference :
    PersistentPreference<String>,
    PreferenceMetadata,
    PreferenceAvailabilityProvider,
    PreferenceSummaryProvider,
    PreferenceBinding {

    private var currentPatch: String? = null

    override val key: String
        get() = "security_key"

    override val purpose: Int
        get() = R.string.security_key_purpose

    override val title: Int
        get() = R.string.security_patch

    override fun intent(context: Context): Intent? =
        Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://calyxos.org/security/bulletin/"))

    override val availabilityDescription =
        "The device must have a security patch level."

    override fun getAvailabilityStability() = PreconditionStability.STABLE_UNTIL_APK_UPDATE

    override fun isAvailable(context: Context) = context.getPatch().isNotEmpty()

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence {
        val currentPatch = context.getPatch()

        val vendorPatch = parseDate(SystemProperties.get(KEY_VENDOR_SECURITY_PATCH, currentPatch))
        val kernelPatch = parseDate(SystemProperties.get(KEY_KERNEL_SECURITY_PATCH, vendorPatch))
        val firmwarePatch =
            parseDate(SystemProperties.get(KEY_FIRMWARE_SECURITY_PATCH, vendorPatch))

        return if (TextUtils.equals(currentPatch, vendorPatch)) {
            currentPatch
        } else {
            context.getString(
                R.string.detailed_security_patch,
                currentPatch,
                kernelPatch,
                vendorPatch,
                firmwarePatch,
            )
        }
    }

    private fun Context.getPatch(): String =
        currentPatch
            ?: (DeviceInfoUtils.getSecurityPatch(getLocale()) ?: "").also { currentPatch = it }

    private fun parseDate(dateStr: String): String {
        if (dateStr.isNotEmpty()) {
            try {
                val template = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val patchLevelDate: Date? = template.parse(dateStr)
                val format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy")
                return DateFormat.format(format, patchLevelDate).toString()
            } catch (_: ParseException) {
                // Parsing failed, return raw string
            }
        }
        return dateStr
    }

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isCopyingEnabled = true
    }

    companion object {
        const val KEY_VENDOR_SECURITY_PATCH: String = "ro.vendor.build.security_patch"
        const val KEY_KERNEL_SECURITY_PATCH: String = "ro.vendor.kernel.security_patch"
        const val KEY_FIRMWARE_SECURITY_PATCH: String = "ro.vendor.firmware.security_patch"
    }
}
// LINT.ThenChange(SecurityPatchLevelPreferenceController.java)
