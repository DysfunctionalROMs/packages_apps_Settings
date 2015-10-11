package com.android.settings.broken;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.InstrumentedFragment;

public class PowerMenuActions extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

	private static final String PREF_SHOW_ADVANCED_REBOOT =
            "power_menu_show_advanced_reboot";

    private SwitchPreference mShowAdvancedReboot;
    private ContentResolver mResolver;

// private variables here
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings_power);

        mResolver = getActivity().getContentResolver();

        mShowAdvancedReboot =
                (SwitchPreference) findPreference(PREF_SHOW_ADVANCED_REBOOT);
        mShowAdvancedReboot.setChecked((Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_SHOW_ADVANCED_REBOOT, 0) == 1));
        mShowAdvancedReboot.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
    // preference changes here
         if (preference == mShowAdvancedReboot) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_SHOW_ADVANCED_REBOOT, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.BROKENPOWER;
    }
}
