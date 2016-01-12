package com.android.settings.broken;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.MetricsLogger;

public class GestureSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings_gestures);
    }
    
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
