/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

import org.apache.log4j.Logger;

import ulsjabberd.xml.*;

public class MessageHandler implements TagHandler{

	static Logger _logger = Logger.getLogger(MessageHandler.class);
	
	JabberConnection jc; 
	Element message;
	
	MessageHandler(JabberConnection jc, Element message){
		this.jc = jc;
		this.message = message;
	}
	
	public void handle(){
		_logger.debug("Working out message tag. ");
		try{
			String to = message.getAttr("to");
			// work out the username and the servername
			String username = to;
			String servername = "";
			String resource = "";
			if(to.indexOf("@")!=-1){
				username = to.substring(0, to.indexOf("@"));
				servername = to.substring(to.indexOf("@"));
				if(servername.indexOf("/")!=-1){
					String t = servername.substring(0, servername.indexOf("/"));
					resource = servername.substring(servername.indexOf("/"));
					servername = t;
				}
			}
			this.jc.a.xmlr.route(message);
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
