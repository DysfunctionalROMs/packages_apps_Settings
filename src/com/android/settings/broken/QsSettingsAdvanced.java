package com.android.settings.broken;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;

import com.android.settings.broken.widget.SeekBarPreferenceCham;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class QsSettingsAdvanced extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_QS_TRANSPARENT_SHADE = "qs_transparent_shade";
    private static final String PREF_QS_TRANSPARENT_HEADER = "qs_transparent_header";
    private static final String PREF_QS_PANEL_LOGO = "qs_panel_logo";
    private static final String PREF_QS_PANEL_LOGO_COLOR = "qs_panel_logo_color";
    private static final String PREF_QS_PANEL_LOGO_ALPHA = "qs_panel_logo_alpha";
    private static final String CUSTOM_HEADER_IMAGE_SHADOW = "status_bar_custom_header_shadow";
    private static final String CUSTOM_HEADER_TEXT_SHADOW = "status_bar_custom_header_text_shadow";
    private static final String CUSTOM_HEADER_TEXT_SHADOW_COLOR = "status_bar_custom_header_text_shadow_color";
    private static final String PREF_NOTIFICATION_ALPHA = "notification_alpha";
    private static final String PREF_QS_STROKE = "qs_stroke";
    private static final String PREF_QS_STROKE_COLOR = "qs_stroke_color";
    private static final String PREF_QS_STROKE_THICKNESS = "qs_stroke_thickness";
    private static final String PREF_QS_CORNER_RADIUS = "qs_corner_radius";

    private SeekBarPreferenceCham mQSShadeAlpha;
    private SeekBarPreferenceCham mQSHeaderAlpha;
    private ListPreference mQSPanelLogo;
    private ColorPickerPreference mQSPanelLogoColor;
    private SeekBarPreferenceCham mQSPanelLogoAlpha;
    private SeekBarPreferenceCham mHeaderShadow;
    private SeekBarPreferenceCham mTextShadow;
    private ColorPickerPreference mTShadowColor;
    private SeekBarPreferenceCham mNotificationsAlpha;
    private ListPreference mQSStroke;
    private ColorPickerPreference mQSStrokeColor;
    private SeekBarPreferenceCham mQSStrokeThickness;
    private SeekBarPreferenceCham mQSCornerRadius;

    static final int DEFAULT_QS_PANEL_LOGO_COLOR = 0x09FF00;
    static final int DEFAULT_HEADER_SHADOW_COLOR = 0xFF000000;
    static final int DEFAULT_QS_STROKE_COLOR = 0xFF202020;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.qs_settings_advanced);
        final ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();

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

        // QS panel Broken logo
        mQSPanelLogo =
                 (ListPreference) findPreference(PREF_QS_PANEL_LOGO);
        int qSPanelLogo = Settings.System.getIntForUser(getContentResolver(),
                        Settings.System.QS_PANEL_LOGO, 0,
                        UserHandle.USER_CURRENT);
        mQSPanelLogo.setValue(String.valueOf(qSPanelLogo));
        mQSPanelLogo.setSummary(mQSPanelLogo.getEntry());
        mQSPanelLogo.setOnPreferenceChangeListener(this);

        // QS panel Broken logo color
        mQSPanelLogoColor =
                (ColorPickerPreference) findPreference(PREF_QS_PANEL_LOGO_COLOR);
        mQSPanelLogoColor.setOnPreferenceChangeListener(this);
        int qSPanelLogoColor = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_PANEL_LOGO_COLOR, DEFAULT_QS_PANEL_LOGO_COLOR);
        String qSHexLogoColor = String.format("#%08x", (0x09FF00 & qSPanelLogoColor));
        mQSPanelLogoColor.setSummary(qSHexLogoColor);
        mQSPanelLogoColor.setNewPreviewColor(qSPanelLogoColor);

        // QS panel Broken logo alpha
        mQSPanelLogoAlpha =
                (SeekBarPreferenceCham) findPreference(PREF_QS_PANEL_LOGO_ALPHA);
        int qSPanelLogoAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_PANEL_LOGO_ALPHA, 51);
        mQSPanelLogoAlpha.setValue(qSPanelLogoAlpha / 1);
        mQSPanelLogoAlpha.setOnPreferenceChangeListener(this);

        // Status Bar header text shadow
        mTextShadow = (SeekBarPreferenceCham) findPreference(CUSTOM_HEADER_TEXT_SHADOW);
        final float textShadow = Settings.System.getFloat(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW, 0);
        mTextShadow.setValue((int)(textShadow));
        mTextShadow.setOnPreferenceChangeListener(this);

        //Status Bar header text shadow color
        mTShadowColor =
                (ColorPickerPreference) findPreference(CUSTOM_HEADER_TEXT_SHADOW_COLOR);
        mTShadowColor.setOnPreferenceChangeListener(this);
        int shadowColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW_COLOR, DEFAULT_HEADER_SHADOW_COLOR);
        String HexColor = String.format("#%08x", (0x000000 & shadowColor));
        mTShadowColor.setSummary(HexColor);
        mTShadowColor.setNewPreviewColor(shadowColor);

        // Status Bar header shadow on custom header images
        mHeaderShadow = (SeekBarPreferenceCham) findPreference(CUSTOM_HEADER_IMAGE_SHADOW);
        final int headerShadow = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, 0);
        mHeaderShadow.setValue((int)((headerShadow / 255) * 100));
        mHeaderShadow.setOnPreferenceChangeListener(this);

        // Notifications alpha
        mNotificationsAlpha = (SeekBarPreferenceCham) findPreference(PREF_NOTIFICATION_ALPHA);
        int notificationsAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_ALPHA, 255);
        mNotificationsAlpha.setValue(notificationsAlpha / 1);
        mNotificationsAlpha.setOnPreferenceChangeListener(this);

        // QS stroke
        mQSStroke =
                (ListPreference) findPreference(PREF_QS_STROKE);
        int qSStroke = Settings.System.getIntForUser(getContentResolver(),
                       Settings.System.QS_STROKE, 1,
                       UserHandle.USER_CURRENT);
        mQSStroke.setValue(String.valueOf(qSStroke));
        mQSStroke.setSummary(mQSStroke.getEntry());
        mQSStroke.setOnPreferenceChangeListener(this);

        // QS stroke color
        mQSStrokeColor =
                (ColorPickerPreference) findPreference(PREF_QS_STROKE_COLOR);
        mQSStrokeColor.setOnPreferenceChangeListener(this);
        int qSIntColor = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_STROKE_COLOR, DEFAULT_QS_STROKE_COLOR);
        String qSHexColor = String.format("#%08x", (0xFF202020 & qSIntColor));
        mQSStrokeColor.setSummary(qSHexColor);
        mQSStrokeColor.setNewPreviewColor(qSIntColor);

        // QS stroke thickness
        mQSStrokeThickness =
                (SeekBarPreferenceCham) findPreference(PREF_QS_STROKE_THICKNESS);
        int qSStrokeThickness = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_STROKE_THICKNESS, 4);
        mQSStrokeThickness.setValue(qSStrokeThickness / 1);
        mQSStrokeThickness.setOnPreferenceChangeListener(this);

        // QS corner radius
        mQSCornerRadius =
                (SeekBarPreferenceCham) findPreference(PREF_QS_CORNER_RADIUS);
        int qSCornerRadius = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_CORNER_RADIUS, 0);
        mQSCornerRadius.setValue(qSCornerRadius / 1);
        mQSCornerRadius.setOnPreferenceChangeListener(this);

        QSSettingsDisabler(qSStroke);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mQSShadeAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_TRANSPARENT_SHADE, alpha * 1);
            return true;
        } else if (preference == mQSHeaderAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_TRANSPARENT_HEADER, alpha * 1);
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
        } else if (preference == mTextShadow) {
            float textShadow = (Integer) objValue;
            float realHeaderValue = (float) ((double) textShadow);
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW, realHeaderValue);
            return true;
        } else if (preference == mTShadowColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_TEXT_SHADOW_COLOR, intHex);
            return true;
        } else if (preference == mHeaderShadow) {
            Integer headerShadow = (Integer) objValue;
            int realHeaderValue = (int) (((double) headerShadow / 100) * 255);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, realHeaderValue);
            return true;
        } else if (preference == mNotificationsAlpha) {
            int alpha = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_ALPHA, alpha * 1);
            return true;
        } else if (preference == mQSStroke) {
            int qSStroke = Integer.parseInt((String) objValue);
            int index = mQSStroke.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.
                    QS_STROKE, qSStroke, UserHandle.USER_CURRENT);
            mQSStroke.setSummary(mQSStroke.getEntries()[index]);
            QSSettingsDisabler(qSStroke);
            return true;
        } else if (preference == mQSStrokeColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_STROKE_COLOR, intHex);
            return true;
        } else if (preference == mQSStrokeThickness) {
            int val = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_STROKE_THICKNESS, val * 1);
            return true;
        } else if (preference == mQSCornerRadius) {
            int val = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_CORNER_RADIUS, val * 1);
            return true;
        }
        return false;
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

    private void QSSettingsDisabler(int qSStroke) {
        if (qSStroke == 0) {
            mQSStrokeColor.setEnabled(false);
            mQSStrokeThickness.setEnabled(false);
        } else if (qSStroke == 1) {
            mQSStrokeColor.setEnabled(false);
            mQSStrokeThickness.setEnabled(true);
        } else {
            mQSStrokeColor.setEnabled(true);
            mQSStrokeThickness.setEnabled(true);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
