
/**
 * is a data source for a csv file
 */
package CCAPI;

import java.net.*;
import java.io.*;
import java.util.*;

public class FileDataSourceCSV{

	public FileDataSourceCSV(){
	}

	public Vector loadCSVFile(String filename){
		Vector ret=new Vector();
		try{
			File f=new File(filename);
			DataInputStream din=new DataInputStream(new FileInputStream(f));
			String l=din.readLine();
			while(l!=null){
				StringTokenizer str=new StringTokenizer(l, " ");
				Candle c=new Candle();
				c.datestring=str.nextToken();
				c.open=Double.parseDouble(str.nextToken());
				c.hi=Double.parseDouble(str.nextToken());
				c.low=Double.parseDouble(str.nextToken());
				c.close=Double.parseDouble(str.nextToken());
				c.volume=Integer.parseInt(str.nextToken());
				ret.insertElementAt(c,0);
				l=din.readLine();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
}
