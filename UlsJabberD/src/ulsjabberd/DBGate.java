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
import ulsjabberd.xml.Element;
import java.util.Vector;

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
					"jdbc:mysql://192.168.40.122/jabberd",
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
			
			_logger.debug("Initializing database.");
			// initializing the onlineMappings
			Statement stmt = connection.createStatement();
			
			_logger.debug("dropping online mappings ...");
			String statement = "drop table if exists onlineMappings;";
			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);
			
			_logger.debug("creating online mappings ...");
			statement =
				"CREATE table onlineMappings (jid varchar(255), index(jid(10)), resource varchar(255), server varchar(255));";
			i = stmt.executeUpdate(statement);
			
			// initialize the history storage
			_logger.debug("dropping historic data ...");
			statement = "drop table if exists historicData;";
			i = stmt.executeUpdate(statement);
			
			_logger.debug("creating historic data ...");
			statement = "CREATE table historicData (messageid BIGINT AUTO_INCREMENT PRIMARY KEY , targetjid varchar(255), index(targetjid(10)), element LONGTEXT);";
			i = stmt.executeUpdate(statement);
			if(stmt!=null)stmt.close();
			
			_logger.debug("Database initialized.");
		}
		catch(Exception e){
			_logger.fatal(e);
			System.exit(0);
		}
			
	}
	
	
	/**
	 * actually stores an historic entry in the database
	 * @param chunk
	 * @return
	 */
	
	public boolean storeHistoricEntry(Element chunk){
		try{
			Statement stmt = connection.createStatement();
			String statement = "insert into historicData (targetjid, element) values ('"+chunk.getAttr("to").toLowerCase()+"', '"+encode(chunk.toString())+"');";
			int i = stmt.executeUpdate(statement);
			if(stmt!=null)stmt.close();
		}
		catch(Exception e){
			_logger.fatal(e);
		}
		
		return true;
	}

	
	public Vector obtainHistoricEntrys(String jid){
		Vector ret = new Vector();
		
		try{
			Statement stmt = connection.createStatement();
			String statement = "select * from historicData where targetjid='"+jid.toLowerCase()+"' order by messageid;";
			_logger.debug(statement);
			ResultSet rs = stmt.executeQuery(statement);
			
			while(rs.next()){
				Element chunk = new Element();
				chunk.buildFromString(decode(rs.getString(3)));
				chunk.storageID = rs.getLong(1);
				_logger.debug("Retrieved: "+chunk.toString());
				ret.addElement(chunk);
			}
			if(stmt!=null)stmt.close();
		}
		catch(Exception e){
			_logger.fatal(e);
		}
		
		return ret;
	}
	
	
	/**
	 * encodes to base 64 
	 * @param input
	 * @return
	 */
	String encode(String input){
		input = Base64Coder.encode(input);
		return input;
	}
	
	/**
	 * decodes to base 64
	 * @param input
	 * @return
	 */
	String decode(String input){
		input = Base64Coder.decode(input);
		return input;
	}
	
}
