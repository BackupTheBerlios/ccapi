package ulsjabberd;
/*
 * Created on 09.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */

import java.util.*;
import java.io.*;
import org.apache.log4j.*;

public class Starter {
	
	static Logger _logger = Logger.getLogger(Starter.class);
	
	
	/**
	 * @return Returns the config.
	 */
	public Properties getConfig() {
		return config;
	}

	private final Properties config;
	
	public Starter(){
		config = new Properties();
		try{
			// constructing the properties for configuration 
			config.load(new FileInputStream("config.file"));
			// 
			String servername = config.getProperty("servername", "localhost");

		}
		catch(Exception e){
			_logger.fatal("Properties not found.");
		}
	}
	
	public static void main(String[] args) {
		// do some basic stuff in here.
		Starter s = new Starter();
		
	}
	
}
