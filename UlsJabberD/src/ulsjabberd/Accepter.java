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
	Vector pollers;
	
	public Vector rawConnections;
	public HashMap connections;
	
	// package local classes
	Pinger pinger;
	Vector workerThreads;
	public UserManager um; 
	
	Vector tagHandlers;
	public ValueAddingServices vas;
	XmlRouter xmlr;
	
	long packetsserved = 0;
	long messagessent = 0;
	
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
		connections = new HashMap();
		rawConnections = new Vector();
		pollers = new Vector();
		tagHandlers = new Vector();
		// construct the pinger
		pinger = new Pinger(this);
		// initializing the user manager
		um = new UserManager(this);
		// initialize the xml router
		xmlr = new XmlRouter(this);
		// initialize the value adding services
		vas = new ValueAddingServices(this);
		
		// initialize the worker threads
		initializeWorkerThreads();
		
		
		
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
				rawConnections.add(jc);
				_logger.debug("Received a connection, #connections: " + rawConnections.size());
				if(rawConnections.size()>(pollers.size()*30)){
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
		rawConnections.remove(jc);
		
		connections.remove(jc.primaryjid);
		_logger.info("Removing connection, #connections : "+rawConnections.size());
	}	

	public void initializeWorkerThreads(){
		workerThreads = new Vector();
		for(int i=0;i<1;i++){
			TagHandlerWorkerThread thwt = new TagHandlerWorkerThread(this);
			workerThreads.add(thwt);
			thwt.start();
		}
	}
	
	/**
	 * pushes a tag handler to our tagHandler vector 
	 * @param t
	 */
	void pushTagHandler(TagHandler t){
		tagHandlers.addElement(t);
	}
	
	/**
	 * popping
	 * @return
	 */
	TagHandler popTagHandler(){
		// need to lock this function
		if(tagHandlers.size()>0){
			TagHandler th = (TagHandler)tagHandlers.elementAt(0);
			tagHandlers.remove(0);
			return th;
		}
		return null;
	}
	
	/**
	 * actually returns the connection with either the highest priority for a given username
	 * or a specific resource
	 *  
	 * @param username
	 * @return
	 */
	public JabberConnection getConnection(String fulljid, String resource){
		_logger.debug("Looking for connection to "+fulljid+" / "+resource);
		int j = rawConnections.size();
		JabberConnection ret = null;
		try{
			//for(int i=0;i<j;i++){
				JabberConnection jc = (JabberConnection)connections.get(Utils.truncateJid(fulljid));
				
				// dumping out the hashmao
				/*Set s = connections.keySet();
				Iterator it = s.iterator();
				while(it.hasNext()){
					String jc1 = (String)it.next();
					//System.out.println(jc1);
				}
				*/
				if(jc!=null){
					_logger.debug("Connection found.");
					return jc;
				}
				else{
					// do a manual search 
					
					for(int i=0;i<j;i++){
						jc = (JabberConnection)rawConnections.elementAt(i);
						if(jc.primaryjid.equalsIgnoreCase(fulljid) && jc.resource.equalsIgnoreCase(resource)){
							ret = jc;
							return ret; 
						}
						if(jc.primaryjid.equalsIgnoreCase(fulljid) && ret == null){
							ret = jc;
						}
						else if(jc.primaryjid.equalsIgnoreCase(fulljid) && jc.priority > ret.priority){
							ret = jc;
						}
						else if(jc.secondaryjids.contains(fulljid) && ret == null){
							ret = jc;
						
						}
						
					}
					return ret;
				}
				
				
				/*_logger.debug("Testing "+jc.primaryjid);
				if(jc.primaryjid.equalsIgnoreCase(fulljid) && jc.resource.equalsIgnoreCase(resource)){
					ret = jc;
					return ret; 
				}
				if(jc.primaryjid.equalsIgnoreCase(fulljid) && ret == null){
					ret = jc;
				}
				else if(jc.primaryjid.equalsIgnoreCase(fulljid) && jc.priority > ret.priority){
					ret = jc;
				}
				else if(jc.secondaryjids.contains(fulljid) && ret == null){
					ret = jc;
				}*/
				
				
			//}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * returns all JabberConnection objects for one particular user
	 * @param truncatedJid
	 * @return
	 */
	public Vector getConnections(String truncatedJid){
		return null;
	}
	
}