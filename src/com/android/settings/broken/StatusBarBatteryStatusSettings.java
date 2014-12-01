/*
 * Copyright (C) 2013 DarkKat
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarBatteryStatusSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_SHOW_TEXT =
            "battery_status_show_text";
    private static final String PREF_SHOW_ANIMATION =
            "battery_status_show_charging_animation";
    private static final String PREF_BATTERY_COLOR =
            "battery_status_battery_color";
    private static final String PREF_TEXT_COLOR =
            "battery_status_text_color";

    private static final int DEFAULT_BATTERY_COLOR = 0xffffffff;
    private static final int DEFAULT_TEXT_COLOR = 0xff000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mShowText;
    private CheckBoxPreference mShowAnimation;
    private ColorPickerPreference mBatteryColor;
    private ColorPickerPreference mTextColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.status_bar_battery_status_settings);
        mResolver = getActivity().getContentResolver();

        boolean showtext = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 0) == 1;

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        mShowText =
                (CheckBoxPreference) findPreference(PREF_SHOW_TEXT);
        mShowText.setChecked(showtext);
        mShowText.setOnPreferenceChangeListener(this);

        mShowAnimation =
                (CheckBoxPreference) findPreference(PREF_SHOW_ANIMATION);
        mShowAnimation.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_CHARGING_ANIMATION,
                0) == 1);
        mShowAnimation.setOnPreferenceChangeListener(this);

        mBatteryColor =
            (ColorPickerPreference) findPreference(PREF_BATTERY_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_COLOR,
                DEFAULT_BATTERY_COLOR);
        mBatteryColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryColor.setSummary(hexColor);
        mBatteryColor.setOnPreferenceChangeListener(this);

        if (showtext) {
            mTextColor =
                    (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                    DEFAULT_TEXT_COLOR); 
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_TEXT_COLOR);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_menu_reset) // use the KitKat backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intHex;
        String hex;

        if (preference == mShowText) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowAnimation) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_CHARGING_ANIMATION,
                    value ? 1 : 0);
            return true;
        } else if (preference == mBatteryColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        StatusBarBatteryStatusSettings getOwner() {
            return (StatusBarBatteryStatusSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_CHARGING_ANIMATION, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_COLOR,
                                    DEFAULT_BATTERY_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    DEFAULT_TEXT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_broken,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_CHARGING_ANIMATION, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_COLOR,
                                    0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    DEFAULT_BATTERY_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
}
