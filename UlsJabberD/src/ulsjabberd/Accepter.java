/*
 * Created on 09.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package ulsjabberd;

import java.net.*;
import java.io.*;

import org.apache.log4j.*;

public class Accepter extends Thread{
	
	static Logger _logger = Logger.getLogger(Accepter.class);
	
	ServerSocket ss;
	Starter s;
	// plain constructor
	public Accepter(Starter s){
		this.s=s;
		try {
			ss = new ServerSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			// accept socket connections
		}
	}
	
}
