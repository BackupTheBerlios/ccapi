/**
 *        GPL
 *        @author         ulrich staudinger, 2004
 *        @version        1.0
 */
package CCAPI;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.*;

import java.net.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ActiveTraderConnection {
    // debug flag
    boolean debug = true;

    /**
     *        holds the host name of the machine where AT2/3 is running
     */
    String host;

    /**
     *        holds the port to the AT3 machine
     */
    int port;

    /**
     *        holds the socket to AT
     */
    Socket socket;

    /**
     *        constructor
     */
    public ActiveTraderConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        ActiveTraderConnection atc = new ActiveTraderConnection("192.168.40.100",
                4242);

        atc.externalOrder("B", "750000", 100, "STU", 100.1, "S", "2004-09-10",
            null);
    }

    /**
     *        opens the connection to AT
     */
    public void open() {
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *        closes the connection to AT
     */
    public void close() {
        try {
            socket.close();
        } catch (Exception e) { // e.printStackTrace();
        }
    }

    /**
     *        buys
     */
    public void externalOrder(String type, String wkn, int amount,
        String exchange, double limit, String restriction, String validity,
        String clearingaccount) {
        try {
            // build the request
            String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

            // mandatory input
            request += "<request type=\"ExternalOrder\">";
            request += ("<ORDER_TYPE>" + type + "</ORDER_TYPE><SECURITY_WKN>" +
            wkn + "</SECURITY_WKN>");
            request += ("<SECURITY_NOMINAL_AMOUNT>" + amount +
            "</SECURITY_NOMINAL_AMOUNT>");

            // optional input
            if (exchange != null) {
                request += ("<STOCKEXCHANGE_ID>" + exchange +
                "</STOCKEXCHANGE_ID>");
            }

            if (limit != -1) {
                request += ("<LIMIT_QUOTATION>" + limit + "</LIMIT_QUOTATION>");
            }

            if (restriction != null) {
                request += ("<LIMIT_ADDEND>" + restriction + "</LIMIT_ADDEND>");
            }

            if (validity != null) {
                request += ("<VALIDITY_DATE>" + validity + "</VALIDITY_DATE>");
            }

            if (clearingaccount != null) {
                request += ("<CLEARING_ACCOUNT_NUMBER>" + clearingaccount +
                "</CLEARING_ACCOUNT_NUMBER>");
            }

            // mandatory input
            request += "</request>";
            request += "\n";

            // send the request
            Document reply = sendRequest(request);

            if (reply != null) {
                // and check for an error 
                org.w3c.dom.Element root = reply.getDocumentElement();

                if (root.getAttribute("errorcode").equals("0")) {
                    if (debug) {
                        System.out.println("Order accepted in AT");
                    }
                } else {
                    System.out.println("Error in order!");
                }
            } else {
                if (debug) {
                    System.out.println("Order accepted.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param request
     * @return
     */
    public Document sendRequest(String request) {
        open();

        if (debug) {
            System.out.println("REQ: " + request);
        }

        String ret = "";

        try {
            OutputStream os = socket.getOutputStream();

            os.write(request.getBytes());
            os.close();

            // check if a cookie was transmitted from server to here.if, then
            // set.
            // get the input stream, the stream from server to here.
            DataInputStream din = new DataInputStream(socket.getInputStream());

            // read all line wise and append to ret.
            String l = din.readLine();

            while (l != null) {
                if (debug) {
                    System.out.println(l);
                }

                ret += new String(l.getBytes("UTF-8"));
                l = din.readLine();
            }

            // everything read, close the socket
            socket.close();

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
}
