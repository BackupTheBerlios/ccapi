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
import java.util.*;
import java.net.*;
import java.io.*;

public class LoadTest extends Thread{

	
	Vector connections = new Vector();
	LoadTest(int min, int max){
		try{
			for(int i=min;i<max;i++){
				Socket s = new Socket("192.168.40.100", 5222);
				System.out.println("Connected #"+i);
				LoadTestConnection ltc = new LoadTestConnection(s, i, min,max);
				connections.add(ltc);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		start();
	}
	
	public void run(){
		while(true){
			
			try{
				for(int i=0;i<connections.size();i++){
					sleep((int)(Math.random()*2000)+10);
					LoadTestConnection ltc = (LoadTestConnection)connections.elementAt(i);
					ltc.act();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		LoadTest lt = new LoadTest(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	}
}


class LoadTestConnection{
	Socket s;
	DataInputStream din;
	DataOutputStream dout;
	int i; 
	int min, max;
	LoadTestConnection(Socket s, int i, int min, int max){
		this.min = min;
		this.max = max; 
		this.i=i;
		this.s = s;
		try{
			din = new DataInputStream(s.getInputStream());
			dout = new DataOutputStream(s.getOutputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	int state = 0;
	void act(){
		try{
			// first read any incoming data. 
			
			int av = din.available();
			if(av > 0){
				byte[] b = new byte[av];
				din.read(b);
				String in = new String(b);
				if(in.startsWith("<message"))System.out.println(in+"\n");
			}
			
			switch(state){
				case 0:
					// send stream header and logg in. 
					String send = "<stream:stream xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns=\"jabber:client\" to=\"gmx.de\" >";
					dout.writeBytes(send);
					// logg in. 
					send = "<iq type=\"set\" id=\"auth_2\" to=\"gmx.de\" >";
					send += "<query xmlns=\"jabber:iq:auth\">";
					send += "<username>tester"+i+"</username>";
					send += "<password>LOADTEST</password>";
					send += "<resource>test</resource>";
					send += "</query></iq>";
					dout.writeBytes(send);
					state = 1;
					break;
				case 1:
					// waiting for stream opened
					
					int target = (int)(Math.random()*(max-min));
					String message = "<message to='tester"+target+"@gmx.de'><body>test</body></message>";
					dout.writeBytes(message);
					
					break;
					
				case 2:
					break;
			}
		}
		catch(Exception e){
			state = 2;
			System.out.println("Error in connection "+i);
			e.printStackTrace();
		}
	}
}