/*
 * Created on 24.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 *
 * This example obtains data from the cortal consors servers and stores the data into a CVS file. 
 *  
 */
package Examples;

import java.util.Vector;
import CCAPI.*;

public class DataRetrievalExample {

	DataRetrievalExample(){
		ConsorsQuoteRetriever cqr = new ConsorsQuoteRetriever();
		
		// We need to obtain the isin through a plain search on the consors servers
		// In this case we search for the official isin of the DAX WKN (846900)
		String isin = cqr.search("846900");
		System.out.println(" Isin for 846900: "+isin);
		
		// We need to obtain the history 
        Vector v = cqr.getHistory(isin);
        // the First candle
        Candle c0 = (Candle) v.elementAt(0);
        // the last candle 
        Candle cX = (Candle) v.elementAt(v.size() - 1);

        System.out.println("First candle  : " + c0.toString());
        System.out.println("Last candle   : " + cX.toString());
	}
	
	public static void main(String[] args) {
		DataRetrievalExample dre = new DataRetrievalExample();
	}
	
}
