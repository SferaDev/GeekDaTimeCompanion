package com.sferadev.geekthetime.companion;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.sferadev.geekthetime.companion.App.getContext;

public class Utils {

    public final static UUID PEBBLE_APP_UUID = UUID.fromString("1c977f4c-d7b2-4632-987a-1e1e01834759");
    public final static int KEY_TAG = 7;
    public static boolean mStartShown = false;
    public static final String downloadURL = "https://raw.githubusercontent.com/SferaDev/GeekDaTimeQuotes/master/quotes";
    public static final File downloadLocation = new File(Environment.getExternalStorageDirectory() + "/.GeekTheTime/");
    static String mWeather, mIP, mGitHub, mMeme, mXDA, mTC, mAndroidPolice, mPhandroid, mReddit, mBTC;

    public static void startAppOnPebble() {
        PebbleKit.startAppOnPebble(getContext(), Utils.PEBBLE_APP_UUID);
    }

    public static void sendString(int key, String string) {
        try {
            startAppOnPebble();
            createToast("DEBUG: Item Sent");
            PebbleDictionary data = new PebbleDictionary();
            data.addString(key, string);
            PebbleKit.sendDataToPebble(getContext(), PEBBLE_APP_UUID, data);
        } catch (Exception e) {
            createToast("Error: Pebble not found");
        }
    }

    public static void createToast(String string) {
        Toast toast = Toast.makeText(getContext(), string, Toast.LENGTH_LONG);
        toast.show();
    }

    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return false;
            }
        }
        return true;
    }

    public static void updateBehaviour(String string) {
        switch (string) {
            case "RANDOM_QUOTE":
                sendString(KEY_TAG, getRandomLine("quotes.txt"));
                break;
            case "CUSTOM_SENTENCE":
                sendString(KEY_TAG, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_custom_tag", "May The Force Be With Ya"));
                break;
            case "WEATHER":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, getWeather());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
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
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, "IP: " + getIP());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "GITHUB_STATUS":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, "GitHub: " + getGitHubStatus());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "AUTO_MEME":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, getAutoMeme());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "XDA":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, "XDA: " + getXDAFeed());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "TC":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, "TC: " + getTCFeed());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "AP":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, "AP: " + getAndroidPoliceFeed());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "PHANDROID":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, "Phandroid: " + getPhandroidFeed());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "REDDIT_CONTENT":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, getReddit());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            case "BTC":
                if (isNetworkAvailable()) {
                    sendString(KEY_TAG, getBTC());
                } else {
                    createToast("DEBUG: Error on " + string);
                }
                break;
            default:
                createToast("DEBUG: Error on " + string);
                throw new IllegalArgumentException("Invalid option" + string);
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
                    int bufferLength;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();
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
            BufferedReader reader = new BufferedReader(new FileReader(downloadLocation + "/" + fileName));
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            Random r = new Random();
            return lines.get(r.nextInt(lines.size()));
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
                    URL mURL = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_location", "Mountain View"));
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mURL.openStream()));
                    JSONObject response = new JSONObject(reader.readLine().toString());
                    JSONArray newTopics = response.getJSONArray("weather");
                    JSONObject data = response.getJSONObject("main");
                    double fTemp = 1.8 * (Double.parseDouble(data.getString("temp")) - 273) + 32;
                    double cTemp = Double.parseDouble(data.getString("temp")) - 273;
                    mWeather = newTopics.getJSONObject(0).getString("main") + " | " + Math.round(fTemp) + "ºF | " + Math.round(cTemp) + "ºC";
                } catch (IOException | JSONException e) {
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
        int level = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) : 0;
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
                    JSONObject response = new JSONObject(reader.readLine());
                    mIP = response.getString("ip");
                } catch (IOException | JSONException e) {
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
                    JSONObject response = new JSONObject(reader.readLine());
                    mGitHub = response.getString("body");
                } catch (IOException | JSONException e) {
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

    public static String getAutoMeme() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL("http://api.automeme.net/text.json?lines=1");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mURL.openStream()));
                    JSONArray response = new JSONArray(reader.readLine());
                    mMeme = response.getString(0);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mMeme;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getTCFeed() {
        final RssParser rss = new RssParser();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RssFeed feed = rss.load("http://feeds.feedburner.com/TechCrunch/");
                    Random r = new Random();
                    List<RssItemBean> items = feed.getItems();
                    RssItemBean item = items.get(r.nextInt(items.size()));
                    mTC = item.getTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mTC;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getXDAFeed() {
        final RssParser rss = new RssParser();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RssFeed feed = rss.load("http://www.xda-developers.com/feed/");
                    Random r = new Random();
                    List<RssItemBean> items = feed.getItems();
                    RssItemBean item = items.get(r.nextInt(items.size()));
                    mXDA = item.getTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mXDA;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getAndroidPoliceFeed() {
        final RssParser rss = new RssParser();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RssFeed feed = rss.load("http://feeds.feedburner.com/AndroidPolice?format=xml");
                    Random r = new Random();
                    List<RssItemBean> items = feed.getItems();
                    RssItemBean item = items.get(r.nextInt(items.size()));
                    mAndroidPolice = item.getTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mAndroidPolice;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String getPhandroidFeed() {
        final RssParser rss = new RssParser();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RssFeed feed = rss.load("http://phandroid.com/feed/");
                    Random r = new Random();
                    List<RssItemBean> items = feed.getItems();
                    RssItemBean item = items.get(r.nextInt(items.size()));
                    mPhandroid = item.getTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return mPhandroid;
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
                    JSONObject response = new JSONObject(reader.readLine());
                    JSONObject data = response.getJSONObject("data");
                    JSONArray newTopics = data.getJSONArray("children");
                    mReddit = newTopics.getJSONObject(r.nextInt(newTopics.length())).getJSONObject("data").getString("title");
                } catch (IOException | JSONException e) {
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

    public static String getBTC() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL("http://api.bitcoincharts.com/v1/weighted_prices.json");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mURL.openStream()));
                    JSONObject response = new JSONObject(reader.readLine());
                    mBTC = "1 BTC | " + response.getJSONObject("USD").getString("24h") + "USD | " + response.getJSONObject("EUR").getString("24h") + "EUR | " + response.getJSONObject("GBP").getString("24h") + "GBP";
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
            return mBTC;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}