package CCAPI;


import java.io.*;
import java.net.*;
import java.util.*;


/**
 *	OSGetter gets a list of Optionsscheine from the stuttgarter euwax servers.  check the source to see which wkns are supported. Add your own wkn<->euwax mappings and mail them to me. Can be used as standalone application. 
 * @author us
 *  
 */
public class OSGetter {

    String baseIsin = "";
    Vector certificates = new Vector();

    boolean debug = false;
	
    HashMap mappings = new HashMap();

    public OSGetter(String isin) {
		
        mappings.put("846900", "dax");
        mappings.put("750000", "thyssenkrupp");
        mappings.put("703712", "rwe");
        mappings.put("519000", "bmw");
        mappings.put("515100", "basf");
        mappings.put("725750", "metro");
        mappings.put("761440", "eon");
        mappings.put("710000", "daimlerchrysler");
        mappings.put("555750", "deutschetelekom");
        mappings.put("578580", "fresenius");
        mappings.put("723610", "siemens");
        mappings.put("823212", "lufthansa");
        mappings.put("760080", "altana");
        mappings.put("750000", "thyssenkrupp");
        mappings.put("802200", "hypovereinsbank");
        mappings.put("593700", "man");
        mappings.put("543900", "continental");
        mappings.put("766400", "vwst");
        mappings.put("717200", "schering");
        mappings.put("575200", "bayer");
        mappings.put("604843", "henkelvz");
        mappings.put("840400", "allianz");
        mappings.put("500340", "adidassalomon");
        mappings.put("695200", "tui");
        mappings.put("803200", "commerzbank");
        mappings.put("514000", "deutschebank");
        mappings.put("555200", "deutschepost");
        mappings.put("648300", "linde");
        mappings.put("581005", "deutscheboerse");
        mappings.put("716460", "sap");
        mappings.put("843002", "muenchenerrueck");
        mappings.put("623100", "infineon");
		
        // get("750000");

        // dump();


    }

    Vector options = new Vector();
    boolean dataFound = true;
    private void get(String wkn) {
        baseIsin = (String) mappings.get(wkn);
        String preurl = "http://www.euwax.de/finder/mta_finder/fnd_build_mta.php?wp_keyword=&fnd_typ=alle&region_basiswert=alle&basiswert=";
        String posturl = "&optionsart=alle&basispreis_ab=&basispreis_bis=&lauf_ab=&lauf_bis=&fndr_sel=os&sort_me=&asc_desc=&nln_min=&nln_max=&special_sel=&back_url=%2Fprod_optionsscheine%2Foptionsscheine.htm&back_target=_self&go.x=21&go.y=8&cutoffhere=&sort_me=&asc_desc=&ll=1&mm=28&page=";

        int firstindex = 0;
        int lastindex = 0;
        int total = 1;

        boolean inTableArea = false;
        boolean inTable = false;

        try {
            while (dataFound) {
                ++firstindex;
                if (!baseIsin.equals("")) {
                    if (debug) {
                        System.out.println("Getting from index " + firstindex);
                    }

                    URL url = new URL(preurl + baseIsin + posturl + firstindex);

                    if (debug) {
                        System.out.println(url);
                    }
                    DataInputStream din = new DataInputStream(url.openStream());
				
                    String table = "";
				
                    String l = din.readLine();

                    while (l != null) {

                        if (inTableArea) {
                            if (l.indexOf("<table border=1") != -1) {
                                inTable = true;					
                            }
						
                            if (inTable) {
                                l = l.replaceAll("<br>", "\n");
                                l = l.replaceAll("</tr>", "</tr>\n");
                                table += l;
							
                            }
						
                            if (l.indexOf("</table>") != -1) {
                                inTableArea = false;
                                inTable = false;
                            }
                        }
                        if (l.indexOf("<! ##### Ende Seitenkopf ###### !>")
                                != -1) {
                            inTableArea = true;
                        }
                        l = din.readLine();
                        // System.out.println(l);

                    }
                    din.close();

                    if (debug) {
                        System.out.println(table);
                    }
				
                    parseTable(table);
				
                }
            }
        } catch (Exception e) {
            if (debug) {
                e.printStackTrace();
            }
        }

    }
	
