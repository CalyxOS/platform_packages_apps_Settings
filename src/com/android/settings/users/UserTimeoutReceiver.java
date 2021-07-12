package com.android.settings.users;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;

import com.android.settings.R;

import lineageos.providers.LineageSettings;

public class UserTimeoutReceiver extends BroadcastReceiver {

    private static final String ACTION_USER_TIMEOUT = "android.intent.action.USER_TIMEOUT";
    private static final String USER_TIMEOUT_CHANNEL = "user_timeout_channel";
    private static final String EXTRA_NOTIFICATION = "notification";

    private AlarmManager mAlarmManager;

    private PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        UserManager userManager = UserManager.get(context);
        if (intent != null) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                mAlarmManager = context.getSystemService(AlarmManager.class);
                mPendingIntent = PendingIntent.getBroadcast(context, context.getUserId(),
                        new Intent(ACTION_USER_TIMEOUT)
                                .setPackage(context.getPackageName()),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                context.getContentResolver().registerContentObserver(
                        LineageSettings.Secure.getUriFor(
                                LineageSettings.Secure.USER_ACTIVITY_END_TIME), false,
                        new ContentObserver(new Handler(Looper.getMainLooper())) {
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                setTimeout(context);
                            }
                        });
                setTimeout(context);
            } else if (ACTION_USER_TIMEOUT.equals(intent.getAction())) {
                if (!intent.getBooleanExtra(EXTRA_NOTIFICATION, false)) {
                    NotificationManager notificationManager = NotificationManager.from(
                            context);
                    NotificationChannel notificationChannel = new NotificationChannel(
                            USER_TIMEOUT_CHANNEL,
                            context.getString(R.string.work_hours_title),
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(notificationChannel);
                    mPendingIntent = PendingIntent.getBroadcast(context, context.getUserId(),
                            new Intent(ACTION_USER_TIMEOUT).setPackage(
                                    context.getPackageName()).putExtra(
                                    EXTRA_NOTIFICATION, true),
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    Notification notification = new Notification.Builder(context,
                            USER_TIMEOUT_CHANNEL)
                            .setSmallIcon(R.drawable.ic_settings_multiuser)
                            .setContentIntent(mPendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationManager.notify(R.drawable.ic_settings_multiuser, notification);
                } else if (userManager.isManagedProfile()) {
                    userManager.requestQuietModeEnabled(true, context.getUser());
                } else {
                    context.getSystemService(ActivityManager.class).stopUser(context.getUserId(),
                            true);
                }
            }
        }
    }

    private void setTimeout(Context context) {
        long time = LineageSettings.Secure.getLong(context.getContentResolver(),
                LineageSettings.Secure.USER_ACTIVITY_END_TIME, -1);
        if (time != -1) {
            mAlarmManager.set(
                    AlarmManager.RTC_WAKEUP, time, AlarmManager.WINDOW_EXACT,
                    AlarmManager.INTERVAL_DAY, mPendingIntent, null);
        } else {
            mAlarmManager.cancel(mPendingIntent);
        }
    }
}