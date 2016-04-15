/*
 * Copyright (C) 2013-2015 SlimRoms
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

package com.android.settings.broken;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.ServiceManager;
import android.provider.Settings;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.IWindowManager;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationSettings extends SettingsPreferenceFragment  implements
        OnPreferenceChangeListener {

    private static final String KEY_HARDWARE_KEYS = "hardwarekeys_settings";
    private static final String KEY_PIE_SETTINGS = "pie_settings";
    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String ONLY_SHOW_RUNNING_TASKS = "only_show_running_tasks";

    private ListPreference mRecentsClearAllLocation;
    private SwitchPreference mRecentsClearAll;
    private SwitchPreference mShowRunningTasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings_navigation);
        PreferenceScreen prefSet = getPreferenceScreen();
        
        // Hide Hardware Keys menu if device doesn't have any
        PreferenceScreen hardwareKeys = (PreferenceScreen) findPreference(KEY_HARDWARE_KEYS);
        int deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);
        if (deviceKeys == 0 && hardwareKeys != null) {
            getPreferenceScreen().removePreference(hardwareKeys);
        }

        final ContentResolver resolver = getActivity().getContentResolver();

        mRecentsClearAll = (SwitchPreference) prefSet.findPreference(SHOW_CLEAR_ALL_RECENTS);
        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 0, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);
        mShowRunningTasks = (SwitchPreference) findPreference(ONLY_SHOW_RUNNING_TASKS);
        mShowRunningTasks.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return -Integer.MAX_VALUE + 1;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) objValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mShowRunningTasks) {
            Settings.System.putInt(getContentResolver(), Settings.System.RECENT_SHOW_RUNNING_TASKS, 
                    ((Boolean) objValue) ? 1 : 0);
            return true;
         }
            return false;
    }
}
