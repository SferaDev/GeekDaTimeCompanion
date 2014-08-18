package com.sferadev.geekthetime.companion;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.prefs.Preferences;

import static com.sferadev.geekthetime.companion.App.*;

public class Utils {

    public final static UUID PEBBLE_APP_UUID = UUID.fromString("1c977f4c-d7b2-4632-987a-1e1e01834759");
    public final static int KEY_TAG = 7; //Custom tag

    public static void startAppOnPebble() {
        PebbleKit.startAppOnPebble(getContext(), Utils.PEBBLE_APP_UUID);
    }

    public static void sendString(int key, String string) {
        PebbleDictionary data = new PebbleDictionary();
        data.addString(key, string);
        PebbleKit.sendDataToPebble(getContext(), PEBBLE_APP_UUID, data);
    }

    public static void createToast(String string) {
        Toast toast = Toast.makeText(getContext(), string, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void updateBehaviour(String string) {
        switch (string) {
            //TODO
            case "RANDOM_QUOTE":
                sendString(KEY_TAG, "MTFBWY");
                break;
            case "CUSTOM_SENTENCE":
                //TODO
                break;
            case "DATE":
                sendString(KEY_TAG, getDate());
                break;
            case "CARRIER":
                sendString(KEY_TAG, getCarrier());
                break;
            case "PHONE_BATTERY":
                sendString(KEY_TAG, "Phone has" + getBatteryLevel());
                break;
            default:
                sendString(KEY_TAG, string);
                //throw new IllegalArgumentException("Invalid option" + object.toString());
        }
    }

    public static String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c.getTime());
    }

    public static String getCarrier() {
        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    public static String getBatteryLevel() {
        Intent batteryIntent = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int    level   = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int    scale   = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int    percent = (level*100)/scale;
        return String.valueOf(percent) + "%";
    }
}
