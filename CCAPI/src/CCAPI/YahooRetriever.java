package CCAPI;

import java.io.DataInputStream;
import java.io.FileInputStream;

import java.net.URL;

import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 *
 * retrieves historical data from yahoo
 *
 * @author us
 *
 */
public class YahooRetriever {
    // http://table.finance.yahoo.com/table.csv?s=^GDAXI&a=10&b=26&c=1990&d=03&e=19&f=2004&g=d&ignore=.csv
    public YahooRetriever() {
    }

    public String getDate(String symbol, int omit) {
        Vector v = new Vector();
        Date d = new Date();

        // System.out.println(""+d.getDate());
        int day = d.getDate();
        int month = d.getMonth();

        System.out.println("Fetching data");

        try {
            URL url = new URL("http://table.finance.yahoo.com/table.csv?s=" +
                    symbol + "&a=01&b=01&c=2003&d=" + month + "&e=" + day +
                    "&f=" + (1900 + d.getYear()) + "&g=d&ignore=.csv");

            // URL url=new URL("http://table.finance.yahoo.com/table.csv?s=^GDAXI&a=10&b=26&c=1990&d=03&e=19&f=2004&g=d&ignore=.csv");
            DataInputStream din = new DataInputStream(url.openStream());
            String l = din.readLine();

            l = din.readLine();

            boolean b1 = true;

            while (l != null) {
                if (omit == 0) {
                    // System.out.println(l);
                    StringTokenizer str = new StringTokenizer(l, ",");
                    String date = str.nextToken();

                    return date;
                } else {
                    omit--;
                }

                l = din.readLine();
            }
        } catch (Exception e) { // e.printStackTrace();
        }

        return "";
    }

    public Vector getCandles(String symbol, int omit) {
        Vector v = new Vector();

        try {
            java.io.File f = new java.io.File(symbol + ".csv");
            java.io.DataOutputStream dout = new java.io.DataOutputStream(new java.io.FileOutputStream(
                        f));

            Date d = new Date();

            // System.out.println(""+d.getDate());
            int day = d.getDate();
            int month = d.getMonth();

            System.out.println("Fetching data");

            try {
                URL url = new URL("http://table.finance.yahoo.com/table.csv?s=" +
                        symbol + "&a=01&b=01&c=2003&d=" + month + "&e=" + day +
                        "&f=" + (1900 + d.getYear()) + "&g=d&ignore=.csv");

                DataInputStream din = new DataInputStream(url.openStream());
                String l = din.readLine();

                l = din.readLine();

                boolean b1 = true;

                while (l != null) {
                    if (omit == 0) {
                        dout.write((l + "\n").getBytes());

                        // System.out.println(l);
                        StringTokenizer str = new StringTokenizer(l, ",");
                        String date = str.nextToken();

                        if (b1) {
                            System.out.println(date);
                        }

                        if (b1) {
                            b1 = false;
                        }

                        Candle c = new Candle();

                        c.open = Double.parseDouble(str.nextToken());
                        c.hi = Double.parseDouble(str.nextToken());
                        c.low = Double.parseDouble(str.nextToken());
                        c.close = Double.parseDouble(str.nextToken());
                        c.volume = Integer.parseInt(str.nextToken());
                        v.addElement(c);
                    } else {
                        omit--;
                    }

                    l = din.readLine();
                }
            } catch (Exception e) { // e.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // invert candles
        Vector v1 = new Vector();

        /* for(int i=0;i<v.size();i++){
         v1.addElement(v.elementAt(v.size()-i-1));
         }
         */
        System.out.println("Fetched " + v.size() + " candles.");

        return v;
    }

    public Candle getCurrentQuote(String symbol) {
        Candle c = new Candle();

        c.isin = symbol;

        try {
            URL url = new URL("http://finance.yahoo.com/d/quotes.csv?s=" +
                    symbol + "&f=sl1d1t1c1ohgv&e=.csv");
            DataInputStream din = new DataInputStream(url.openStream());

            String l = din.readLine();

            // System.out.println(l);
            StringTokenizer str = new StringTokenizer(l, ",");

            str.nextToken();
            c.close = Double.parseDouble(str.nextToken());
            str.nextToken();
            str.nextToken();
            str.nextToken();

            c.open = Double.parseDouble(str.nextToken());
            c.hi = Double.parseDouble(str.nextToken());
            c.low = Double.parseDouble(str.nextToken());
            c.volume = Integer.parseInt(str.nextToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    /**
     * load the history of a symbol
     * @param candles
     */
    public Vector loadHistory(String symbol) {
        Vector v = new Vector();
        int n = 0;

        try {
            java.io.File f = new java.io.File(symbol + ".csv");
            java.io.DataInputStream din = new java.io.DataInputStream(new FileInputStream(
                        f));

            String l = din.readLine();

            while (l != null) {
                // parse the line
                try {
                    // StringTokenizer str=new StringTokenizer(l, ",");
                    StringTokenizer str = new StringTokenizer(l, ";");
                    String date = str.nextToken();

                    Candle c = new Candle();

                    // temp
                    String s1 = str.nextToken();

                    s1 = str.nextToken();
                    c.close = Double.parseDouble(str.nextToken());

                    /* c.open=Double.parseDouble(str.nextToken());
                     c.hi=Double.parseDouble(str.nextToken());
                     c.low=Double.parseDouble(str.nextToken());
                     c.close=Double.parseDouble(str.nextToken());
                     c.volume=Integer.parseInt(str.nextToken());
                     */
                    v.addElement(c);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                l = din.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public Candle getDayQuote(String symbol) {
        try {
            URL url = new URL("http://finance.yahoo.com/d/quotes.csv?s=" +
                    symbol + "&f=sl1d1t1c1ohgv&e=.csv");
            DataInputStream din = new DataInputStream(url.openStream());
            String l = din.readLine();
            StringTokenizer str = new StringTokenizer(l, ";");

            Candle c = new Candle();

            c.isin = str.nextToken();

            c.close = Double.parseDouble(str.nextToken());
            str.nextToken();
            str.nextToken();
            c.open = Double.parseDouble(str.nextToken());
            c.hi = Double.parseDouble(str.nextToken());
            c.close = Double.parseDouble(str.nextToken());
            c.volume = Integer.parseInt(str.nextToken());

            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
