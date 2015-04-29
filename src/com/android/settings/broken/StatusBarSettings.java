
package com.android.settings.broken;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    // Statusbar battery percent
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    // Quick Pulldown
    public static final String STATUS_BAR_QUICK_QS_PULLDOWN = "status_bar_quick_qs_pulldown";
    // Clock summary
    private static final String KEY_STATUS_BAR_CLOCK = "clock_style_pref";

    // Statusbar battery percent
    private ListPreference mStatusBarBattery;
    // Quick Pulldown
    private SwitchPreference mStatusBarQuickQsPulldown;
    // Clock summary
    private PreferenceScreen mClockStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        //Statusbar battery percent
        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);

        int batteryStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBattery.setValue(String.valueOf(batteryStyle));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        // Quick Pulldown
        mStatusBarQuickQsPulldown = (SwitchPreference) getPreferenceScreen()
                .findPreference(STATUS_BAR_QUICK_QS_PULLDOWN);
        mStatusBarQuickQsPulldown.setChecked((Settings.System.getInt(getActivity()
                .getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0) == 1));
        mStatusBarQuickQsPulldown.setOnPreferenceChangeListener(this);

        // Clock summary
        mClockStyle = (PreferenceScreen) getPreferenceScreen()
                .findPreference(KEY_STATUS_BAR_CLOCK);
        updateClockStyleDescription();

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBattery) {
            int batteryStyle = Integer.valueOf((String) objValue);
            int index = mStatusBarBattery.findIndexOfValue((String) objValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryStyle);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarQuickQsPulldown) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
	updateClockStyleDescription();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateClockStyleDescription() {
	if (mClockStyle == null) {
	    return;
        }
	if (Settings.System.getInt(getContentResolver(),
	        Settings.System.STATUS_BAR_CLOCK, 1) == 1) {
	    mClockStyle.setSummary(getString(R.string.enabled));
        } else {
	    mClockStyle.setSummary(getString(R.string.disabled));
        }
    }
}

