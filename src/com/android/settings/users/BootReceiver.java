package com.android.settings.users;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
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
                PackageManager packageManager = context.getPackageManager();
                List<String> requiredApps = Arrays.asList(context.getResources().getStringArray(
                        R.array.required_apps_managed_profile));
                List<UserHandle> profiles = userManager.getUserProfiles();
                for (UserHandle profile : profiles) {
                    for (String pkg : requiredApps) {
                        try {
                            packageManager.getPackageInfoAsUser(pkg, 0, profile.getIdentifier());
                        } catch (PackageManager.NameNotFoundException e) {
                            new PackageInstaller(AppGlobals.getPackageManager()
                                    .getPackageInstaller(), context.getPackageName(),
                                    profile.getIdentifier()).installExistingPackage(pkg,
                                    PackageManager.INSTALL_REASON_UNKNOWN, null);
                            AppGlobals.getPackageManager().setComponentEnabledSetting(
                                    new ComponentName(context, BootReceiver.class),
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                    PackageManager.DONT_KILL_APP, profile.getIdentifier());
                        }
                    }
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