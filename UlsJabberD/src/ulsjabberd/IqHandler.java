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
			if(xmlns.equals("jabber:iq:roster")){
				// roster requested.
				sendRoster();
			}
		}
		else if(iq.getElement("vcard")!=null){
			// obtain the vcard. 
		}
	}
	
	public void sendRoster(){
		String data = "<iq type='result' id='"+iq.getAttr("id")+"'>";
		data += "<query xmlns='jabber:iq:roster'>";
		data += "<item subscription='both' jid='uls@gmx.net'/>";
		data += "<item subscription='both' jid='uls2@gmx.net'/>";
		data += "</query>";
		data += "</iq>";
		jc.send(data);
	}
}

