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

import org.apache.log4j.Logger;

import CCAPI.*;
import CCAPI.DataRetrieval.ConsorsQuoteRetriever;

import java.util.*;

public class DataRetrievalFutureExample extends Thread{

	static Logger log = Logger.getLogger(LocalPortfolio.class);
	
	ConsorsQuoteRetriever cqr;
	String wkn; 
	// following specify wether the 3, 10, 60 or 5 minute candles have been sent already. 
	boolean spansent1 = false;
	boolean spansent2 = false;
	boolean spansent3 = false;
	boolean spansent4 = false;

	Date date1, date2, date3, date4;

	boolean fiveMinuteSave = false;


	Candle current = new Candle(), current2 = new Candle(), current3 = new Candle(), current_five_minute = new Candle();
	DatabaseLayer dbl = new DatabaseLayer();
	
	DataRetrievalFutureExample(){
		cqr = new ConsorsQuoteRetriever();
		wkn = "11400707";
		start();
	}
	
	public static void main(String[] args) {
		DataRetrievalFutureExample dre = new DataRetrievalFutureExample();
	}
	
	boolean running = true;
	boolean portfoliochecked=false;

	public void run() {
		
		while (running) {
			try {
				Thread.sleep(5000);
				log.debug("Checking date+time.");
				System.out.println("*** HalfTimeRetrieverFuture for "+wkn);
				Date d1 = new Date();
				
				int tz=d1.getTimezoneOffset()/60;
				
				
				
				d1.setHours(d1.getHours()+1+tz);
				System.out.println("Date:"+d1.getHours()+"h/"+d1.getMinutes()+"m/"+d1.getDay()+"d");
				System.out.println("\n Current time: "+d1.toString() + " \n");	
				d1.setSeconds(0);			
	
				boolean b1=false;
				boolean b2=false;
				
				if ( (d1.getHours()==9 && d1.getMinutes()>=15) || d1.getHours()>9 ) b1 = true;
				if ( (d1.getHours()==20 && d1.getMinutes()<=15) || d1.getHours()<20 ) b2 = true;
				if (  (b1 && b2) && d1.getDay()>0 && d1.getDay()<6) {
					
					log.debug("Retrieving dax future value.");	
					
					
					///// CHANGE DATASOURCE HERE ! 					
	
					double value = cqr.getQuote(wkn);
				


					System.out.println("   Retrieved dax future value :"+value);
					log.debug("Retrieved future: "+value);

					//build the candles
					if(value!=0){
							
							date1 = new Date();
							date2 = new Date();
							date3 = new Date();
							date4 = new Date();

							//update the ten minute candle
							if (current.open == -1){
								current.open = value;
								current.close=value;
								current.hi=value;
								current.low=value;
							}
							if (value > current.hi)
								current.hi = value;
							if (value < current.low)
								current.low = value;
								
							current.date = date1;




							if (current.close != value) {
								current.close = value;
							}
		
							//update the three minute candle
							if (current2.open == -1){
								current2.open = value;
								current2.close=value;
								current2.hi=value;
								current2.low=value;
							}
								
							if (value > current2.hi)
								current2.hi = value;
							if (value < current2.low)
								current2.low = value;
							
							current2.date = date2;

							if (current2.close != value) {
								current2.close = value;
							}
		
							//update the hour candle
							if (current3.open == -1){
								current3.open = value;
								current3.close=value;
								current3.hi=value;
								current3.low=value;
							}
							if (value > current3.hi)
								current3.hi = value;
							if (value < current3.low)
								current3.low = value;
							

							current3.date = date3;

							if (current3.close != value) {
								current3.close = value;
							}
							
							// update the five minute candle
							if(current_five_minute.open == -1){
								current_five_minute.open = value;
								current_five_minute.close = value;
								current_five_minute.hi = value;
								current_five_minute.low = value;
							}
							if(value > current_five_minute.hi)
								current_five_minute.hi = value;
							if(value < current_five_minute.low)
								current_five_minute.low = value;
							current_five_minute.close = value;

							// date
							//if(current_five_minute.date == null)current_five_minute.date = date4;

					}
						

					Date d = new Date();

					// ten minute check 
					if (!b2 && ((d.getMinutes() % 10) == 0 && (!(d.getMinutes() == 0 && d.getHours() == 9 )))) {
						System.out.println("Saving ten minute candle. ");
						if (!spansent1 & current.close!=0 & current.close!=-1) {
							dbl.saveTenMinuteCandleDaxFuture(current);
							current = new Candle();
							spansent1=true;
						}
					} else {
						spansent1 = false;
					}
					// three minute check 
					if (!b2 && (d.getMinutes() % 3 == 0)) {
						System.out.println("Saving three minute candle.");
						if (!spansent2 & current.close!=0 & current2.close!=-1) {
							dbl.saveThreeMinuteCandleDaxFuture(current2);
							current2 = new Candle();

							spansent2=true;

						}
					} else {
						spansent2 = false;
					}
					// hour check 
					if ((d.getMinutes() == 0) && d.getHours()!=9 ) {
						System.out.println("About to save hour candle dax.");
						if (!spansent3 & current3.close!=0 & current3.close!=-1) {
							dbl.saveHourCandleDaxFuture(current3);
							current3    = new Candle();
							spansent3=true;
						}
					} else {
						spansent3 = false;
					}
						
					
					if(fiveMinuteSave && ! b2){
						dbl.saveFiveMinuteCandleDaxFuture(current_five_minute);
					}			
					if(b2){
						current_five_minute.date = null;
					}


					// five minute check 
					if ((d.getMinutes()%5 == 0) && (!(d.getMinutes()==0 && d.getHours()==9 )) ){
						System.out.println("About to save five minute candle");
						if(!spansent4 && current_five_minute.close != 0 && current_five_minute.close!= -1){
							if(current_five_minute.date == null){
								current_five_minute.date = d;
								fiveMinuteSave = true;
							}


							dbl.saveFiveMinuteCandleDaxFuture(current_five_minute);
							current_five_minute = new Candle();
							// set the next date of the candle
							
							//current_five_minute.date = d;
							Calendar cal = Calendar.getInstance();
							cal.set(Calendar.SECOND, 0);
							cal.add(Calendar.MINUTE, 5);
							cal.add(Calendar.MINUTE, -15);	
							System.out.println("Candle date: " + current_five_minute.date.toString());
							System.out.println(" / calendar: " + cal.getTime().toString());	
							current_five_minute.date = cal.getTime();
							
							spansent4 = true;
						}
					}
					else{
						spansent4 = false;
					}
				
				}
				else{
					System.out.println("   Not retrieving value for "+this.wkn+"  due to date check.");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	
}

