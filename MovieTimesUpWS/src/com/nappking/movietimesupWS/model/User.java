package com.nappking.movietimesupWS.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	//Table
    public static final String TABLE = "User";
    //Columns
    public static final String ROWID = 		"_id";
    public static final String USER = 		"idUser";
    public static final String NAME = 		"name";
    public static final String EMAIL = 		"email";
    public static final String PASSWORD =	"password";
    public static final String SCORE = 		"score";
    public static final String SECONDS = 	"seconds";
    public static final String LOCKED = 	"locked";
    public static final String UNLOCKED = 	"unlocked";
    public static final String LASTUPDATE = "lastUpdate";
    public static final String LASTFOREGROUND = "lastForeground";
    public static final String DAYS = 		"days";
    public static final String MOVIES = 	"movies";
    public static final String WILDCARD =	"wildcard";
    public static final String MASTERPIECE ="masterpiece";
    public static final String CULT =		"cult";
    public static final String AMERICA =	"america";
    public static final String EUROPE =		"europe";
    public static final String ASIA =		"asia";
    public static final String EXOTIC =		"exotic";
    
	//Fields
    private long _id;
    private String user, email, password, name;
    private int score;
    private int seconds;
    private ArrayList<Integer> lockedMovies;
    private ArrayList<Integer> unlockedMovies;
    private long lastUpdate;
    private long lastForeground;
    private int days;
    private int movies;
    private int wildcard;
    private int masterpiece;
    private int cult;
    private int america;
    private int europe;
    private int asia;
    private int exotic;
    
    //creators
	public User() {super();}
    
    //getters
    public long getId()								{return this._id;}    
    public String getUser()							{return this.user;}  
    public String getName()							{return this.name;}  
    public String getEmail()						{return this.email;}  
    public String getPassword()						{return this.password;}  
    public int getScore()							{return this.score;}
    public int getSeconds()							{return this.seconds;}
    public ArrayList<Integer> getLockedMovies()		{return this.lockedMovies;}
    public ArrayList<Integer> getUnlockedMovies()	{return this.unlockedMovies;}
    public long getLastUpdate()						{return this.lastUpdate;}
    public long getLastForeground()					{return this.lastForeground;}
    public int getDays()							{return this.days;}
    public int getMovies()							{return this.movies;}
    public int getMasterpiece()						{return this.masterpiece;}
    public int getWildcard()						{return this.wildcard;}
    public int getCult()							{return this.cult;}
    public int getAmerica()							{return this.america;}
    public int getEurope()							{return this.europe;}
    public int getAsia()							{return this.asia;}
    public int getExotic()							{return this.exotic;}

    //setters
    public void setId(long id)											{this._id=id;}  
    public void setUser(String user)									{this.user=user;}
    public void setName(String name)									{this.name=name;}
    public void setEmail(String email)									{this.email=email;}
    public void setPassword(String password)							{this.password=password;}
	public void setScore(int score)										{this.score=score;} 
	public void setSeconds(int seconds)									{this.seconds=seconds;} 
    public void setLockedMovies(ArrayList<Integer> lockedMovies)		{this.lockedMovies = lockedMovies;}
    public void setUnlockedMovies(ArrayList<Integer> unlockedMovies)	{this.unlockedMovies = unlockedMovies;}
    public void setLastUpdate(long lastUpdate)							{this.lastUpdate=lastUpdate;}	
    public void setLastForeground(long lastForeground)					{this.lastForeground=lastForeground;}	
	public void setDays(int days)										{this.days=days;} 	
	public void setMovies(int movies)									{this.movies=movies;}
    public void setMasterpiece(int masterpiece)							{this.masterpiece = masterpiece;}
    public void setWildcard(int wildcard)								{this.wildcard = wildcard;}
    public void setCult(int cult)										{this.cult = cult;}
    public void setAmerica(int america)									{this.america = america;}
    public void setEurope(int europe)									{this.europe = europe;}
    public void setAsia(int asia)										{this.asia = asia;}
    public void setExotic(int exotic)									{this.exotic = exotic;}
    
}
