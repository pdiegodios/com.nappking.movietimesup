package com.nappking.movietimesupWS.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nappking.movietimesupWS.database.ConnectionDB;
import com.nappking.movietimesupWS.model.User;

public class UserDao implements IUserDao{
	
	private Connection _connection;
	private ResultSet _result;
	private PreparedStatement _statement;
	
	//Statements
	private String insert = "INSERT into "+User.TABLE+"("+User.USER+", "+User.NAME+", "+
			User.SCORE+", "+User.SECONDS+", "+User.LOCKED+", "+User.UNLOCKED+", "+User.LASTUPDATE+", "+
			User.LASTFOREGROUND+", "+User.DAYS+", "+User.MOVIES+", "+User.MASTERPIECE+", "+User.CULT+", "+
			User.AMERICA+", "+User.EUROPE+", "+User.ASIA+", "+User.EXOTIC+") values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private String update = "UPDATE "+User.TABLE +" set "+User.NAME+" = ?, "+User.SCORE+" = ?, "+
			User.SECONDS+" = ?, "+User.LOCKED+" = ?, "+User.UNLOCKED+" = ?, "+User.LASTUPDATE+" = ?, "+
			User.LASTFOREGROUND+" = ?, "+User.DAYS+" = ?, "+User.MOVIES+" = ?, "+User.MASTERPIECE+" = ?, "+
			User.CULT+" = ?, "+User.AMERICA+" = ?, "+User.EUROPE+" = ?, "+User.ASIA+" = ?, "+
			User.EXOTIC+" = ?  WHERE "+User.USER +" = ?";
	private String selectAll = "SELECT * from "+User.TABLE;
	private String selectByID = "SELECT * from "+User.TABLE+" WHERE "+User.USER +"= ?";
	
	public int save(User c) throws SQLException {
		int idInserted = 0;
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			_statement.setString(1, c.getUser());
			_statement.setString(2, c.getName());
			_statement.setInt(3, c.getScore());
			_statement.setInt(4, c.getSeconds());
			_statement.setString(5, toString(c.getLockedMovies()));
			_statement.setString(6, toString(c.getUnlockedMovies()));
			_statement.setLong(7, c.getLastUpdate());
			_statement.setLong(8, c.getLastForeground());
			_statement.setInt(9, c.getDays());
			_statement.setInt(10, c.getMovies());
			_statement.setInt(11, c.getMasterpiece());
			_statement.setInt(12, c.getCult());
			_statement.setInt(13, c.getAmerica());
			_statement.setInt(14, c.getEurope());
			_statement.setInt(15, c.getAsia());
			_statement.setInt(16, c.getExotic());
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

	public int update(User c) throws SQLException {
		int nupdate = 0;
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(update);
			_statement.setString(1, c.getName());
			_statement.setInt(2, c.getScore());
			_statement.setInt(3, c.getSeconds());
			_statement.setString(4, toString(c.getLockedMovies()));
			_statement.setString(5, toString(c.getUnlockedMovies()));
			_statement.setLong(6, c.getLastUpdate());
			_statement.setLong(7, c.getLastForeground());
			_statement.setInt(8, c.getDays());
			_statement.setInt(9, c.getMovies());
			_statement.setInt(10, c.getMasterpiece());
			_statement.setInt(11, c.getCult());
			_statement.setInt(12, c.getAmerica());
			_statement.setInt(13, c.getEurope());
			_statement.setInt(14, c.getAsia());
			_statement.setInt(15, c.getExotic());
			_statement.setString(16, c.getUser());
			
			nupdate = _statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			_connection.close();
			_statement.close();
		}
		return nupdate;
	}

	public List<User> getAll() throws SQLException {
		List<User> users = new ArrayList<User>();
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(selectAll);
			_result = _statement.executeQuery();				
			while (_result.next()) {
				User user = new User();
				user.setId(_result.getLong(1));
				user.setUser(_result.getString(2));
				user.setName(_result.getString(3));
				user.setScore(_result.getInt(4));
				user.setSeconds(_result.getInt(5));
				user.setLockedMovies(fromString(_result.getString(6)));
				user.setUnlockedMovies(fromString(_result.getString(7)));
				user.setLastUpdate(_result.getLong(8));
				user.setLastForeground(_result.getLong(9));
				user.setDays(_result.getInt(10));
				user.setMovies(_result.getInt(11));
				user.setMasterpiece(_result.getInt(12));
				user.setCult(_result.getInt(13));
				user.setAmerica(_result.getInt(14));
				user.setEurope(_result.getInt(15));
				user.setAsia(_result.getInt(16));
				user.setExotic(_result.getInt(17));
				users.add(user);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return users;
	}

	public User get(String idUser) throws SQLException {
        User user = null;		
		try {
			_connection = new ConnectionDB().getConnection();
			_statement = _connection.prepareStatement(selectByID);
			_statement.setString(1, idUser);
			_result = _statement.executeQuery();			
			while (_result.next()) {
				user = new User();
				user.setId(_result.getLong(1));
				user.setUser(_result.getString(2));
				user.setName(_result.getString(3));
				user.setScore(_result.getInt(4));
				user.setSeconds(_result.getInt(5));
				user.setLockedMovies(fromString(_result.getString(6)));
				user.setUnlockedMovies(fromString(_result.getString(7)));
				user.setLastUpdate(_result.getLong(8));
				user.setLastForeground(_result.getLong(9));
				user.setDays(_result.getInt(10));
				user.setMovies(_result.getInt(11));
				user.setMasterpiece(_result.getInt(12));
				user.setCult(_result.getInt(13));
				user.setAmerica(_result.getInt(14));
				user.setEurope(_result.getInt(15));
				user.setAsia(_result.getInt(16));
				user.setExotic(_result.getInt(17));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return user;
	} 

	
	//AUXILIAR METHODS
	private String toString(ArrayList<Integer> values){
		String result="";
		if(values!=null && !values.isEmpty()){
			for(int i=0;i<values.size()-1;i++){
				result=result+String.valueOf(values.get(i))+";";
			}
			result=result+values.get(values.size()-1);
		}
		return result;
	}
	
	private ArrayList<Integer> fromString(String values){
		ArrayList<Integer> result = new ArrayList<Integer>();
		if(values!=null && !values.equals("")){
			String[] arrayValues = values.split(";");
			for(String value: arrayValues){
				result.add(Integer.parseInt(value));
			}
		}
		return result;
	}
}
