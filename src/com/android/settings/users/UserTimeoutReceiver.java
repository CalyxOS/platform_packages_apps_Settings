package com.android.settings.users;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;

import lineageos.providers.LineageSettings;

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
                for (UserInfo userInfo : userManager.getAliveUsers()) {
                    long time = LineageSettings.Secure.getLongForUser(context.getContentResolver(),
                            LineageSettings.Secure.USER_ACTIVITY_END_TIME, -1, userInfo.id);
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