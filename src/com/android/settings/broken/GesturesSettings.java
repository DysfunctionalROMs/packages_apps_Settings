package com.android.settings.broken;

import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.InstrumentedFragment;

public class GesturesSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.broken_settings_gestures);
    }

    @Override
    protected int getMetricsCategory() {
        return InstrumentedFragment.BROKENGESTURES;
    }
}
