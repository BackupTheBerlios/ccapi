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
 *	Class to handle server 2 server connections ... 
 *
 */
import java.net.*;
import java.io.*;

public class Server2ServerConnection {
	
	String hostentry = "";
	Socket s;
	
	Server2ServerConnection(String hostentry){
		this.hostentry = hostentry;
		connect();
	}
	
	/**
	 * called to actually connect to the other server. 
	 *
	 */
	void connect(){
		try{
			Socket s = new Socket(hostentry, 5269);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * called to send data to this server. 
	 * @param data
	 */
	void send(String data){
		
	}
	
	
}
