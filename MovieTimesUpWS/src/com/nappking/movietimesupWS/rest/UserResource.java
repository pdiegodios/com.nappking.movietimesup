package com.nappking.movietimesupWS.rest;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.nappking.movietimesupWS.dao.IUserDao;
import com.nappking.movietimesupWS.dao.UserDao;
import com.nappking.movietimesupWS.model.User;

public class UserResource {	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String idUser;

	IUserDao iUserDao = new UserDao();
	
	public UserResource(UriInfo uriInfo, Request request, String idUser){
		this.uriInfo = uriInfo;
		this.request = request;
		this.idUser = idUser;
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public User getUser(){
		User user = null;
		try {
			user = iUserDao.get(idUser);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		if(user==null)
			throw new RuntimeException("Get: user with id:" + idUser +  " not found");
		return user;
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_JSON })
	public String putUser(JAXBElement<User> user) {
		String result = "";
		User s = user.getValue();
		try {
			result = ""+iUserDao.save(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return result;
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	public String postUser(JAXBElement<User> user) {
		String result = "";
		User s = user.getValue();		
		try {
			result = ""+ iUserDao.update(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return result;
	}
	
}
