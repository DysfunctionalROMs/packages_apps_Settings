package com.android.settings.broken;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.MetricsLogger;

public class VolumeRockerSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String VOLUME_ROCKER_WAKE = "volume_rocker_wake";
    private static final String KEY_VOLBTN_MUSIC_CTRL = "volbtn_music_controls";
    private static final String PREF_VOLBTN_SWAP = "button_swap_volume_buttons";

    private SwitchPreference mVolumeRockerWake;
    private SwitchPreference mVolBtnMusicCtrl;
    private SwitchPreference mVolBtnSwap;
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
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mVolumeRockerWake) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(), VOLUME_ROCKER_WAKE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVolBtnMusicCtrl) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_MUSIC_CONTROLS,
                    (Boolean) objValue ? 1 : 0);
            return true;
        } else if (preference == mVolBtnSwap) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SWAP_VOLUME_BUTTONS,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
    
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.BROKEN_VOLUME;
    }
}
