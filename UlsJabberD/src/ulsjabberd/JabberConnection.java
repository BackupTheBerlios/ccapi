/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

import ulsjabberd.xml.TagListener;
import ulsjabberd.xml.XMLTagParser;
import ulsjabberd.xml.Element;

public class JabberConnection implements TagListener{
	
	Socket s;
	DataInputStream din;
	DataOutputStream dout;
	
	XMLTagParser xtp;
	
	static Logger _logger = Logger.getLogger(JabberConnection.class);
	
	int state = 0;
	final static int PREAUTH = 0;
	final static int INAUTH = 1;
	final static int AUTHENTICATED = 2;
	
	Accepter a;
	/**
	 * constructor. 
	 * @param a
	 * @param s
	 */
	public JabberConnection(Accepter a, Socket s){
		this.s=s;
		this.a=a;
		try{
			din = new DataInputStream(s.getInputStream());
			dout = new DataOutputStream(s.getOutputStream());
			xtp = new XMLTagParser();
			xtp.attach(this);
		}
		catch(Exception e){
			removeConnection();
		}
	}
	
	/**
	 * @return Returns the s.
	 */
	public Socket getS() {
		return s;
	}
	
	/**
	 * @param s The s to set.
	 */
	public void setS(Socket s) {
		this.s = s;
	}
	
	/**
	 * remove this connection from it's parent stream  
	 *
	 */
	public void removeConnection(){
		// error handling routine to actually remove the connection from the connections vector of accepter.
	}
	
	/**
	 * triggered to actually work out some incoming data
	 */
	public void workOut(){
		try{
			int avai = s.getInputStream().available();
			byte[] bytes = new byte[avai];
			din.read(bytes, 0, avai);
			//System.out.println("Read: "+new String(bytes));
			parseData(bytes);
			
		}
		catch(Exception e){
			e.printStackTrace();
			this.removeConnection();
		}
	}
	
	/**
	 * handing the data to our parser
	 * @param bytes
	 */
	public void parseData(byte[] bytes){
		for(int i=0;i<bytes.length;i++){
			xtp.addChar((char)bytes[i]);
		}
	}
	
	/**
	 * called when a tag starts
	 */
	public void tagStart(Element e){
		System.out.println("TagStart: "+e.toString());
		// tag handler routine. 
		
		switch(state){
			case PREAUTH:
				// before authentication
				if(e.name.startsWith("stream:stream")){
					_logger.debug("Stream tag recieved, switching state of connection");
					state = INAUTH;
					// creating new xml parser for sanitys sake. 
					xtp = new XMLTagParser();
					xtp.attach(this);
				}
				else{
					//send error and break connection
					sendError(0, "error");
					this.breakConnection();
				}
				break;
				
			case INAUTH:
				// during authentication
				// need to check for ip tags
				if(e.name.startsWith("iq")){
					// check the content of the iq tag
					
					// the complete authentication logic. 
					
				}
				else{
					// send error and break connection
					sendError(0, "error");
					this.breakConnection();
				}
				break;
				
			default: 
				break;
		}
	}
	
	/**
	 * called when a tag stopps. 
	 */
	public void tagStop(Element e){
		System.out.println("TagStop: "+e.toString());
	}
	
	/**
	 * sending an error
	 * @param code
	 * @param errortext
	 */
	public void sendError(int code, String errortext){
		try{
			dout.writeChars("<error>");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * breaking connection and remove the connection from our connections vector
	 */
	public void breakConnection(){
		_logger.debug("Breaking connection");
		try{
			s.close();
		}
		catch(Exception e){
			//e.printStackTrace();
		}
		a.removeConnection(this);
	}
	
	/**
	 * send a keep alive
	 */
	public void keepAlive(){
		try{
			dout.writeChars("\n");
		}
		catch(Exception e){
			//e.printStackTrace();
			_logger.debug("Connection not writable");
			// error when sending keep alive
			this.breakConnection();
		}
	}
	
}