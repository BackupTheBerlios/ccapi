/*
 * Created on 24.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 * An example which does do a plain rsi system check -> check financial documents about the rsi system 
 * 
 */
package Examples;

import java.util.*;
import CCAPI.*;
import CCAPI.DataRetrieval.ConsorsQuoteRetriever;

public class RSISystem {
	
	RSISystem(){
		// doing a plain rsi system 
		ConsorsQuoteRetriever cqr = new ConsorsQuoteRetriever();
		Vector data = cqr.getHistory(cqr.search("846900"));
		
		FinancialLibrary fl = new FinancialLibrary();
		double rsi0, rsi1;
		
		rsi0 = fl.RSI(14, data, 0);
		rsi1 = fl.RSI(14, data, 1);
		
		if( rsi1 < 30  && rsi0 > 30 ){
			System.out.println("BUY.");
		}

		if( rsi1 > 70  && rsi0 < 70 ){
			System.out.println("SELL.");
		}		
	}
	
	public static void main(String[] args) {
		RSISystem rsis = new RSISystem();
	}

	
}
