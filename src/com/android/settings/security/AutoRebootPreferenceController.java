package com.android.settings.security;

import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.events.OnResume;

import java.util.concurrent.TimeUnit;

public class AutoRebootPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, OnResume, Preference.OnPreferenceChangeListener {

    private static final String KEY_AUTO_REBOOT = "auto_reboot";
    private static final String PREF_KEY_SECURITY_CATEGORY = "security_category";
    private static final boolean DEBUG = true;

    private PreferenceCategory mSecurityCategory;
    private boolean mIsAdmin;
    private final UserManager mUm;

    public AutoRebootPreferenceController(Context context) {
        super(context);
        mUm = UserManager.get(context);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mSecurityCategory = screen.findPreference(PREF_KEY_SECURITY_CATEGORY);
        updatePreferenceState();
    }

    @Override
    public boolean isAvailable() {
        mIsAdmin = mUm.isAdminUser();
        return mIsAdmin;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_AUTO_REBOOT;
    }

    // TODO: should we use onCreatePreferences() instead?
    private void updatePreferenceState() {

        if (mSecurityCategory == null) {
            log("updatePreferenceState called with null mSecurityCategory");
            return;
        }

        if (mIsAdmin) {
            ListPreference autoRebootPsfs = (ListPreference) mSecurityCategory.findPreference(KEY_AUTO_REBOOT);
            final long currentTimeout = Settings.Global.getLong(mContext.getContentResolver(),
                    Settings.Global.DEVICE_REBOOT_TIMEOUT, 0);
            log("updatePreferenceState called from admin/owner user update psfs value with currentValue :  " + currentTimeout);
            if (autoRebootPsfs != null) {
                autoRebootPsfs.setValue(String.valueOf(currentTimeout));
                updateTimeoutPreferenceDescription(autoRebootPsfs,
                        Long.parseLong(autoRebootPsfs.getValue()));
            }
        } else {
            log("updatePreferenceState isn't called from admin/owner user removing ");
            mSecurityCategory.removePreference(mSecurityCategory.findPreference(KEY_AUTO_REBOOT));
        }
    }

    @Override
    public void onResume() {
        updatePreferenceState();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        log("onPreferenceChange key : " + key + " value : " + value + " isString? : " + (value instanceof  String) + " isAdminUser? : " + mUm.isAdminUser());
        if (KEY_AUTO_REBOOT.equals(key) && value instanceof String && mIsAdmin) {
            int timeout = Integer.parseInt((String) value);
            Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.DEVICE_REBOOT_TIMEOUT, timeout);
            updateTimeoutPreferenceDescription((ListPreference) preference, timeout);
        }
        return true;
    }

    private void log(String log) {
        if (DEBUG && log != null) {
            Log.d("AutoReboot", log);
        }
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
        log("timeoutDescription: " + timeoutDescription);
        if (timeoutDescription != null) {
            if (currentTimeout != 0)
                summary = mContext.getString(R.string.auto_reboot_summary, timeoutDescription);
            else
                summary = mContext.getString(R.string.auto_reboot_summary2);
        }
        preference.setSummary(summary);
    }

}
