
package com.android.settings.broken;

import android.content.pm.PackageManager;
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
import com.android.settings.util.Helpers;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    // General
    private static String STATUS_BAR_GENERAL_CATEGORY = "status_bar_general_category";
    // Native battery percentage
    private static final String STATUS_BAR_NATIVE_BATTERY_PERCENTAGE = "status_bar_native_battery_percentage";
    // Quick Pulldown
    public static final String STATUS_BAR_QUICK_QS_PULLDOWN = "status_bar_quick_qs_pulldown";
    // Clock summary
    private static final String KEY_STATUS_BAR_CLOCK = "clock_style_pref";

    // LockClock
    private static final String KEY_LOCKCLOCK = "lock_clock";
    // Package name of the cLock app
    public static final String LOCKCLOCK_PACKAGE_NAME = "com.cyanogenmod.lockclock";

    // General
    private PreferenceCategory mStatusBarGeneralCategory;
    // Native battery percentage
    private SwitchPreference mStatusBarNativeBatteryPercentage;
    // Quick Pulldown
    private SwitchPreference mStatusBarQuickQsPulldown;
    // Clock summary
    private PreferenceScreen mClockStyle;
    // LockClock
    private Preference mLockClock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);

        PackageManager pm = getPackageManager();

        // General category
        mStatusBarGeneralCategory = (PreferenceCategory) findPreference(STATUS_BAR_GENERAL_CATEGORY);

        // Native battery percentage
        mStatusBarNativeBatteryPercentage = (SwitchPreference) getPreferenceScreen()
                .findPreference(STATUS_BAR_NATIVE_BATTERY_PERCENTAGE);
        mStatusBarNativeBatteryPercentage.setChecked((Settings.System.getInt(getActivity()
                .getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_NATIVE_BATTERY_PERCENTAGE, 0) == 1));
        mStatusBarNativeBatteryPercentage.setOnPreferenceChangeListener(this);

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

        // cLock app check
        mLockClock = (Preference) getPreferenceScreen()
                .findPreference(KEY_LOCKCLOCK);
        if (!Helpers.isPackageInstalled(LOCKCLOCK_PACKAGE_NAME, pm)) {
            getPreferenceScreen().removePreference(mLockClock);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        boolean value = (Boolean) objValue;
        if (preference == mStatusBarNativeBatteryPercentage) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NATIVE_BATTERY_PERCENTAGE, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarQuickQsPulldown) {
            Settings.System.putInt(getActivity().getContentResolver(),
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