    private void parseTable(String table) {
        ByteArrayInputStream bin = new ByteArrayInputStream(table.getBytes());
        DataInputStream din = new DataInputStream(bin);

        dataFound = false;
        try {
            String l = din.readLine();

            while (l != null) {
				
                // search for the line start
                if (l.startsWith("<tr><td class=\"inhalt0")) {
                    // parse a line
                    l = l.replaceAll("<td class=\"inhalt0\"", ";");
                    StringTokenizer str = new StringTokenizer(l, ";");

                    str.nextToken();
						
                    Option o = new Option();					

                    String wkn = str.nextToken();

                    wkn = extractWKN(wkn);
                    if (debug) {
                        System.out.println("WKN:" + wkn);
                    }
                    o.wkn = wkn;
				
                    String art = str.nextToken();

                    art = extractArt(art);
                    if (debug) {
                        System.out.println("Art:" + art);
                    }
                    o.type = art;					
                    String bwert = str.nextToken();

                    bwert = extractBWert(bwert);
                    if (debug) {
                        System.out.println("BWert:" + bwert);
                    }
                    o.basevalue = bwert;
                    String basispreis = str.nextToken();

                    basispreis = extractBasispreis(basispreis);
                    if (debug) {
                        System.out.println("Base: " + basispreis);
                    }
                    o.baseprice = basispreis;
                    String faelligkeit = str.nextToken();

                    faelligkeit = extractFaelligkeit(faelligkeit);
                    if (debug) {
                        System.out.println("Faelligkeit:" + faelligkeit);
                    }
                    o.validity = faelligkeit;
                    String bv = str.nextToken();

                    bv = extractBV(bv);
                    if (debug) {
                        System.out.println("BV:" + bv);
                    }
                    o.bv = bv;
                    String emittent = str.nextToken();

                    emittent = extractEmi(emittent);
                    if (debug) {
                        System.out.println("Emi:" + emittent);
                    }
                    o.emittent = emittent;
                    options.addElement(o);
                    dataFound = true;		
                }
				
                l = din.readLine();
            }
		
        } catch (Exception e) {
            if (debug) {
                e.printStackTrace();
            }
        }
		
    }

    private String extractEmi(String f) {
        f = f.substring(0, f.length() - 10);
        f = f.substring(f.indexOf("\">") + 2);
        return f;
    }
	
    private String extractBV(String f) {
        f = f.substring(0, f.length() - 5);
        f = f.substring(f.indexOf("\">") + 2);
        f = f.replaceAll(",", ".");
		
        return f;
    }

    private String extractFaelligkeit(String f) {
        f = f.substring(0, f.length() - 5);
        f = f.substring(f.indexOf("\">") + 2);
        return f;
    }
	
    private String extractBasispreis(String bpreis) {
        bpreis = bpreis.substring(0, bpreis.length() - 5);
        bpreis = bpreis.substring(bpreis.indexOf("\">") + 2);
        // bpreis=bpreis.replaceAll(".", "");
        String strike = "";
        StringTokenizer str = new StringTokenizer(bpreis, ".");

        while (str.hasMoreTokens()) {
            strike += str.nextToken();
        }
        bpreis = strike.replaceAll(",", ".");
        return bpreis;
    }
	
    private String extractBWert(String bwert) {
        bwert = bwert.substring(0, bwert.length() - 5);
        bwert = bwert.substring(bwert.indexOf("\">") + 2);
        return bwert;
    }
	
    private String extractWKN(String wkn) {
        wkn = wkn.substring(0, wkn.length() - 9);
        wkn = wkn.substring(wkn.indexOf("\">") + 2);
        wkn = wkn.substring(wkn.indexOf("\">") + 2);
        // System.out.println(wkn);
		
        return wkn;
    }
	
    private String extractArt(String art) {
        art = art.substring(0, art.length() - 5);
        art = art.substring(art.indexOf("\">") + 2);
        return art;
    }

    public Vector getOptions(String wkn) {
        options = new Vector();
        get(wkn);
        return options;
    }

    public Vector getOptions(String wkn, double min, double max) {
        options = new Vector();
        get(wkn);
        Vector ret = new Vector();

        for (int i = 0; i < options.size(); i++) {
            Option o = (Option) options.elementAt(i);
            double v1 = Double.parseDouble(o.baseprice);

            if (v1 >= min && v1 <= max) {
                ret.addElement(o);
            }
        }
        options = ret;
        return ret;
    }
	
    public void sort() {
        Vector sorted = new Vector();
		
    }
	
    public void dump() {
        QuickSort.sort(options);
        for (int i = 0; i < options.size(); i++) {
            Option o = (Option) options.elementAt(i);

            o.dump();
        }
    }
	
    public static void printUsage() {
        System.out.println("OSGetter (c) Ulrich Staudinger");
        System.out.println("Usage: ");		
        System.out.println("java OSGetter <wkn> <basepricemin> <basepricemax>");
    }
	
    public static void main(String[] args) {
		
        if (!(args.length > 0)) {
            printUsage();
            System.exit(0);
        } else {
			
            OSGetter os = new OSGetter("");

            // os.get(args[0]);
            if (args.length == 1) {
                os.getOptions(args[0]);
            } else {
                os.getOptions(args[0], Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]));
            }
            os.dump();
			
        }
    }
	
}

