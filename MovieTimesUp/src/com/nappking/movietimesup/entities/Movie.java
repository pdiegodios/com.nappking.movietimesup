package com.nappking.movietimesup.entities;

import java.io.File;
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
    public static final String FILMAFFINITY_ID = "filmaffinityId";
    public static final String IMDB_ID = "imdbId";
    public static final String ROOM = "room";
    public static final String MASTERPIECE = "masterpiece";
    public static final String CULT = "cult";
    
    //Example-->http://www.filmaffinity.com/en/film745662.html
    public static final String FILMAFFINITY_WEB = "http://www.filmaffinity.com/";
    //Example-->http://www.imdb.com/title/tt0093870
    public static final String IMDB_WEB = "http://www.imdb.com/title/";
	
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
    @DatabaseField(columnName = FILMAFFINITY_ID)
    private String		filmaffinityId;
    @DatabaseField(columnName = IMDB_ID)
    private String 		imdbId;
    @DatabaseField(columnName = ROOM)
    private int 		room;
    @DatabaseField(columnName = MASTERPIECE)
    private boolean		masterpiece;
    @DatabaseField(columnName = CULT)
    private boolean 		cult;
    
    
    //GETTERS
    public int getRowid()				{return _id;}
	public int getId() 					{return id;}
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
	public String getFilmaffinityId()	{return filmaffinityId;}
	public String getImdbId()	 		{return imdbId;}
	public int getRoom()	 			{return room;}
	public boolean getMasterpiece()		{return masterpiece;}
	public boolean getCult()	 		{return cult;}
	
	public String getFilmaffinityURL(String language){
		return FILMAFFINITY_WEB+language+File.separator+"film"+filmaffinityId+".html";
	}
	
	public String getImdbURL(){
		return IMDB_WEB+imdbId;
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
	public void setFilmaffinityId(String faId)		{this.filmaffinityId = faId;}
	public void setImdbID(String imdbId)			{this.imdbId = imdbId;}

}
