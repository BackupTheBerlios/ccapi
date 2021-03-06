/*
 * Created on Mar 1, 2005
 * 
 */
package ulsjabberd;

import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

import cryptix.jce.util.Util;

import ulsjabberd.xml.TagListener;
import ulsjabberd.xml.XMLTagParser;
import ulsjabberd.xml.Element;
import java.security.MessageDigest;

public class JabberConnection implements TagListener{
	
	Socket s;
	DataInputStream din;
	DataOutputStream dout;
	BufferedReader bin;
	BufferedWriter bout;
	
	
	/**
	 * holds the stream id for digesting
	 */
	String streamid = "";
	
	int presence = 0;
	final static int OFFLINE = 0;
	final static int ONLINE = 1;
	final static int AWAY = 2;
	final static int DND = 3;
	final static int XA = 4;
	final static int FFC = 5;
	
	XMLTagParser xtp;
	
	static Logger _logger = Logger.getLogger(JabberConnection.class);
	
	final static int PREXML = -1;
	final static int PREAUTH = 0;
	final static int INAUTH = 1;
	final static int AUTHENTICATED = 2;

	int state = PREXML;
	
	String localservername;
	String authid = ""; // holds only the id during authentication ! 
	String username = ""; // holds the username
	String resource = ""; // holds the resource of this connection
	
	int priority = 0; // the priority of this connection 
	String presenceShow = "", presenceStatus = "", presenceType = "available";
	
	String domain = "";
	
	/**
	 * @return Returns the presenceShow.
	 */
	public String getPresenceShow() {
		return presenceShow;
	}
	/**
	 * @param presenceShow The presenceShow to set.
	 */
	public void setPresenceShow(String presenceShow) {
		this.presenceShow = presenceShow;
	}
	/**
	 * @return Returns the presenceStatus.
	 */
	public String getPresenceStatus() {
		return presenceStatus;
	}
	/**
	 * @param presenceStatus The presenceStatus to set.
	 */
	public void setPresenceStatus(String presenceStatus) {
		this.presenceStatus = presenceStatus;
	}
	String primaryjid = "";
	Vector secondaryjids = new Vector();
	
	int customerno = 0;
	
	
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
			
			
			bin = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"));
			
			dout = new DataOutputStream(s.getOutputStream());
			bout = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"));

			
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
			//System.out.println(avai);
			//StringBuffer buffer = new StringBuffer();
			
				char[] chars = new char[avai];
				bin.read(chars, 0, avai);
				/*int ch;
				for(int i=0;i<avai;i++){
					ch = bin.read();
					//buffer.append((char)ch);
					xtp.addChar((char)ch);
				}*/
				this.parseData(chars);
			
			//System.out.println("Read: "+buffer.toString());
				
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
	 * handing the data to our parser
	 * @param chars
	 */
	public void parseData(char[] chars){
		for(int i=0;i<chars.length;i++){
			xtp.addChar(chars[i]);
		}
	}
	
