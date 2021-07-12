package com.android.settings.users;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;

import java.util.UUID;

public class UserTimeoutReceiver extends BroadcastReceiver {

    private static final String ACTION_USER_TIMEOUT = "android.intent.action.USER_TIMEOUT";
    private static final String EXTRA_USER_ID = "user_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        UserManager userManager = UserManager.get(context);
        if (intent != null && userManager.isSystemUser()) {
            if (ACTION_USER_TIMEOUT.equals(intent.getAction())) {
                int userId = intent.getIntExtra(EXTRA_USER_ID, -1);
                if (userId != -1) {
                    if (userManager.isManagedProfile(userId)) {
                        userManager.requestQuietModeEnabled(true,
                                UserHandle.of(userId));
                    }
                }
            } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                for (UserInfo userInfo : userManager.getUsers(true)) {
                    long time = Settings.Secure.getLongForUser(context.getContentResolver(),
                            Settings.Secure.USER_ACTIVITY_END_TIME, -1, userInfo.id);
                    if (time != -1) {
                        context.getSystemService(AlarmManager.class).setRepeating(
                                AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY,
                                PendingIntent.getBroadcast(context, userInfo.id,
                                        new Intent(ACTION_USER_TIMEOUT)
                                                .setPackage(context.getPackageName())
                                        .putExtra(EXTRA_USER_ID, userInfo.id),
                                        PendingIntent.FLAG_UPDATE_CURRENT));
                    }
                }
            }
        }
    }
}