package CCAPI;

import java.io.*;

import java.util.Date;


/**
 * A candle. the financial library works only with candles, for most calculations inside the financial library, the close value is used.
 *
 * @author us
 *
 */
public class Candle implements Serializable {
    private static final long serialVersionUID = 1234567890L;
    public java.util.HashMap indicators = new java.util.HashMap();
    public double hi = 0;
    public double low = 10000000;
    public double open = -1;
    public double close = -1;
    public int volume = 0;
    public String isin = "";
    public String fullname = "";
    public String datestring = "";
    public String wkn = "";
    public Date date = null;

    public Candle() {
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

    public String toString() {
        if (date != null) {
            return date.toGMTString() + "/" + open + "/" + hi + "/" + low +
            "/" + close + "/" + volume;
        } else {
            return datestring+"/"+fullname + "/" + isin + "/" + datestring + "/" + open + "/" +
            hi + "/" + low + "/" + close + "/" + volume;
        }
    }

    public String stringShort() {
        int k1 = (int) (((close - open) / open) * 10000);

        String l1 = "" + open;
        String l2 = "" + hi;
        String l3 = "" + low;
        String l4 = "" + close;

        String ret = "";

        ret += isin;

        for (int i = 0; i < (10 - isin.length()); i++) {
            ret += " ";
        }

        ret += l1;

        for (int i = 0; i < (10 - l1.length()); i++) {
            ret += " ";
        }

        ret += l2;

        for (int i = 0; i < (10 - l2.length()); i++) {
            ret += " ";
        }

        ret += l3;

        for (int i = 0; i < (10 - l3.length()); i++) {
            ret += " ";
        }

        ret += l4;

        for (int i = 0; i < (10 - l4.length()); i++) {
            ret += " ";
        }

        return ret + ((double) k1 / 100) + "%";
    }

    public double getHi() {
        return hi;
    }

    public double getClose() {
        return close;
    }

    public double getLow() {
        return low;
    }

    public double getOpen() {
        return open;
    }

    private void readObject(ObjectInputStream aInputStream)
        throws ClassNotFoundException, IOException {
        // read object (and rebuild) from aInputStream
        aInputStream.defaultReadObject();
    }

    /**
     * This is the default implementation of writeObject.
     * Customise if necessary.
     */
    private void writeObject(ObjectOutputStream aOutputStream)
        throws IOException {
        // perform the default serialization for all non-transient, non-static fields
        aOutputStream.defaultWriteObject();
    }
}
