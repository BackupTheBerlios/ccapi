/*
 * Created on Mar 7, 2005
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
import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

public class WebServer extends Thread{
	
	static Logger _logger = Logger.getLogger(WebServer.class);
	
	int port;
	Starter s;
	ServerSocket ss;
	WebServer(Starter s, int port){
		this.port = port;
		this.s = s;
		try{
			ss = new ServerSocket(port);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void run(){
		while(true){
			try{
				Socket s = ss.accept();
				handle(s);
				//s.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void handle(Socket s){
		try{
			DataInputStream din = new DataInputStream(s.getInputStream());
			String l = din.readLine();
			StringTokenizer str = new StringTokenizer(l, " ");
			
			str.nextToken();
			String url = str.nextToken();
			//System.out.println(url);
			workOut(url, s);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void workOut(String url, Socket s){
		try{
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			
			dumpHeader(dout);
			
			String ret = "";
			if ( url.equals("/") ){
				ret = generateIndex();
			}
			
			dout.writeBytes("Content-Length: "+ret.length()+"\n");
			dout.writeBytes("\n");
			
			dout.writeBytes(ret+"\n");
			dout.flush();
			dout.flush();
			
			//s.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void dumpHeader(DataOutputStream dout) throws Exception{
		dout.write("HTTP/1.0 200 OK\n".getBytes());
		dout.write("Content-Type: text/html\n".getBytes());
		dout.write("Connection: Close\n".getBytes());
	
	}
	
	String generateIndex() {
		
		String ret = "";
		ret += ("<html>");
		ret += ("<body>");
		
		ret += "<h2>Users online</h2>\n";
		ret += "Count : "+this.s.a.rawConnections.size()+"<br/>\n";
		
		ret += "<h2>Packets served</h2>\n";
		ret += "Packet count : "+this.s.a.packetsserved+"<br/>\n";
		ret += "Message count : "+this.s.a.messagessent+"<br/>\n";
		
		ret += "<h1>Online dump</h1>";
		Set s = this.s.a.connections.keySet();
		Iterator it = s.iterator();
		while(it.hasNext()){
			String jc1 = (String)it.next();
			JabberConnection jc2 = (JabberConnection)this.s.a.connections.get(jc1);
			ret+=jc1+" is "+jc2.getPresenceType()+"/"+jc2.getPresenceShow()+"/"+jc2.getPresenceStatus()+"<br/>";
			//System.out.println(jc1);
			
			
			
			
		}
		
		
		ret += ("<h1>Roster dump</h1>");
		
		Vector v = this.s.a.um.rosters;
		
		for(int i=0;i<v.size();i++){
			Roster r = (Roster)v.elementAt(i);
			ret+= "<br/>Roster for "+r.customerno+"<br/>";
			for(int j = 0;j<r.entries.size();j++){
				RosterEntry re = (RosterEntry)r.entries.elementAt(j);
				ret += re.displayname+" / "+re.jid+" / " + re.subscription+"<br/>";
			}
		}
		ret += ("</body>");
		ret += ("</html>");
		return ret;
	}
}
