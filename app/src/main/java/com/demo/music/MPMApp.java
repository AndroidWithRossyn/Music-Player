package com.demo.music;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MPMApp extends Application {
    public static boolean equalizerBound = false;
    public static ServiceConnection equalizerServiceConnection = new ServiceConnection() { 
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MPMApp.equalizerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            MPMApp.equalizerBound = false;
        }
    };
}
