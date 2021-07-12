package com.android.settings.users;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

public class UserTimeoutReceiver extends BroadcastReceiver {

    private static final String EXTRA_USER_ID = "user_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        int userId = intent.getIntExtra(EXTRA_USER_ID, -1);
        if (userId != -1) {
            UserManager userManager = context.getSystemService(UserManager.class);
            if (userManager.isManagedProfile(userId)) {
                userManager.requestQuietModeEnabled(true, UserHandle.of(userId));
            } else {
                try {
                    ActivityManager.getService().stopUser(userId, false, null);
                } catch (RemoteException e) {
                    Log.e(UserTimeoutReceiver.class.getSimpleName(), "Failed to stop user " + userId, e);
                }
            }
        }
    }
}