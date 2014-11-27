/*
 * Copyright (C) 2014 DarkKat
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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PowerMenuSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_REBOOT =
            "power_menu_reboot";
    private static final String PREF_AIRPLANE =
            "power_menu_airplane";
    private static final String PREF_USERS =
            "power_menu_users";
    private static final String PREF_SETTINGS =
            "power_menu_settings";
    private static final String PREF_LOCKDOWN =
            "power_menu_lockdown";
    private static final String PREF_SILENT =
            "power_menu_silent";

    private CheckBoxPreference mReboot;
    private CheckBoxPreference mAirplane;
    private CheckBoxPreference mUsers;
    private CheckBoxPreference mSettings;
    private CheckBoxPreference mLockdown;
    private CheckBoxPreference mSilent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_menu_settings);

        mReboot = (CheckBoxPreference) findPreference(PREF_REBOOT);
        mReboot.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_REBOOT, 1) == 1);
        mReboot.setOnPreferenceChangeListener(this);

        mAirplane = (CheckBoxPreference) findPreference(PREF_AIRPLANE);
        mAirplane.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_AIRPLANE, 1) == 1);
        mAirplane.setOnPreferenceChangeListener(this);

        mUsers = (CheckBoxPreference) findPreference(PREF_USERS);
        mUsers.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_USERS, 0) == 1);
        mUsers.setOnPreferenceChangeListener(this);

        mSettings = (CheckBoxPreference) findPreference(PREF_SETTINGS);
        mSettings.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_SETTINGS, 0) == 1);
        mSettings.setOnPreferenceChangeListener(this);

        mLockdown = (CheckBoxPreference) findPreference(PREF_LOCKDOWN);
        mLockdown.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_LOCKDOWN, 0) == 1);
        mLockdown.setOnPreferenceChangeListener(this);

        mSilent = (CheckBoxPreference) findPreference(PREF_SILENT);
        mSilent.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_SILENT, 1) == 1);
        mSilent.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mReboot) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_REBOOT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mAirplane) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_AIRPLANE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mUsers) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_USERS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSettings) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_SETTINGS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mLockdown) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_LOCKDOWN,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSilent) {
            value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_SILENT,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
