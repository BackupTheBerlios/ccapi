package CCAPI;

import java.io.DataInputStream;

import java.net.URL;



/**
 *
 * retrieves a quote from comdirect
 *
 * @author us
 *
 *
 */
public class ComdirectQuoteRetriever {
    String retrdate = "";

    public ComdirectQuoteRetriever() {
    }

    public double[] retrieveData(String wkn) {
        double[] ret = { -1, -1 };

        System.gc();

        try {
            URL u = new URL(
                    "http://isht.comdirect.de/html/detail/ticklist/main.html?sSym=" +
                    wkn);

            // http://isht.comdirect.de/html/detail/ticklist/main.html?sSym=DE0009535245.C41
            DataInputStream din = new DataInputStream(u.openStream());
            String l = din.readLine();
            int line = 1;

            while (l != null) {
                l = l.trim();

                if (line == 91) {
                    // the line containing the date
                    System.out.println(l);

                    int i1 = l.indexOf("fTable") + 8;
                    int i2 = l.length() - 5;

                    try {
                        retrdate = l.substring(i1, i2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (line == 92) {
                    // the line containing the value
                    System.out.println(l);
                    l = l.substring(46, l.length() - 11);
                    System.out.println("l:" + l);

                    if (l.startsWith("G")) {
                        l = l.substring(1);
                    }

                    l = l.trim();

                    java.util.StringTokenizer str = new java.util.StringTokenizer(l,
                            ".");
                    String l1 = l;

                    while (str.countTokens() > 1) {
                        l1 = str.nextToken() + str.nextToken();
                    }

                    l1 = l1.replaceAll(",", ".");

                    double value = Double.parseDouble(l1);

                    ret[0] = value;
                } else if (line == 93) {
                    System.out.println(l);
                    l = l.substring(46, l.length() - 5);
                    System.out.println("l:" + l);
                    ret[1] = 0;
                }

                l = din.readLine();
                line++;
            }

            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
