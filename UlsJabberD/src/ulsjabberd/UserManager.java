/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

import org.apache.log4j.Logger;

/**
 * @author us
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import org.apache.log4j.*;
import java.util.*;

import net.gmx.gmxlib.db.*;
import net.gmx.gmxlib.rpc.IGmxRpc;
import net.gmx.gmxlib.rpc.RPCSax;

public class UserManager {

	static Logger _logger = Logger.getLogger(UserManager.class);
	Accepter a;
	
	/**
	 * does the password checking
	 */
	RPCSax rpcCheckPwd;
	
	/**
	 * retrieves the customerno 
	 */
	RPCSax rpcObtainCustomerno;
	
	/** 
	 * obtains all email addresses
	 */
	RPCSax rpcGetEmailAddresses;
	
	// will holds the rosters for users. 
	Vector rosters = new Vector();
	
	/**
	 * UserManager
	 */
	public UserManager(Accepter a){
		this.a = a;
		_logger.info("Starting up user manager. ");
		// initializing
		rpcCheckPwd = RPCSax.create("Config","passwd_getPassword");
		rpcGetEmailAddresses = RPCSax.create("Config", "aliases_getAllAddresses");
		rpcObtainCustomerno = RPCSax.create("Config", "aliases_getCustomernoForAlias");
	}
	
	/**
	 * returns the password for a certain customerno
	 * @param customerno
	 * @return
	 */
	public String getPwd(int customerno){
		_logger.debug("Obtaining pwd for "+customerno);
		try{		
			this.rpcCheckPwd.setParam("customerno", customerno);
			IGmxRpc answer = rpcCheckPwd.invoke();
			_logger.debug("Data obtained.");
			_logger.debug(answer.toXML());
			return answer.getString("passwd");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		// ... 
		return "uls_personal_entrance";
		
	}
	
	/**
	 * 
	 * validates a password
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean validatePwd(int customerno, String password){
		_logger.debug("Validating "+customerno+"/"+password);
		
		try{
			
			this.rpcCheckPwd.setParam("customerno", customerno);
			IGmxRpc answer = rpcCheckPwd.invoke();
			_logger.debug("Data obtained.");
			_logger.debug(answer.toXML());
			if(answer.getString("passwd").equals(password))return true;
			
			//System.out.println(answer.getString("passwd"));
			/*DBE dbe = new DBE();
			List l = dbe.selectNull("CUSTOMERNO", "ALIASES", "ALIAS='"+username+"'");
			
			if(l == null){
				_logger.debug("No user found. ");
			}
			else{
				// ok, user name exists, need to validate password
				int customerno = Integer.parseInt((String)l.get(0));
				_logger.debug("Mapping found, #customer: "+customerno);
				l = dbe.selectNull("PASSWORD", "AUTH", "CUSTOMERNO="+customerno);
				String pwd = (String)l.get(0);
				// comparing pwd. 
				if(pwd.equals(password)){
					return true;
				}
				else{
					return false;
				}
			}*/
			
		}
		catch(Exception e){
			//e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * returns the full roster for user, including offline contacts.
	 * @param username
	 * @return 
	 */
	public Roster getFullRoster(int customerno){
		/*for(int i=0;i<rosters.size();i++){
			Roster r=(Roster)rosters.elementAt(i);
			if(r.username.equals(username))return r;
		}*/
		// ok, still in here, need to obtain a new roster
		Roster ret = new Roster(this.a.s.dbgate);
		ret.customerno = customerno;
		ret.load();
		
		if(getRosterId(customerno) != -1){
			// replace the roster
			rosters.setElementAt(ret, getRosterId(customerno));
		}
		else{
			// add the roster
			rosters.add(ret);
		}
		
		return ret; 
	}
		
	public int getRosterId(int customerno){
		int ret = -1;
		
		for(int i=0;i<rosters.size();i++){
			Roster re = (Roster)rosters.elementAt(i);
			if(re.customerno == customerno) return i;
		}
		
		return ret;
	}
	
	public void dumpVector(Vector vec){
		for(int i=0;i<vec.size();i++){
			Object o = vec.elementAt(i);
			if(o instanceof RosterEntry){
				_logger.debug(((RosterEntry)vec.elementAt(i)).jid);	
			}
			else if(o instanceof String){
				_logger.debug(((String)vec.elementAt(i)));
			}
		}
	}
		
	/**
	 * returns the roster with only online users for a username 
	 * @param username
	 * @return
	 */
	public Vector getOnlineRoster(int customerno){
		Vector ret = new Vector();
		for(int i=0;i<rosters.size();i++){
			Roster r=(Roster)rosters.elementAt(i);
			if(r.customerno == customerno){
				// check if each contact is online or not. 
				
				_logger.debug("Dumping out full roster for "+customerno);
				dumpVector(r.entries);
				
				// TODO: REALLY IMPLEMENT A BETTER SEARCH ALGORITHM
				for(int j=0;j<r.entries.size();j++){
					RosterEntry re = (RosterEntry)r.entries.elementAt(j);
					if(isOnline(re.jid))ret.add(re.jid);
				}
				_logger.debug("Dumping out online roster for "+customerno);
				dumpVector(ret);
				return ret;
			}
		}		
		return ret; 
	}

	/**
	 * obtaining the secondary jids for a customerno 
	 * @param customerno
	 * @return
	 */
	public Vector getSecondaryJids(int customerno){
		Vector ret = new Vector();
		try{
			this.rpcGetEmailAddresses.setParam("customerno", customerno);
			IGmxRpc answer = rpcGetEmailAddresses.invoke();
			_logger.debug("Data obtained.");
			_logger.debug(answer.toXML());
			
			for(Iterator it = answer.paramNames();it.hasNext();) {
				String jid = "";
				
				final String fullName = (String)it.next();
				System.out.println(fullName);
				if(fullName.startsWith("GMXAliases")){
					final Map m = answer.getMap(fullName);
					
					for(Iterator it2 = m.keySet().iterator();it2.hasNext();){
						String keyname  = (String)it2.next();
						String value = (String)m.get(keyname);
						ret.add(value);
						_logger.debug("Adding seondary email: "+value);
					}
				}
				else{
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 
	 * @param jid
	 * @return
	 */
	public boolean isOnline(String jid){
		
		JabberConnection jc = (JabberConnection) this.a.connections.get(jid);
		if(jc!=null)if(jc.primaryjid.equals(jid) || jc.secondaryjids.contains(jid))return true;
		
		return false;
	}
	
	/**
	 * retrieves through gmxrpc a customerno for a full address
	 * @param fulladdress
	 * @return
	 */
	public int getCustomerno(String fulladdress){
		try{
			//
			this.rpcObtainCustomerno.setParam("alias", fulladdress);
			this.rpcObtainCustomerno.setParam("domain", "");
			IGmxRpc answer = rpcObtainCustomerno.invoke();
			_logger.debug("Data obtained.");
			_logger.debug(answer.toXML());
			
			_logger.debug(""+answer.getInt("customerno"));
			return answer.getInt("customerno");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * returns the roster for a jid
	 * @param jid
	 * @return
	 */
	public Roster getRoster(int customerno){
		Roster ret = null;
		_logger.fatal("Holding "+rosters.size()+" rosters.");
		for(int i=0;i<rosters.size();i++){
			Roster r = (Roster)rosters.elementAt(i);
			System.out.println("Get Roster: Comparing "+r.customerno + " == "+customerno);
			if(r.customerno == customerno) return r;
		}
		// manually need to load the roster. 
		ret = new Roster(this.a.s.dbgate);
		ret.customerno=customerno;
		ret.load();
		rosters.add(ret);
		
		return ret;
	}
	
	public void setRoster(int customerno, Roster roster){
		int index = -1;
		for(int i=0;i<rosters.size();i++){
			Roster r = (Roster)rosters.elementAt(i);
			System.out.println("Comparing "+r.customerno + " == "+customerno);
			if(r.customerno == customerno){
				index = i;
				break;
			}
		}
		if(index != -1){
			_logger.debug("Updating vector entry.");
			rosters.remove(index);
			rosters.add(roster);
		}
		
		Roster r = this.getRoster(customerno);
		System.out.println("--- Finally dumping roster: " +r.toXml());
	}
	
}


