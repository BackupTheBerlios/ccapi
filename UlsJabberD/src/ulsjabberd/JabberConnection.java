/*
 * Created on Mar 1, 2005
 * 
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
	String presenceShow, presenceStatus, presenceType;
	
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
		System.out.println("State "+state);
		System.out.println("TagStart: "+e.toString());
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
							// send the stream welcome
							sendStreamHeader();
							state = INAUTH;
							// creating new xml parser for sanitys sake. 
							xtp.reset();		
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
		System.out.println("TagStop: "+e.toString());
		try{
			switch(state){
				case INAUTH:
					if(e.name.equals("iq")){
						// work out an iq query.
						if(authid.equals("")){
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
							Element password = e.getElement("query").getElement("password");
							Element digest = e.getElement("query").getElement("digest");
							Element hash = e.getElement("query").getElement("hash");
							Element resource = e.getElement("query").getElement("resource");
							this.resource = (resource!=null)?resource.getText() : "gmx";
	
							if(password != null){
								// ok, 
								String pwd = password.getText();
								// TODO: INSERT MAPPING BETWEEN alias@server to customerno
								
								String domain = e.getAttr("to");
								String alias = username;
								String fulladdress = alias+"@"+domain;
								
								customerno = a.um.getCustomerno(fulladdress);
								
								/*
								try{
									this.customerno=Integer.parseInt(username);
								}
								catch(Exception ex){
									// error, therefore bailing out. 
									ex.printStackTrace();
									sendError((String)e.attributes.get("id"), "401", "Unauthorized");
									breakConnection();
									break;
								}
								*/
								
								// validate password. 
								if(a.um.validatePwd(customerno, pwd)){
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
							else if(digest!=null && hash!=null){
								
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
			dout.writeChars("\n");
		}
		catch(Exception e){
			//e.printStackTrace();
			_logger.debug("Connection not writable");
			// error when sending keep alive
			this.breakConnection();
		}
	}
	
	void sendStreamHeader(){
		//String header = "<?xml version='1.0?>\n";
		String header = "<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' from='"+localservername+"' id='"+Math.random()+"'>";
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
		//data +="<digest/>";
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
			dout.write(text.getBytes());
		}
		catch(Exception e){
			//e.printStackTrace();
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
	void sendSingleRosterItem(String subscription, String name, String jid){
		String data = "<iq type='set'><query xmlns='jabber:iq:roster'>";
		data += "<item ask='subscribe' subscription='none'";
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
	 * sending a presence 
	 * @param to
	 * @param type
	 * @param show
	 * @param status
	 */
	void sendPresence(String from, String type, String show, String status){
		// actually send some data 
		String data = "<presence from='"+from+"'>";
		if(!show.equals(""))data+="<show>"+show+"</show>";
		if(!status.equals(""))data+="<status>"+show+"</status>";
		data += "</presence>";
		send(data);
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
				sendPresence(jc.primaryjid, jc.getPresenceType(), jc.getPresenceShow(), jc.getPresenceStatus());
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
		}
	}
}