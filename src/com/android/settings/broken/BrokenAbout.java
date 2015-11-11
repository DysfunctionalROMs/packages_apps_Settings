package com.android.settings.broken;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

import com.android.internal.logging.MetricsLogger;
import java.util.Date;
import com.android.settings.R;
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrokenAbout extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
			
	private static final String LOG_TAG = "BrokenAbout";	
	private static final String KEY_SLIM_OTA = "slimota";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_about);
        
        // Only the owner should see the Updater settings, if it exists
        if (UserHandle.myUserId() == UserHandle.USER_OWNER) {
            removePreferenceIfPackageNotInstalled(findPreference(KEY_SLIM_OTA));
        } else {
            getPreferenceScreen().removePreference(findPreference(KEY_SLIM_OTA));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
    ContentResolver resolver = getActivity().getContentResolver();
	return false;
	}

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    private boolean removePreferenceIfPackageNotInstalled(Preference preference) {
        String intentUri=((PreferenceScreen) preference).getIntent().toUri(1);
        Pattern pattern = Pattern.compile("component=([^/]+)/");
        Matcher matcher = pattern.matcher(intentUri);

        String packageName=matcher.find()?matcher.group(1):null;
        if(packageName != null) {
            try {
                PackageInfo pi = getPackageManager().getPackageInfo(packageName,
                        PackageManager.GET_ACTIVITIES);
                if (!pi.applicationInfo.enabled) {
                    Log.e(LOG_TAG,"package "+packageName+" is disabled, hiding preference.");
                    getPreferenceScreen().removePreference(preference);
                    return true;
                }
            } catch (NameNotFoundException e) {
                Log.e(LOG_TAG,"package "+packageName+" not installed, hiding preference.");
                getPreferenceScreen().removePreference(preference);
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.ABOUT_BROKEN;
    }
}
