package com.nappking.movietimesupWS.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * En la clase se carga el Driver (mysql en este caso), la dirección de la base de datos 
 * y el login y pass de la misma para poder acceder a los datos.
 * @author pdiego
 */
public class ConnectionDB {	
	
	private Connection c;	
	
	public Connection getConnection()  {	 
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection("jdbc:mysql://ec2-23-21-211-172.compute-1.amazonaws.com/movietimesup", "nappking", "NpI8j8uF");
			//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			//c = DriverManager.getConnection("jdbc:sqlserver://localhost\\glasof;databaseName=empresas;instanceName=GLASOF;integratedSecurity=true;");
		}catch (SQLException e) {
			System.out.println("Error al conectar con la Base de Datos");
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			System.out.println("Driver no encontrado");
			e.printStackTrace();
		} 
		return c;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		new ConnectionDB().getConnection();
	}
}
