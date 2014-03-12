package com.nappking.movietimesup;

import java.util.ArrayList;

import com.nappking.movietimesup.R;
import com.nappking.movietimesup.notifications.NotificationService;
import com.nappking.movietimesup.task.DownloadMoviesTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Initial Splash Screen to present the app and to take the chance to download movies if
 * there are new movies available.
 * 
 * @author Nappking - pdiego
 */
public class SplashActivity extends Activity{
	ImageView iEllipsis;
	AnimationDrawable animEllipsis;
	public static int mSecondsExtra;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        initiate();     	
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new DownloadMoviesTask(this).execute();
        checkNotifications();
    }
    
    public static void setSecondsExtra(int seconds){
    	mSecondsExtra = seconds;
    }

	@Override
    protected void onPause() {
    	super.onPause();
    	if(animEllipsis!=null && animEllipsis.isRunning()){
    		animEllipsis.stop();
    	}
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	if(animEllipsis!=null && !animEllipsis.isRunning()){
    		animEllipsis.start();
    	}
    }
    
    private void initiate(){
        iEllipsis = (ImageView) findViewById(R.id.loading);
        animEllipsis = (AnimationDrawable) iEllipsis.getDrawable();
        animEllipsis.start();
    }
    
    private void checkNotifications(){
    	/**
    	 * Ojo!! hay que recuperar de preferencias si tiene las notificaciones activadas y el período
    	 */
		//SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);		
		//Recuperamos intervalo de consulta para las notificaciones
		int interval = 24; //min
		
		//Servicio de Alarma
		AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
		
		//Clase Encargada de llamar a las distintas notificaciones
	    Intent i = new Intent(getBaseContext(), NotificationService.class);
	    
	    //Agregamos las distintas notificaciones
	    Bundle myBundle=new Bundle();
	    ArrayList<Integer> notifications = new ArrayList<Integer>();	    
	    notifications.add(NotificationService.MOVIEID);
	    myBundle.putSerializable(NotificationService.NOTIFICATIONS,notifications);
	    i.putExtras(myBundle);
	    PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pi);
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + interval*60*60*1000,
				interval*60*60*1000, pi);
	}
}
