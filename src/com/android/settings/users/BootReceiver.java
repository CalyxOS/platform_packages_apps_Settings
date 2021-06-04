package com.android.settings.users;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;

import com.android.internal.R;

import java.util.Arrays;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UserManager userManager = UserManager.get(context);
        if (userManager.isManagedProfile()) {
            PackageManager packageManager = context.getPackageManager();
            List<String> requiredApps = Arrays.asList(context.getResources().getStringArray(
                    R.array.required_apps_managed_profile));
            for (String pkg : requiredApps) {
                try {
                    packageManager.getPackageInfoAsUser(pkg, 0, userManager.getUserHandle());
                } catch (PackageManager.NameNotFoundException e) {
                    try {
                        new PackageInstaller(AppGlobals.getPackageManager().getPackageInstaller(),
                                context.getPackageName(), userManager.getUserHandle())
                                .installExistingPackage(pkg, PackageManager.INSTALL_REASON_UNKNOWN,
                                        null);
                    } catch (RemoteException re) {
                        Log.e(BootReceiver.class.getSimpleName(), "Could not install " + pkg +
                                " in user " + userManager.getUserHandle(), re);
                    }
                }
            }
        }
    }
}