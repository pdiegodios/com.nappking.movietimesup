package com.nappking.movietimesup.database;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;

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
}
