package com.nappking.movietimesupWS.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nappking.movietimesupWS.dao.IMovieDao;
import com.nappking.movietimesupWS.dao.MovieDao;
import com.nappking.movietimesupWS.model.Movie;

@Path("/movies")
public class MoviesResource {
	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_ERROR = "error";
	
	IMovieDao iMovieDao= new MovieDao(MovieDao.ES);
		
	// Return the list of Movies for apps && for the browser
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<Movie> getMovies() throws SQLException {
		List<Movie> movies = iMovieDao.getAll();
		return movies;  
	}
	
	// Return the list of Movies for the browser
	@GET
	@Produces({MediaType.TEXT_XML})
	public List<Movie> getMoviesHTML() throws SQLException {
		List<Movie> movies = iMovieDao.getAll();
		return movies;  
	}

	// Return all movies since a specific movie id
	@GET
	@Path("/since/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public List<Movie> getMovies(@PathParam("id") String id) throws NumberFormatException, SQLException{
		List<Movie> movies = iMovieDao.getSince(Long.parseLong(id));
		return movies;
	}
	
	// Return the number of movies
	@GET
	@Path("/count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() throws SQLException{
		int count = iMovieDao.getAll().size();
		return String.valueOf(count);
	}
	
	// Return a specific movie by id
	@Path("{id}")
	public MovieResource getMovie(@PathParam("id") String id){
		return new MovieResource(uriInfo, request, id, MovieDao.ES);
	}
		
	// Save and Update Movies
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String saveOrUpdate(
		  @FormParam("movies") String movies,
	      @FormParam("action") String action,
	      @Context HttpServletResponse servletResponse) throws IOException {

		Gson myGson = new Gson();
		List<String> listSaved = new ArrayList<String>();
			  
	    JsonParser parser = new JsonParser();
	    JsonArray moviesArray = parser.parse(movies.toString()).getAsJsonArray();
	    for(JsonElement movie : moviesArray ){
	    	Movie c  = myGson.fromJson(movie, Movie.class);
	    	try {
	    		String id="";
	    		if(action.equalsIgnoreCase("SAVE")){
	    			id = ""+iMovieDao.save(c);
	    		}
	    		else if(action.equalsIgnoreCase("UPDATE")){
	    			id = ""+iMovieDao.update(c);
	    		}
	    		listSaved.add(id);
	    	}catch (SQLException e) {
	    		e.printStackTrace();
		    }
	    }	      
		return myGson.toJson(listSaved);
	}
}
