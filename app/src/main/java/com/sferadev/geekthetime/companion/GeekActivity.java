package com.sferadev.geekthetime.companion;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import static com.sferadev.geekthetime.companion.Utils.*;

public class GeekActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isServiceRunning(UpdateService.class)) {
            Intent i= new Intent(this, UpdateService.class);
            this.startService(i);
        }
        addPreferencesFromResource(R.xml.pref_main);
        getPreferenceScreen().findPreference("key_open_app").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startAppOnPebble();
                        return false;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_behaviour").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        updateBehaviour(object.toString());
                        return true;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_custom_tag").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        //sendString(KEY_TAG, object.toString());
                        preference.setSummary(object.toString());
                        return true;
                    }
                }
        );
    }

}
