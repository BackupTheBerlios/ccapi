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

import org.apache.log4j.Logger;


public class PresenceHandler implements TagHandler {

	static Logger _logger = Logger.getLogger(PresenceHandler.class);
	JabberConnection jc; 
	Element presence;
	
	public PresenceHandler(JabberConnection jc, Element presence){
		this.jc = jc;
		this.presence = presence; 
	}
	
	/**
	 * CAREFUL - WE ARE DOING OUTBOUND PRESENCE HANDLING IN HERE ! 
	 */
	public void handle() {
		boolean deliver = true;
		try{
			System.out.println("Working out presence tag : "+presence.toString());
			boolean routeGlobal = true;;
			
			if(presence.getAttr("to")!=null){
				
				if(presence.getAttr("type")!=null){
					if(presence.getAttr("type").equals("subscribe")){
						System.out.println("**** SUBSCRIPTION RECOGNIZED");
						// need to push a roster tag
						// TODO: push to all connections
						Roster r = this.jc.a.um.getRoster(this.jc.customerno);
						
						System.out.println(r.toXml());
						
						RosterEntry re = r.getRosterEntry(presence.getAttr("to"));
						if(re!=null)
							this.jc.sendSingleRosterItem("subscribe", "none", re.displayname, presence.getAttr("to"));
						// need to stamp the presence tag with the bare jid. 
						presence.attributes.put("to", Utils.truncateJid(presence.getAttr("to")));
						presence.attributes.put("from", Utils.truncateJid(presence.getAttr("from")));
						// check the rewriting
						System.out.println("TO: "+presence.getAttr("to"));
						System.out.println("FROM: "+presence.getAttr("from"));
						
						// must set the outbound status of the from's roster 
						r.setSubscriptionModeOutbound(Utils.truncateJid(presence.getAttr("to")), "subscribe");
						
					}
					else if(presence.getAttr("type").equals("subscribed")){
						
						String truncatedJid = Utils.truncateJid(presence.getAttr("to"));
//						 need to stamp the presence tag with the bare jid. 
						presence.attributes.put("to", Utils.truncateJid(presence.getAttr("to")));
						presence.attributes.put("from", Utils.truncateJid(presence.getAttr("from")));
						// check the rewriting
						System.out.println("TO: "+presence.getAttr("to"));
						System.out.println("FROM: "+presence.getAttr("from"));
						
						
//						 ok, user accepted subscription request 
						// need to do update the roster entries
						
						

						// ok, need to check if the target is an onserver contact 
						// if so, do some special handling
						Roster r = this.jc.a.um.getRoster(this.jc.customerno); 
						if(r.setSubscriptionModeOutbound(presence.getAttr("to"), presence.getAttr("type"))){
							// ok , we need to forward this chunk
						}
						else{
							deliver = false;
						}
						this.jc.a.um.setRoster(jc.customerno, r);

						

						// need to do a roster push
						RosterEntry re = r.getRosterEntry(truncatedJid);
						this.jc.sendSingleRosterItem("", re.subscription, re.displayname, truncatedJid);
						
						
						JabberConnection jcFrom = this.jc.a.getConnection(truncatedJid, "");
						// TODO: need to do a presence push! 
						if(jcFrom!=null){
							jc.sendPresence(truncatedJid, "available", jcFrom.getPresenceStatus(), jcFrom.getPresenceShow());
						}
						
						
						
					}
					else if(presence.getAttr("type").equals("unsubscribe")){
						//


//						 need to stamp the presence tag with the bare jid. 
						presence.attributes.put("to", Utils.truncateJid(presence.getAttr("to")));
						presence.attributes.put("from", Utils.truncateJid(presence.getAttr("from")));
						// check the rewriting
						System.out.println("TO: "+presence.getAttr("to"));
						System.out.println("FROM: "+presence.getAttr("from"));
						
					
						// checking if we actually need to forward this chunk. 
						Roster r = this.jc.a.um.getRoster(this.jc.customerno); 
						if(r.setSubscriptionModeOutbound(presence.getAttr("to"), presence.getAttr("type"))){
							// ok , we need to forward this chunk
						}
						else{
							deliver = false;
						}
						this.jc.a.um.setRoster(jc.customerno, r);
						
						
						
					}
					else if(presence.getAttr("type").equals("unsubscribed")){
						//

						
						
						
						

//						 need to stamp the presence tag with the bare jid. 
						presence.attributes.put("to", Utils.truncateJid(presence.getAttr("to")));
						presence.attributes.put("from", Utils.truncateJid(presence.getAttr("from")));
						// check the rewriting
						System.out.println("TO: "+presence.getAttr("to"));
						System.out.println("FROM: "+presence.getAttr("from"));
						
						Roster r = this.jc.a.um.getRoster(this.jc.customerno); 
						if(r.setSubscriptionModeOutbound(presence.getAttr("to"), presence.getAttr("type"))){
							// ok , we need to forward this chunk
						}
						else{
							deliver = false;
						}
						this.jc.a.um.setRoster(jc.customerno, r);					
					}
				}
				
				if(deliver)this.jc.a.xmlr.route(presence);
				// need to check (see p. 34, rfc 3921) if this is a presence subscription request
				
			}
			else{
				
				// plain initial presence recieved ...
				// or internal presence arrived 
				
				
				// need to rewrite 
				if(presence.getAttr("from")==null){
					presence.attributes.put("from", jc.primaryjid+"/"+jc.resource);
				}
				if(presence.getAttr("from").indexOf("/")==-1){
					presence.attributes.put("from", presence.getAttr("from")+"/"+jc.resource);
				}
				
				if(presence.getElement("show")!=null){
					jc.setPresenceShow(presence.getElement("show").getText());
					jc.setPresenceType("available");
				}
				if(presence.getElement("status")!=null){
					jc.setPresenceStatus(presence.getElement("status").getText());
					jc.setPresenceType("available");
				}
				if(presence.getElement("priority")!=null)jc.setPriority(Integer.parseInt(presence.getElement("priority").getText()));
				if(presence.getAttr("type")!=null){
					
					jc.setPresenceType(presence.getAttr("type"));
					if(jc.getPresenceType().equals("unavailable")){
						/// TODO: need to close the connection. 	
						// TODO: need to do a presence push.
						
					}
				}
				else{
					if(presence.getElement("show")==null && presence.getElement("status")==null){
						jc.setPresenceType("available");
						jc.setPresenceShow("");
						jc.setPresenceStatus("");
					}
				}
			
				// and now routing 
				
		
				// getting at first the users online buddys 
				Vector v = jc.a.um.getOnlineRoster(jc.customerno);				
				// now dispatching to buddys
				for(int i=0;i<v.size();i++){
					String s = (String)v.elementAt(i);
					_logger.debug("Routing presence packet to "+s);
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
