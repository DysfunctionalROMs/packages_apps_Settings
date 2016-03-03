/*
 *  Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.android.settings.broken;

import com.android.internal.logging.MetricsLogger;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.broken.SystemSettingSwitchPreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.broken.widget.SeekBarPreferenceCham;

public class LockScreenSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final int IMAGE_PICK = 1;

    private static final String KEY_WALLPAPER_SET = "lockscreen_wallpaper_set";
    private static final String KEY_WALLPAPER_CLEAR = "lockscreen_wallpaper_clear";
    private static final String LSWEATHER = "ls_weather";
    private static final String LOCKSCREEN_ALPHA = "lockscreen_alpha";
    private static final String LOCKSCREEN_SECURITY_ALPHA = "lockscreen_security_alpha";
    private static final String KEY_LOCKSCREEN_BLUR_RADIUS = "lockscreen_blur_radius";

    private Preference mSetWallpaper;
    private Preference mClearWallpaper;
    private SystemSettingSwitchPreference mLsTorch;
    private PreferenceScreen mLsWeather;
    private SeekBarPreferenceCham mLsAlpha;
    private SeekBarPreferenceCham mLsSecurityAlpha;
    private SeekBarPreferenceCham mBlurRadius;
    
    private static final int MY_USER_ID = UserHandle.myUserId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.broken_settings_lockscreen);
        
        PreferenceCategory generalCategory = (PreferenceCategory) findPreference("lockscreen_gen");
        PreferenceScreen prefScreen = getPreferenceScreen();
        
        ContentResolver resolver = getActivity().getContentResolver();
        LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());

        mSetWallpaper = (Preference) findPreference(KEY_WALLPAPER_SET);
        mClearWallpaper = (Preference) findPreference(KEY_WALLPAPER_CLEAR);
        mLsWeather = (PreferenceScreen)findPreference(LSWEATHER);
        
        mLsAlpha = (SeekBarPreferenceCham) findPreference(LOCKSCREEN_ALPHA);
        float alpha = Settings.System.getFloat(resolver,
                Settings.System.LOCKSCREEN_ALPHA, 0.45f);
        mLsAlpha.setValue((int)(100 * alpha));
        mLsAlpha.setOnPreferenceChangeListener(this);

        mLsSecurityAlpha = (SeekBarPreferenceCham) findPreference(LOCKSCREEN_SECURITY_ALPHA);
        if (lockPatternUtils.isSecure(MY_USER_ID)) {
        float alpha2 = Settings.System.getFloat(resolver,
                Settings.System.LOCKSCREEN_SECURITY_ALPHA, 0.75f);
        mLsSecurityAlpha.setValue((int)(100 * alpha2));
        mLsSecurityAlpha.setOnPreferenceChangeListener(this);
        } else if (mLsSecurityAlpha != null) {
            prefScreen.removePreference(mLsSecurityAlpha);
        }
        
        mLsTorch = (SystemSettingSwitchPreference) prefScreen.findPreference("keyguard_toggle_torch");
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            generalCategory.removePreference(mLsTorch);
        }
        
        mBlurRadius = (SeekBarPreferenceCham) findPreference(KEY_LOCKSCREEN_BLUR_RADIUS);
        mBlurRadius.setValue(Settings.System.getInt(getContentResolver(),
               Settings.System.LOCKSCREEN_BLUR_RADIUS, 14));
        mBlurRadius.setOnPreferenceChangeListener(this);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
         if (preference == mLsAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putFloat(resolver,
                    Settings.System.LOCKSCREEN_ALPHA, alpha / 100.0f);
            return true;
        } else if (preference == mLsSecurityAlpha) {
            int alpha2 = (Integer) objValue;
            Settings.System.putFloat(resolver,
                    Settings.System.LOCKSCREEN_SECURITY_ALPHA, alpha2 / 100.0f);
            return true;
        } else if (preference == mBlurRadius) {
            int width = ((Integer)objValue).intValue();
            Settings.System.putInt(getContentResolver(),
                Settings.System.LOCKSCREEN_BLUR_RADIUS, width);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSetWallpaper) {
            setKeyguardWallpaper();
            return true;
        } else if (preference == mClearWallpaper) {
            clearKeyguardWallpaper();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Intent intent = new Intent();
                intent.setClassName("com.android.wallpapercropper", "com.android.wallpapercropper.WallpaperCropActivity");
                intent.putExtra("keyguardMode", "1");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void setKeyguardWallpaper() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    private void clearKeyguardWallpaper() {
        WallpaperManager wallpaperManager = null;
        wallpaperManager = WallpaperManager.getInstance(getActivity());
        wallpaperManager.clearKeyguardWallpaper();
    }
}
