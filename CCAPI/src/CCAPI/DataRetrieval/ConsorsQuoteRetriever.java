/*
 * Created on 02.03.2005
 * 
 * GPL protected. Author: Ulrich Staudinger
 *  
 */
package CCAPI.DataRetrieval;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import CCAPI.Candle;

public class ConsorsQuoteRetriever {

	boolean debug = true;

	public ConsorsQuoteRetriever() {

	}

	double getQuote(String consorsid) {
		try {
			URL url = new URL(
					"http://info.consors.de/financeinfos/snapshot.do?ID_NAME=ID_OSI&ID="
							+ consorsid);
			// InputStream is=url.openStream();
			DataInputStream din = new DataInputStream(new BufferedInputStream(
					url.openStream()));

			String name = "";
			String isin2 = "";

			// read all line wise and append to ret.
			String l = din.readLine();
			boolean quotes = false;

			while (l != null) {
				//if (debug)System.out.println("ConsorsQuoteRetriever READ: " +
				// l);

				if (l.indexOf("<td valign=\"middle\" class=\"h1\" align=\"center\"><font color=\"#008000\"><strong>") != -1) {
					
					// work out the value
					l = l.substring(l.indexOf("<strong>")+8, l.indexOf("</strong>"));
					System.out.println("ConsorsQuoteRetriever READ: " + l);
					// need to reformat the input
					String v = "";
					StringTokenizer str = new StringTokenizer(l, ".");
					while(str.hasMoreTokens()){
						v += str.nextToken();
					}
					v = v.replace(',','.');
					System.out.println("Value: "+v);
					
					return Double.parseDouble(v);
				}
				// l+= new String(l.getBytes("UTF-8"));
				l = din.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) {
		ConsorsQuoteRetriever cqr = new ConsorsQuoteRetriever();
		double v = cqr.getQuote("11235558");
		System.out.println(v);

	}
}