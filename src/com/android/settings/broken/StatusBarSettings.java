package com.android.settings.broken;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.internal.logging.MetricsLogger;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.internal.logging.MetricsLogger;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_LOCK_CLOCK = "lock_clock";
    private static final String KEY_LOCK_CLOCK_PACKAGE_NAME = "com.cyanogenmod.lockclock";
    private static final String PRE_QUICK_PULLDOWN = "quick_pulldown";
    private static final String STATUS_BAR_TEMPERATURE_STYLE = "status_bar_temperature_style";
    private static final String STATUS_BAR_TEMPERATURE = "status_bar_temperature";

    private PreferenceScreen mLockClock;
    private ListPreference mQuickPulldown;
    private ListPreference mStatusBarTemperature;
    private ListPreference mStatusBarTemperatureStyle;
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();
        
        mQuickPulldown = (ListPreference) findPreference(PRE_QUICK_PULLDOWN);
        if (!Utils.isPhone(getActivity())) {
            prefSet.removePreference(mQuickPulldown);
        } else {
            // Quick Pulldown
            mQuickPulldown.setOnPreferenceChangeListener(this);
            int statusQuickPulldown = Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 1);
            mQuickPulldown.setValue(String.valueOf(statusQuickPulldown));
            updateQuickPulldownSummary(statusQuickPulldown);
        }

        // mLockClock 
    	mLockClock = (PreferenceScreen) findPreference(KEY_LOCK_CLOCK);
        if (!Utils.isPackageInstalled(getActivity(), KEY_LOCK_CLOCK_PACKAGE_NAME)) {
            prefSet.removePreference(mLockClock);
        }
        
        // tempature
        mStatusBarTemperature = (ListPreference) findPreference(STATUS_BAR_TEMPERATURE);
        int temperatureShow = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0);
        mStatusBarTemperature.setValue(String.valueOf(temperatureShow));
        mStatusBarTemperature.setSummary(mStatusBarTemperature.getEntry());
        mStatusBarTemperature.setOnPreferenceChangeListener(this);
        
        mStatusBarTemperatureStyle = (ListPreference) findPreference(STATUS_BAR_TEMPERATURE_STYLE);
        int temperatureStyle = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_WEATHER_TEMP_STYLE, 0);
        mStatusBarTemperatureStyle.setValue(String.valueOf(temperatureStyle));
        mStatusBarTemperatureStyle.setSummary(mStatusBarTemperatureStyle.getEntry());
        mStatusBarTemperatureStyle.setOnPreferenceChangeListener(this);

        enableStatusBarTemperatureDependents();
    }
    
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
		if (preference == mQuickPulldown) {
            int statusQuickPulldown = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN,
                    statusQuickPulldown);
            updateQuickPulldownSummary(statusQuickPulldown);
            return true;
        } else if (preference == mStatusBarTemperature) {
            int temperatureShow = Integer.valueOf((String) objValue);
            int index = mStatusBarTemperature.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 
                    temperatureShow);
            mStatusBarTemperature.setSummary(
                    mStatusBarTemperature.getEntries()[index]);
            enableStatusBarTemperatureDependents();
            return true;
        } else if (preference == mStatusBarTemperatureStyle) {
            int temperatureStyle = Integer.valueOf((String) objValue);
            int index = mStatusBarTemperatureStyle.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_WEATHER_TEMP_STYLE,
                    temperatureStyle);
            mStatusBarTemperatureStyle.setSummary(
                    mStatusBarTemperatureStyle.getEntries()[index]);
            return true;
        }
        return false;
    }
    
    private void enableStatusBarTemperatureDependents() {
        int temperatureShow = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP,0);
        if (temperatureShow == 0) {
            mStatusBarTemperatureStyle.setEnabled(false);
        } else {
            mStatusBarTemperatureStyle.setEnabled(true);
        }
    }

    private void updateQuickPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else {
            Locale l = Locale.getDefault();
            boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
            String direction = res.getString(value == 2
                    ? (isRtl ? R.string.quick_pulldown_right : R.string.quick_pulldown_left)
                    : (isRtl ? R.string.quick_pulldown_left : R.string.quick_pulldown_right));
            mQuickPulldown.setSummary(res.getString(R.string.summary_quick_pulldown, direction));
        }
    }
}
