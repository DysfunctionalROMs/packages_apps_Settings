/*
 * Copyright (C) 2013 Slimroms Project
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

import com.android.internal.logging.MetricsLogger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.Date;

public class KeyguardStatusBarClockStyle extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String TAG = "KeyguardStatusBarClockStyle";

    private static final String PREF_ENABLE = "keyguard_clock_style";
    private static final String PREF_AM_PM_STYLE = "keyguard_status_bar_am_pm";
    private static final String PREF_CLOCK_DATE_DISPLAY = "keyguard_clock_date_display";
    private static final String PREF_CLOCK_DATE_STYLE = "keyguard_clock_date_style";
    private static final String PREF_CLOCK_DATE_FORMAT = "keyguard_clock_date_format";
    private static final String KEYGUARD_STATUS_BAR_CLOCK = "keyguard_status_bar_show_clock";

    public static final int KEYGUARD_CLOCK_DATE_STYLE_LOWERCASE = 1;
    public static final int KEYGUARD_CLOCK_DATE_STYLE_UPPERCASE = 2;
    private static final int KEYGUARD_CUSTOM_CLOCK_DATE_FORMAT_INDEX = 18;

    private static final int MENU_RESET = Menu.FIRST;

    private static final int DLG_RESET = 0;

    private ListPreference mKeyguardClockStyle;
    private ListPreference mKeyguardClockAmPmStyle;
    private ListPreference mKeyguardClockDateDisplay;
    private ListPreference mKeyguardClockDateStyle;
    private ListPreference mKeyguardClockDateFormat;
    private SwitchPreference mKeyguardStatusBarClock;

    private boolean mCheckPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCustomView();
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }

    private PreferenceScreen createCustomView() {
        mCheckPreferences = false;
        PreferenceScreen prefSet = getPreferenceScreen();
        if (prefSet != null) {
            prefSet.removeAll();
        }

        addPreferencesFromResource(R.xml.broken_settings_keyguard_clock);
        prefSet = getPreferenceScreen();

        PackageManager pm = getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return null;
        }

        mKeyguardClockStyle = (ListPreference) findPreference(PREF_ENABLE);
        mKeyguardClockStyle.setOnPreferenceChangeListener(this);
        mKeyguardClockStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.KEYGUARD_STATUSBAR_CLOCK_STYLE,
                0)));
        mKeyguardClockStyle.setSummary(mKeyguardClockStyle.getEntry());

        mKeyguardClockAmPmStyle = (ListPreference) prefSet.findPreference(PREF_AM_PM_STYLE);
        mKeyguardClockAmPmStyle.setOnPreferenceChangeListener(this);
        mKeyguardClockAmPmStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.KEYGUARD_STATUSBAR_CLOCK_AM_PM_STYLE,
                0)));
        boolean is24hour = DateFormat.is24HourFormat(getActivity());
        if (is24hour) {
            mKeyguardClockAmPmStyle.setSummary(R.string.status_bar_am_pm_info);
        } else {
            mKeyguardClockAmPmStyle.setSummary(mKeyguardClockAmPmStyle.getEntry());
        }
        mKeyguardClockAmPmStyle.setEnabled(!is24hour);

        mKeyguardClockDateDisplay = (ListPreference) findPreference(PREF_CLOCK_DATE_DISPLAY);
        mKeyguardClockDateDisplay.setOnPreferenceChangeListener(this);
        mKeyguardClockDateDisplay.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_DISPLAY,
                0)));
        mKeyguardClockDateDisplay.setSummary(mKeyguardClockDateDisplay.getEntry());

        mKeyguardClockDateStyle = (ListPreference) findPreference(PREF_CLOCK_DATE_STYLE);
        mKeyguardClockDateStyle.setOnPreferenceChangeListener(this);
        mKeyguardClockDateStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_STYLE,
                0)));
        mKeyguardClockDateStyle.setSummary(mKeyguardClockDateStyle.getEntry());

        mKeyguardClockDateFormat = (ListPreference) findPreference(PREF_CLOCK_DATE_FORMAT);
        mKeyguardClockDateFormat.setOnPreferenceChangeListener(this);
        if (mKeyguardClockDateFormat.getValue() == null) {
            mKeyguardClockDateFormat.setValue("EEE");
        }

        parseClockDateFormats();

        mKeyguardStatusBarClock = (SwitchPreference) prefSet.findPreference(KEYGUARD_STATUS_BAR_CLOCK);
        mKeyguardStatusBarClock.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.KEYGUARD_STATUS_BAR_CLOCK, 1) == 1));
        mKeyguardStatusBarClock.setOnPreferenceChangeListener(this);

        boolean mKeyguardClockDateToggle = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_DISPLAY, 0) != 0;
        if (!mKeyguardClockDateToggle) {
            mKeyguardClockDateStyle.setEnabled(false);
            mKeyguardClockDateFormat.setEnabled(false);
        }

        setHasOptionsMenu(true);
        mCheckPreferences = true;
        return prefSet;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!mCheckPreferences) {
            return false;
        }
        AlertDialog dialog;

        if (preference == mKeyguardClockAmPmStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mKeyguardClockAmPmStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_STATUSBAR_CLOCK_AM_PM_STYLE, val);
            mKeyguardClockAmPmStyle.setSummary(mKeyguardClockAmPmStyle.getEntries()[index]);
            return true;
        } else if (preference == mKeyguardClockStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mKeyguardClockStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_STATUSBAR_CLOCK_STYLE, val);
            mKeyguardClockStyle.setSummary(mKeyguardClockStyle.getEntries()[index]);
            return true;
        } else if (preference == mKeyguardClockDateDisplay) {
            int val = Integer.parseInt((String) newValue);
            int index = mKeyguardClockDateDisplay.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_DISPLAY, val);
            mKeyguardClockDateDisplay.setSummary(mKeyguardClockDateDisplay.getEntries()[index]);
            if (val == 0) {
                mKeyguardClockDateStyle.setEnabled(false);
                mKeyguardClockDateFormat.setEnabled(false);
            } else {
                mKeyguardClockDateStyle.setEnabled(true);
                mKeyguardClockDateFormat.setEnabled(true);
            }
            return true;
        } else if (preference == mKeyguardClockDateStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mKeyguardClockDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_STYLE, val);
            mKeyguardClockDateStyle.setSummary(mKeyguardClockDateStyle.getEntries()[index]);
            parseClockDateFormats();
            return true;
        } else if (preference == mKeyguardStatusBarClock) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.KEYGUARD_STATUS_BAR_CLOCK,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mKeyguardClockDateFormat) {
            int index = mKeyguardClockDateFormat.findIndexOfValue((String) newValue);

            if (index == KEYGUARD_CUSTOM_CLOCK_DATE_FORMAT_INDEX) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.clock_date_string_edittext_title);
                alert.setMessage(R.string.clock_date_string_edittext_summary);

                final EditText input = new EditText(getActivity());
                String oldText = Settings.System.getString(
                    getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_FORMAT);
                if (oldText != null) {
                    input.setText(oldText);
                }
                alert.setView(input);

                alert.setPositiveButton(R.string.menu_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        String value = input.getText().toString();
                        if (value.equals("")) {
                            return;
                        }
                        Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_FORMAT, value);

                        return;
                    }
                });

                alert.setNegativeButton(R.string.menu_cancel,
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        return;
                    }
                });
                dialog = alert.create();
                dialog.show();
            } else {
                if ((String) newValue != null) {
                    Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_FORMAT, (String) newValue);
                }
            }
            return true;
        }
        return false;
    }

    private void parseClockDateFormats() {
        // Parse and repopulate mClockDateFormats's entries based on current date.
        String[] dateEntries = getResources().getStringArray(R.array.keyguard_clock_date_format_entries_values);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();

        int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.KEYGUARD_STATUSBAR_CLOCK_DATE_STYLE, 0);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == KEYGUARD_CLOCK_DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == KEYGUARD_CLOCK_DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }

                parsedDateEntries[i] = newDate;
            }
        }
        mKeyguardClockDateFormat.setEntries(parsedDateEntries);
    }
}
