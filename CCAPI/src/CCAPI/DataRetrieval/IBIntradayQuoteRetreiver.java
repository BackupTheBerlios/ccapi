/*
 * Created on 19.03.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package CCAPI.DataRetrieval;
import com.ib.client.*;

public class IBIntradayQuoteRetreiver implements EWrapper{
	
	
    public void tickPrice( int tickerId, int field, double price, int canAutoExecute){
    	
    }
    
    public void tickSize( int tickerId, int field, int size){
    	
    }
    
    public void orderStatus( int orderId, String status, int filled, int remaining,
            double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId){
    	
    }
    
    public void openOrder( int orderId, Contract contract, Order order){
    	
    }
    
    public void error( String str){
    	System.out.println(str);
    }
    
    public void connectionClosed(){
    	
    }
    
    public void updateAccountValue(String key, String value, String currency, String accountName){
    	
    }
    
    public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, 
            double averageCost, double unrealizedPNL, double realizedPNL, String accountName){
    	
    }
    
    public void updateAccountTime(String timeStamp){
    	
    }
    
    public void nextValidId( int orderId){
    	
    	
    }
    
    public void contractDetails(ContractDetails contractDetails){
    	
    }
    
    public void execDetails( int orderId, Contract contract, Execution execution){
    	
    }
    
    public void error(int id, int errorCode, String errorMsg){
    	
    }
    
    public void updateMktDepth( int tickerId, int position, int operation, int side, double price, int size){
    	
    }
    
    public void updateMktDepthL2( int tickerId, int position, String marketMaker, int operation, int side, double price, int size){
    	
    }
    
    public void updateNewsBulletin( int msgId, int msgType, String message, String origExchange){
    	
    }
    
    public void managedAccounts( String accountsList){
    	
    }
    
    public void receiveFA(int faDataType, String xml){
    	
    }
    

	public IBIntradayQuoteRetreiver(){
		EClientSocket ecs = new EClientSocket(this);
		ecs.eConnect("", 1234, 100);
		
		while(true){
			try{
				Thread.sleep(100);
			}
			catch(Exception e){
				
			}
		}
	}
	
	
	public static void main(String[] args) {
		IBIntradayQuoteRetreiver ib = new IBIntradayQuoteRetreiver();
	}
}
