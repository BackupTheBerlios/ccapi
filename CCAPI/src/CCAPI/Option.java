package CCAPI;

import CCAPI.helpers.Sortable;


/**
 * This is an Optionsschein class, as to be found in a list obtained through OSGetter
 * @author us
 *
 */
public class Option implements Sortable {
	
    public String basevalue = "";
    public String wkn = "";
    public String baseprice = "";
    public String emittent = "";
    public String validity = "";
    public String bv = "";
    public String type = "";

    /**
     * dumps out the option into the system.out.println and returns it as a string
     *
     */
    public String dump() {
        String ret = (wkn + "/" + type + "/" + basevalue + "/" +
            baseprice + "/" + validity + "/" + bv + "/" + emittent);
        System.out.println(ret);
        return ret; 
    }

    /**
     * can't remember what i needed this for ... 
     */
    public int compare(Object b) {
        double v1 = Double.parseDouble(baseprice);
        double v2 = Double.parseDouble(((Option) b).baseprice);

        if (v2 > v1) {
            return 1;
        }

        return -1;
    }
}
