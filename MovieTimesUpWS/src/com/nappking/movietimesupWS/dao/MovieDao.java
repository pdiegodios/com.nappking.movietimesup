package com.nappking.movietimesupWS.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nappking.movietimesupWS.database.ConnectionDB;
import com.nappking.movietimesupWS.model.Movie;


public class MovieDao implements IMovieDao{
	public final static String EN = "EN";
	public final static String ES = "ES";
	
	private Connection _connection;
	private ResultSet _result;
	private PreparedStatement _statement;
	
	private String table="", insert="", update="", delete="", selectAll="", selectByID="", selectSince;
		
	public MovieDao(String language){
		if (language.equalsIgnoreCase(EN)){
			table=Movie.TABLE_EN;
		}
		else if (language.equalsIgnoreCase(ES)){
			table=Movie.TABLE_ES;
		}
		createSentences();
	}
	
	public int save(Movie c) throws SQLException {
		int idInserted = 0;
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			_statement.setString(1, c.getTitle());
			_statement.setString(2, c.getOriginalTitle());
			_statement.setString(3, c.getAlternativeTitle());
			_statement.setInt(4, c.getYear());
			_statement.setString(5, c.getCountry());
			_statement.setString(6, c.getContinent());
			_statement.setString(7, c.getDirector());
			_statement.setString(8, c.getGenre());
			_statement.setString(9, c.getPlot());
			_statement.setString(10, toString(c.getCast()));
			_statement.setString(11, toString(c.getQuotes()));
			_statement.setString(12, toString(c.getOthers()));
			_statement.setString(13, toString(c.getCharacters()));
			_statement.setString(14, c.getPoster());
			_statement.setInt(15, c.getPoints());
			_statement.setString(16, c.getFilmaffinityId());
			_statement.setString(17, c.getImdbId());
			_statement.setString(18, c.getStream());
			_statement.setInt(19, c.getCinema());
			_statement.setBoolean(20, c.isMasterpiece());
			_statement.setBoolean(21, c.isCult());
			_statement.executeUpdate();
			
			_result = _statement.getGeneratedKeys();
			if (_result.next()) {
		          idInserted = _result.getInt(1);
		    }			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			_connection.close();
			_statement.close();
		}
		return idInserted;
	}

	public int update(Movie c) throws SQLException {
		int nupdate = 0;
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(update);
			_statement.setString(1, c.getTitle());
			_statement.setString(2, c.getOriginalTitle());
			_statement.setString(3, c.getAlternativeTitle());
			_statement.setInt(4, c.getYear());
			_statement.setString(5, c.getCountry());
			_statement.setString(6, c.getContinent());
			_statement.setString(7, c.getDirector());
			_statement.setString(8, c.getGenre());
			_statement.setString(9, c.getPlot());
			_statement.setString(10, toString(c.getCast()));
			_statement.setString(11, toString(c.getQuotes()));
			_statement.setString(12, toString(c.getOthers()));
			_statement.setString(13, toString(c.getCharacters()));
			_statement.setString(14, c.getPoster());
			_statement.setInt(15, c.getPoints());
			_statement.setString(16, c.getFilmaffinityId());
			_statement.setString(17, c.getImdbId());
			_statement.setString(18, c.getStream());
			_statement.setInt(19, c.getCinema());
			_statement.setBoolean(20, c.isMasterpiece());
			_statement.setBoolean(21, c.isCult());
			_statement.setLong(22, c.getId());
			
			nupdate = _statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			_connection.close();
			_statement.close();
		}
		return nupdate;
	}

	public int delete(long id) throws SQLException {
		int ndelete = 0;
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(delete);
			_statement.setLong(1, id);
			ndelete = _statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			_connection.close();
			_statement.close();
		}
		return ndelete;
	}

	public List<Movie> getAll() throws SQLException {
		List<Movie> movies = new ArrayList<Movie>();
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(selectAll);
			_result = _statement.executeQuery();				
			while (_result.next()) {
				Movie movie = new Movie();
				movie.setId(_result.getLong(1));
				movie.setTitle(_result.getString(2));
				movie.setOriginalTitle(_result.getString(3));
				movie.setAlternativeTitle(_result.getString(4));
				movie.setYear(_result.getInt(5));
				movie.setCountry(_result.getString(6));
				movie.setContinent(_result.getString(7));
				movie.setDirector(_result.getString(8));
				movie.setGenre(_result.getString(9));
				movie.setPlot(_result.getString(10));
				movie.setCast(fromString(_result.getString(11)));
				movie.setQuotes(fromString(_result.getString(12)));
				movie.setOthers(fromString(_result.getString(13)));
				movie.setCharacters(fromString(_result.getString(14)));
				movie.setPoster(_result.getString(15));
				movie.setPoints(_result.getInt(16));
				movie.setFilmaffinityId(_result.getString(17));
				movie.setImdbId(_result.getString(18));
				movie.setStream(_result.getString(19));
				movie.setCinema(_result.getInt(20));
				movie.setMasterpiece(_result.getBoolean(21));
				movie.setCult(_result.getBoolean(22));
				movies.add(movie);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return movies;
	}

	public List<Movie> getSince(long id) throws SQLException {
		List<Movie> movies = new ArrayList<Movie>();
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(selectSince);
			_statement.setLong(1, id);
			_result = _statement.executeQuery();				
			while (_result.next()) {
				Movie movie = new Movie();
				movie.setId(_result.getLong(1));
				movie.setTitle(_result.getString(2));
				movie.setOriginalTitle(_result.getString(3));
				movie.setAlternativeTitle(_result.getString(4));
				movie.setYear(_result.getInt(5));
				movie.setCountry(_result.getString(6));
				movie.setContinent(_result.getString(7));
				movie.setDirector(_result.getString(8));
				movie.setGenre(_result.getString(9));
				movie.setPlot(_result.getString(10));
				movie.setCast(fromString(_result.getString(11)));
				movie.setQuotes(fromString(_result.getString(12)));
				movie.setOthers(fromString(_result.getString(13)));
				movie.setCharacters(fromString(_result.getString(14)));
				movie.setPoster(_result.getString(15));
				movie.setPoints(_result.getInt(16));
				movie.setFilmaffinityId(_result.getString(17));
				movie.setImdbId(_result.getString(18));
				movie.setStream(_result.getString(19));
				movie.setCinema(_result.getInt(20));
				movie.setMasterpiece(_result.getBoolean(21));
				movie.setCult(_result.getBoolean(22));
				movies.add(movie);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return movies;
	}

	public Movie get(long id) throws SQLException {
        Movie movie = null;		
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(selectByID);
			_statement.setLong(1, id);
			_result = _statement.executeQuery();			
			while (_result.next()) {
				movie = new Movie();
				movie.setId(_result.getInt(1));
				movie.setTitle(_result.getString(2));
				movie.setOriginalTitle(_result.getString(3));
				movie.setAlternativeTitle(_result.getString(4));
				movie.setYear(_result.getInt(5));
				movie.setCountry(_result.getString(6));
				movie.setContinent(_result.getString(7));
				movie.setDirector(_result.getString(8));
				movie.setGenre(_result.getString(9));
				movie.setPlot(_result.getString(10));
				movie.setCast(fromString(_result.getString(11)));
				movie.setQuotes(fromString(_result.getString(12)));
				movie.setOthers(fromString(_result.getString(13)));
				movie.setCharacters(fromString(_result.getString(14)));
				movie.setPoster(_result.getString(15));
				movie.setPoints(_result.getInt(16));
				movie.setFilmaffinityId(_result.getString(17));
				movie.setImdbId(_result.getString(18));
				movie.setStream(_result.getString(19));
				movie.setCinema(_result.getInt(20));
				movie.setMasterpiece(_result.getBoolean(21));
				movie.setCult(_result.getBoolean(22));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return movie;
	} 
	
	//AUXILIAR METHODS
	private String toString(String[] values){
		String result="";
		for(int i=0;i<values.length-1;i++){
			result=result+values[i]+";";
		}
		result=result+values[values.length-1];
		return result;
	}
	
	private String[] fromString(String values){
		String[] result = values.split(";");
		return result;
	}
	
	private void createSentences(){
		//Statements
		insert = "INSERT into "+table+"("+Movie.TITLE+", "+Movie.ORIGINAL_TITLE+", "+
				Movie.ALTERNATIVE_TITLE+", "+Movie.YEAR+", "+Movie.COUNTRY+", "+
				Movie.CONTINENT+", "+Movie.DIRECTOR+", "+Movie.GENRE+", "+Movie.PLOT+", "+
				Movie.CAST+", "+Movie.QUOTES+", "+Movie.OTHERS+", "+Movie.CHARACTERS+", "+
				Movie.POSTER+", "+Movie.POINTS+", "+Movie.FILMAFFINITY+", "+Movie.IMDB+", "+
				Movie.STREAM+", "+Movie.CINEMA+", "+Movie.MASTERPIECE+", "+Movie.CULT+")"+
				" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		update = "UPDATE "+table +" set "+Movie.TITLE+" = ?, "+Movie.ORIGINAL_TITLE+" = ?, "+
				Movie.ALTERNATIVE_TITLE+" = ?, "+Movie.YEAR+" = ?, "+Movie.COUNTRY+" = ?, "+
				Movie.CONTINENT+" = ?, "+Movie.DIRECTOR+" = ?, "+Movie.GENRE+" = ?, "+
				Movie.PLOT+" = ?, "+Movie.CAST+" = ?, "+ Movie.QUOTES+" = ?, "+Movie.OTHERS+" = ?, "+
				Movie.CHARACTERS+" = ?, "+Movie.POSTER+" = ?, "+Movie.POINTS+" = ?, "+
				Movie.FILMAFFINITY+" = ?, "+Movie.IMDB+" = ?, "+Movie.IMDB+" = ?, "+Movie.CINEMA+" = ?, "+
				Movie.MASTERPIECE+" = ?, "+Movie.CULT+" = ? WHERE "+Movie.ROWID +" = ?";
		delete = "DELETE from "+table+" where "+Movie.ROWID+" = ?";
		selectAll = "SELECT * from "+table;
		selectSince = "SELECT * from "+table+" WHERE "+Movie.ROWID + " > ?";
		selectByID = "SELECT * from "+table+" WHERE "+Movie.ROWID +" = ?";
	}

}
