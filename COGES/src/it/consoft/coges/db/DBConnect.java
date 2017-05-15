package it.consoft.coges.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {

	private final String jdbcURL =  "jdbc:sqlserver://BELMONTNEW:1666;databaseName=TIRESIA;user=tiresiaadm;password=admin$1994;";

	public Connection getConnection(){
		
		Connection conn = null;
		
		try {
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(jdbcURL); 
		
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		
		
		return conn;
	}
	
}
