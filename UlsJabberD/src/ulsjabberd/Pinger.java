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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Pinger extends Thread{

	Accepter a;
	public Pinger(Accepter a){
		this.a=a;
		start();
	}
	
	public void run(){
		while(true){
			
			// iterate over the connections and send a nop.
			int n = a.rawConnections.size();
			for(int i=0;i<n;i++){
				try{
					JabberConnection jc = (JabberConnection)a.rawConnections.elementAt(i);
					if(jc!=null){
						// send a keep alive
						jc.keepAlive();
					}
					else{
						break;
					}
				}
				catch(Exception e){
					//e.printStackTrace();
					break;
				}
			}
			
			// safety sleeping
			try{
				sleep(10000);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
