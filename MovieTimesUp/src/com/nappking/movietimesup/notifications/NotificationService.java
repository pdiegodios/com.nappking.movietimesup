package com.nappking.movietimesup.notifications;

import com.nappking.movietimesup.task.DownloadMoviesTask;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class NotificationService extends Service {
	public static final String NOTIFICATIONS = "Notifications";
	public static final int MOVIEID=1;
    private WakeLock mWakeLock;
    
    /**
     * From the service there will not be communication with other components.
     * It works on silence
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }  
    
    /**
     * To support devices with API level lower than 5
     */
    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }
    
    /**
     * It's called for API levels 5 or bigger. It returns START_NON STICKY to do not
     * restart the service if it was killed for poor resource conditions (memmory/cpu)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }
    
    /**
     * We release our Wake Lock. It ensures than when the service stops (killed for resources,
     * called from stopself, ecc.) the wake lock is released.
     */
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }
    
    /**
     * It's called when onStart/onStartCommand is called from the system. We call to the asynctask
     * to download new movies which return the number of new movies with a notification.
     */
	private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NOTIFICATION");
        mWakeLock.acquire();
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getActiveNetworkInfo().isConnected()) {
            stopSelf();
            return;
        }        
        new DownloadMoviesTask(this.getBaseContext(), true).execute();        
    }    
}