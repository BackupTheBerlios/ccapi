package CCAPI;


import java.io.DataInputStream;
import java.net.URL;


/**
 *	retrieves realtime informations from the stuttgarter euwax servers, currently only ask is retrieved. 
 *
 */
public class EuwaxQuoteRetriever {

    EuwaxQuoteRetriever() {}
	
    public double retrieveAsk(String wkn) {
        System.gc();
        double value = 0.0;

        try {
            URL u = new URL(
                    "http://www.rt.boerse-stuttgart.de/pages/details/main.html?action=overview&sSymbol="
                            + wkn);
            // http://isht.comdirect.de/html/detail/ticklist/main.html?sSym=DE0009535245.C41
            DataInputStream din = new DataInputStream(u.openStream());
            String l = din.readLine();
            int line = 1;

            while (l != null) {
                l = l.trim();
                if (line == 84) {
                    // the line containing the everyfuck
                    int i1 = l.indexOf("Bid : ");
                    int i2 = l.indexOf("Quotierungszeitpunkt");

                    l = l.substring(i1, l.length() - i2);
					
                    // crop the Bid : 
                    l = l.substring(6);
					
                    int i3 = l.indexOf("</td>");
                    String bid = l.substring(0, i3);

                    System.out.println("Extracted <" + bid + ">");
					
                    int i4 = l.indexOf("Ask");

                    System.out.println("i4: " + i4);
                    l = l.substring(i4 + 6, l.length() - i4 - 6);
                    System.out.println("l <" + l + ">");
					
                    int i5 = l.indexOf("</td>");
                    String ask = l.substring(0, i5);
					
                    // replace all commas
                    ask = ask.replaceAll(",", ".");
                    bid = bid.replaceAll(",", ".");
					
                    value = Double.parseDouble(ask);
					
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
