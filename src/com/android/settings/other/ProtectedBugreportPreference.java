/*
 * Copyright (C) 2022 The Calyx Institute
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

package com.android.settings.other;

import android.content.Context;
import android.util.AttributeSet;

import com.android.settings.BugreportPreference;

/**
 * A BugreportPreference for which a PreferenceController handles clicks and shows the bug report
 * dialog manually, allowing access to the dialog to be protected.
 */
public class ProtectedBugreportPreference extends BugreportPreference {

    public ProtectedBugreportPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        // Do nothing; showDialog must be called by a controller.
    }

    public void showDialog() {
        super.onClick();
    }
}
