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
 * holds a roster for a jid
 */

import java.util.*;

import org.apache.log4j.Logger;

import net.gmx.gmxlib.rpc.*;

public class Roster {
	static Logger _logger = Logger.getLogger(JabberConnection.class);
	
	/**
	 * holds the username for which user this roster may be used
	 */
	String username = "";  
	
	/**
	 * some rpc function calls
	 */
	RPCSax rpcLoadContacts;
	
	Vector entries = new Vector(); 
	/**
	 * plain constructor
	 *
	 */
	Roster(){
		// actively creating igmxrpc
		rpcLoadContacts = RPCSax.create("Addressbook","getaddressbycategory");		
	}
	
	void load(){
		// actually loading the roster from somewhere ( i.e. database )
		// for now: gmxrpc
		try{
			rpcLoadContacts.setParam("customerno","23216823");
			rpcLoadContacts.setParam("request","getaddressbycategory");
			rpcLoadContacts.setParam("address_items","address_id,nickname,firstname,lastname,email");
			rpcLoadContacts.setParam("address_record_items","standard_email,email");
			
			IGmxRpc answer = rpcLoadContacts.invoke();
			_logger.debug("Data obtained.");
			_logger.debug(answer.toXML());
			
			
			// work out the reply. 
			for(Iterator it = answer.paramNames();it.hasNext();) {
				RosterEntry re = new RosterEntry();
				
				final String fullName = (String)it.next();
				final Map m = answer.getMap(fullName);
				final Map m2 = (Map)m.get("address");
				
				// setting the roster entries
				re.displayname = (String)m2.get("firstname")+ " "+ (String)m2.get("lastname");
				final List l = (List)m.get("records");
				final Map m3 = (Map)l.get(0);
				
				final Map m4 = (Map)m3.get("data");
				re.jid = (String)m4.get("email");
				re.subscription="both";
				
				this.entries.add(re);
				
			}
			
		}
		catch(Exception e){
			_logger.fatal(e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * will return all the rosterentrys as one large string (without the encapsulating query)
	 * @return
	 */
	public String toXml(){
		String ret = "";
		for(int i=0;i<this.entries.size();i++){
			RosterEntry re = (RosterEntry)entries.elementAt(i);
			ret += re.toXml();
		}
		return ret; 
	}

}
