package CCAPI;


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

    public void dump() {
        System.out.println(wkn + "/" + type + "/" + basevalue + "/" +
            baseprice + "/" + validity + "/" + bv + "/" + emittent);
    }

    public int compare(Object b) {
        double v1 = Double.parseDouble(baseprice);
        double v2 = Double.parseDouble(((Option) b).baseprice);

        if (v2 > v1) {
            return 1;
        }

        return -1;
    }
}
