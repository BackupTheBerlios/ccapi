/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

/**
 * @author us
 *
 * a gate to our database
 * 
 */

import java.sql.*;
import org.apache.log4j.*;

public class DBGate {
	
	static Logger _logger = Logger.getLogger(DBGate.class);
	
	/**
	 * holds the connection to the database
	 */
	Connection connection; 
	
	/**
	 * a dbgate constructor
	 */
	DBGate(){
		_logger.info("Initializing database gate");
		// initialize the connection
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
				String l = ex.toString();
			}
			connect();		
	}
	
	/**
	 * actually connects the connection to the database
	 */
	public void connect(){
		_logger.info("Connecting to database");
		try{
			connection =
				DriverManager.getConnection(
					"jdbc:mysql://192.168.40.122/test",
					"root",
					"root");
			_logger.info("Database connected. ");
		} catch (SQLException ex) {
			ex.printStackTrace();
			String l = "";
			l += ("SQLException: " + ex.getMessage());
			l += ("SQLState: " + ex.getSQLState());
			l += ("VendorError: " + ex.getErrorCode());
			_logger.fatal(ex);
			_logger.fatal(l);
		}
	}
	
	/**
	 * actually disconnects the connection to the database
	 */
	public void disconnect(){
		
	}
	
	/**
	 * checks if the database is initialized already. 
	 * @return
	 */
	public boolean checkIfInitialized(){
		return true;
	}
	
	/**
	 * creates the table structures from scratch. 
	 */
	public void initialize(){
		try {
			Statement stmt = connection.createStatement();
			String statement = "drop table if exists roster;";
			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);
			statement =
				"CREATE table roster (jid varchar(255), type varchar(255), price_buy float, date_buy varchar(255), price_sell float, date_sell varchar(255), stoplevel float, change_percent float, change_net float , cash float);";
			i = stmt.executeUpdate(statement);
			if(stmt!=null)stmt.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
			
	}
}
