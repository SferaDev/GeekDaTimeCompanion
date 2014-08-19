package com.sferadev.geekthetime.companion;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import java.net.MalformedURLException;
import java.net.URL;

import static com.sferadev.geekthetime.companion.Utils.*;

public class GeekActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWeather();
        addPreferencesFromResource(R.xml.pref_main);
        getPreferenceScreen().findPreference("key_open_app").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startAppOnPebble();
                        try {
                            getFile(new URL(downloadURL), "quotes.txt");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
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
                        sendString(KEY_TAG, object.toString());
                        preference.setSummary(object.toString());
                        return true;
                    }
                }
        );
    }

    private void getWeather() {
        /*LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, longitude + "," + latitude, duration);
        toast.show();*/
    }

}
