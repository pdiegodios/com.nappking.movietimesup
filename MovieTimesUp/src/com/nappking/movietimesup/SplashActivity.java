package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.facebook.android.friendsmash.R;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.notifications.NotificationService;
import com.nappking.movietimesup.task.DownloadMoviesTask;
import com.nappking.movietimesup.task.LoadUserDataTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Initial Splash Screen to present the app and to take the chance to download movies if
 * there are new movies available.
 * 
 * @author Nappking - pdiego
 */
public class SplashActivity extends DBActivity{
	ImageView iEllipsis;
	AnimationDrawable animEllipsis;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        // Hide the notification barinitiate();
        initiate();     	
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
     	try {
			Dao<User,Integer> daoUser = getHelper().getUserDAO();
			User user = daoUser.queryForId(1);
			if(user!=null){
				//If user exists we have to check if there are newer data in WS or
				//update data in WS if necessary
				new LoadUserDataTask(this,user).execute();
				
				/**
				 * We update number of days in row using the game to get points for fidelity
				 */
				user = daoUser.queryForId(1);
				Calendar today = GregorianCalendar.getInstance();
				Calendar lastEntry=GregorianCalendar.getInstance();
				lastEntry.setTimeInMillis(user.getLastUpdate());
				Log.i("DATE", lastEntry.get(Calendar.DATE)+"/"+lastEntry.get(Calendar.MONTH));
				if(lastEntry.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR) ||
						lastEntry.get(Calendar.YEAR) != today.get(Calendar.YEAR)){
					//If it's a different day
					lastEntry.add(Calendar.DATE, 1);
					if(today.get(Calendar.DAY_OF_YEAR) == lastEntry.get(Calendar.DAY_OF_YEAR) &&
							today.get(Calendar.YEAR) == lastEntry.get(Calendar.YEAR)){
						Log.i("LAST_ENTRY", "YESTERDAY");
						//The user entered yesterday to the game
						user.setDays(user.getDays()+1);
					}
					else{
						Log.i("LAST_ENTRY", "NOT TODAY");
						//The user didn't enter yesterday or today before now, so the days in row will be reset
						user.setDays(0);
					}					
				}
				user.setLastUpdate(System.currentTimeMillis());
				daoUser.update(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        new DownloadMoviesTask(this).execute();
        checkNotifications();
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
		int interval = 24; //hours
		
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
