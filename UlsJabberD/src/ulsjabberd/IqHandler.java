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
		_logger.debug("Working out iq tag");
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
				Roster r = this.jc.a.um.getFullRoster(jc.customerno);
				String jid = iq.getElement("query").getElement("item").getAttr("jid");
				String name = iq.getElement("query").getElement("item").getAttr("name");
				if(jid!=null){
					if(name==null){
						name="";
					}
					if(r.addContact(jid,name)){
						// ok, push and ack 
						String subscription = "none";
						this.jc.sendSingleRosterItem(subscription, name, jid);
						this.jc.sendIqAck(iq.getAttr("id"));
						_logger.debug("added contact.");
					}
					else{
						// send error
						this.jc.sendError(iq.getAttr("id"), "100", "Error while inserting to database");
						_logger.debug("error while adding contact");
					}
					
				}
			}
			// TODO: add functionality in iq recognition for remove and update contact
			
		}
		else if(iq.getElement("vcard")!=null){
			// obtain the vcard. 
			// TODO: update and retreive the vcard
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

