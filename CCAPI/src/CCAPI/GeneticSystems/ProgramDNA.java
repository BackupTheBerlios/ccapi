/*
 * Created on 28.02.2005
 * 
 * GPL protected.
 * Author: Ulrich Staudinger
 * 
 */
package CCAPI.GeneticSystems;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
/**
 * contains the dna of a program and actually evolves the data. <br/>
 * A dna is an xml chunk <br/>
 * Must be some sort of simply language, must be evaluatable. There must be an entry and an exit rule. 
 * 
 * 
 * Symbols of this language: <br/>
 * CLOSE[x] - returns the close before x days <br/>
 * '(' and ')' - actually saying what to evaluate <br/>
 * RSI[x] - returns the rsi of entries x before current <br/>
 * SMA[x,y] - means the SMA of x entries before current 
 * '>', '=' and '<' - if syntax, lower, equal and higher than <br/>
 * 0...9  - figures and numbers <br/>
 * +,-,/,* - primitive calculations <br/>
 * &, | - AND and OR operations  
 * <br/>
 * <br/>
 * If the whole expression is evaluated as true - the entry or exit is done.<br/> 
 * <br/>
 * <br/>
 * example of a dna: <br/>
 *  ( CLOSE[10] > CLOSE[1] ) | ( CLOSE[1] < CLOSE[4] )
 *  
 * example of a mutation 
 * 
 * .
 * -<-
 * (-<-)
 * (-<-)|.
 * (-<-)|(->-)
 * ((-<-)|(->-))
 * ((-<-)|(->-))&.
 * 
 * distinguishing between logical and calculation parameters - 
 * in the upper case a minus is a calculation parameter and a dot is a logical parameter
 * brackets may be added around the whole dna all the time
 * at the end and at the beginning a boolean parameter may be appended
 *    when added at the end, a spaceholder for a logical parameter must be appended
 *    with respect to the position of the boolean parameter
 * 
 * dots must always at first become replaced with brackets.  
 * 
 * ((-<-)|(->-))&.
 * ((-<-)|(->-))&(->-)
 * (((-<-)|(->-))&(->-))
 * (((-<-)|(->-))&(->-))|.
 * (((-<-)|(->-))&(->-))|(.)
 * (((-<-)|(->-))&(->-))|(->-)
 * 
 * this all leads to the problem that mutation and extension of the dna takes place only at the end 
 * of the strings. And extension must be possible within the gene. 
 * 
 * sort of mutation trees. 
 * 
 * (((-<-)|(->-))&.)|(->-)
 * (((-<-)|(->-))&(.))|(->-)
 * (((-<-)|(->-))&(.|.))|(->-)
 * (((-<-)|(->-))&((.)|(.)))|(->-)
 * (((-<-)|(->-))&((-<-)|(->-)))|(->-)
 * 
 * inner mutation may only happen by replacing a bracketed term with a new placeholder [.] { (->-) ==> . }
 * the . term must then at first become bracketed [(.)] 
 * a bracketed dot term may be replaced by a logical mutation [., .|., .&.] or a 
 *    logical combination term [->-, -<-, -=-]    
 * as long as a bracketed dot [.] term exists no other bracketed term may be replaced with a dot term 
 * 
 * value terms may be replaced with other value terms
 * 
 * Vocabulary: 
 * dot term = [.] is a placeholder
 * logical mutation = [., .|., .&.] start point for other mutations 
 * logical combination term  = [->-, -<-, -=-] end point of a mutation 
 * value term = [numbers, RSI, STOCH, etc] values that represent a real value
 * bracketed term  = [(..)], a term encapsulated by brackets, holding either
 *    a dot term, logical mutation term, logical combination term or a value term.   
 * 
 * problem is: 
 * the whole language must be boolean oriented. 
 * means, a node must always be part of a boolean node or be a boolean node. 
 * if there is no boolean node in the beginning, a boolean node must be generated. 
 * Nodes must be marked as mutable - maybe some sort of mutation grouping - like gt and lt can only replace 
 * other gt and lt and equals. -> Gene-Sorts are Compares, plain values, logical operators    
 * 
 * in case a gene is an AND, it can mutate to AND or OR or GT or LT 
 * 
 * Inserting new genes must be solved
 * At some spots in the DNA, new genes can be inserted - in a GT|LT comparison no other GT|LT comparison 
 * is valid
 * 
 * Plain values may mutate to other plain values
 * 
 * 
 * <entry>
 * <gt>
 * <close>1</close>
 * <close>2</close>
 * </gt>
 * </entry>
 * 
 */

import org.apache.crimson.tree.DOMImplementationImpl;
import org.w3c.dom.*;

public class ProgramDNA {
	/**
	 * the DNA chromosomes 
	 */
	public Document entrydna, exitdna;

	// helper objects
	/**
	 * contains the document parser
	 */
	DocumentBuilder db;
	
	/**
	 * a very plain constructor
	 */
	public ProgramDNA(){
		initialize();
		
	}
	
