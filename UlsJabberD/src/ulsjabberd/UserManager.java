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
	
	// does the password checking
	RPCSax rpcCheckPwd;
	
	/** 
	 * obtains all email addresses
	 */
	RPCSax rpcGetEmailAddresses;
	
	// will holds the rosters for users. 
	Vector rosters = new Vector();
	
	/**
	 * UserManager
	 */
	public UserManager(){
		_logger.info("Starting up user manager. ");
		// initializing
		rpcCheckPwd = RPCSax.create("Config","passwd_getPassword");
		rpcGetEmailAddresses = RPCSax.create("Config", "aliases_getAllAddresses");
	}
	/**
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
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * returns the full roster for user, including offline contacts.
	 * @param username
	 * @return 
	 */
	public Roster getFullRoster(String username){
		for(int i=0;i<rosters.size();i++){
			Roster r=(Roster)rosters.elementAt(i);
			if(r.username.equals(username))return r;
		}
		// ok, still in here, need to obtain a new roster
		Roster ret = new Roster();
		ret.username=username;
		ret.load();
		return ret; 
	}
	
	/**
	 * returns the roster with only online users. 
	 * @param username
	 * @return
	 */
	public Vector getOnlineRoster(String username){
		Vector ret = new Vector();
		ret.addElement("uls");
		ret.addElement("uls2");
		return ret; 
	}

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
}


