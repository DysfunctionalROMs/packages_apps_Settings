
package com.android.settings.broken;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class BrokenSettings extends SettingsPreferenceFragment {
	private static final String KEY_LOCK_CLOCK =
            "lock_clock";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings);

        // Remove the lock clock preference if its not installed
        if (!isPackageInstalled("com.cyanogenmod.lockclock")) {
            removePreference(KEY_LOCK_CLOCK);
        }

    }

    private boolean isPackageInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
           pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
           installed = true;
        } catch (PackageManager.NameNotFoundException e) {
           installed = false;
        }
        return installed;
    }
}
