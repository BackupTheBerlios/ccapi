package CCAPI;


/**
 *
 * a quote object - contains a quote.
 *  @author us
 *
 */
public class Quote {
    /**
     * the security id, is the ISIN or WKN.
     */
    public String security = "";

    /**
     * the full name for this security
     */
    public String security_name = "";

    /**
     * the short code for the exchange, for example ETR, STR, OTC
     */
    public String exchange = "";

    /**
     * the fully qualified name for the exchange, f.e. XETRA, Stuttgart or ausserb?rslich
     */
    public String exchange_name = "";

    /**
     * contains the bid value
     */
    public double bid;

    /**
     * contains the ask value
     */
    public double ask;

    /**
     * contains the bid size, if available
     */
    public double bidvolume;

    /**
     * contains the ask volume, if available
     */
    public double askvolume;

    /**
     * contains the current quotation, the '*kurs*'
     */
    public double quotation;

    /**
     * just a very flat constructor.
     *
     */
    public Quote() {
    }
}
