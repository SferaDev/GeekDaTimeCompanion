package com.sferadev.geekthetime.companion;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.sferadev.geekthetime.companion.App.getContext;

public class Utils {

    static String mWeather;
    static String mIP;
    static String mGitHub;
    static String mReddit;

    public final static UUID PEBBLE_APP_UUID = UUID.fromString("1c977f4c-d7b2-4632-987a-1e1e01834759");
    public final static int KEY_TAG = 7; //Custom tag

    public static String downloadURL = "https://raw.githubusercontent.com/SferaDev/GeekDaTimeQuotes/master/quotes";
    public static File downloadLocation = new File(Environment.getExternalStorageDirectory() + "/.GeekTheTime/");

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

    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void updateBehaviour(String string) {
        switch (string) {
            //TODO
            case "RANDOM_QUOTE":
                sendString(KEY_TAG, getRandomLine("quotes.txt"));
                break;
            case "CUSTOM_SENTENCE":
                sendString(KEY_TAG, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_custom_tag", "Not found"));
                break;
            case "WEATHER":
                sendString(KEY_TAG, getWeather());
                break;
            case "DATE":
                sendString(KEY_TAG, getDate());
                break;
            case "CARRIER":
                sendString(KEY_TAG, getCarrier());
                break;
            case "PHONE_BATTERY":
                sendString(KEY_TAG, "Phone has " + getBatteryLevel());
                break;
            case "IP":
                sendString(KEY_TAG, "IP: " + getIP());
                break;
            case "GITHUB_STATUS":
                sendString(KEY_TAG, "GitHub: " + getGitHubStatus());
                break;
            case "REDDIT_CONTENT":
                sendString(KEY_TAG, getReddit());
                break;
            default:
                sendString(KEY_TAG, "Coming Soon!");
                //throw new IllegalArgumentException("Invalid option" + object.toString());
        }
    }

    public static void getFile(final URL url, final String fileName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    downloadLocation.mkdirs();
                    File file = new File(downloadLocation, fileName);
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = urlConnection.getInputStream();

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static String getRandomLine(String fileName) {
        String theLine = "None";
        try {
            // Read in the file into a list of strings
            BufferedReader reader = new BufferedReader(new FileReader(downloadLocation + "/" + fileName));
            List<String> lines = new ArrayList<String>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            Random r = new Random();
            return lines.get(r.nextInt(lines.size()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return theLine;
    }

    public static String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c.getTime());
    }

    public static String getWeather() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_location", ""));
                    Random r = new Random();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mURL.openStream()));
                    JSONObject response = new JSONObject(reader.readLine().toString());
                    JSONArray newTopics = response.getJSONArray("weather");
                    JSONObject data = response.getJSONObject("main");
                    double fTemp = 1.8 * (Double.parseDouble(data.getString("temp")) - 273) + 32;
                    double cTemp = Double.parseDouble(data.getString("temp")) - 273;
                    mWeather =  newTopics.getJSONObject(0).getString("main") + " | " + Math.round(fTemp) + "ºF | " + Math.round(cTemp) + "ºC";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mWeather;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getCarrier() {
        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    public static String getBatteryLevel() {
        Intent batteryIntent = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int percent = (level * 100) / scale;
        return String.valueOf(percent) + "%";
    }

    public static String getIP() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL("http://jsonip.com/");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mURL.openStream()));
                    JSONObject response = new JSONObject(reader.readLine().toString());
                    mIP = response.getString("ip");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mIP;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getGitHubStatus() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL("https://status.github.com/api/last-message.json");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mURL.openStream()));
                    JSONObject response = new JSONObject(reader.readLine().toString());
                    mGitHub = response.getString("body");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mGitHub;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getReddit() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL("http://www.reddit.com/new.json?sort=new");
                    Random r = new Random();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mURL.openStream()));
                    JSONObject response = new JSONObject(reader.readLine().toString());
                    JSONObject data = response.getJSONObject("data");
                    JSONArray newTopics = data.getJSONArray("children");
                    mReddit = newTopics.getJSONObject(r.nextInt(newTopics.length())).getJSONObject("data").getString("title");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mReddit;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }
}
