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
    
	//Fields
    @DatabaseField(generatedId = true, columnName = ROWID)
    private long _id;
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

    //setters
    public void setId(long id)										{this._id=id;}  
    public void setUser(String user)								{this.user=user;}
    public void setName(String name)								{this.name=name;}
	public void setScore(int score)									{this.score=score;} 
	public void setSeconds(int seconds)								{this.seconds=seconds;} 
    public void setLockedMovies(ArrayList<String> lockedMovies)		{this.lockedMovies = lockedMovies;}
    public void setUnlockedMovies(ArrayList<String> unlockedMovies)	{this.unlockedMovies = unlockedMovies;}
    
    public void removeLockedMovie(String idMovie){
    	for(String id: this.lockedMovies){
    		if(idMovie.equals(id)){
    			this.lockedMovies.remove(id);
    			break;
    		}
    	}
    }
    	
}
