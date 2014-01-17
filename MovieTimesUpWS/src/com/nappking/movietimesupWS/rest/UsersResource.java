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
import com.nappking.movietimesupWS.dao.IUserDao;
import com.nappking.movietimesupWS.dao.UserDao;
import com.nappking.movietimesupWS.model.User;

@Path("/users")
public class UsersResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_ERROR = "error";
	
	IUserDao iUserDao= new UserDao();
		
	// Return the list of Users for apps && for the browser
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<User> getUsers() throws SQLException {
		List<User> users = iUserDao.getAll();
		return users;  
	}
	
	// Return the list of Movies for the browser
	@GET
	@Produces({MediaType.TEXT_XML})
	public List<User> getUsersHTML() throws SQLException {
		List<User> users = iUserDao.getAll();
		return users;  
	}
	
	// Return the number of users
	@GET
	@Path("/count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() throws SQLException{
		int count = iUserDao.getAll().size();
		return String.valueOf(count);
	}
	
	// Return a specific user by id
	@Path("{idUser}")
	public UserResource getUser(@PathParam("idUser") String idUser){
		return new UserResource(uriInfo, request, idUser);
	}
		
	// Save and Update Movies
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String saveOrUpdate(
		  @FormParam("users") String users,
	      @FormParam("action") String action,
	      @Context HttpServletResponse servletResponse) throws IOException {

		Gson myGson = new Gson();
		List<String> listSaved = new ArrayList<String>();
			  
	    JsonParser parser = new JsonParser();
	    JsonArray usersArray = parser.parse(users.toString()).getAsJsonArray();
	    for(JsonElement user : usersArray ){
	    	User c  = myGson.fromJson(user, User.class);
	    	try {
	    		String id="";
	    		if(action.equalsIgnoreCase("SAVE")){
	    			id = ""+iUserDao.save(c);
	    		}
	    		else if(action.equalsIgnoreCase("UPDATE")){
	    			id = ""+iUserDao.update(c);
	    		}
	    		listSaved.add(id);
	    	}catch (SQLException e) {
	    		e.printStackTrace();
		    }
	    }	      
		return myGson.toJson(listSaved);
	}
}
