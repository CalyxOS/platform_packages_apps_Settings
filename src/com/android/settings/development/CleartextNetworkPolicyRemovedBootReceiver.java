package com.android.settings.development;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.UserManager;

import com.android.settings.R;

import lineageos.providers.LineageSettings;

public class CleartextNetworkPolicyRemovedBootReceiver extends BroadcastReceiver {

    private static final int oneShotUpdateInfoNotificationID = 100;
    private static final String UPDATE_INFO_URL = "https://calyxos.org/global-no-cleartext";
    private static final String updateInfoChannelID = "updateInfo";
    private static final String CLEARTEXT_NETWORK_POLICY = "cleartext_network_policy";

    @Override
    public void onReceive(Context context, Intent intent) {
        final boolean isMainUser = UserManager.get(context).isMainUser();
        if (isMainUser && LineageSettings.Global.getInt(context.getContentResolver(),
                CLEARTEXT_NETWORK_POLICY, -1) != -1) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UPDATE_INFO_URL));

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE);

            String longDescription =
                    context.getString(R.string.update_info_cleartext_removal_desc_long);

            Notification notification = new Notification.Builder(context, updateInfoChannelID)
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentTitle(context.getString(R.string.update_info_title))
                    .setContentText(context.getString(R.string.update_info_cleartext_removal_desc))
                    .setStyle(new Notification.BigTextStyle().bigText(longDescription))
                    .setContentIntent(contentIntent)
                    .setCategory(Notification.CATEGORY_SYSTEM)
                    .setAutoCancel(false)
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;

            NotificationManager notificationManager = context.getSystemService(
                    NotificationManager.class);
            NotificationChannel notificationChannel = new NotificationChannel(
                    updateInfoChannelID,
                    context.getString(R.string.update_info_channel_title),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription(
                    context.getString(R.string.update_info_channel_desc2));
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(oneShotUpdateInfoNotificationID, notification);
        }
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, CleartextNetworkPolicyRemovedBootReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
