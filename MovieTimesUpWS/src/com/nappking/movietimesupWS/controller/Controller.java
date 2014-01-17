package com.nappking.movietimesupWS.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nappking.movietimesupWS.dao.MovieDao;
import com.nappking.movietimesupWS.dao.UserDao;
import com.nappking.movietimesupWS.model.Movie;
import com.nappking.movietimesupWS.model.User;


public class Controller extends HttpServlet{

	private static final long serialVersionUID = 1L;

	public void doGetMovie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MovieDao dao = new MovieDao(MovieDao.ES);
		try {
			List<Movie> movies =  dao.getAll();
			System.out.println("Size : "+movies.size());			
		} catch (SQLException e) {			
			e.printStackTrace();
		}
	}
	
	public void doGetUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserDao dao = new UserDao();
		try {
			List<User> users =  dao.getAll();
			System.out.println("Size : "+users.size());			
		} catch (SQLException e) {			
			e.printStackTrace();
		}
	}

}
