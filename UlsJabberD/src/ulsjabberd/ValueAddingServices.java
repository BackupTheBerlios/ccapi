/*
 * Created on Mar 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

import org.apache.log4j.Logger;

public class ValueAddingServices {
	
	static Logger _logger = Logger.getLogger(ValueAddingServices.class);
	
	Accepter a;
	
	ValueAddingServices(Accepter a){
		this.a = a;
	}
	
	/**
	 * sends an Ad to all connected users. 
	 */
	public void sendAd(String subject, String body, String xhtml){
		_logger.debug("Sending ad : "+subject+"/"+body+"/"+xhtml);
		/*		String ad = "<message from='gmx.net' to='"+to+"'><body>plaintext </body>" +
		"<html xmlns='http://jabber.org/protocol/xhtml-im'>" +
		"<body xmlns='http://www.w3.org/1999/xhtml'>" +
		"<p>Hey, are you licensed to <a href='http://www.jabber.org/'>Jabber</a>?</p>" +
		"<p><img src='http://www.jabber.org/images/psa-license.jpg'" +
		"alt='A License to Jabber' height='261' width='537'/></p>" +
		"</body></html></message>";

		//JabberConnection jc = this.a.getConnection(to, "");
		//if(jc!=null)jc.send(ad);
		 * 
		 * 
		 */
		
		// do a raw dispatch
		int j = this.a.rawConnections.size();
		for(int i=0;i<j;i++){
			try{
				JabberConnection jc = (JabberConnection)a.rawConnections.elementAt(i);
				String ad = "<message from='"+jc.domain+"' to='"+jc.primaryjid+"'>" +
						"<subject>"+subject+"</subject><body>"+body+"</body>" +xhtml+"</message>";
				jc.send(ad);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * sends an Ad to all ustaudinegr 
	 */
	void sendPersonalAd(String body, String xhtml){
		String to = "ustaudinger@gmx-gmbh.de";
		String from = "gmx.net";
		
		
		String ad = "<message from='gmx.net' to='"+to+"'><body>"+body+"</body>" +
		   xhtml + "</message>";

		JabberConnection jc = this.a.getConnection(to, "");
		if(jc!=null)jc.send(ad);
		
	}
	
	/**
	 * sends a personal welcome ad. 
	 * @param customerno
	 * @param jc
	 */
	void sendPersonalWelcomeAd(int customerno, JabberConnection jc){
		
		_logger.debug("Sending welcome ad to "+customerno);
		
		String ad = "<message from='"+jc.domain+"' to='"+jc.primaryjid+"'><body>Herzlich willkommen "+customerno+"! " +
				"Es ist jetzt "+(new java.util.Date())+"</body>" +
				"<html xmlns='http://jabber.org/protocol/xhtml-im'>" +
				"<body xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>Herzlich willkommen "+customerno+" @ </p><p><img src='http://www.jabber.org/images/psa-license.jpg'" +
				"alt='A License to Jabber' height='261' width='537'/></p>" +
				"</body></html>"+
				"</message>";
		
		jc.send(ad);
	}
	
}