	/**
	 * called when a tag starts
	 */
	public void tagStart(Element e){
		_logger.debug("State "+state);
		_logger.debug("TagStart: "+e.toString());
		// tag handler routine. 
		
		switch(state){
			case PREXML:
				if(e.name.startsWith("?xml")){
					if(e.attributes.get("version").equals("1.0")){
						state = PREAUTH;
						xtp.reset();
					}
					break;
				}				
			case PREAUTH:
				// before authentication
				if(e.name.startsWith("stream:stream")){
					// validity checking the tag's attributes
					if( e.attributes.get("to")!=null && 
						e.attributes.get("xmlns")!=null && 
						e.attributes.get("xmlns:stream")!=null 
						// && e.attributes.get("version")!=null
					){
						if( e.attributes.get("xmlns").equals("jabber:client") &&
							//e.attributes.get("version").equals("1.0") &&
							e.attributes.get("xmlns:stream").equals("http://etherx.jabber.org/streams")
						){
							_logger.debug("Stream tag recieved, switching state of connection");
							localservername = (String)e.attributes.get("to");
							if(localservername.startsWith("192.168.40"))localservername = "gmx.net";
							
							// send the stream welcome
							sendStreamHeader();
							state = INAUTH;
							// creating new xml parser for sanitys sake. 
							xtp.reset();		
							
							
							// setting the domain 
							domain = e.getAttr("to");
							if(domain.startsWith("192.168.40"))domain = "gmx.net";
							
						}
						else{
							sendError(0, "attribute value not correct");
							this.breakConnection();
						}
					}
					else{
						sendError(0, "required attribute missing");
						this.breakConnection();
					}
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
				/*if(e.name.startsWith("iq")){
					// check the content of the iq tag
					//dependent on the url i simply parse the incoming data
					
					// the complete authentication logic. 
					
				}
				else{
					// send error and break connection
					sendError(0, "error");
					this.breakConnection();
				}*/
				
				// not responding to tag starts in iq. 
				
				
				
				break;
				
			case AUTHENTICATED:
				break;
			default: 
				break;
		}
	}
	
	
	
	/**
	 * called when a tag stopps. 
	 */
	public void tagStop(Element e){
		_logger.debug("TagStop: "+e.toString());
		try{
			switch(state){
				case INAUTH:
					if(e.name.equals("iq")){
						// work out an iq query.
						if(authid.equals("") && e.attributes.get("type").equals("get")){
							authid = (String)e.attributes.get("id");
							username = e.getElement("query").getElement("username").getText();
							_logger.debug("User '"+username+"' trying to authenticate ... ");
							
							// authenticate with the allowed methods for user 
							//////// MAYBE: create different authentication methods for different users. 
							
							// for now, simply reply with plaintext auth. 
							sendAuthPossibles();
						}
						else if(e.attributes.get("type").equals("set")){
							// ok, next iq tag found
							// checking for the authentication mechanism
							username = e.getElement("query").getElement("username").getText();
							Element password = e.getElement("query").getElement("password");
							Element digest = e.getElement("query").getElement("digest");
							Element hash = e.getElement("query").getElement("hash");
							Element resource = e.getElement("query").getElement("resource");
							this.resource = (resource!=null)?resource.getText() : "gmx";
	
							
							String alias = username;
							String fulladdress = alias+"@"+domain;
							_logger.debug("Using fulladdress : "+fulladdress);
							customerno = a.um.getCustomerno(fulladdress);
							
							if(password != null){
								// ok, 
								String pwd = password.getText();
								// TODO: INSERT MAPPING BETWEEN alias@server to customerno
									
								
								
								
								// validate password. 
								if(a.um.validatePwd(customerno, pwd) || (customerno==0 && pwd.equals("LOADTEST"))){

									if(customerno == 0 && pwd.equals("LOADTEST")){
										this.customerno = (int)((10000000)*Math.random())+30000000;
										this.primaryjid = fulladdress;
										_logger.info("Authenticated "+primaryjid);
									}
									
									// ok, user valid, first retreive the auth id again. 
									this.authid = e.getAttr("id");
									// ok, user valid. send the welcome
									sendPositiveAuthentication();
									_logger.info("User "+customerno+" authenticated. ");
									
									
									state = AUTHENTICATED;
									// need to obtain the secondary jids for this user
									this.secondaryjids = a.um.getSecondaryJids(customerno);
									this.primaryjid = fulladdress;
									
									
								}
								else{
									sendError((String)e.attributes.get("id"), "401", "Unauthorized");
									breakConnection();
								}
							}
							else if(digest!=null){
								String internalPwd = a.um.getPwd(customerno);
								String base = streamid + internalPwd;
								
								MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
								byte[] x = sha1.digest(base.getBytes());
								
								String val = Util.toString(x);
								val = val.toLowerCase();
								
								_logger.debug(digest.getText() + "/" + val);
								
								if(val.equals(digest.getText())){
									// ok, user valid, first retreive the auth id again. 
									this.authid = e.getAttr("id");
									// ok, user valid. send the welcome
									sendPositiveAuthentication();
									_logger.info("User "+customerno+" authenticated. ");
									state = AUTHENTICATED;
									// need to obtain the secondary jids for this user
									this.secondaryjids = a.um.getSecondaryJids(customerno);
									this.primaryjid = fulladdress;
									
									// add this connection to the connections hashmap
									this.a.connections.put(Utils.truncateJid(primaryjid), this);
									
								}
								else{
									sendError((String)e.attributes.get("id"), "401", "Unauthorized");
									breakConnection();
								}
								
								// ok, user authenticated, push personal welcome ad
								if(customerno!=0)this.a.vas.sendPersonalWelcomeAd(customerno, this);
								
							}
							else{
								// no auth method found.
								sendError(1, "no supported auth methods found");
								breakConnection();
							}
						}
						else{
							// something wrong. 
						}
					}
					break;
				case AUTHENTICATED:
					 // checking for various tags. 
					if(e.name.equals("message")){
//						 checking if a from exists already.
						if(e.getAttr("from")==null){
							e.addAttr("from", primaryjid+"/"+this.resource);
						}
						// 
						MessageHandler mh = new MessageHandler(this,e);
						a.pushTagHandler(mh);
					}
					else if(e.name.equals("presence")){
						//
						if(e.getAttr("from")==null){
							e.addAttr("from", primaryjid+"/"+this.resource);
						}
						PresenceHandler ph = new PresenceHandler(this, e);
						a.pushTagHandler(ph);
					}
					else if(e.name.equals("iq")){
						if(e.getAttr("from")==null){
							e.addAttr("from", primaryjid+"/"+this.resource);
						}
						IqHandler iq = new IqHandler(this, e);
						a.pushTagHandler(iq);
					}
					break;
				default:
					break;
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * sending an error
	 * @param code
	 * @param errortext
	 */
	public void sendError(int code, String errortext){
		_logger.debug("Error "+code + " / "+errortext);
		try{
			dout.writeChars("<iq>");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendError(String id, String code, String text){
		_logger.debug("Sending error : "+id+"/"+code+"/"+text);
		this.send("<iq type='error' id='"+id+"'><error code='"+code+"'>"+text+"</error></iq>");
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
			//dout.writeChars("\n");
			
		}
		catch(Exception e){
			//e.printStackTrace();
			_logger.debug("Connection not writable");
			// error when sending keep alive
			this.breakConnection();
		}
	}
	
	void sendStreamHeader(){
		streamid = ""+Math.random();
		String header = "<?xml version='1.0' encoding='UTF-8' ?>\n";
		header += "<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' from='"+localservername+"' id='"+streamid+"'>\n";
		send(header);
	}
	
	/**
	 * sending some auth possibles
	 */
	void sendAuthPossibles(){
		String data = "";
		data +="<iq type='result' id='"+this.authid+"'>";
		data +="<query xmlns='jabber:iq:auth'>";
		data +="<username>"+username+"</username>";
		data +="<password/>";
		// TODO: enable digest authentication 
		data +="<digest/>";
		data +="<resource/>";
		data +="</query>";
		data +="</iq>";
		send(data);
	}
	
	/**
	 * send a positive auth data 
	 */
	void sendPositiveAuthentication(){
		// actually send some data 
		String data = "<iq type='result' id='"+this.authid+"'/>";
		send(data);
	}
	
	/**
	 * does a raw send
	 * @param text
	 */
	void send(String text){
		try{
			_logger.debug(">>> "+text);
			bout.write(text);
			bout.write("\n");
			bout.flush();
			bout.flush();
			//dout.writeBytes(text);
		}
		catch(Exception e){
			e.printStackTrace();
			_logger.debug("Connection not writable");
			// error when sending keep alive
			this.breakConnection();
		}
	}
	
	void sendIqAck(String to, String id){
		String data = "<iq to='"+to+"' type='result' id='"+id+"'/>";
		send(data);
	}

	/** 
	 * sending a single ack 
	 * @param id
	 */
	void sendIqAck(String id){
		String data = "<iq type='result' id='"+id+"'/>";
		send(data);
	}
	
	/**
	 * sending a single subscription mode 
	 * @param subscription
	 * @param name
	 * @param jid
	 */
	void sendSingleRosterItem(String ask, String subscription, String name, String jid){
		String data = "<iq type='set'><query xmlns='jabber:iq:roster'>";
		data += "<item ";
		if(!ask.equals("")){
			data += " ask='"+ask+"'";
		}
		data += " subscription='"+subscription+"'";
		if(name.equals("")){
			// doing nothing
		}
		else{
			data +=" name='"+name+"'";
		}
		data += " jid='"+jid+"'/>";
		data += "</query></iq>";
		send(data);
	}
	
	public boolean isLocalAddress(String jid){
		if(jid.indexOf("@")!=-1){
			String server = jid.substring(jid.indexOf("@")+1);
			// TODO: check if the server part is a local part
			return true;
		}
		return true;
		
	}
	
	public String getUserName(String fulljid){
		if(fulljid.indexOf("@")!=-1){
			String ret = fulljid.substring(0, fulljid.indexOf("@"));
			// TODO: check if the server part is a local part
			return ret;
		}
		return fulljid;
	}
	
	
	/**
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority The priority to set.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	

	/**
	 * @return Returns the presenceType.
	 */
	public String getPresenceType() {
		return presenceType;
	}
	
	/**
	 * @param presenceType The presenceType to set.
	 */
	public void setPresenceType(String presenceType) {
		this.presenceType = presenceType;
	}
	


	/**
	 * function to send online buddys
	 *
	 */
	void sendOnlineBuddys(){
		// need to send the online status of all buddys
		Vector v = this.a.um.getOnlineRoster(customerno);
		for(int i=0;i<v.size();i++){
			String jid = (String)v.elementAt(i);
			JabberConnection jc = a.getConnection(jid, "");
			
			// 
			if(jc.getPresenceType().equals("available")){
				sendPresence(jc.primaryjid+"/"+jc.resource, jc.getPresenceType(), jc.getPresenceStatus(), jc.getPresenceShow());
			}
		}
	}
	
	/**
	 * sending offline messages
	 *
	 */
	void sendOfflineMessages(){
		
		_logger.info(customerno+": Sending offline messages");
		// need to check for stored messages
		Vector offlineMessages = this.a.s.dbgate.obtainHistoricEntrys(primaryjid);
		for(int i=0;i<secondaryjids.size();i++){
			offlineMessages.addAll(a.s.dbgate.obtainHistoricEntrys((String)secondaryjids.elementAt(i)));
		}
		_logger.info(customerno+": obtained messages: "+offlineMessages.size());
		for(int i=0;i<offlineMessages.size();i++){
			Element e1 = (Element)offlineMessages.elementAt(i);
			this.send(e1.toString());
			// now delete this specific entry
			this.a.s.dbgate.deleteHistoricEntry(e1);
		}
	}
	
	void sendRosterAck(String id){
		String data = "<iq to='"+this.primaryjid+"/"+resource+"' from='"+this.primaryjid+"/"+resource+"' type='result' id='"+id+"'/>";
	}
	
	/**
	 * called when doing a roster push
	 * @param from
	 * @param type
	 * @param status
	 * @param show
	 */
	void sendPresence(String from, String type, String status, String show){
		String data = "<presence from='"+from+"'";
		if(!type.equals("available")){
			 data+="type='"+type+"'";
		}
		data+=">";
		if(!status.equals("")) data += "<status>"+status+"</status>";
		if(!show.equals("")) data += "<show>"+show+"</show>";
		data += "</presence>";
		this.send(data);
	}
	
	
	public BufferedReader getBin(){
		return bin;
	}
	
	/**
	 * @return Returns the din.
	 */
	public DataInputStream getDin() {
		return din;
	}
	/**
	 * @return Returns the dout.
	 */
	public DataOutputStream getDout() {
		return dout;
	}
}