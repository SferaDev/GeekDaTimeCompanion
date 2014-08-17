package com.sferadev.geekthetime.companion;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpdateService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.createToast("Boot Received + Service Started");
        return Service.START_STICKY;
    }

    public void onDestroy() {
        Utils.createToast("Service Killed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO
        return null;
    }
}
