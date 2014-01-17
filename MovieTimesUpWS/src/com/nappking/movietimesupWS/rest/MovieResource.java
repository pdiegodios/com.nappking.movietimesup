package com.nappking.movietimesupWS.rest;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.nappking.movietimesupWS.dao.IMovieDao;
import com.nappking.movietimesupWS.dao.MovieDao;
import com.nappking.movietimesupWS.model.Movie;

public class MovieResource {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;
	String language;

	IMovieDao iMovieDao;
	
	public MovieResource(UriInfo uriInfo, Request request, String id, String language){
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.language = language;
		iMovieDao=new MovieDao(this.language);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Movie getMovie(){
		Movie movie = null;
		try {
			movie = iMovieDao.get(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		if(movie==null)
			throw new RuntimeException("Get: Movie with " + id +  " not found");
		return movie;
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_JSON })
	public String putMovie(JAXBElement<Movie> movie) {
		String result = "";
		Movie c = movie.getValue();
		try {
			result = ""+iMovieDao.save(c);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return result;
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public String postMovie(JAXBElement<Movie> movie) {
		String result = "";
		Movie c = movie.getValue();
		
		try {
			result = ""+ iMovieDao.update(c);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return result;
	}

	@DELETE 
    public void deleteMovie() {
		try {
			int delete = iMovieDao.delete(Integer.parseInt(id));
			if(delete == 0)
				throw new RuntimeException("Delete: Movie with " + id +  " not found");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }	
	
}
