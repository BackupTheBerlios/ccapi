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
 * does the history stuff, stores, retrieves and deleted entries 
 * in the user's data storage database 
 */
import ulsjabberd.xml.Element;
import java.util.*;

public class HistoryStorage {

	HistoryStorage(){
		
	}
	
	/**
	 * adds an element into the database
	 * @param jid
	 * @param element
	 */
	public void addElement(String jid, Element element){
		
	}
	
	/**
	 * removes an element with a given id from the database
	 *
	 */
	public void removeElement(int id){
		
	}
	/**
	 * retrieves all storage elements for a given jid. 
	 * @param jid
	 * @return
	 */
	public Vector retrieveElements(String jid){
		Vector ret = new Vector();
		try{
			
		}
		catch(Exception e){
			
			e.printStackTrace();
		}
		return ret;
	}
}
