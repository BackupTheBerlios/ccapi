/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

/**
 * @author us
 *
 * Handles only and explicitly iq tags. 
 */

import org.apache.log4j.Logger;

import ulsjabberd.xml.*;

public class IqHandler implements TagHandler{
	
	static Logger _logger = Logger.getLogger(IqHandler.class);
	
	Element iq;
	JabberConnection jc; 
	
	IqHandler(JabberConnection jc, Element iq){
		this.iq = iq;
		this.jc = jc; 
	}
	
	public void handle(){
		// do something with this tag handler
		_logger.debug("Working out iq tag : "+iq.toString());
		if(iq.getElement("query")!=null){
			// obtain the namespace 
			String xmlns = iq.getElement("query").getAttr("xmlns");
			if(xmlns.equals("jabber:iq:roster") && iq.getAttr("type").equals("get")){
				_logger.debug("Working out roster get");
				// roster requested.
				sendRoster();
				// need to send online buddys
				jc.sendOnlineBuddys();
				// may send offline messages
				jc.sendOfflineMessages();
			}
			else if(xmlns.equals("jabber:iq:roster") && iq.getAttr("type").equals("set")){
				// adding user to jid requested
				_logger.debug("Working out roster set");
				Roster r = this.jc.a.um.getRoster(jc.customerno);
				String jid = iq.getElement("query").getElement("item").getAttr("jid");
				String name = iq.getElement("query").getElement("item").getAttr("name");
				String subscription = iq.getElement("query").getElement("item").getAttr("subscription");
				
				if(subscription == null)subscription  = "";

				// core jid handling 
				String truncatedJid = jid;
				if(jid.indexOf("/")!=-1){
					truncatedJid = truncatedJid.substring(0, jid.indexOf("/"));
				}
				if(name==null){
					name=jid;
				}
				
				// doing subscription remove
				if(subscription.equals("remove")){
					// remove the contact from this user's roster
					// TODO: add functionality in iq recognition for remove and update contact
					if(r.removeContact(truncatedJid)){
//						 TODO: need to push out change to all connections.
						this.jc.sendSingleRosterItem("", subscription, name, jid);
						// need to ack to current connection
						this.jc.sendIqAck(iq.getAttr("id"));
					}
					else{
						// need to send failure
						this.jc.sendError(iq.getAttr("id"), "100", "Error while inserting to database");
						_logger.fatal("error while updating contact");
					}
						
				}
				else{
					if(jid!=null){
					
						// need to check of the roster contains the contact already.
						if(r.contains(truncatedJid)){
							_logger.fatal("JID is in roster. ");
							// updating contact in roster. 
							//if(r.updateContact(truncatedJid, name, new java.util.Vector())) {
	//							 TODO: need to push out change to all connections.
								this.jc.sendSingleRosterItem("", subscription, name, jid);
								// need to ack to current connection
								this.jc.sendIqAck(iq.getAttr("id"));
							/*}
							else{
								// need to send failure
								this.jc.sendError(iq.getAttr("id"), "100", "Error while inserting to database");
								_logger.fatal("error while updating contact");
							}*/
						}
						else{
							_logger.fatal("JID is not in roster. ");
							// contact not yet in roster
							
							if(r.addContact(truncatedJid,name)){
								// ok, push and ack 
								// TODO: send to all connections of this particular user, not just to the initiating one. 
								this.jc.sendSingleRosterItem("", "none", name, jid);
								//this.jc.sendRosterAck(iq.getAttr("id"));
								this.jc.sendIqAck(iq.getAttr("id"));
								_logger.debug("contact added.");
							}
							else{
								// send error
								this.jc.sendError(iq.getAttr("id"), "100", "Error while inserting to database");
								_logger.debug("error while adding contact");
							}
							jc.a.um.setRoster(jc.a.um.getCustomerno(jc.primaryjid), r);
						}
						
					}
				}
			}
			

			
			
			
			
		}
		else if(iq.getElement("vcard")!=null){
			// obtain the vcard. 
			// TODO: update and retreive the vcard
		}
		
		if(iq.getAttr("to")!=null){
			// need to forward the tag. 
			String jid = iq.getAttr("to");
			String truncatedJid = jid;
			String resource = "";
			if(jid.indexOf("/")!=-1){
				truncatedJid = truncatedJid.substring(0, jid.indexOf("/"));
				resource = jid.substring(jid.indexOf("/"));
			}
			
			JabberConnection jc = this.jc.a.getConnection(truncatedJid, resource);
			if(jc != null){
				jc.send(iq.toString());
			}
			else{
				// TODO: NEED TO STORE IQ QUERIES
			}
			
			
		}
		
	}
	
	public void sendRoster(){
		Roster r = this.jc.a.um.getFullRoster(jc.customerno);
		
		String data = "<iq type='result' id='"+iq.getAttr("id")+"'>";
		data += "<query xmlns='jabber:iq:roster'>";
		data += r.toXml();
		data += "</query>";
		data += "</iq>";
		
		jc.send(data);
	}
}

