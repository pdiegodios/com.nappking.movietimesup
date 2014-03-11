package com.nappking.movietimesup.entities;

import java.io.Serializable;
import java.util.ArrayList;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable
public class User implements Serializable{

	private static final long serialVersionUID = -503193754637009126L;
	
	//Table
    public static final String TABLE = "User";
    
    //Columns
    public static final String ROWID = 		"_id";
    public static final String USER = 		"idUser";
    public static final String NAME = 		"name";
    public static final String SCORE = 		"score";
    public static final String SECONDS = 	"seconds";
    public static final String LOCKED = 	"locked";
    public static final String UNLOCKED = 	"unlocked";
    public static final String LASTUPDATE = "lastUpdate";
    public static final String DAYS = 		"days";
    public static final String MOVIES =		"movies";
    
	//Fields
    @DatabaseField(generatedId = true, columnName = ROWID)
    private int _id;
    @DatabaseField(columnName = USER)
    private String user;
    @DatabaseField(columnName = NAME)
    private String name;
    @DatabaseField(columnName = SCORE)
    private int score;
    @DatabaseField(columnName = SECONDS)
    private int seconds;
    @DatabaseField(columnName = LOCKED, dataType = DataType.SERIALIZABLE)
    private ArrayList<String> lockedMovies;
    @DatabaseField(columnName = UNLOCKED, dataType = DataType.SERIALIZABLE)
    private ArrayList<String> unlockedMovies;
    @DatabaseField(columnName = LASTUPDATE)
    private long lastUpdate;
    @DatabaseField(columnName = DAYS)
    private int days;
    @DatabaseField(columnName = MOVIES)
    private int movies;
    
    //creators
	public User() {super();}
    
    //getters
    public long getId()							{return this._id;}    
    public String getUser()						{return this.user;}    
    public String getName()						{return this.name;} 
    public int getScore()						{return this.score;}
    public int getSeconds()						{return this.seconds;}
    public ArrayList<String> getLockedMovies()	{return this.lockedMovies;}
    public ArrayList<String> getUnlockedMovies(){return this.unlockedMovies;}
    public long getLastUpdate()					{return this.lastUpdate;}
    public int getDays()						{return this.days;}
    public int getMovies()						{return this.movies;}

    //setters
    public void setId(int id)										{this._id=id;}  
    public void setUser(String user)								{this.user=user;}
    public void setName(String name)								{this.name=name;}
	public void setScore(int score)									{this.score=score;} 
	public void setSeconds(int seconds)								{this.seconds=seconds;} 
    public void setLockedMovies(ArrayList<String> lockedMovies)		{this.lockedMovies = lockedMovies;}
    public void setUnlockedMovies(ArrayList<String> unlockedMovies)	{this.unlockedMovies = unlockedMovies;}
    public void setLastUpdate(long date)							{this.lastUpdate = date;}
    public void setDays(int days)									{this.days = days;}
    public void setMovies(int movies)								{this.movies = movies;}
    
    public void removeLockedMovie(int idMovie){
    	String id = String.valueOf(idMovie);
    	for(String idlocked: this.lockedMovies){
    		if(id.equals(idlocked)){
    			this.lockedMovies.remove(idlocked);
    			break;
    		}
    	}
    }
    
    public void addLockedMovie(int idMovie){
    	String id = String.valueOf(idMovie);
    	if(!lockedMovies.contains(id)){
    		lockedMovies.add(id);
    	}
    }

    public void addUnlockedMovie(int idMovie){
    	String id = String.valueOf(idMovie);
    	if(!unlockedMovies.contains(id)){
    		unlockedMovies.add(id);
    	}
    }
    	
}
