package com.sferadev.geekthetime.companion;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.sferadev.geekthetime.companion.Utils.*;

public class UpdateService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createToast("Boot Received + Service Started");
        return Service.START_STICKY;
    }

    public void onDestroy() {
        createToast("Service Killed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO
        return null;
    }
}
