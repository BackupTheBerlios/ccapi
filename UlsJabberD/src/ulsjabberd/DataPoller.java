/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

import java.net.*;
import java.io.*;

public class DataPoller extends Thread{

	Accepter a;
	
	public DataPoller(Accepter a){
		this.a=a;
	}
	
	public void run(){
		while(true){
			
			// need to
			try{
				int j = a.connections.size(); 
				for(int i=0;i<j;i++){
					JabberConnection jc=(JabberConnection)a.connections.elementAt(i);
					//System.out.println("Stream "+i+": "+jc.getS().getInputStream().available());
					if( jc.getDin().available()>0){
						// do the actual data parsing.
						jc.workOut();
					}
				}
				
				sleep(100);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
}
