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
 * a plain roster entry
 * 
 */

import java.util.*;

public class RosterEntry {

	String displayname = "";
	String jid = "";
	Vector groups = new Vector(); // contains strings of all groups. 
	String subscription = "";
	
	RosterEntry(){
		
	}
	
	public String dumpToXml(){
		String ret = "<item subscription='"+subscription+"' name='"+displayname+"' jid='"+jid+"'>";
		for(int i=0;i<groups.size();i++){
			String g = (String)groups.elementAt(i);
			ret +="<group>"+g+"</group>";
		}
		ret +="</item>";
		return ret; 
	}
	
}

