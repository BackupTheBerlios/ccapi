/*
 * Created on Mar 1, 2005
 *
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
			
			// set the presence of the jc at first. 
			if(presence.getElement("show")!=null)jc.setPresenceShow(presence.getElement("show").getText());
			if(presence.getElement("priority")!=null)jc.setPriority(Integer.parseInt(presence.getElement("priority").getText()));
			
			if(presence.getAttr("to")!=null){
				this.jc.a.xmlr.route(presence);
				
				if(this.jc.isLocalAddress(presence.getAttr("to"))){
					jc.a.getConnection(this.jc.getUserName(presence.getAttr("to")), "").send(presence.toString());
				}
				else{
					// do an external dispatch
				}
				
			}
			else{
				
				// plain initial presence recieved ... 
				// need to manually add to's 
				// getting at first the users online buddys 
				Vector v = jc.a.um.getOnlineRoster(jc.username);
				
				for(int i=0;i<v.size();i++){
					String s = (String)v.elementAt(i);
					presence.attributes.put("to", s);
					this.jc.a.xmlr.route(presence);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
