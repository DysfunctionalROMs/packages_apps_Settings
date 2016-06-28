/*
 * Copyright (C) 2015 The CyanogenMod Project
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

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.broken.qs.QSTiles;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.broken.widget.SeekBarPreferenceCham;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class QsSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

	private static final String PREF_BLOCK_ON_SECURE_KEYGUARD = "block_on_secure_keyguard";
    private static final String PREF_QS_TRANSPARENT_SHADE = "qs_transparent_shade";
    private static final String PREF_QS_TRANSPARENT_HEADER = "qs_transparent_header";
    private static final String PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String PREF_QS_PANEL_LOGO = "qs_panel_logo";
    private static final String PREF_QS_PANEL_LOGO_COLOR = "qs_panel_logo_color";
    private static final String PREF_QS_PANEL_LOGO_ALPHA = "qs_panel_logo_alpha";

    private Preference mQSTiles;
    private SwitchPreference mBlockOnSecureKeyguard;
    private SeekBarPreferenceCham mQSShadeAlpha;
    private SeekBarPreferenceCham mQSHeaderAlpha;
    private ListPreference mTileAnimationStyle;
    private ListPreference mTileAnimationDuration;
    private ListPreference mQSPanelLogo;
    private ColorPickerPreference mQSPanelLogoColor;
    private SeekBarPreferenceCham mQSPanelLogoAlpha;

    static final int DEFAULT_QS_PANEL_LOGO_COLOR = 0x09FF00;

    private static final int MY_USER_ID = UserHandle.myUserId();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.qs_settings);
        final ContentResolver resolver = getActivity().getContentResolver();
        final LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());

        PreferenceScreen prefSet = getPreferenceScreen();
        mQSTiles = findPreference("qs_order");

        mBlockOnSecureKeyguard = (SwitchPreference) findPreference(PREF_BLOCK_ON_SECURE_KEYGUARD);
        if (lockPatternUtils.isSecure(MY_USER_ID)) {
            mBlockOnSecureKeyguard.setChecked(Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD, 1) == 1);
            mBlockOnSecureKeyguard.setOnPreferenceChangeListener(this);
        } else if (mBlockOnSecureKeyguard != null) {
            prefSet.removePreference(mBlockOnSecureKeyguard);
        }

        // QS shade alpha
        mQSShadeAlpha =
                (SeekBarPreferenceCham) prefSet.findPreference(PREF_QS_TRANSPARENT_SHADE);
        int qSShadeAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_TRANSPARENT_SHADE, 255);
        mQSShadeAlpha.setValue(qSShadeAlpha / 1);
        mQSShadeAlpha.setOnPreferenceChangeListener(this);

        // QS header alpha
        mQSHeaderAlpha =
                (SeekBarPreferenceCham) prefSet.findPreference(PREF_QS_TRANSPARENT_HEADER);
        int qSHeaderAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_TRANSPARENT_HEADER, 255);
        mQSHeaderAlpha.setValue(qSHeaderAlpha / 1);
        mQSHeaderAlpha.setOnPreferenceChangeListener(this);

        mTileAnimationStyle = (ListPreference) findPreference(PREF_TILE_ANIM_STYLE);
        int tileAnimationStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.ANIM_TILE_STYLE, 0,
                UserHandle.USER_CURRENT);
        mTileAnimationStyle.setValue(String.valueOf(tileAnimationStyle));
        updateTileAnimationStyleSummary(tileAnimationStyle);
        updateAnimTileDuration(tileAnimationStyle);
        mTileAnimationStyle.setOnPreferenceChangeListener(this);

        mTileAnimationDuration = (ListPreference) findPreference(PREF_TILE_ANIM_DURATION);
        int tileAnimationDuration = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.ANIM_TILE_DURATION, 2000,
                UserHandle.USER_CURRENT);
        mTileAnimationDuration.setValue(String.valueOf(tileAnimationDuration));
        updateTileAnimationDurationSummary(tileAnimationDuration);
        mTileAnimationDuration.setOnPreferenceChangeListener(this);

        // QS panel Broken logo
        mQSPanelLogo =
                 (ListPreference) findPreference(PREF_QS_PANEL_LOGO);
        int qSPanelLogo = Settings.System.getIntForUser(resolver,
                        Settings.System.QS_PANEL_LOGO, 0,
                        UserHandle.USER_CURRENT);
        mQSPanelLogo.setValue(String.valueOf(qSPanelLogo));
        mQSPanelLogo.setSummary(mQSPanelLogo.getEntry());
        mQSPanelLogo.setOnPreferenceChangeListener(this);

        // QS panel Broken logo color
        mQSPanelLogoColor =
                (ColorPickerPreference) findPreference(PREF_QS_PANEL_LOGO_COLOR);
        mQSPanelLogoColor.setOnPreferenceChangeListener(this);
        int qSPanelLogoColor = Settings.System.getInt(resolver,
                Settings.System.QS_PANEL_LOGO_COLOR, DEFAULT_QS_PANEL_LOGO_COLOR);
        String qSHexLogoColor = String.format("#%08x", (0x09FF00 & qSPanelLogoColor));
        mQSPanelLogoColor.setSummary(qSHexLogoColor);
        mQSPanelLogoColor.setNewPreviewColor(qSPanelLogoColor);

        // QS panel Broken logo alpha
        mQSPanelLogoAlpha =
                (SeekBarPreferenceCham) findPreference(PREF_QS_PANEL_LOGO_ALPHA);
        int qSPanelLogoAlpha = Settings.System.getInt(resolver,
                Settings.System.QS_PANEL_LOGO_ALPHA, 51);
        mQSPanelLogoAlpha.setValue(qSPanelLogoAlpha / 1);
        mQSPanelLogoAlpha.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        int qsTileCount = QSTiles.determineTileCount(getActivity());
        mQSTiles.setSummary(getResources().getQuantityString(R.plurals.qs_tiles_summary,
                    qsTileCount, qsTileCount));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mBlockOnSecureKeyguard) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD,
                    (Boolean) objValue ? 1 : 0);
            return true;
        } else if (preference == mQSShadeAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_TRANSPARENT_SHADE, alpha * 1);
            return true;
        } else if (preference == mQSHeaderAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_TRANSPARENT_HEADER, alpha * 1);
            return true;
        } else if (preference == mTileAnimationStyle) {
            int tileAnimationStyle = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_STYLE,
                    tileAnimationStyle, UserHandle.USER_CURRENT);
            updateTileAnimationStyleSummary(tileAnimationStyle);
            updateAnimTileDuration(tileAnimationStyle);
            return true;
        } else if (preference == mTileAnimationDuration) {
            int tileAnimationDuration = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_DURATION,
                    tileAnimationDuration, UserHandle.USER_CURRENT);
            updateTileAnimationDurationSummary(tileAnimationDuration);
            return true;
        } else if (preference == mQSPanelLogo) {
            int qSPanelLogo = Integer.parseInt((String) objValue);
            int index = mQSPanelLogo.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.
                    QS_PANEL_LOGO, qSPanelLogo, UserHandle.USER_CURRENT);
            mQSPanelLogo.setSummary(mQSPanelLogo.getEntries()[index]);
            QSPanelLogoSettingsDisabler(qSPanelLogo);
            return true;
        } else if (preference == mQSPanelLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_PANEL_LOGO_COLOR, intHex);
            return true;
        } else if (preference == mQSPanelLogoAlpha) {
            int val = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_PANEL_LOGO_ALPHA, val * 1);
            return true;
        }
        return false;
    }

    private void updateTileAnimationStyleSummary(int tileAnimationStyle) {
        String prefix = (String) mTileAnimationStyle.getEntries()[mTileAnimationStyle.findIndexOfValue(String
                .valueOf(tileAnimationStyle))];
        mTileAnimationStyle.setSummary(getResources().getString(R.string.qs_set_animation_style, prefix));
    }

    private void updateTileAnimationDurationSummary(int tileAnimationDuration) {
        String prefix = (String) mTileAnimationDuration.getEntries()[mTileAnimationDuration.findIndexOfValue(String
                .valueOf(tileAnimationDuration))];
        mTileAnimationDuration.setSummary(getResources().getString(R.string.qs_set_animation_duration, prefix));
    }

    private void updateAnimTileDuration(int tileAnimationStyle) {
        if (mTileAnimationDuration != null) {
            if (tileAnimationStyle == 0) {
                mTileAnimationDuration.setSelectable(false);
            } else {
                mTileAnimationDuration.setSelectable(true);
            }
        }
    }

    private void QSPanelLogoSettingsDisabler(int qSPanelLogo) {
        if (qSPanelLogo == 0) {
            mQSPanelLogoColor.setEnabled(false);
            mQSPanelLogoAlpha.setEnabled(false);
        } else if (qSPanelLogo == 1) {
            mQSPanelLogoColor.setEnabled(false);
            mQSPanelLogoAlpha.setEnabled(true);
        } else {
            mQSPanelLogoColor.setEnabled(true);
            mQSPanelLogoAlpha.setEnabled(true);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
