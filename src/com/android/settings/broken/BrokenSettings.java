
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
	private static final String PREF_MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";

	private SwitchPreference mSelinux;
	private ListPreference mMsob;

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
        mMsob = (ListPreference) findPreference(PREF_MEDIA_SCANNER_ON_BOOT);
        mMsob.setValue(String.valueOf(
                Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0)));
        mMsob.setSummary(mMsob.getEntry());
        mMsob.setOnPreferenceChangeListener(this);
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
        } else if (preference == mMsob) {
			String value = (String) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MEDIA_SCANNER_ON_BOOT,
                    Integer.valueOf(value));

            mMsob.setValue(String.valueOf(value));
            mMsob.setSummary(mMsob.getEntry());
            return true;
        }
        return false;
    }
}
