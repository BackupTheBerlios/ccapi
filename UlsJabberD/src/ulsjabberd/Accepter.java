/*
 * Created on 09.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package ulsjabberd;

import java.net.*;
import java.util.*;
import java.io.*;

import org.apache.log4j.*;

public class Accepter extends Thread{
	
	static Logger _logger = Logger.getLogger(Accepter.class);
	
	ServerSocket ss;
	Starter s;
	Vector connections, pollers;
	
	Pinger pinger; 
	
	/**
	 * plain constructor
	 * @param s
	 */
	public Accepter(Starter s){
		this.s=s;
		try {
			_logger.info("Opening server socket at port "+s.getConfig().getProperty("port"));
			ss = new ServerSocket(Integer.parseInt(s.getConfig().getProperty("port")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR - QUITTING.");
			System.exit(0);
		}
		connections = new Vector();
		pollers = new Vector();
		// construct the pinger
		pinger = new Pinger(this);
		
	}
	
	/**
	 * adding a data poller
	 */
	public void addDataPoller(){
		this._logger.info("Adding a data poller.");
		DataPoller dp = new DataPoller(this);
		pollers.add(dp);
		dp.start();
	}
	
	/**
	 * called to remove one data poller
	 */
	public void removeDataPoller(){
		_logger.info("Removing a poller.");
		if(pollers.size()>0)pollers.remove(0);
	}
	
	/**
	 * run thread
	 */
	public void run(){
		while(true){
			// accept socket connections
			try{
				Socket s=ss.accept();
				JabberConnection jc = new JabberConnection(this, s);
				connections.add(jc);
				_logger.debug("Received a connection, #connections: " + connections.size());
				if(connections.size()>(pollers.size()*30)){
					// need to add a new poller
					addDataPoller();
				}				
			}
			catch(Exception e){
				e.printStackTrace();
				try{
					sleep(1000);
				}
				catch(Exception e1){
					// ommitting this exception
					// e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * removing a jabber connection 
	 * @param jc
	 */
	public void removeConnection(JabberConnection jc){
		connections.remove(jc);
		_logger.info("Removing connection, #connections "+connections.size());
	}	

}