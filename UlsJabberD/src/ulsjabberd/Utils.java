/*
 * Created on Mar 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

/**
 * @author us
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Utils {

	static public String truncateJid(String in){
		String jid = in;
		String truncatedJid = jid;
		String resource = "";
		if(jid.indexOf("/")!=-1){
			truncatedJid = truncatedJid.substring(0, jid.indexOf("/"));
			resource = jid.substring(jid.indexOf("/"));
		}
		return truncatedJid;
	}

	static public String truncateResource(String in){
			String jid = in;
			String truncatedJid = jid;
			String resource = "";
			if(jid.indexOf("/")!=-1){
				truncatedJid = truncatedJid.substring(0, jid.indexOf("/"));
				resource = jid.substring(jid.indexOf("/"));
			}
			return resource;
	}
}
