/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

/**
 * @author us
 *
 * routes an xml package.  
 */
import org.apache.log4j.Logger;
import java.util.Vector;
import java.text.SimpleDateFormat;
import ulsjabberd.xml.Element;
import java.util.Date;

public class XmlRouter {

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
	
	static Logger _logger = Logger.getLogger(XmlRouter.class);
	Accepter a; 
	XmlRouter(Accepter a){
		this.a = a;
	}
	
	public void route(Element chunk){
		try{
			_logger.debug("Routing:  "+chunk.toString());
			String truncatedTo = chunk.getAttr("to");
			String resource = "";
			if(truncatedTo.indexOf("/")!=-1){
				truncatedTo = truncatedTo.substring(0, truncatedTo.indexOf("/"));
				resource = truncatedTo.substring(truncatedTo.indexOf("/")+1);
			}
			// in case of presence we need to dispatch to all targets.
			if(chunk.name.equals("presence")){
				Vector v = getAllLocalJabberConnections(truncatedTo);
				for(int i=0;i<v.size();i++){
					JabberConnection jc = (JabberConnection)v.elementAt(i);
					chunk.attributes.put("to", chunk.attributes.get("to")+"/"+jc.resource);
					jc.send(chunk.toString());
				}
			}
			else if(chunk.name.equals("message")){
				// need to implement message dispatching
				
				JabberConnection targetConnection = a.getConnection(truncatedTo, resource);
				if(targetConnection!=null){
					_logger.debug("Sending message to target. ");
					targetConnection.send(chunk.toString());
				}
				else{
					_logger.debug("No local target connection found. Storing in database");
					chunk.addAttr("to", truncatedTo);
					// need to add a timestamp to the chunk
					Element x = new Element("x");
					x.addAttr("from", chunk.getAttr("from"));
					x.addAttr("xmlns", "jabber:x:delay");
					x.addAttr("stamp", sdf1.format(new Date())+"T"+sdf2.format(new Date()) );
					chunk.addElement(x);
					this.a.s.dbgate.storeHistoricEntry(chunk);
				}
				
				
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	Vector getAllLocalJabberConnections(String jid){
		Vector ret = new Vector();
		try{
			for(int i=0;i<this.a.connections.size();i++){
				JabberConnection jc = (JabberConnection)this.a.connections.elementAt(i);
				if(jc.secondaryjids.contains(jid))ret.add(jc);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;	
	}
	
	
}
