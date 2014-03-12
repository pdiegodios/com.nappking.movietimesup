package com.nappking.movietimesupWS.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Movie {
	
	//Table
    public static final String TABLE_ES = "Movie_ES";
    public static final String TABLE_EN = "Movie_EN";
    
    //Columns
    public static final String ROWID = "_id";
    public static final String TITLE = "title";
    public static final String ALTERNATIVE_TITLE = "alternative_title";
    public static final String ORIGINAL_TITLE = "original_title";
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
    public static final String FILMAFFINITY = "filmaffinity_id";
    public static final String IMDB = "imdb_id";
    public static final String ROOM = "room";
    public static final String MASTERPIECE = "masterpiece";
    public static final String CULT = "cult";
	
	//Fields
    private long 		_id;
    private String 		title;
    private String 		alternative_title;
    private String 		original_title;
    private int 		year;
    private String 		country;
    private String 		continent;
    private String 		director;
    private String 		genre;
    private String 		plot;
    private String[] 	cast;
    private String[] 	quotes;
    private String[] 	others;
    private String[] 	characters;
    private String 		poster;
    private int 		points;
    private String 		filmaffinity_id;
    private String		imdb_id;
    private int			room;
    private boolean 	masterpiece;
    private boolean		cult;
    
    //creators
	public Movie() {super();}
    
    //GETTERS
	public long getId() 				{return _id;}
	public String getTitle() 			{return title;}
	public String getAlternativeTitle()	{return alternative_title;}
	public String getOriginalTitle()	{return original_title;}
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
	public String getFilmaffinityId()	{return filmaffinity_id;}
	public String getImdbId()	 		{return imdb_id;}
	public int getRoom()	 			{return room;}
	public boolean isMasterpiece()		{return masterpiece;}
	public boolean isCult()				{return cult;}
	
	//SETTERS
	public void setId(long id) 						{this._id = id;}
	public void setTitle(String title) 				{this.title = title;}
	public void setAlternativeTitle(String a_title) {this.alternative_title = a_title;}
	public void setOriginalTitle(String o_title) 	{this.original_title = o_title;}
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
	public void setFilmaffinityId(String fa_id)		{this.filmaffinity_id = fa_id;}
	public void setImdbId(String imdb_id)			{this.imdb_id = imdb_id;}
	public void setRoom(int room)					{this.room = room;}
	public void setMasterpiece(boolean masterpiece)	{this.masterpiece = masterpiece;}
	public void setCult(boolean cult)				{this.cult = cult;}

}
