package CCAPI;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * class to retrieve historical informations from the cortal consors servers.
 * works very well.
 *  
 */
public class ConsorsQuoteRetriever {
	boolean debug = false;

	/**
	 * plain constructor
	 */
	public ConsorsQuoteRetriever() {
	}

	// overloaded constructor that does generate debug messages
	public ConsorsQuoteRetriever(boolean debug) {
		this.debug = debug;
	}

	/**
	 * retrieves the history
	 */
	public Vector getHistory(String isin) {
		Vector ret = new Vector();

		try {
			URL url = new URL(
					"http://chartdata.consors.onvista.de/data/quotes.html");
			HttpURLConnection hpcon = (HttpURLConnection) url.openConnection();

			hpcon.setRequestProperty("POST", "/data/quotes.html HTTP/1.1");

			hpcon.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			hpcon.setRequestProperty("User-Agent", "GeVaSyS NetClient");
			hpcon.setRequestProperty("cookie", "");
			hpcon.setRequestProperty("Pragma", "no-cache");

			hpcon.setRequestProperty("Host", "chartdata.consors.onvista.de");

			String request = "message=%23+DATAOBJECT%0AQUERY%0A%23+Function%0AgetLongtermHistory%0A%23+UserId%0Auser%0A%23+ISIN%0A"
					+ isin
					+ "%0A%23+Exchange%0AGER%0A%23+BeginDate%0A%0A%23+EndDate%0A%0A%23%0A";

			hpcon.setRequestProperty("Content-Length", "" + request.length());

			hpcon.setDoOutput(true);

			OutputStream os = hpcon.getOutputStream();

			os.write(request.getBytes());
			os.close();

			// InputStream is=url.openStream();
			DataInputStream din = new DataInputStream(new BufferedInputStream(
					hpcon.getInputStream()));

			String name = "";
			String isin2 = "";

			// read all line wise and append to ret.
			String l = din.readLine();
			boolean quotes = false;

			while (l != null) {
				if (debug)
					System.out.println("ConsorsQuoteRetriever READ: " + l);

				if (quotes) {
					if (l.equals("#")) {
						break;
					}

					StringTokenizer str = new StringTokenizer(l, ";");
					Candle c = new Candle();

					c.datestring = str.nextToken();
					c.open = Double.parseDouble(str.nextToken());
					c.hi = Double.parseDouble(str.nextToken());
					c.low = Double.parseDouble(str.nextToken());
					c.close = Double.parseDouble(str.nextToken());
					c.volume = Integer.parseInt(str.nextToken());

					//
					c.fullname = name;
					c.isin = isin2;

					ret.addElement(c);
				}

				if (l.startsWith("#Datetime;Open;High;Low;Close")) {
					quotes = true;
				}

				if (l.startsWith("# Name")) {
					name = din.readLine();
				}

				if (l.startsWith("# ISIN")) {
					isin2 = din.readLine();
				}

				// l+= new String(l.getBytes("UTF-8"));
				l = din.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (debug)
			System.out.println("ConsorsQuoteRetriever Read " + ret.size()
					+ " candles");

		Vector r1 = new Vector();

		for (int i = 0; i < ret.size(); i++) {
			r1.addElement((Candle) ret.elementAt(ret.size() - 1 - i));
		}

		return r1;
	}

	public String search(String q) {
		try {
			URL url = new URL(
					"http://chartdata.consors.onvista.de/data/search.html");
			HttpURLConnection hpcon = (HttpURLConnection) url.openConnection();

			hpcon.setRequestProperty("POST", "/data/search.html HTTP/1.1");

			hpcon.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			hpcon.setRequestProperty("User-Agent", "GeVaSyS NetClient");
			hpcon.setRequestProperty("cookie", "");
			hpcon.setRequestProperty("Pragma", "no-cache");

			hpcon.setRequestProperty("Host", "chartdata.consors.onvista.de");

			// String
			// request="message=%23+DATAOBJECT%0AQUERY%0A%23+Function%0AgetLongtermHistory%0A%23+UserId%0Auser%0A%23+ISIN%0A99456%0A%23+Exchange%0AGER%0A%23+BeginDate%0A%0A%23+EndDate%0A%0A%23%0A";
			String request = "message=%23+DATAOBJECT%0AQUERY%0A%23+Function%0Asearch%0A%23+UserId%0Auser%0A%23+Pattern%0A"
					+ q + "%0A%23%0A";

			hpcon.setRequestProperty("Content-Length", "" + request.length());

			hpcon.setDoOutput(true);

			OutputStream os = hpcon.getOutputStream();

			os.write(request.getBytes());
			os.close();

			// InputStream is=url.openStream();
			DataInputStream din = new DataInputStream(new BufferedInputStream(
					hpcon.getInputStream()));

			// read all line wise and append to ret.
			String l = din.readLine();

			if (l == null) {
				if (debug)
					System.out.println("LINE = NULL!!!!");
			}

			while (l != null) {
				if (debug)
					System.out.println(l);

				if (l.startsWith("# Name; ISIN; NSIN")) {
					l = din.readLine();
					if(debug)System.out.println(l);

					StringTokenizer str = new StringTokenizer(l, ";");
					String name = str.nextToken();
					String ret = str.nextToken();

					ret = ret.trim();

					return ret;
				}

				// l+= new String(l.getBytes("UTF-8"));
				l = din.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void main(String[] args) {
		ConsorsQuoteRetriever cqr = new ConsorsQuoteRetriever();
		Vector v = cqr.getHistory(cqr.search("846900"));
		Candle c0 = (Candle) v.elementAt(0);
		Candle cX = (Candle) v.elementAt(v.size() - 1);

		System.out.println("Candle 0: " + c0.toString());
		System.out.println("Candle X: " + cX.toString());
	}
}