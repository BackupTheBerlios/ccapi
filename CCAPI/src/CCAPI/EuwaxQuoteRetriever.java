package CCAPI;

import java.io.DataInputStream;

import java.net.URL;


/**
 *        retrieves realtime informations from the stuttgarter euwax servers, currently only ask is retrieved.
 *
 */
public class EuwaxQuoteRetriever {
    public EuwaxQuoteRetriever() {
    }

    public static void main(String[] args){
		EuwaxQuoteRetriever e=new EuwaxQuoteRetriever();
		e.retreiveQuote("BNP17D");
    }
    
    public double[] retreiveQuote(String wkn) {
        System.gc();

        double[] value = new double[2];

        try {
            URL u = new URL(
			"http://www.rt.boerse-stuttgart.de/pages/search/main.html?searchterm="+
                    wkn);

            // http://isht.comdirect.de/html/detail/ticklist/main.html?sSym=DE0009535245.C41
            DataInputStream din = new DataInputStream(u.openStream());
            String l = din.readLine();
            int line = 1;

            while (l != null) {
		//System.out.println("line: "+line+" : "+ l);
		l = l.trim();

                if (line == 112) {
                    // the line containing the everyfuck
                    int i1 = l.indexOf("Geld:");
                    int i2 = l.indexOf("Quotierungszeitpunkt");

                    l = l.substring(i1+5, l.length() - i2);

                    // crop the Bid : 
                    l = l.substring(6);
			
                    int i3 = l.indexOf("</td>");
                    String bid = l.substring(0, i3);

                    //System.out.println("Extracted <" + bid + ">");

                    int i4 = l.indexOf("Brief:");

                    //System.out.println("i4: " + i4);
                    l = l.substring(i4 + 6 + 6, l.length() - i4 - 6);
                    //System.out.println("l <" + l + ">");

                    int i5 = l.indexOf("</td>");
                    String ask = l.substring(0, i5);

                    // replace all commas
                    ask = ask.replaceAll(",", ".");
                    bid = bid.replaceAll(",", ".");

                    value[1] = Double.parseDouble(ask);
		    value[0] = Double.parseDouble(bid);
                }

                l = din.readLine();
                line++;
            }

            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
}
