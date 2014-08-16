package com.sferadev.geekthetime.companion;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class GeekActivity extends PreferenceActivity {

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("1c977f4c-d7b2-4632-987a-1e1e01834759");
    public int KEY_QUOTE = 0, KEY_SHOW_QUOTE = 1, KEY_SHOW_BT = 2, KEY_SHOW_BATTERY = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        getPreferenceScreen().findPreference("key_open_app").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
                        return false;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_show_custom_quote").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        //TODO: Enable/Disable Random
                        return true;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_custom_quote").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        sendString(KEY_QUOTE, object.toString());
                        preference.setSummary(object.toString());
                        return false;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_show_quote").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        sendString(KEY_SHOW_QUOTE, object.toString());
                        return true;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_show_bt").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        sendString(KEY_SHOW_BT, object.toString());
                        return true;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_show_battery").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        sendString(KEY_SHOW_BATTERY, object.toString());
                        return true;
                    }
                }
        );
    }

    private void sendString(int key, String string) {
        PebbleDictionary data = new PebbleDictionary();
        data.addString(key, string);
        PebbleKit.sendDataToPebble(getApplicationContext(), PEBBLE_APP_UUID, data);
    }
    private void sendInt(int key, int integer) {
        PebbleDictionary data = new PebbleDictionary();
        data.addInt32(key, integer);
        PebbleKit.sendDataToPebble(getApplicationContext(), PEBBLE_APP_UUID, data);
    }
}
