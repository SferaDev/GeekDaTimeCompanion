package com.sferadev.geekthetime.companion;

import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class Utils {

    public final static UUID PEBBLE_APP_UUID = UUID.fromString("1c977f4c-d7b2-4632-987a-1e1e01834759");
    public final static int KEY_TAG = 0; //Custom tag
    public final static int KEY_WIP = 1; //Show tag
    public final static int KEY_SHOW_BT = 2; //Show bt
    public final static int KEY_SHOW_BATTERY = 3; //Show battery

    public static void startAppOnPebble() {
        PebbleKit.startAppOnPebble(App.getContext(), Utils.PEBBLE_APP_UUID);
    }

    public static void sendString(int key, String string) {
        PebbleDictionary data = new PebbleDictionary();
        data.addString(key, string);
        PebbleKit.sendDataToPebble(App.getContext(), PEBBLE_APP_UUID, data);
    }

    public static void sendInt(int key, int integer) {
        PebbleDictionary data = new PebbleDictionary();
        data.addInt32(key, integer);
        PebbleKit.sendDataToPebble(App.getContext(), PEBBLE_APP_UUID, data);
    }

    public static void createToast(String string) {
        Toast toast = Toast.makeText(App.getContext(), string, Toast.LENGTH_LONG);
        toast.show();
    }
}
