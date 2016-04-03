
package com.android.settings.broken;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.provider.Settings.SettingNotFoundException;
import com.android.internal.util.broken.AbstractAsyncSuCMDProcessor;
import com.android.internal.util.broken.CMDProcessor;
import com.android.internal.util.broken.Helpers;
import com.android.internal.util.broken.BrokenUtils;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.Utils;

import java.io.File;
import java.io.IOException;
import java.io.DataOutputStream;

public class BrokenSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
	
	private static final String SELINUX = "selinux";
	
	private SwitchPreference mSelinux;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings);
        
        final ContentResolver resolver = getActivity().getContentResolver();
	    final PreferenceScreen prefScreen = getPreferenceScreen();
	    
	    //SELinux
        mSelinux = (SwitchPreference) findPreference(SELINUX);
        mSelinux.setOnPreferenceChangeListener(this);

        if (CMDProcessor.runShellCommand("getenforce").getStdout().contains("Enforcing")) {
            mSelinux.setChecked(true);
            mSelinux.setSummary(R.string.selinux_enforcing_title);
        } else {
            mSelinux.setChecked(false);
            mSelinux.setSummary(R.string.selinux_permissive_title);
        }
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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mSelinux) {
            if (newValue.toString().equals("true")) {
                CMDProcessor.runSuCommand("setenforce 1");
                mSelinux.setSummary(R.string.selinux_enforcing_title);
            } else if (newValue.toString().equals("false")) {
                CMDProcessor.runSuCommand("setenforce 0");
                mSelinux.setSummary(R.string.selinux_permissive_title);
            }
            return true;
        }
        return false;
    }
}
