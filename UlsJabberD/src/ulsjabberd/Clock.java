/*
 * Created on Mar 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

import ulsjabberd.cronservices.*;

/**
 * @author us
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.util.*;

public class Clock extends Thread{

	Vector cronjobs = new Vector();
	Starter s;
	Clock(Starter s){
		this.s=s;
	}

	public void run(){
		while(true){
		
			try{
				System.out.println("Clock ticking.");
				sleep(10000);
				for(int i=0;i<cronjobs.size();i++){
					CronService cs = (CronService)cronjobs.elementAt(i);
					cs.act();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
	}
	
	
}
