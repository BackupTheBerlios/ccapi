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
	static Logger _logger = Logger.getLogger(Roster.class);
	
	/**
	 * holds the username for which user this roster may be used
	 */
	int customerno;
	
	/**
	 * some rpc function calls
	 */
	RPCSax rpcLoadContacts;
	RPCSax rpcAddContact;
	RPCSax rpcRemoveContact;
	
	DBGate dbg;
	
	Vector entries = new Vector(); 
	/**
	 * plain constructor
	 *
	 */
	Roster(DBGate dbg){
		this.dbg = dbg;
		// actively creating igmxrpc
		rpcLoadContacts = RPCSax.create("Addressbook","getaddressbycategory");
		rpcAddContact = RPCSax.create("Addressbook", "editaddress");
		rpcRemoveContact = RPCSax.create("Addressbook", "removeaddress");
	}
	
	void load(){
		// actually loading the roster from somewhere ( i.e. database )
		// for now: gmxrpc
		try{
			/*rpcLoadContacts.setParam("customerno",customerno);
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
				String firstname = (String)m2.get("firstname");
				String lastname = (String)m2.get("lastname");
				String nick = (String)m2.get("nickname");
				
				if(!nick.equals(""))re.displayname = nick;
				else{
					re.displayname = firstname +" "+lastname;
				}
				final List l = (List)m.get("records");
				final Map m3 = (Map)l.get(0);
				
				final Map m4 = (Map)m3.get("data");
				re.jid = (String)m4.get("email");
				re.subscription="both";
				
				this.entries.add(re);
				
			}
			*/
			
			this.entries = this.dbg.loadRosterEntrys(customerno);
			
		}
		catch(Exception e){
			_logger.fatal(e);
			e.printStackTrace();
		}
		
	}
	
	public boolean addContact(String jid, String name){
		_logger.debug("Adding contact");
		try{
			// add the contact to both addressbooks. 
			// TODO: ADDRBOOK must be able to add a single contact
			
			
			
			this.rpcAddContact.setParam("customerno", customerno);
			
			if(name.equals(""))name = jid;
			
			rpcAddContact.setParam("request","editaddress");
			rpcAddContact.setParam("nickname",name);
			rpcAddContact.setParam("email_p", jid);
			
			_logger.debug("Sending "+rpcAddContact.toXML());
			/*IGmxRpc answer = rpcAddContact.invoke();
			_logger.debug("Data obtained.");
			_logger.debug("Receiving " + answer.toXML());
			*/
			// ok, need to add this to our roster entry
			RosterEntry re = new RosterEntry();
			re.displayname = name;
			re.jid=jid;
			re.subscription = "none";
			entries.add(re);
			_logger.debug("RosterEntry added to roster for customerno "+customerno);
			// adding to the dbgate
			this.dbg.updateRosterEntry(customerno, re);
			
			
			_logger.debug(this.toXml());
			
		}
		catch(Exception e){
			e.printStackTrace();
			_logger.fatal(e);
			return false;
		}
		return true;
	}
	
	public boolean removeContact(String jid){
		

		
		try{
			// add the contact to both addressbooks. 
			// TODO: ADDRBOOK must be able to remove a single contact
			

			// ok, rpc removal ok - need to kill remove the contact
			for(int i=0;i<entries.size();i++){
				RosterEntry re = (RosterEntry)entries.elementAt(i);
				if(re.jid.equals(jid)){
					entries.remove(i);
					break;
				}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean updateContact(String jid, String name, Vector groups){
		try{
			// add the contact to both addressbooks. 
			// TODO: ADDRBOOK must be able to update a single contact
			
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
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

	/**
	 * checks if a jid is in the roster. 
	 * @param truncatedJid
	 * @return
	 */
	public boolean contains(String truncatedJid){
		System.out.println("Checking if "+truncatedJid+" is in roster.");
		System.out.println("Roster holds #: "+entries.size());
		for(int i=0;i<this.entries.size();i++){
			RosterEntry re = (RosterEntry)entries.elementAt(i);
			_logger.fatal(truncatedJid +" - "+re.jid);
			if(re.jid.equals(truncatedJid))return true;
		}
		return false;
	}
	
	/**
	 * Obtaining one specific roster entry
	 * @param truncatedJid
	 * @return
	 */
	public RosterEntry getRosterEntry(String truncatedJid){
		
		for(int i=0;i<this.entries.size();i++){
			RosterEntry re = (RosterEntry)entries.elementAt(i);
			if(re.jid.equals(truncatedJid))return re;
		}
		return null; 
	}
	
	/**
	 * subscription Mode can bei either subscribed or unsubscribed
	 * if the return is true, the presence tag must be routed. 
	 * @param truncatedJid
	 * @param subscriptionMode
	 * @return
	 */
	public boolean setSubscriptionModeOutbound(String truncatedJid, String subscriptionMode){
		
		// first get the corresponding roster entry
		boolean ret = false;
		RosterEntry re = null;
		int index = 0;
		for(int i=0;i<this.entries.size();i++){
			re = (RosterEntry)entries.elementAt(i);
			if(re.jid.equals(truncatedJid)){
				index = i;
				break;
			}
		}
		_logger.fatal("outbound: working out roster of "+this.customerno+" for entry "+truncatedJid);
		_logger.fatal("outbound: Setting to "+subscriptionMode);
		_logger.fatal("outbound: Current subscription mode "+re.subscription);
		
		if(subscriptionMode.equals("subscribed")){
			if(re.subscription.equals("none")){
				// no state change, no routeing
				ret = false;
			}
			else if(re.subscription.equals("none + pending out")){
//				 no state change, no routeing
				ret = false;
			}
			else if(re.subscription.equals("none + pending in")){
//				 state change + routing
				re.subscription = "from";
				ret = true;
			}
			else if(re.subscription.equals("none + pending in/out")){
//				 state change + routing
				re.subscription = "from + pending out";
				ret = true;
				
			}
			else if(re.subscription.equals("to")){
//				 no state change + no routing
				ret = false;

			}
			else if(re.subscription.equals("to + pending in")){
//				 state change + routing
				re.subscription = "both";
				ret = true;

			}
			else if(re.subscription.equals("from")){
//				 no state change + no routing
				ret = false;
			}
			else if(re.subscription.equals("from + pending out")){
//				 no state change + no routing
				ret = false;
			}
			else if(re.subscription.equals("both")){
				// no change, don't route
				ret = false;
			}			
		}
		else if(subscriptionMode.equals("unsubscribed")){
			if(re.subscription.equals("none")){
				// no state change, no routeing
				ret = false;
			}
			else if(re.subscription.equals("none + pending out")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending in")){
//				 state change + routing
				re.subscription = "none";
				ret = true;
			}
			else if(re.subscription.equals("none + pending in/out")){
//				 state change + routing
				re.subscription = "none + pending out";
				ret = true;
				
			}
			else if(re.subscription.equals("to")){
//				 no state change + no routing
				ret = false;

			}
			else if(re.subscription.equals("to + pending in")){
//				 state change + routing
				re.subscription = "to";
				ret = true;

			}
			else if(re.subscription.equals("from")){
				re.subscription = "none";
				ret = true;
			}
			else if(re.subscription.equals("from + pending out")){
				re.subscription = "none + pending out";
				ret = true;
			}
			else if(re.subscription.equals("both")){
				re.subscription = "to";
				ret = true;			
			}

			
		}
		else if(subscriptionMode.equals("subscribe")){
			if(re.subscription.equals("none")){
				ret = true;
				re.subscription = "none + pending out";
				
			}
				
		}
		else if(subscriptionMode.equals("unsubscribed")){
			if(re.subscription.equals("none")){
				// no state change, no routeing
				ret = false;
			}
			else if(re.subscription.equals("none + pending out")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending in")){
//				 state change + routing
				re.subscription = "none";
				ret = true;
			}
			else if(re.subscription.equals("none + pending in/out")){
//				 state change + routing
				re.subscription = "none + pending out";
				ret = true;
				
			}
			else if(re.subscription.equals("to")){
//				 no state change + no routing
				ret = false;

			}
			else if(re.subscription.equals("to + pending in")){
//				 state change + routing
				re.subscription = "to";
				ret = true;

			}
			else if(re.subscription.equals("from")){
				re.subscription = "none";
				ret = true;
			}
			else if(re.subscription.equals("from + pending out")){
				re.subscription = "none + pending out";
				ret = true;
			}
			else if(re.subscription.equals("both")){
				re.subscription = "to";
				ret = true;			
			}

			
		}

		// replacing current entry. 
		entries.setElementAt(re, index);
		
		// TODO: finally i need to store the new roster entry in the database
		_logger.fatal("outbound: set to  "+re.subscription);

		// updating in the database
		this.dbg.updateRosterEntry(customerno, re);
		
		return ret; 
	}
	
	
	public boolean setSubscriptionModeInbound(String jid, String mode){
		// first get the corresponding roster entry
		boolean ret = false;
		RosterEntry re = null;
		int index = -1;
		for(int i=0;i<this.entries.size();i++){
			re = (RosterEntry)entries.elementAt(i);
			if(re.jid.equals(jid)){
				index = i;
				break;
			}
		}
		if(index == -1){
			re = new RosterEntry();
			re.jid = jid;
			re.displayname = jid;
			re.subscription = "none";
		}
		
		_logger.fatal("inbound: working out roster of "+this.customerno+" for entry "+jid);
		_logger.fatal("inbound: Setting to "+mode);
		_logger.fatal("inbound: Current subscription mode "+re.subscription);
		
		if(mode.equals("subscribe")){
			if(re.subscription.equals("none")){
				re.subscription = "none + pending in";
				ret = true;
			}
			else if(re.subscription.equals("none + pending out")){
				ret = true;
				re.subscription = "none + pending out/in";
			}
			else if(re.subscription.equals("none + pending in")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending out/in")){
				ret = false;
			}
			else if(re.subscription.equals("to")){
				ret = true;
				re.subscription = "to + pending in";
			}
			else if(re.subscription.equals("to + pending in")){
				ret = false;
			}
			else if(re.subscription.equals("from")){
				ret = false;
			}
			else if(re.subscription.equals("from + pending out")){
				ret = false;
			}
			else if(re.subscription.equals("both")){
				ret = false;
			}
		}
		else if(mode.equals("unsubscribe")){
			if(re.subscription.equals("none")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending out")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending in")){
				ret = true;
				re.subscription = "none";
			}
			else if(re.subscription.equals("none + pending out/in")){
				ret = true;
				re.subscription = "none + pending out";
			}
			else if(re.subscription.equals("to")){
				ret = false;
			}
			else if(re.subscription.equals("to + pending in")){
				ret = true;
				re.subscription = "to";
			}
			else if(re.subscription.equals("from")){
				ret = true;
				re.subscription = "none";
			}
			else if(re.subscription.equals("from + pending out")){
				ret = true;
				re.subscription = "none + pending out";
			}
			else if(re.subscription.equals("both")){
				ret = true;
				re.subscription = "to";
			}
		}
		else if(mode.equals("subscribed")){
			if(re.subscription.equals("none")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending out")){
				ret = true;
				re.subscription = "to";
			}
			else if(re.subscription.equals("none + pending in")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending out/in")){
				ret = true;
				re.subscription = "to + pending in";
			}
			else if(re.subscription.equals("to")){
				ret = false;
			}
			else if(re.subscription.equals("to + pending in")){
				ret = false;
			}
			else if(re.subscription.equals("from")){
				ret = false;
			}
			else if(re.subscription.equals("from + pending out")){
				ret = true;
				re.subscription = "both";
			}
			else if(re.subscription.equals("both")){
				ret = false;
			}
		}
		if(mode.equals("unsubscribed")){
			if(re.subscription.equals("none")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending out")){
				ret = true;
				re.subscription = "none";
			}
			else if(re.subscription.equals("none + pending in")){
				ret = false;
			}
			else if(re.subscription.equals("none + pending out/in")){
				ret = true;
				re.subscription = "none + pending in";
			}
			else if(re.subscription.equals("to")){
				ret = true;
				re.subscription = "none";
			}
			else if(re.subscription.equals("to + pending in")){
				ret = true;
				re.subscription = "none + pending in";
			}
			else if(re.subscription.equals("from")){
				ret = false;
			}
			else if(re.subscription.equals("from + pending out")){
				ret = true;
				re.subscription = "from";
			}
			else if(re.subscription.equals("both")){
				ret = true;
				re.subscription = "from";
			}
		}	
		
		// update the entry
		if(index == -1){
			entries.add(re);
		}
		else{
			entries.setElementAt(re, index);
		}

		// updating in the database
		this.dbg.updateRosterEntry(customerno, re);
		
		// TODO: propably need to save the roster to db in here.
		_logger.fatal("inbound: set to  "+re.subscription);
		
		return ret;
	}
	
}
