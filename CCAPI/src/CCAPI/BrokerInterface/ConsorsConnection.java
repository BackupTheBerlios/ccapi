package CCAPI.BrokerInterface;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import CCAPI.Quote;


/*
 * Created on Feb 1, 2004
 *
 *
 *
 * guter hebel das ding.
 *
 *
 *
 */
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.net.URL;

import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 *
 *  Connects to consors server - direct!
 *
 * Consors uses a plain https request model to trade. (Which is good and
 * secure.) Thus, the client sends requests and gets an immediate response!
 * Session TANs are for security reasons, not supported!
 *
 *
 *  @author us
 *
 *
 */
public class ConsorsConnection extends Thread {
    /**
     * VIV - Contains the account number [KONTONUMMER]
     */
    String p1 = "970xxxxxx"; // kontonummer

    /**
     * VIV - Contains the Pin [PIN]
     */
    String p2 = "xyzyx"; // pin

    /**
     * start of ccapi code
     *
     */
    DataOutputStream log;

    /**
     * specifies whether to print debug messages or not.
     */
    boolean debug = true;

    /**
     * a global variable, true upon positive login, false upon negative.
     */
    public boolean loggedin = false;

    /**
     * a helper - ignore.
     */
    boolean running = true;

    /**
     * VIV - an array of TANs, may hold up to 50 TANs.
     */
    String[] tan = new String[50];

    /**
     * the base url
     */
    URL url = null;
    String sessioncookie = null;

    /**
     * default constructor
     *
     */
    public ConsorsConnection() {
        initializeLogger();

        // because this class extends Thread, we can simply call start() to call the Thread's run() loop
        if (loggedin) {
            start();
        }
    }

    /**
     * initializes the consors connection - remember: this is just for using this class directly.
     *
     *
     * @author us
     *
     */
    public static void main(String[] args) {
        ConsorsConnection c = new ConsorsConnection();
    }

