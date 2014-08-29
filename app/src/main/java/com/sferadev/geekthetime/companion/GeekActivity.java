package com.sferadev.geekthetime.companion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import static com.sferadev.geekthetime.companion.App.getContext;
import static com.sferadev.geekthetime.companion.Utils.isServiceRunning;
import static com.sferadev.geekthetime.companion.Utils.startAppOnPebble;
import static com.sferadev.geekthetime.companion.Utils.updateBehaviour;

public class GeekActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isServiceRunning(UpdateService.class)) {
            Intent i = new Intent(this, UpdateService.class);
            this.startService(i);
        }

        addPreferencesFromResource(R.xml.pref_main);
        updateSummary();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
    }

    private void updateSummary() {
        findPreference("key_custom_tag").setSummary("Custom Tag: " + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_custom_tag", "May The Force Be With You"));
        findPreference("key_location").setSummary("Weather in: " + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_location", "Unknown"));
        findPreference("key_refresh").setSummary("Refresh Rate: " + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_refresh", "10"));
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        updateSummary();
    }

}
