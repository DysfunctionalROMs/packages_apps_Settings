/* 
 * Copyright (C) 2015 DarkKat
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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockScreenShortcutsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_COLORIZE_CUSTOM_ICONS =
            "shortcuts_colorize_custom_icons";

    private SwitchPreference mColorizeCustomIcons;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen_shortcuts_settings);
        mResolver = getActivity().getContentResolver();

        mColorizeCustomIcons =
                (SwitchPreference) findPreference(PREF_COLORIZE_CUSTOM_ICONS);
        mColorizeCustomIcons.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_SCREEN_SHORTCUTS_COLORIZE_CUSTOM_ICONS, 0) == 1);
        mColorizeCustomIcons.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mColorizeCustomIcons) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_SCREEN_SHORTCUTS_COLORIZE_CUSTOM_ICONS,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
