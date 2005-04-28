/*
 * Created on 27.04.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package Examples;



import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import CCAPI.Candle;


public class DataMirror {

	
	public DataMirror(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			String l = ex.toString();
		}
		connect();
		
		
		try {
			Statement stmt = con2.createStatement();
			String statement = "CREATE table symboldata (isin varchar(250)," +
					" timestamp varchar(250), open double, hi double, low double, " +
					"close double, volume int(11), wkn varchar(50));";
			int i = stmt.executeUpdate(statement);
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		 * isin       varchar(250)  latin1_swedish_ci          PRI                      select,insert,update,references         
timestamp  varchar(250)  latin1_swedish_ci          PRI                      select,insert,update,references         
open       double        NULL               YES             (NULL)           select,insert,update,references         
hi         double        NULL               YES             (NULL)           select,insert,update,references         
low        double        NULL               YES             (NULL)           select,insert,update,references         
close      double        NULL               YES             (NULL)           select,insert,update,references         
volume     int(11)       NULL               YES             (NULL)           select,insert,update,references         
wkn        varchar(50)   latin1_swedish_ci  YES 
		 */
		
		try{
			
			// select all data
			String statement = "select * from symboldata;";
			
			Statement stmt1 = con1.createStatement();
			Statement stmt2 = con2.createStatement();
			ResultSet rs = stmt1.executeQuery(statement);
	
			Vector tempvector = new Vector();
			int k = 0;
			
			//int cc=rsmd.getColumnCount();
			while (rs.next()) {
				
				String statement2 = "insert into symboldata values( '"+rs.getString(1)+"', " +
						"'"+rs.getString(2)+"', "+rs.getDouble(3)+", "+rs.getDouble(4)+", " +
						rs.getDouble(5)+", "+rs.getDouble(6)+", "+rs.getInt(7)+", '"+rs.getString(8)+"');";
				
				stmt2.executeUpdate(statement2);
								
			}
			
			// close the statements
			stmt1.close();
			stmt2.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		// insert all data. 
		
		disconnect();
	}


	Connection con1 = null, con2 = null;

	void connect() {
		try {
			con1 = DriverManager.getConnection(
					"jdbc:mysql://localhost/jbossdb", "root", "");
			con2 = DriverManager.getConnection(
					"jdbc:mysql://localhost/target", "root", "");
			
			System.out.println("Database connected.");
		} catch (SQLException ex) {
			ex.printStackTrace();
			String l = "";
			l += ("SQLException: " + ex.getMessage());
			l += ("SQLState: " + ex.getSQLState());
			l += ("VendorError: " + ex.getErrorCode());
			System.exit(0);
		}
	}

	void disconnect() {

		try {
			con1.close();
			con2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	
	public static void main(String[] args) {
		DataMirror dm = new DataMirror();
	}
}
