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

public class UserManager {

	static Logger _logger = Logger.getLogger(UserManager.class);
	
	public UserManager(){
		_logger.info("Starting up user manager. ");
	}
	
	public boolean validatePwd(String username, String password){
		_logger.debug("Validating "+username+"/"+password);
		return true;
	}
	
	// need to obtain the roster in here
	/**
	 * returns the full roster for user, including offline contacts.
	 * @param username
	 * @return 
	 */
	public Vector getFullRoster(String username){
		Vector ret = new Vector(); 
		
		
		
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
	
}


