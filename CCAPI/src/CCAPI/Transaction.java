/*
 * Created on 25.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package CCAPI;

import org.w3c.dom.*;


public class Transaction {
	
	public String isin;
	public int amount; 
	public String exchange; 
	public double price; 
	public String type;
	public String date;
	
	/**
	 * deserializing the transaction 
	 * @param node
	 */
	public void deserialize(Node node){
		NodeList nl = node.getChildNodes();
		for(int i=0;i<nl.getLength();i++){
			Node n = nl.item(i);
			if(n.getNodeName().equals("isin")){
				// work out isin 
				isin = n.getNodeValue();
			}
			else if(n.getNodeName().equals("exchange")){
				// work out exchange
				exchange = n.getNodeValue();
			}
			else if(n.getNodeName().equals("amount")){
				// work out amount
				amount = Integer.parseInt(n.getNodeValue());
			}
			else if(n.getNodeName().equals("price")){
				// work out price
				price = Double.parseDouble(n.getNodeValue());
			}
			else if(n.getNodeName().equals("type")){
				// work out type
				type = n.getNodeValue();
			}
			else if(n.getNodeName().equals("date")){
				// work out date 
				date = n.getNodeValue();
			}
		}
	}
	
}
