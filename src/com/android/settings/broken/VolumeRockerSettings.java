package com.android.settings.broken;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.MetricsLogger;

import java.util.Locale;

public class VolumeRockerSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String VOLUME_ROCKER_WAKE = "volume_rocker_wake";
    private static final String PREF_VOLBTN_SWAP = "button_swap_volume_buttons";
    private static final String KEY_VOLBTN_MUSIC_CTRL = "volbtn_music_controls";
    private static final String KEY_VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";
		
    private SwitchPreference mVolumeRockerWake;
    private SwitchPreference mVolBtnSwap;
    private SwitchPreference mVolBtnMusicCtrl;
    private ListPreference mVolumeKeyCursorControl;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings_volume);

        // volume rocker wake
        mVolumeRockerWake = (SwitchPreference) findPreference(VOLUME_ROCKER_WAKE);
        mVolumeRockerWake.setOnPreferenceChangeListener(this);
        int volumeRockerWake = Settings.System.getInt(getContentResolver(),
                VOLUME_ROCKER_WAKE, 0);
        mVolumeRockerWake.setChecked(volumeRockerWake != 0);
        
        // volume swap
        mVolBtnSwap = (SwitchPreference) findPreference(PREF_VOLBTN_SWAP);
        mVolBtnSwap.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SWAP_VOLUME_BUTTONS, 0) == 1);
        mVolBtnSwap.setOnPreferenceChangeListener(this);

        // volume music control
        mVolBtnMusicCtrl = (SwitchPreference) findPreference(KEY_VOLBTN_MUSIC_CTRL);
        mVolBtnMusicCtrl.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_MUSIC_CONTROLS, 1) != 0);
        mVolBtnMusicCtrl.setOnPreferenceChangeListener(this);
		try {
        if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.VOLUME_ROCKER_WAKE) == 1) {
        mVolBtnMusicCtrl.setEnabled(false);
		mVolBtnMusicCtrl.setSummary(R.string.volume_button_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }
        
        mVolumeKeyCursorControl = (ListPreference) findPreference(KEY_VOLUME_KEY_CURSOR_CONTROL);
        mVolumeKeyCursorControl.setOnPreferenceChangeListener(this);
        int cursorControlAction = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0);
        mVolumeKeyCursorControl.setValue(String.valueOf(cursorControlAction));
        updateVolumeKeyCursorControl(cursorControlAction);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mVolumeRockerWake) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(), VOLUME_ROCKER_WAKE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVolBtnSwap) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SWAP_VOLUME_BUTTONS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVolBtnMusicCtrl) {
			boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_MUSIC_CONTROLS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVolumeKeyCursorControl) {
            int cursorControlAction = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL,
                    cursorControlAction);
            updateVolumeKeyCursorControl(cursorControlAction);
            return true;
        }
        return false;
    }
    
    private void updateVolumeKeyCursorControl(int value) {
		Resources res = getResources();

        if (value == 0) {
            // cursor control deactivated
            mVolumeKeyCursorControl.setSummary(res.getString(R.string.volbtn_cursor_control_off));
        } else {
            Locale l = Locale.getDefault();
            boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
            String direction = res.getString(value == 2
                    ? (isRtl ? R.string.volbtn_cursor_control_on : R.string.volbtn_cursor_control_on_reverse)
                    : (isRtl ? R.string.volbtn_cursor_control_on_reverse : R.string.volbtn_cursor_control_on));
            mVolumeKeyCursorControl.setSummary(res.getString(R.string.volbtn_cursor_control_summary, direction));
        }
	}
		
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
