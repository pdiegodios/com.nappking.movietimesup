package com.nappking.movietimesup.database;

import android.support.v4.app.Fragment;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Clase genérica para las actividades con acceso a la Base de Datos
 * @author pdiego
 */
public abstract class DBFragment extends Fragment{
	protected static final String TAG = null;
	private DBHelper _DBHelper;
	
	/**
	 * Función para instanciar la base de datos desde la actividad
	 * @return
	 */
	protected DBHelper getHelper(){
		if(_DBHelper==null){
			_DBHelper = OpenHelperManager.getHelper(this.getActivity(), DBHelper.class);
		}
		return _DBHelper;
	}
	
	/**
	 * En el mismo momento que una actividad hija de esta clase se destruye, 
	 * se liberan los recursos de acceso a la BDD.
	 */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_DBHelper != null) {
            OpenHelperManager.releaseHelper();
            _DBHelper = null;
        }
    }
}
