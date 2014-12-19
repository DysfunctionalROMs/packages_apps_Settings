package com.android.settings.broken;

import android.os.Bundle;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.internal.logging.MetricsLogger;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_LOCK_CLOCK = "lock_clock";
    private static final String KEY_LOCK_CLOCK_PACKAGE_NAME = "com.cyanogenmod.lockclock";

    private PreferenceScreen mLockClock;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();

        // mLockClock 
    	mLockClock = (PreferenceScreen) findPreference(KEY_LOCK_CLOCK);
        if (!Utils.isPackageInstalled(getActivity(), KEY_LOCK_CLOCK_PACKAGE_NAME)) {
            prefSet.removePreference(mLockClock);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

		// preference changes here
        return false;
    }
    
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
