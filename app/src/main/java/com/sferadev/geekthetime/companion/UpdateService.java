package com.sferadev.geekthetime.companion;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sferadev.geekthetime.companion.App.getContext;
import static com.sferadev.geekthetime.companion.Utils.createToast;
import static com.sferadev.geekthetime.companion.Utils.downloadURL;
import static com.sferadev.geekthetime.companion.Utils.getFile;
import static com.sferadev.geekthetime.companion.Utils.updateBehaviour;

public class UpdateService extends Service {

    ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doService();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        doService();
                    }
                }, 0, 10, TimeUnit.MINUTES);
        return Service.START_STICKY;
    }

    public void onDestroy() {
        createToast("Service Killed");
        scheduler.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO
        return null;
    }

    private void doService() {
        createToast("Service Started");
        try {
            getFile(new URL(downloadURL), "quotes.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        updateBehaviour(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_behaviour", ""));
    }
}
