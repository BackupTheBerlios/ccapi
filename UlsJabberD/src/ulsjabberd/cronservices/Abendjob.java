/*
 * Created on Mar 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd.cronservices;

/**
 * @author us
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates

 * */

import ulsjabberd.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class Abendjob implements CronService{
	Starter s;
	
	/**
	 * constructor
	 * @param s
	 */
	public Abendjob(Starter s){
		this.s = s;
	}
	
	/**
	 * called to actually act
	 */
	public void act(){
		System.out.println("Acting");
		Date d = new Date();
		if(d.getHours()==15){
			// work out the tv movie stuff
			
			try{
				URL url = new URL("http://www.tvmovie.de/startseite/index.html");
				DataInputStream din = new DataInputStream(new BufferedInputStream(url.openStream()));
				
				String wann = "";
				String program = "";
				String title = "";
				String text;
				String ad = "";
				
				String l = din.readLine();
				int counter = -1;
				while(l!=null){
				
					if(l.indexOf("name=\"tagestipp")!=-1){
						counter++;
					}
					if(counter == 3){
						System.out.println(l);
						// extract program 
						wann = l.substring(l.indexOf("<b>")+3);
						wann = wann.substring(0, wann.indexOf("</b>"));
						
					}
					else if(counter == 16){
						System.out.println(l);
						program = l.substring(l.indexOf("sans-serif\">")+12);
						program = program.substring(0, program.indexOf("<br>"));
						//this.s.a.vas.sendAd("Tagestipp TVMovie!", wann+"\n"+program, "");
					}
					else if(counter == 22){
						System.out.println(l);
						title = l.substring(l.indexOf("<b>")+3);
						l = title;
						title = title.substring(0, title.indexOf("</b>"));
						text = l.substring(l.indexOf("sans-serif\">")+12);
						text = text.substring(0, text.indexOf("<"));
						
						
						this.s.a.vas.sendAd("Tagestipp TVMovie - immer um 15h GMT!", "Filmtitel: "+title+"\n"+wann+"\n"+program+"\n"+text+"\n\n... und mit HTML eben dann mit bildern, aktuell verschickt zu "+this.s.a.connections.size()+" Benutzern (online)", "");
						
					}
					
					if(counter!=-1)counter++;
					l = din.readLine();
				}
		
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	
	
}