	/**
	 * initializes the dna already
	 * @param dna
	 */
	/*public ProgramDNA(String entrydna, String exitdna){
		// do the assignment
		this.entrydna = entrydna;
		this.exitdna = exitdna;
	}*/
	public void initialize(){
		
		entrydna = DOMImplementationImpl.getDOMImplementation().createDocument("ROOT", "entrydna", null);
		exitdna = DOMImplementationImpl.getDOMImplementation().createDocument("ROOT", "exitdna", null);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try{
			db = dbf.newDocumentBuilder();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * mutates the entry
	 * @return
	 */
	public void mutateEntry(){
		// return mutate(entrydna);
	}
	/**
	 * mutates the exit
	 * @return
	 */
	public void mutateExit(){
		// return mutate(exitdna);
	}
	
	public boolean getRandomBoolean(){
		if ( Math.random() > 0.5){
			return true;
		}
		return false; 
	}
	
	public int getRandomInt(int max){
		int ret = (int)(Math.random()*max);
		System.out.println("Max: "+max+ " / "+ret);
		return ret;
	}
	
	/**
	 * returns a random node
	 * @param dna
	 * @return
	 */
	public Node getRandomNode(Document dna){
		Node ret; 
		if(dna.getDocumentElement().getChildNodes().getLength()>0){
			// do a random select
			
			
			//Node node = dna.getDocumentElement().getChildNodes().item((int)(Math.random()*dna.getDocumentElement().getChildNodes().getLength()));
			
			// need to find an algorithm to actually walk the tree. 
			
			Node node = dna.getDocumentElement();
			while(getRandomBoolean()){
				if(node.getChildNodes().getLength()==0)break;
				node = node.getChildNodes().item(getRandomInt(node.getChildNodes().getLength()));
				if(node.getNodeName().equals("#text")){
					if(node.getParentNode().getChildNodes().getLength()==1){
						node = node.getParentNode();
						break;
					}
				}
				if(node!=null){
					if(node.getNodeName()!=null){
						while(node.getNodeName().equals("#text")){
							node = node.getParentNode().getChildNodes().item(getRandomInt(node.getParentNode().getChildNodes().getLength()));
						}
					}
				}
				while(node == null){
					node = node.getChildNodes().item(getRandomInt(node.getChildNodes().getLength()));
				}
			}
			System.out.println("Selected "+node.getNodeName());
			
			/*if (!node.getNodeName().equals("close") && !node.getNodeName().equals("hi") 
				&& !node.getNodeName().equals("low") && !node.getNodeName().equals("open")	
				){*/
				ret = node;
			//}
		}
		else{
			// simply append
			ret = dna.getDocumentElement();
		}
		return ret;
	}
	
	public Document appendRandom(Document dna, Element e){
		if(dna.getDocumentElement().getChildNodes().getLength()>0){
			// do a random select
			String ename = e.getNodeName();
			
			//Node node = dna.getDocumentElement().getChildNodes().item((int)(Math.random()*dna.getDocumentElement().getChildNodes().getLength()));
			
			// need to find an algorithm to actually walk the tree. 
			
			Node node = dna.getDocumentElement();
			while(getRandomBoolean()){
				if(node.getChildNodes().getLength()==0)break;
				node = node.getChildNodes().item(getRandomInt(node.getChildNodes().getLength()));
				if(node.getNodeName().equals("#text")){
					if(node.getParentNode().getChildNodes().getLength()==1){
						node = node.getParentNode();
						break;
					}
				}
				if(node!=null){
					if(node.getNodeName()!=null){
						while(node.getNodeName().equals("#text")){
							node = node.getParentNode().getChildNodes().item(getRandomInt(node.getParentNode().getChildNodes().getLength()));
						}
					}
				}
				while(node == null){
					node = node.getChildNodes().item(getRandomInt(node.getChildNodes().getLength()));
				}
			}
			System.out.println("Selected "+node.getNodeName());
			
			/*if (!node.getNodeName().equals("close") && !node.getNodeName().equals("hi") 
				&& !node.getNodeName().equals("low") && !node.getNodeName().equals("open")	
				){*/
				node.appendChild(e);
			//}
		}
		else{
			// simply append
			dna.getDocumentElement().appendChild(e);
		}
		return dna;
	}
	
	public Document mutate(Document dna){
		
		int mutationtype = (int)(Math.random()*8);
		System.out.println("Using mutation type "+mutationtype);
		//mutationtype = 0;
		switch(mutationtype){
			case 0:
				// adding some brackets
				Element n = dna.createElement("open");
				n.appendChild(dna.createTextNode(""+(int)(50*Math.random())));
				// simply get any node from the dna
				appendRandom(dna, n);
				
				// appended. 
				
				break;
				
			case 1: 
				// changing a random node through another node 
				
//				 adding some brackets
				Element h = dna.createElement("hi");
				h.appendChild(dna.createTextNode(""+(int)(50*Math.random())));
				// simply get any node from the dna
				appendRandom(dna, h);
				
				// appended. 
				
				break;
				
			case 3:
//				 adding some brackets
				Element l = dna.createElement("low");
				l.appendChild(dna.createTextNode(""+(int)(50*Math.random())));
				// simply get any node from the dna
				appendRandom(dna, l);
				
				// appended. 
				break;
				
			case 4:
//				 adding some brackets
				Element c = dna.createElement("close");
				c.appendChild(dna.createTextNode(""+(int)(50*Math.random())));
				// simply get any node from the dna
				appendRandom(dna, c);
				
				// appended. 
				break;
				
			case 5:
				// adding a gt 
				Element gt = dna.createElement("gt");
				appendRandom(dna, gt);
				//dna.getDocumentElement().appendChild(gt);
				
				break;
			default:
				// doing nothing
				break;
			
		}	
		return dna; 
	}
	
	
	// evaluates this program and returns the calculated profit
	public double evaluate(Vector candles){
		
		// need to do some tree walking
		
		 
		
		
		
		return 0.0;
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
	 * problem is the mutation start. somehow the porgam must have a proper start.
	 * if the program has a proper start 
	 * 
	 * @param args
	 */
	
	public static void main(String[] args){
		ProgramDNA pdna = new ProgramDNA();
		for(int i=0;i<10;i++){
			pdna.entrydna = pdna.mutate(pdna.entrydna);
		}
		
		System.out.println(pdna.dump(pdna.entrydna.getDocumentElement()));
	}
	
}
