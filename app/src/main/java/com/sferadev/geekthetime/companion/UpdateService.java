package com.sferadev.geekthetime.companion;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;

import static com.sferadev.geekthetime.companion.App.*;
import static com.sferadev.geekthetime.companion.Utils.*;

public class UpdateService extends Service {

    final Handler handler = new Handler();
    final static int updateTime = 1000 * 60 * 10;
    Runnable r;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateTag();
        updateFile();
        r = new Runnable() {
            @Override
            public void run() {
                updateTag();
                handler.postDelayed(this, updateTime);
            }
        };
        handler.postDelayed(r, updateTime);
        return Service.START_STICKY;
    }

    public void onDestroy() {
        handler.removeCallbacks(r);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateFile() {
        try {
            getFile(new URL(downloadURL), "quotes.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void updateTag() {
        updateBehaviour(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_behaviour", "May The Force Be With You All"));
    }
}
