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
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.util.broken.GreetingTextHelper;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.widget.SeekBarPreferenceCham;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarGreetingSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String PREF_SHOW_GREETING =
            "greeting_show_greeting";
    private static final String PREF_CUSTOM_TEXT =
            "greeting_custom_text";
    private static final String PREF_TIMEOUT =
            "greeting_timeout";
    private static final String PREF_PREVIEW =
            "greeting_preview";
    private static final String PREF_COLOR =
            "greeting_color";
    private static final String PREF_COLOR_DARK_MODE =
            "greeting_color_dark_mode";

    private static final int HIDDEN = 2;

    private static final int WHITE = 0xffffffff;
    private static final int TRANSLUCENT_BLACK = 0x99000000;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private ListPreference mShowGreeting;
    private EditTextPreference mCustomText;
    private SeekBarPreferenceCham mTimeOut;
    private Preference mPreview;
    private ColorPickerPreference mColor;
    private ColorPickerPreference mColorDarkMode;

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

        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.status_bar_greeting_settings);

        mShowGreeting =
                (ListPreference) findPreference(PREF_SHOW_GREETING);
        int showGreeting = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_SHOW_GREETING, 1);
        mShowGreeting.setValue(String.valueOf(showGreeting));
        mShowGreeting.setOnPreferenceChangeListener(this);

        if (showGreeting != HIDDEN) {
            int intColor;
            String hexColor;

            mCustomText = (EditTextPreference) findPreference(PREF_CUSTOM_TEXT);
            mCustomText.getEditText().setHint(
                    GreetingTextHelper.getDefaultGreetingText(getActivity()));
            mCustomText.setDialogMessage(getString(R.string.greeting_custom_text_dlg_message,
                    GreetingTextHelper.getDefaultGreetingText(getActivity())));
            mCustomText.setOnPreferenceChangeListener(this);

            mTimeOut =
                    (SeekBarPreferenceCham) findPreference(PREF_TIMEOUT);
            int timeout = Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_GREETING_TIMEOUT, 400);
            mTimeOut.setValue(timeout / 1);
            mTimeOut.setOnPreferenceChangeListener(this);

            mPreview = findPreference(PREF_PREVIEW);
            mPreview.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showGreetingPreview();
                    return true;
                }
            });

            mColor =
                    (ColorPickerPreference) findPreference(PREF_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_COLOR,
                    WHITE);
            mColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColor.setSummary(hexColor);
            mColor.setOnPreferenceChangeListener(this);

            mColorDarkMode =
                    (ColorPickerPreference) findPreference(PREF_COLOR_DARK_MODE);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_COLOR_DARK_MODE,
                    TRANSLUCENT_BLACK);
            mColorDarkMode.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColorDarkMode.setSummary(hexColor);
            mColorDarkMode.setOnPreferenceChangeListener(this);

            updateCustomTextPreference();
        } else {
            removePreference(PREF_CUSTOM_TEXT);
            removePreference(PREF_TIMEOUT);
            removePreference(PREF_PREVIEW);
            removePreference(PREF_COLOR);
            removePreference(PREF_COLOR_DARK_MODE);
        }

        updateShowGreetingSummary(showGreeting);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mShowGreeting) {
            int showGreeting = Integer.valueOf((String) newValue);
            int index = mShowGreeting.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_SHOW_GREETING, showGreeting);
            refreshSettings();
            return true;
        } else if (preference == mCustomText) {
            String text = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.STATUS_BAR_GREETING_CUSTOM_TEXT, text);
            updateCustomTextPreference();
        } else if (preference == mTimeOut) {
            int timeout = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_GREETING_TIMEOUT, timeout * 1);
            return true;
        } else if (preference == mColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mColorDarkMode) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_GREETING_COLOR_DARK_MODE, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void updateShowGreetingSummary(int index) {
        int resId;

        if (index == 0) {
            resId = R.string.greeting_show_greeting_always_summary;
        } else if (index == 1) {
            resId = R.string.greeting_show_greeting_once_summary;
        } else {
            resId = R.string.greeting_show_greeting_never_summary;
        }
        mShowGreeting.setSummary(getResources().getString(resId));
    }

    private void updateCustomTextPreference() {
        String customText = Settings.System.getString(mResolver,
                Settings.System.STATUS_BAR_GREETING_CUSTOM_TEXT);
        if (customText == null) {
            customText = "";
        }
        mCustomText.setText(customText);
        mCustomText.setSummary(customText.isEmpty()
                ? GreetingTextHelper.getDefaultGreetingText(getActivity()) : customText);
    }

    private void showGreetingPreview() {
        Intent i = new Intent();
        i.setAction("com.android.settings.SHOW_GREETING_PREVIEW");
        getActivity().sendBroadcast(i);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
