package com.nappking.movietimesup.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;

/**
 * Clase donde se crean los distintos DAO para acceder a las tablas de SQLite. 
 * En esta clase se especifica además el nombre y la versión de la base de datos
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "DBTimesUp.db";
    private static final int DATABASE_VERSION = 1;
    
    //DAO's
    private Dao<User, Integer> _userDAO;
    private Dao<Movie, Integer> _movieDAO;
    
 
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static SQLiteDatabase getDatabaseConnection(Context c){
		return new DBHelper(c).getWritableDatabase();
	}
 
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connection) {
        try {
        	//Creacion de las tablas de SQLite
            TableUtils.createTable(connection, User.class);
            TableUtils.createTable(connection, Movie.class);            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connection, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),"Upgrading database from version " + oldVersion + " to "+ newVersion 
        		+ ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Movie.TABLE);
        onCreate(db, connection);
    }
 
    //Método para recuperar DAO y poder Acceder a la tabla User con diferentes queries
    public Dao<User, Integer> getUserDAO() throws SQLException {
        if (_userDAO == null)	_userDAO = getDao(User.class);
        return _userDAO;
    }
    
    //Método para recuperar DAO y poder Acceder a la tabla Movie con diferentes queries
    public Dao<Movie, Integer> getMovieDAO() throws SQLException {
        if (_movieDAO == null)	_movieDAO = getDao(Movie.class);
        return _movieDAO;
    }
    
    @Override
    public void close() {
    	//Se cierra la conexión con la BD
        super.close();
        _userDAO 	= null;
        _movieDAO 	= null;
    }
 
}
