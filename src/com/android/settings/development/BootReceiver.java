package com.android.settings.development;

import android.app.AppGlobals;
import android.app.backup.IBackupManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.R;

import java.util.Arrays;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UserManager userManager = UserManager.get(context);
        if (userManager.isAdminUser()) {
            try {
                if (Settings.Global.getInt(context.getContentResolver(),
                        Settings.Global.CLEARTEXT_NETWORK_POLICY, 0) > 0) {
                    SystemProperties.set(StrictMode.GLOBAL_CLEARTEXT_PROPERTY, "true");
                }
                AppGlobals.getPackageManager().setComponentEnabledSetting(
                        new ComponentName(context, BootReceiver.class),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP, userManager.getUserHandle());
            } catch (RemoteException re) {
                Log.w(BootReceiver.class.getSimpleName(), re);
            }
        }
    }
}