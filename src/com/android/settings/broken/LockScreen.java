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
import android.database.ContentObserver;
import com.android.internal.util.broken.QSUtils;
import android.os.Bundle;
import android.os.Handler;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.widget.SeekBarPreferenceCham;

public class LockScreen extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

	private static final String KEY_LONGPRESS_LOCK_FOR_TORCH = "long_press_lock_icon_torch";
	private static final String KEY_LOCKSCREEN_BLUR_RADIUS = "lockscreen_blur_radius";

	private SwitchPreference mLongPressForTorch;
	private SwitchPreference mToggleAppInstallation;
	private SeekBarPreferenceCham mBlurRadius;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.lock_screen);
    PreferenceScreen prefSet = getPreferenceScreen();
    ContentResolver resolver = getActivity().getContentResolver();

    mBlurRadius = (SeekBarPreferenceCham) findPreference(KEY_LOCKSCREEN_BLUR_RADIUS);
        mBlurRadius.setValue(Settings.System.getInt(resolver,
                Settings.System.LOCKSCREEN_BLUR_RADIUS, 14));
        mBlurRadius.setOnPreferenceChangeListener(this);

    if(!QSUtils.deviceSupportsFlashLight(getActivity())) {
	    mLongPressForTorch = (SwitchPreference)
	    prefSet.findPreference(KEY_LONGPRESS_LOCK_FOR_TORCH);
            if (mLongPressForTorch != null) {
               prefSet.removePreference(mLongPressForTorch);
		   }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
		ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mBlurRadius) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, width);
            return true;
		}
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
