/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

/**
 * @author us
 */

import ulsjabberd.xml.Element;
import java.util.*;

public class PresenceHandler implements TagHandler {

	JabberConnection jc; 
	Element presence;
	
	public PresenceHandler(JabberConnection jc, Element presence){
		this.jc = jc;
		this.presence = presence; 
	}
	
	public void handle() {
		try{
			Vector v = jc.a.um.getOnlineRoster(jc.username);
			for(int i=0;i<v.size();i++){
				String s = (String)v.elementAt(i);
				JabberConnection target = jc.a.getConnection(s, "");
				if(target != null)target.send(presence.toString());
				else{
					System.out.println("No connection for "+s);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
