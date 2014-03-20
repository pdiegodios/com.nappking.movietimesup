package com.nappking.movietimesup.database;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.MovieTimesUpApplication;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.LoadUserDataTask;

/**
 * Clase genérica para las actividades con acceso a la Base de Datos
 * @author pdiego
 */
public abstract class DBFragmentActivity extends FragmentActivity{
	protected static final String TAG = null;
	private DBHelper _DBHelper;
	
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
			int totalMovies = (int)getHelper().getMovieDAO().countOf();
			User user = daoUser.queryForId(1);
			if(user!=null){
				Calendar now = GregorianCalendar.getInstance();
				if((now.getTimeInMillis()>(((MovieTimesUpApplication)getApplication()).getLastUpdateCall()
						+MovieTimesUpApplication.TIME_FOR_SERVICE)) || (totalMovies>user.getMovies())){
					Log.i("UPDATE USER", "IT'S TIME TO CHECK WS");
					//It's more than 10min since last time it was updated or there are new movies
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
