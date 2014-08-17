package com.sferadev.geekthetime.companion;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

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
                        Utils.startAppOnPebble();
                        return false;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_custom_tag").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        Utils.sendString(Utils.KEY_TAG, object.toString());
                        preference.setSummary(object.toString());
                        return true;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_show_bt").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        Utils.sendInt(Utils.KEY_SHOW_BT, (Boolean.parseBoolean(object.toString()) ? 1 : 0));
                        return true;
                    }
                }
        );
        getPreferenceScreen().findPreference("key_show_battery").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        Utils.sendInt(Utils.KEY_SHOW_BATTERY, (Boolean.parseBoolean(object.toString()) ? 1 : 0));
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
