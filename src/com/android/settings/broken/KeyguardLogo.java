/*
 * Copyright (C) 2016 Cyanide Android (rogersb11)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.broken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class KeyguardLogo extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEYGUARD_LOGO_SHOW = "keyguard_logo_show";
    private static final String KEYGUARD_LOGO_COLOR = "keyguard_logo_color";
    private static final String KEYGUARD_LOGO_CUSTOM = "keyguard_logo_custom";
    private static final String KEYGUARD_CUSTOM_RESET = "keyguard_custom_reset";

    public final static int CUSTOM_IMAGE = 1;

    private static final int DEFAULT_COLOR = 0xffffffff;
    private static final int BROKEN_GREEN = 0xBDF648;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mShowLogo;
    private ColorPickerPreference mLogoColor;
    private Preference mCustomLogo;
    private Preference mCustomLogoReset;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.keyguard_logo);
        mResolver = getActivity().getContentResolver();

        boolean showLogo = Settings.System.getInt(mResolver,
                Settings.System.KEYGUARD_LOGO_SHOW, 0) == 1;

        mShowLogo =
                (SwitchPreference) findPreference(KEYGUARD_LOGO_SHOW);
        mShowLogo.setChecked(showLogo);
        mShowLogo.setOnPreferenceChangeListener(this);

        
        if (showLogo) {
            mLogoColor =
                (ColorPickerPreference) findPreference(KEYGUARD_LOGO_COLOR);
            int intColor = Settings.System.getInt(mResolver,
                    Settings.System.KEYGUARD_LOGO_COLOR, 0xffffffff);
            mLogoColor.setNewPreviewColor(intColor);
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mLogoColor.setSummary(hexColor);
            mLogoColor.setOnPreferenceChangeListener(this);

            mCustomLogo = findPreference(KEYGUARD_LOGO_CUSTOM);
            mCustomLogoReset = findPreference(KEYGUARD_CUSTOM_RESET);
        } else {
            removePreference("logo_cat_colors");
            removePreference(KEYGUARD_LOGO_CUSTOM);
            removePreference(KEYGUARD_CUSTOM_RESET);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_reset_button) // use the reset settings icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }
    
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(
                    mResolver, Settings.System.KEYGUARD_LOGO_SHOW,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.KEYGUARD_LOGO_COLOR, intHex);
            preference.setSummary(hex);
            return true; 
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCustomLogo) {
            setCustomLogo();
            return true;
        } else if (preference == mCustomLogoReset) {
            Settings.System.putString(mResolver,
            mCustomLogo.getKey(), "");
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CUSTOM_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Settings.System.putString(mResolver, KEYGUARD_LOGO_CUSTOM, selectedImage.toString());
        }
    }

    private void setCustomLogo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CUSTOM_IMAGE);
    }
    
    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        KeyguardLogo getOwner() {
            return (KeyguardLogo) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.KEYGUARD_LOGO_SHOW, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.KEYGUARD_LOGO_COLOR,
                                    DEFAULT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.KEYGUARD_LOGO_SHOW, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.KEYGUARD_LOGO_COLOR,
                                    BROKEN_GREEN);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }
}
