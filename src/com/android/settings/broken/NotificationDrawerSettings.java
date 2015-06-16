/*
 * Copyright (C) 2015 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.broken;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.broken.qs.QSTiles;
import com.android.settings.Utils;

public class NotificationDrawerSettings extends SettingsPreferenceFragment {
    private Preference mQSTiles;
    private static final String FORCE_EXPANDED_NOTIFICATIONS = "force_expanded_notifications";

	private SwitchPreference mForceExpanded;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.notification_drawer_settings);

        ContentResolver resolver = getActivity().getContentResolver();
        PackageManager pm = getPackageManager();

        mQSTiles = findPreference("qs_order");

        mForceExpanded = (SwitchPreference) findPreference(FORCE_EXPANDED_NOTIFICATIONS);
        mForceExpanded.setChecked((Settings.System.getInt(resolver,
                Settings.System.FORCE_EXPANDED_NOTIFICATIONS, 0) == 1));
    }

    @Override
    public void onResume() {
        super.onResume();

        int qsTileCount = QSTiles.determineTileCount(getActivity());
        mQSTiles.setSummary(getResources().getQuantityString(R.plurals.qs_tiles_summary,
                    qsTileCount, qsTileCount));
    }

     @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		 if  (preference == mForceExpanded) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FORCE_EXPANDED_NOTIFICATIONS, checked ? 1:0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
