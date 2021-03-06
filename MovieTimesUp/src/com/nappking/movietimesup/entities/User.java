package com.nappking.movietimesup.entities;

import java.io.Serializable;
import java.util.ArrayList;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nappking.movietimesup.MovieTimesUpApplication;


@DatabaseTable
public class User implements Serializable{

	private static final long serialVersionUID = -503193754637009126L;
	
	//Table
    public static final String TABLE = "User";
    
    //Columns
    public static final String ROWID = 		"_id";
    public static final String USER = 		"idUser";
    public static final String EMAIL = 		"email";
    public static final String PASSWORD =	"password";
    public static final String NAME = 		"name";
    public static final String SCORE = 		"score";
    public static final String SECONDS = 	"seconds";
    public static final String LOCKED = 	"locked";
    public static final String UNLOCKED = 	"unlocked";
    public static final String LASTUPDATE = "lastUpdate";
    public static final String LASTFOREGROUND = "lastForeground";
    public static final String DAYS = 		"days";
    public static final String MOVIES =		"movies";
    public static final String CINEMAS =	"cinemas";
    public static final String WILDCARD =	"wildcard";
    public static final String MASTERPIECE ="masterpiece";
    public static final String MASTERPIECESOLD ="masterpieceSold";
    public static final String CULT =		"cult";
    public static final String CULTSOLD =	"cultSold";
    public static final String AMERICA =	"america";
    public static final String EUROPE =		"europe";
    public static final String ASIA =		"asia";
    public static final String EXOTIC =		"exotic";
    
	//Fields
    @DatabaseField(generatedId = true, columnName = ROWID)
    private int _id;
    @DatabaseField(columnName = USER)
    private String user;
    @DatabaseField(columnName = EMAIL)
    private String email;
    @DatabaseField(columnName = PASSWORD)
    private String password;
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
    @DatabaseField(columnName = LASTFOREGROUND)
    private long lastForeground;
    @DatabaseField(columnName = DAYS)
    private int days;
    @DatabaseField(columnName = MOVIES)
    private int movies;
    @DatabaseField(columnName = WILDCARD)
    private int wildcard;
    @DatabaseField(columnName = MASTERPIECE)
    private int masterpiece;
    @DatabaseField(columnName = MASTERPIECESOLD)
    private int masterpieceSold;
    @DatabaseField(columnName = CULT)
    private int cult;
    @DatabaseField(columnName = CULTSOLD)
    private int cultSold;
    @DatabaseField(columnName = AMERICA)
    private int america;
    @DatabaseField(columnName = EUROPE)
    private int europe;
    @DatabaseField(columnName = ASIA)
    private int asia;
    @DatabaseField(columnName = EXOTIC)
    private int exotic;
    
    //creators
	public User() {super();}
    
    //getters
    public long getId()							{return this._id;}    
    public String getUser()						{return this.user;}   
    public String getEmail()					{return this.email;}   
    public String getPassword()					{return this.password;}    
    public String getName()						{return this.name;} 
    public int getScore()						{return this.score;}
    public int getSeconds()						{return this.seconds;}
    public ArrayList<String> getLockedMovies()	{return this.lockedMovies;}
    public ArrayList<String> getUnlockedMovies(){return this.unlockedMovies;}
    public long getLastUpdate()					{return this.lastUpdate;}
    public long getLastForeground()				{return this.lastForeground;}
    public int getDays()						{return this.days;}
    public int getMovies()						{return this.movies;}
    public int getWildcard()					{return this.wildcard;}
    public int getMasterpiece()					{return this.masterpiece;}
    public int getMasterpieceSold()				{return this.masterpieceSold;}
    public int getCult()						{return this.cult;}
    public int getCultSold()					{return this.cultSold;}
    public int getAmerica()						{return this.america;}
    public int getEurope()						{return this.europe;}
    public int getAsia()						{return this.asia;}
    public int getExotic()						{return this.exotic;}
    public int getTotalCinemas(){
    	return (unlockedMovies.size()/MovieTimesUpApplication.UNLOCK_LEVEL)+1;
    }
    public int getTotalSolved(){
    	return unlockedMovies.size();
    }

    //setters
    public void setId(int id)										{this._id=id;}  
    public void setUser(String user)								{this.user=user;}
    public void setEmail(String email)								{this.email=email;}   
    public void setPassword(String password)						{this.password=password;}    
    public void setName(String name)								{this.name=name;}
	public void setScore(int score)									{this.score=score;} 
	public void setSeconds(int seconds)								{this.seconds=seconds;} 
    public void setLockedMovies(ArrayList<String> lockedMovies)		{this.lockedMovies = lockedMovies;}
    public void setUnlockedMovies(ArrayList<String> unlockedMovies)	{this.unlockedMovies = unlockedMovies;}
    public void setLastUpdate(long millis)							{this.lastUpdate = millis;}
    public void setLastForeground(long millis)						{this.lastForeground = millis;}
    public void setDays(int days)									{this.days = days;}
    public void setMovies(int movies)								{this.movies = movies;}
    public void setWildcard(int wildcard)							{this.wildcard = wildcard;}
    public void setMasterpiece(int masterpiece)						{this.masterpiece = masterpiece;}
    public void setMasterpieceSold(int masterpieceSold)				{this.masterpieceSold = masterpieceSold;}
    public void setCult(int cult)									{this.cult = cult;}
    public void setCultSold(int cultSold)							{this.cultSold = cultSold;}
    public void setAmerica(int america)								{this.america = america;}
    public void setEurope(int europe)								{this.europe = europe;}
    public void setAsia(int asia)									{this.asia = asia;}
    public void setExotic(int exotic)								{this.exotic = exotic;}
    
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
