package CCAPI;

import java.net.*;
import java.io.*;
import org.htmlparser.*;
import org.htmlparser.util.*;
import org.htmlparser.visitors.*;
import org.htmlparser.tags.*;

/**
 * obtains the bnp paribas options from the european stock exchange
 */

public class OSGetterEuwax{

	public String getKODax(String type, double from, double to){
		
		try{
			Parser p=new Parser("http://www.euwax.de/finder/mta_finder/fnd_build_mta.php?wp_keyword=&fnd_typ=alle&region_basiswert=alle&basiswert=alle&optionsart="+type+"&basispreis_ab=&basispreis_bis=&ko_schwelle_ab="+from+"&ko_schwelle_bis="+to+"&lauf_ab=&lauf_bis=&emittent%5B1%5D=bnp&fndr_sel=kop&sort_me=&asc_desc=&nln_min=&nln_max=&special_sel=&back_url=%2Fprod_ko%2Fknockout.htm&back_target=_self&go.x=0&go.y=0");
			Node nodes[] = p.extractAllNodesThatAre(TableTag.class);
		
			for(int i=0;i<nodes.length;i++){
				TableTag tt=(TableTag)nodes[i];
				
				System.out.println(tt.getRowCount());
				if(tt.getRowCount()>2){
					//ok, work out the result set. 
					for(int j=2;j<tt.getRowCount()-1;j++){
						TableRow tr=tt.getRow(j);
						TableColumn[] tc=tr.getColumns();
						NodeList nl=tc[0].getChild(0).getChildren();
						Node[] n=nl.toNodeArray();
						System.out.println(n[0].getText());
						return n[0].getText();
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	

	public static void main(String[] args){
		OSGetterEuwax o=new OSGetterEuwax();
		o.getKODax("call", 4000.0,4000.0);
	}
}
