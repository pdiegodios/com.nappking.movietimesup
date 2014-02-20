package com.nappking.movietimesupWS.model;

import java.sql.Date;
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
    public static final String SCORE = 		"score";
    public static final String SECONDS = 	"seconds";
    public static final String LOCKED = 	"locked";
    public static final String UNLOCKED = 	"unlocked";
    public static final String LASTUPDATE = "lastUpdate";
    
	//Fields
    private long _id;
    private String user, name;
    private int score;
    private int seconds;
    private ArrayList<Integer> lockedMovies;
    private ArrayList<Integer> unlockedMovies;
    private long lastUpdate;
    
    //creators
	public User() {super();}
    
    //getters
    public long getId()								{return this._id;}    
    public String getUser()							{return this.user;}  
    public String getName()							{return this.name;}  
    public int getScore()							{return this.score;}
    public int getSeconds()							{return this.seconds;}
    public ArrayList<Integer> getLockedMovies()		{return this.lockedMovies;}
    public ArrayList<Integer> getUnlockedMovies()	{return this.unlockedMovies;}
    public long getLastUpdate()						{return this.lastUpdate;}

    //setters
    public void setId(long id)											{this._id=id;}  
    public void setUser(String user)									{this.user=user;}
    public void setName(String name)									{this.name=name;}
	public void setScore(int score)										{this.score=score;} 
	public void setSeconds(int seconds)									{this.seconds=seconds;} 
    public void setLockedMovies(ArrayList<Integer> lockedMovies)		{this.lockedMovies = lockedMovies;}
    public void setUnlockedMovies(ArrayList<Integer> unlockedMovies)	{this.unlockedMovies = unlockedMovies;}
    public void setLastUpdate(long lastUpdate)							{this.lastUpdate=lastUpdate;}		
    
}
