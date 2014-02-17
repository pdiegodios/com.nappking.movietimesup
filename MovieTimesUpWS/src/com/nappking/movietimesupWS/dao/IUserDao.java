package com.nappking.movietimesupWS.dao;

import java.sql.SQLException;
import java.util.List;

import com.nappking.movietimesupWS.model.User;

public interface IUserDao {

	public int save(User user) throws SQLException;
	public int update(User user) throws SQLException;
	public User get(String idUser) throws SQLException;
	public List<User> getAll() throws SQLException;
}
