package com.nappking.movietimesup.database;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.MovieTimesUpApplication;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.LoadUserDataTask;

/**
 * Clase genérica para las actividades con acceso a la Base de Datos
 * @author pdiego
 */
public abstract class DBActivity extends Activity{
	protected static final String TAG = null;
	private DBHelper _DBHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Hide the notification bar
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	/**
	 * Función para instanciar la base de datos desde la actividad
	 * @return
	 */
	protected DBHelper getHelper(){
		if(_DBHelper==null){
			_DBHelper = OpenHelperManager.getHelper(this, DBHelper.class);
		}
		return _DBHelper;
	}
	
	/**
	 * En el mismo momento que una actividad hija de esta clase se destruye, 
	 * se liberan los recursos de acceso a la BDD.
	 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_DBHelper != null) {
            OpenHelperManager.releaseHelper();
            _DBHelper = null;
        }
    }
    
	protected void updateUser(){
		Dao<User, Integer> daoUser;
		try {
			daoUser = getHelper().getUserDAO();
			User user = daoUser.queryForId(1);
			if(user!=null){
				Calendar now = GregorianCalendar.getInstance();
				if((now.getTimeInMillis()>(((MovieTimesUpApplication)getApplication()).getLastUpdateCall()+MovieTimesUpApplication.TIME_FOR_SERVICE))){
					Log.i("UPDATE USER", "IT'S TIME TO CHECK WS");
					//It's more than 15min since last time it was updated 
					((MovieTimesUpApplication)getApplication()).setLastUpdateCall(System.currentTimeMillis());
					Log.i("LOAD USER FROM:", this.toString());
					new LoadUserDataTask(this, user).execute();
				}						
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
