package com.sferadev.geekthetime.companion;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class GeekActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        getPreferenceScreen().findPreference("key_test").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        preference.setSummary(R.string.app_name);
                        return false;
                    }
                }
        );
    }
}
