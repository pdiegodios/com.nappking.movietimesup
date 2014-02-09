package com.nappking.movietimesup.entities;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nappking.movietimesup.database.DBHelper;

@DatabaseTable
public class Movie implements Serializable{
	
	private static final long serialVersionUID = 1493560767675114344L;
	
	//Table
    public static final String TABLE = "Movie";
    
    //Columns
    public static final String ROWID = "_id";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String ALTERNATIVE_TITLE = "alternativeTitle";
    public static final String ORIGINAL_TITLE = "originalTitle";
    public static final String YEAR = "year";
    public static final String COUNTRY = "country";
    public static final String CONTINENT = "continent";
    public static final String DIRECTOR = "director";
    public static final String GENRE = "genre";
    public static final String PLOT = "plot";
    public static final String CAST = "cast";
    public static final String QUOTES = "quotes";
    public static final String OTHERS = "others";
	public static final String CHARACTERS = "characters";
    public static final String POSTER = "poster";
    public static final String POINTS = "points";
	
	//Fields
    @DatabaseField(generatedId = true, columnName = ROWID)
    private int 		_id;
    @DatabaseField(columnName = ID)
    private int 		id;
    @DatabaseField(columnName = TITLE)
    private String 		title;
    @DatabaseField(columnName = ALTERNATIVE_TITLE)
    private String 		alternativeTitle;
    @DatabaseField(columnName = ORIGINAL_TITLE)
    private String 		originalTitle;
    @DatabaseField(columnName = YEAR)
    private int 		year;
    @DatabaseField(columnName = COUNTRY)
    private String 		country;
    @DatabaseField(columnName = CONTINENT)
    private String 		continent;
    @DatabaseField(columnName = DIRECTOR)
    private String 		director;
    @DatabaseField(columnName = GENRE)
    private String 		genre;
    @DatabaseField(columnName = PLOT)
    private String 		plot;
    @DatabaseField(columnName = CAST, dataType = DataType.SERIALIZABLE)
    private String[] 	cast;
    @DatabaseField(columnName = QUOTES, dataType = DataType.SERIALIZABLE)
    private String[] 	quotes;
    @DatabaseField(columnName = OTHERS, dataType = DataType.SERIALIZABLE)
    private String[] 	others;
    @DatabaseField(columnName = CHARACTERS, dataType = DataType.SERIALIZABLE)
    private String[] 	characters;
    @DatabaseField(columnName = POSTER)
    private String 		poster;
    @DatabaseField(columnName = POINTS)
    private int 		points;
    
    //GETTERS
	public long getId() 				{return id;}
	public String getTitle() 			{return title;}
	public String getAlternativeTitle()	{return alternativeTitle;}
	public String getOriginalTitle()	{return originalTitle;}
	public int getYear() 				{return year;}
	public String getCountry()	 		{return country;}
	public String getContinent()		{return continent;}
	public String getDirector() 		{return director;}
	public String getGenre()	 		{return genre;}
	public String getPlot() 			{return plot;}
	public String[] getCast() 			{return cast;}
	public String[] getQuotes()		 	{return quotes;}
	public String[] getOthers() 		{return others;}
	public String[] getCharacters()		{return characters;}
	public String getPoster()	 		{return poster;}
	public int getPoints()	 			{return points;}
	
	public boolean isUnlocked(Context context){
		boolean result = false;
		try {
			DBHelper helper = OpenHelperManager.getHelper(context, DBHelper.class);
			Dao<User,Integer> daoUser = helper.getUserDAO();
			User user = daoUser.queryForId(0);
			helper = null;
			OpenHelperManager.releaseHelper();
			if(user!=null){
				ArrayList<String> unlockedMovies = user.getUnlockedMovies();
				if (unlockedMovies.contains(this.id+""))
					result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean isLocked(Context context){
		boolean result = false;
		try {
			DBHelper helper = OpenHelperManager.getHelper(context, DBHelper.class);
			Dao<User,Integer> daoUser = helper.getUserDAO();
			User user = daoUser.queryForId(0);
			helper = null;
			OpenHelperManager.releaseHelper();
			if(user!=null){
				ArrayList<String> lockedMovies = user.getLockedMovies();
				if (lockedMovies.contains(this.id+""))
					result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//SETTERS
	public void setRowid(int _id)					{this._id = _id;}
	public void setId(int id) 						{this.id = id;}
	public void setTitle(String title) 				{this.title = title;}
	public void setAlternativeTitle(String a_title) {this.alternativeTitle = a_title;}
	public void setOriginalTitle(String o_title) 	{this.originalTitle = o_title;}
	public void setYear(int year) 					{this.year = year;}
	public void setCountry(String country) 			{this.country = country;}
	public void setContinent(String continent) 		{this.continent = continent;}
	public void setDirector(String director)	 	{this.director = director;}
	public void setGenre(String genre) 				{this.genre = genre;}
	public void setPlot(String plot)	 			{this.plot = plot;}
	public void setCast(String[] cast) 				{this.cast = cast;}
	public void setQuotes(String[] quotes) 			{this.quotes = quotes;}
	public void setOthers(String[] others) 			{this.others = others;}
	public void setCharacters(String[] characters) 	{this.characters = characters;}
	public void setPoster(String poster)			{this.poster = poster;}
	public void setPoints(int points) 				{this.points = points;}

}