    /**
     * initializes the connection.
     *
     */
    public void initializeConnection() {
        try {
            url = new URL(
                    "https://brokerage-a3.cortalconsors.de/activetrader3/");

            CCAPITrustManager tm = new CCAPITrustManager();
            X509TrustManager[] tms = { tm };
            SSLContext ctx = SSLContext.getInstance("SSL");

            ctx.init(null, tms, new SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

            // go here !
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loggs in
     *
     * @param accountno
     *                      cointains the account number.
     * @param pin
     *                      contains the pin.
     */
    public void login(String accountno, String pin) {
        // <ATRADER_VERSION>2.8.5</ATRADER_VERSION>
        p1 = accountno;
        p2 = pin;

        initializeConnection();

        String text =
            "<?xml version='1.0'?><request servicename='ComplexLogin' language='DE'>\n" +
            " <COMPANY>GE</COMPANY>\n<ACCOUNTNO>" + accountno +
            "</ACCOUNTNO>\n<PIN>" + pin +
            "</PIN>\n<ATRADER_VERSION>3.2.5</ATRADER_VERSION>" // here we hand over , as assigned by consors, the string CCAPI
             +" <OS_NAME>AT</OS_NAME></request>"; // here we send, as assigned by consors, the string JAVA

        Document response = this.sendRequest(text);

        if (debug) {
            System.out.println("RESP:" + response.toString());
        }

        org.w3c.dom.Element root = response.getDocumentElement();
        String rescode = root.getAttribute("errorcode");

        if (rescode.equals("0")) {
            // successfull login, we set the global boolean loggedin to true 
            loggedin = true;
            start();
            System.out.println("Successfull login");
        } else {
            // not successfull login, we set the global boolean loggedin to false	
            loggedin = false;
        }
    }

    /**
     * buy an isin
     * @param isin
     * @param amount
     * @param exchange
     * @return
     */
    public boolean buy(String isin, int amount, String exchange) {
        return false;
    }

    /**
     * sell an isin
     * @param isin
     * @param amount
     * @param exchange
     * @return
     */
    public boolean sell(String isin, int amount, String exchange) {
        return false;
    }

    /**
     * retrieves the current quote of an isin via OTC.
     * @param isin
     * @return
     */
    public Quote getOTCQuote(String isin) {
        Quote q1 = null;

        if (marketOpen()) {
            String request =
                "<?xml version='1.0'?><request servicename='MWGetQuote' language='DE'>" +
                "<ORDERTYPE>B</ORDERTYPE><SECURITY_WKN>" + isin +
                "</SECURITY_WKN>" +
                "<SECURITY_NOMINAL_AMOUNT>1000.0</SECURITY_NOMINAL_AMOUNT>" +
                "<STOCKEXCHANGE_ID>OTC</STOCKEXCHANGE_ID>" + "</request>";

            Document reply = sendRequest(request);

            // now we parse the reply document.
            org.w3c.dom.Element root = reply.getDocumentElement();

            if (root.getAttribute("errorcode").equals("0")) {
                q1 = new Quote();

                // ok, reply contains correct quote
                if (debug) {
                    System.out.println("" + root.getFirstChild().getClass());
                }

                if (debug) {
                    System.out.println("" +
                        root.getFirstChild().getNextSibling().getClass());
                }

                org.w3c.dom.Element tag = (org.w3c.dom.Element) root.getFirstChild();

                while (tag != null) {
                    if (debug) {
                        System.out.println("parsing tag:" + tag.getNodeName());
                    }

                    if (tag.getNodeName().equals("MAX_BUY_AMOUNT")) {
                        q1.bidvolume = Double.parseDouble(tag.getFirstChild()
                                                             .getNodeValue());
                    } else if (tag.getNodeName().equals("MAX_SELL_AMOUNT")) {
                        q1.askvolume = Double.parseDouble(tag.getFirstChild()
                                                             .getNodeValue());
                    } else if (tag.getNodeName().equals("BUY_QUOTATION")) {
                        q1.ask = Double.parseDouble(tag.getFirstChild()
                                                       .getNodeValue());
                    } else if (tag.getNodeName().equals("SELL_QUOTATION")) {
                        q1.bid = Double.parseDouble(tag.getFirstChild()
                                                       .getNodeValue());
                    } // else

                    // if(tag.getNodeName().equals("QUOTE_DATE"))q1.bidvolume=Double.parseDouble(tag.getFirstChild().getNodeValue());
                    // else
                    // if(tag.getNodeName().equals("QUOTE_TIME"))q1.bidvolume=Double.parseDouble(tag.getFirstChild().getNodeValue());
                    else if (tag.getNodeName().equals("SECURITY_NAME")) {
                        q1.security_name = (tag.getFirstChild().getNodeValue());
                        System.out.println("security name:" + q1.security_name);
                    } else if (tag.getNodeName().equals("STOCKEXCHANGE_NAME")) {
                        q1.exchange_name = (tag.getFirstChild().getNodeValue());
                    }

                    tag = (org.w3c.dom.Element) tag.getNextSibling();

                    // if(tag!=null)tag=(org.w3c.dom.Element)tag.getNextSibling();
                }
            } else {
                this.initializeConnection();
            }
        }

        return q1;
    }

    /**
     * gets an exchange quotation from a public exchange (not OTC).
     * @param isin
     * @param exchange
     * @return
     */
    public Quote getPublicExchangeQuote(String isin, String exchange) {
        Quote q1 = null;

        if (marketOpen()) {
            String request = "<?xml version='1.0'?>" +
                "<request servicename='GetActQuote' language='DE'>" +
                "<REALTIME_QUOTE>1</REALTIME_QUOTE>" +
                "<ROWSET name='QUOTES'><ROW num='1'>" + "<SECURITY_WKN>" +
                isin + "</SECURITY_WKN>" + "<STOCKEXCHANGE_ID>" + exchange +
                "</STOCKEXCHANGE_ID>" + "</ROW></ROWSET>" + "</request>";

            Document reply = sendRequest(request);

            // now we parse the reply document.
            org.w3c.dom.Element root = reply.getDocumentElement();

            if (root.getAttribute("errorcode").equals("0")) {
                // ok, reply contains correct quote
                if (debug) {
                    System.out.println("" + root.getFirstChild().getClass());
                }

                if (debug) {
                    System.out.println("" +
                        root.getFirstChild().getNextSibling().getClass());
                }

                org.w3c.dom.Element rowset = (org.w3c.dom.Element) root.getFirstChild()
                                                                       .getNextSibling();

                NodeList rowlist = rowset.getElementsByTagName("ROW");

                for (int i = 0; i < rowlist.getLength(); i++) {
                    org.w3c.dom.Element row = (org.w3c.dom.Element) rowlist.item(i);

                    if (row.getNodeName().equals("ROW")) {
                        // ok. row found.
                        q1 = new Quote();

                        org.w3c.dom.Element e1 = (org.w3c.dom.Element) row.getFirstChild()
                                                                          .getNextSibling();

                        while (e1 != null) {
                            if (e1.getNodeName().equals("BID")) {
                                q1.bid = Double.parseDouble(e1.getFirstChild()
                                                              .getNodeValue());
                            } else if (e1.getNodeName().equals("ASK")) {
                                q1.ask = Double.parseDouble(e1.getFirstChild()
                                                              .getNodeValue());
                            } else if (e1.getNodeName().equals("ASK_VOLUME")) {
                                q1.askvolume = Double.parseDouble(e1.getFirstChild()
                                                                    .getNodeValue());
                            } else if (e1.getNodeName().equals("BID_VOLUME")) {
                                q1.bidvolume = Double.parseDouble(e1.getFirstChild()
                                                                    .getNodeValue());
                            } else if (e1.getNodeName().equals("STOCKEXCHANGE_ID")) {
                                q1.exchange = (e1.getFirstChild().getNodeValue());
                            } else if (e1.getNodeName().equals("STOCKEXCHANGE_NAME")) {
                                q1.exchange_name = (e1.getFirstChild()
                                                      .getNodeValue());
                            } else if (e1.getNodeName().equals("SECURITY_NAME")) {
                                q1.security_name = (e1.getFirstChild()
                                                      .getNodeValue());
                                System.out.println("Security name:" +
                                    q1.security_name);
                            } else if (e1.getNodeName().equals("SECURITY_WKN")) {
                                q1.security = (e1.getFirstChild().getNodeValue());
                            } else if (e1.getNodeName().equals("QUOTATION")) {
                                q1.quotation = Double.parseDouble(e1.getFirstChild()
                                                                    .getNodeValue());
                            }

                            e1 = (org.w3c.dom.Element) e1.getNextSibling()
                                                         .getNextSibling();
                        }
                    }
                }
            } else {
                this.initializeConnection();
            }
        }

        return q1;
    }

    // -------------- routines below getting more general.

    /**
     * sends a request to the server and returns a document. Everything must get
     * wrapped into a request-response envelope.
     *
     * @param request
     * @return
     */
    public Document sendRequest(String request) {
        if (debug) {
            System.out.println("REQ: " + request);
        }

        saveToLog("--- REQ:    " + (new java.util.Date()).toGMTString() + "\n" +
            request);

        String ret = "";

        try {
            // crete the connection
            HttpsURLConnection hpcon = (HttpsURLConnection) url.openConnection();

            // set all the stuff for the hpcon.
            // do not forget to set the content length
            hpcon.setRequestProperty("Content-Length",
                "" + Integer.toString(request.getBytes().length));
            hpcon.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            hpcon.setDoOutput(true);

            // if we are in a session, we set the cookie.
            if (sessioncookie != null) {
                hpcon.setRequestProperty("Cookie", sessioncookie);
            }

            // writing the data to the http connection
            OutputStream os = hpcon.getOutputStream();

            os.write(request.getBytes());
            os.close();

            // check if a cookie was transmitted from server to here.if, then
            // set.
            String s1 = hpcon.getHeaderField("Set-Cookie");

            if (debug) {
                System.out.println("Cookie: " + s1);
            }

            if (s1 != null) {
                sessioncookie = s1;
            }

            // get the input stream, the stream from server to here.
            DataInputStream din = new DataInputStream(hpcon.getInputStream());

            // read all line wise and append to ret.
            String l = din.readLine();

            while (l != null) {
                if (debug) {
                    System.out.println(l);
                }

                ret += new String(l.getBytes("UTF-8"));
                l = din.readLine();
            }

            // pipe the return into the logger.
            saveToLog("--- RES:     " + (new java.util.Date()).toGMTString() +
                "\n" + ret);

            // build the documentbuilder. cause it's an abstract class ...
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docbuilder = dbf.newDocumentBuilder();

            // small workaround to parse the string ret through the document
            // builder
            ByteArrayInputStream bais = new ByteArrayInputStream(ret.getBytes());

            return docbuilder.parse(bais);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * checks if german markets are open, useful whenever this program is run on a server not in germany! (i.e. us east coast, etc)
     */
    public boolean marketOpen() {
        // date check
        java.util.Date d1 = new java.util.Date();

        // adjust the timezone
        int tz = d1.getTimezoneOffset() / 60;

        d1.setHours(d1.getHours() + 1 + tz);

        if ((d1.getHours() > 7) & (d1.getHours() < 23) & (d1.getDay() > 0) &
                (d1.getDay() < 6)) {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (running) {
            try {
                sleep(90000);

                // do something to keep the session alive, i.e. getDaxQuote();

                /* System.out.println(
                 "***CCAPI-Session keep alive: 846900: "
                 + (new java.util.Date().toGMTString())
                 + this.getPublicExchangeQuote("846900", "ETR").quotation);*/

                // System.out.println("821025:
                // "+this.getOTCQuote("821025").bid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // -------------- logger routines below

    /**
     * initializes the logger.
     *
     */
    public void initializeLogger() {
        try {
            String sessionstart = (new java.util.Date().toGMTString()).replaceAll(" ",
                    "_");
            File f = new File(sessionstart + ".log");

            log = new DataOutputStream(new FileOutputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * saves 'what' to the log file!
     *
     * @param what
     */
    public void saveToLog(String what) {
        try {
            log.write((what + "\n").getBytes());
            log.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
