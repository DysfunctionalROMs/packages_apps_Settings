/*
 * Copyright (C) 2014-2015 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.broken;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarWeatherIcon extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_TEMPERATURE_STYLE = "status_bar_temperature_style";
    private static final String PREF_STATUS_BAR_WEATHER_FONT_STYLE = "status_bar_weather_font_style";
    private static final String PREF_STATUS_BAR_WEATHER_COLOR = "status_bar_weather_color";

    private ListPreference mStatusBarTemperature;
    private ListPreference mStatusBarTemperatureFontStyle;
    private ColorPickerPreference mStatusBarTemperatureColor;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.status_bar_weather_icon);

        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarTemperature = (ListPreference) findPreference(STATUS_BAR_TEMPERATURE_STYLE);
        int temperatureStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0);
        mStatusBarTemperature.setValue(String.valueOf(temperatureStyle));
        mStatusBarTemperature.setSummary(mStatusBarTemperature.getEntry());
        mStatusBarTemperature.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_WEATHER_FONT_STYLE);
        mStatusBarTemperatureFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarTemperatureFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_WEATHER_FONT_STYLE, 0)));
        mStatusBarTemperatureFontStyle.setSummary(mStatusBarTemperatureFontStyle.getEntry());

         mStatusBarTemperatureColor =
            (ColorPickerPreference) findPreference(PREF_STATUS_BAR_WEATHER_COLOR);
        mStatusBarTemperatureColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_WEATHER_COLOR, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mStatusBarTemperatureColor.setSummary(hexColor);
            mStatusBarTemperatureColor.setNewPreviewColor(intColor);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarTemperature) {
            int temperatureStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarTemperature.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, temperatureStyle);
            mStatusBarTemperature.setSummary(
                    mStatusBarTemperature.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarTemperatureFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mStatusBarTemperatureFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_WEATHER_FONT_STYLE, val);
            mStatusBarTemperatureFontStyle.setSummary(mStatusBarTemperatureFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarTemperatureColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_WEATHER_COLOR, intHex);
            return true;
        }
        return false;
    }
}
