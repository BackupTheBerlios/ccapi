/*
 * Created on 25.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 * Example which buys and sells 100 thyssen krupp shares via the ActiveTrader Connection 
 * 
 */

package Examples;
import CCAPI.*;

public class ConsorsActiveTraderBuySell {

	// our plain constructor
	ConsorsActiveTraderBuySell(){
		
		// Establishing the connection 
		ActiveTraderConnection atc = new ActiveTraderConnection("localhost", 4242);
		// this would buy ThyssenKrup in Stuttgart with a limit of 100.1 (S), 
		// the order is valid on 2004-09-10
        atc.externalOrder("B", "750000", 100, "STU", 100.1, "S", "2004-09-10", null);
        
        // and now we simply sell it again
        atc.externalOrder("S", "750000", 100, "STU", 100.1, "S", "2004-09-10", null);
        
	}
	
	public static void main(String[] args) {
		ConsorsActiveTraderBuySell r = new ConsorsActiveTraderBuySell();
	}
}
