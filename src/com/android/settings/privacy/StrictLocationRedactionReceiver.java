/*
 * Copyright (C) 2024 The Calyx Institute
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

package com.android.settings.privacy;

import static android.provider.Settings.Secure.STRICT_LOCATION_REDACTION;

import static com.android.settings.Utils.isProfileOf;
import static com.android.settings.Utils.syncSecureSettingWithProfiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;

import java.util.List;

public class StrictLocationRedactionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final UserManager userManager = UserManager.get(context);
        if (intent == null || userManager.isProfile()) {
            return;
        }
        switch (intent.getAction()) {
            case Intent.ACTION_PROFILE_ADDED:
                final UserHandle addedUser = intent.getParcelableExtra(Intent.EXTRA_USER);
                syncSecureSettingWithProfiles(context, STRICT_LOCATION_REDACTION, addedUser);
                break;
        }
    }

    public void register(final Context context) {
        final UserManager userManager = UserManager.get(context);
        if (userManager.isProfile()) {
            return;
        }
        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(STRICT_LOCATION_REDACTION),
                /* notifyForDescendants */ false,
                new ContentObserver(new Handler(Looper.getMainLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        syncSecureSettingWithProfiles(context, STRICT_LOCATION_REDACTION,
                                /* profileId */ null);
                    }
                });
        context.registerReceiver(this, new IntentFilter(Intent.ACTION_PROFILE_ADDED));
    }
}
