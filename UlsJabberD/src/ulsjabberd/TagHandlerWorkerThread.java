/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ulsjabberd;

public class TagHandlerWorkerThread extends Thread{
	Accepter a;
	TagHandlerWorkerThread(Accepter a){
		this.a=a;
	}
	
	public void run(){
		while(true){
			try{
				TagHandler th = a.popTagHandler();
				
				if(th == null)sleep(100);
				else{
					// work out tag handler.
					a.packetsserved++;
					th.handle();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
