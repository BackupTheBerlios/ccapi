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
import org.apache.log4j.xml.DOMConfigurator;
import com.mysql.*;
import ulsjabberd.cronservices.*;

public class Starter {
	
	static Logger _logger = Logger.getLogger(Starter.class);
	
	Clock clock;
	/**
	 * @return Returns the config.
	 */
	public Properties getConfig() {
		return config;
	}

	private final Properties config;
	public Accepter a;
	DBGate dbgate;
	WebServer ws; 
	
	public Starter(){
		
		// the log4j configuration
		DOMConfigurator.configure("log4j.xml");
		
		// the internal configuration file
		config = new Properties();
		try{
			// constructing the properties for configuration 
			config.load(new FileInputStream("config.file"));
			// 
			String servername = config.getProperty("servername", "localhost");
			System.out.println(config.getProperty("port"));
			
			// initialize the database
			dbgate = new DBGate();
			//dbgate.initialize();
			
			// ok,init done. open the Accepter
			a=new Accepter(this);
			a.start();
			// done with accepting. 
			// oopening up the webserver
			ws = new WebServer(this,8080);
			ws.start();
			
			// start the cron clock
			clock = new Clock(this);
			
			Abendjob aj = new Abendjob(this);
			clock.cronjobs.add(aj);
			
			clock.start();
			
		}
		catch(Exception e){
			_logger.fatal(e);
		}
	}
	
	public static void main(String[] args) {
		// do some basic stuff in here.
		Starter s = new Starter();
		
	}
	
}
