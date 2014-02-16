package com.nappking.movietimesupWS.dao;

import java.sql.SQLException;
import java.util.List;

import com.nappking.movietimesupWS.model.Movie;

public interface IMovieDao {

	public int save(Movie movie) throws SQLException;
	public int update(Movie movie) throws SQLException;
	public int delete(long idMovie) throws SQLException;
	public List<Movie> getAll() throws SQLException;
	public List<Movie> getSince(long id) throws SQLException;
	public Movie get(long id) throws SQLException;
	
}
