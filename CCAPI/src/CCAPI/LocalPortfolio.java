/*
 * Created on 25.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 * contains classes to manage a portfolio on disc.
 * a portfolio on hard drive  
 * 
 * 
 */

package CCAPI;
import java.util.*;

import javax.swing.text.html.parser.DocumentParser;
import javax.xml.transform.dom.*;
import javax.xml.parsers.*;

import org.apache.crimson.tree.DOMImplementationImpl;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import java.io.*;

public class LocalPortfolio {
	static Logger _logger = Logger.getLogger(LocalPortfolio.class);
	
	/** 
	 * holds the local positions 
	 */
	Vector transactions = new Vector();
	/**
	 * represents the complete portfolio in xml 
	 */
	Document doc;
	/**
	 * the root
	 */
	Element root;
	/**
	 * contains the document parser
	 */
	DocumentBuilder db;
	/**
	 * holds the portfolio name 
	 */
	String portfolioname;
	/**
	 * opens the default portfolio 
	 *
	 */
	public LocalPortfolio(){
		this("default.portfolio");
		
		doc = DOMImplementationImpl.getDOMImplementation().createDocument("A", "B", null);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			db = dbf.newDocumentBuilder();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		root = doc.createElement("portfolio");
		//root.appendChild(doc.createElement("abcd"));
		//root.appendChild(doc.createElement("abcd2"));
		//root.appendChild(doc.createTextNode("some value"));
		//doc.appendChild(root);
		
		System.out.println(dump(root));
	}
	
	/**
	 * Opens a portfolio with a file name 
	 * @param portfolioname
	 */
	public LocalPortfolio(String portfolioname){
		this.portfolioname = portfolioname; 
		open();
	}
	
	/**
	 * adding a transaction to our vector holding transactions 
	 * @param t
	 */
	public void addTransaction(Transaction t){
		transactions.add(t);
		// about to save the data
		save();
	}
	
	/**
	 * opens the portfolio and reads the content into memory
	 *
	 */
	public void open(){
		// open the portfolio
		try{
			_logger.debug("Opening "+portfolioname);
			System.out.println(portfolioname);
			File f = new File(portfolioname);
			
			//need to parse the data 
			Document doc = db.parse(f);
			Element root = doc.getDocumentElement();
			// check if we actually have a portfolio name 
			if(root.getNodeName().equals("portfolio")){
				// ok, portfolio found.  
				NodeList nl = root.getChildNodes();
				for(int i=0;i<nl.getLength();i++){
					Node n = nl.item(i);
					if(n.getNodeName().equals("transaction")){
						Transaction t = new Transaction();
						t.deserialize(n);
						transactions.add(t);
					}
				}
				
			}
			System.out.println(root.getNodeName());
			
		}
		catch(Exception e){
			e.printStackTrace();
			_logger.fatal(e);
		}
	}
	
	/**
	 * saves the portfolio to disc
	 */
	public void save(){
		//
		
		for(int i=0;i<transactions.size();i++){
			Transaction t = (Transaction)transactions.elementAt(i);
			
			Element transaction = doc.createElement("transaction");
			Element isin = doc.createElement("isin");
			Element exchange = doc.createElement("exchange");
			Element amount = doc.createElement("amount");
			Element price = doc.createElement("price");
			Element type = doc.createElement("type");
			Element date = doc.createElement("date");
			
			// fill in the values
			isin.appendChild(doc.createTextNode(t.isin));
			exchange.appendChild(doc.createTextNode(t.exchange));
			amount.appendChild(doc.createTextNode(""+t.amount));
			price.appendChild(doc.createTextNode(""+t.price));
			type.appendChild(doc.createTextNode(t.type));
			date.appendChild(doc.createTextNode(t.date));

			// append the childs
			transaction.appendChild(isin);
			transaction.appendChild(exchange);
			transaction.appendChild(amount);
			transaction.appendChild(price);
			transaction.appendChild(type);
			transaction.appendChild(date);
			
			// 
			root.appendChild(transaction);
		}
		
		System.out.println(dump(root));
		
		File f = new File(portfolioname);
		try{
			DataOutputStream dout = new DataOutputStream(new FileOutputStream(f));
			dout.write(dump(root).getBytes());
			dout.flush();
			dout.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * dumps out the portfolio and returns the xml string 
	 * @return
	 */
	public String dump(Node node){
		String ret = "";
		
		/// switch dependent on the node type
		
		if ( node.getNodeType() == 1){
			// node type 
			ret+= "<"+node.getNodeName()+">";
			
			if(node.hasChildNodes()){
				NodeList nl = node.getChildNodes();
				for (int i=0;i<nl.getLength();i++){
					ret += dump(nl.item(i));
				}	
			}
			ret+= "</"+node.getNodeName()+">";
		}
		else if(node.getNodeType() == 3){
			// text node 
			ret += node.getNodeValue();
		}
		
		return ret; 
	}
	
	/**
	 * actually clears the portfolio
	 *
	 */
	public void clear(){
		transactions = new Vector();
		save();
	}
	
	public static void main(String[] args){
		LocalPortfolio lp = new LocalPortfolio();
		Transaction t = new Transaction();
		t.isin = "abcd";
		lp.addTransaction(t);
		lp.save();
		System.out.println("loading now.... \n\n");
		lp.open();
	}
}
