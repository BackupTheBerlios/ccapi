package ulsjabberd.xml;

public class XMLTagParser {
    public static void main(String[] args) {
        String tag = "   <a key1='value1'      key2='value2'><b keyb='value3'>test<c  c=\"cval\"></c><longtog/><d>testd</d></b></a>   ";
        XMLTagParser x = new XMLTagParser();
        for (int i = 0; i < tag.length(); i++) {
            char c = (char)tag.charAt(i);
            x.addChar(c);
        }
    }

    
    XMLTagParser parent = null;
    XMLTagParser child = null;

    public XMLTagParser() {
    }

    public XMLTagParser(XMLTagParser x) {
        parent = x;
    }

    public void setChild(XMLTagParser x) {
        child = x;
    }

    public void setLevel(int i) {
        level = i;
    }

    int level = 0;
    public int state = 0;
    final int S0 = 0;
    final int S1 = 10;
    final int S2 = 20;
    final int S3 = 30;
    final int S4 = 40;
    final int S5 = 50;
    final int S6 = 60;
    final int S7 = 70;
    final int S8 = 80;
    final int S9 = 90;
    final int S10 = 100;
    final int S11 = 110;
    Element element = new Element();
    public String tagname = "";
    PHashtable attributes = new PHashtable();
    String attributename = "";
    String attributevalue = "";
    String cdata = "";
    String closetagname = "";

    public void addChar(char c1) {
        String c = "" + c1;
        if (c.equals("\\")) {
            if (state == S8) cdata += c;
        }
        else {
            // System.out.println("Level: "+level);
            if (child != null) {
                child.addChar(c1);
            }
            else {
                //System.out.println("Parsing:"+c1+"    in state:"+state);
                switch (state) {
                    case S0:
                        state0(c);
                        break;
                    case S1:
                        state1(c);
                        break;
                    case S2:
                        state2(c);
                        break;
                    case S3:
                        state3(c);
                        break;
                    case S4:
                        state4(c);
                        break;
                    case S5:
                        state5(c);
                        break;
                    case S6:
                        state6(c);
                        break;
                    case S7:
                        state7(c);
                        break;
                    case S8:
                        state8(c);
                        break;
                    case S9:
                        state9(c);
                        break;
                    case S10:
                        state10(c);
                        break;
                    case S11:
                        state11(c);
                        break;
                }
            }
            //	    System.out.println("Now in state:"+state);
        }
    }

    public void state0(String c) {
        if (c.equals("<")) state = S1;
    }

    public void state1(String c) {
        if (c.equals(">")) {
            tagStart();
            state = S8;
        }
        else if (c.equals("/")) {
            state = S10;
        }
        else {
            if (c.equals(" ")) {
                state = S2;
                attributename = "";
            }
            else {
                tagname += c;
            }
        }
    }

    public void state2(String c) {
        if (c.equals("?")) state = S1;
        if (c.equals("=")) {
            state = S3;
        }
        else {
            if (!(c.equals(" "))) attributename += c;
        }
    }

    public void state3(String c) {
        if (c.equals("\"") | c.equals("'")) {
            state = S4;
            attributevalue = "";
        }
    }

    public void state4(String c) {
        if (c.equals("?")) {
            state = S5;
        }
        else if (c.equals("\"") | c.equals("'")) {
            state = S5;
            attributes.put(attributename, attributevalue);
        }
        else {
            state = S4;
            attributevalue += c;
        }
    }

    public void state5(String c) {
        if (c.equals("?")) {
            state = S5;
        }
        else if (c.equals(">")) {
            state = S8;
            tagStart();
        }
        else if (c.equals(" ")) {
            state = S5;
        }
        else if (c.equals("/")) {
            state = S10;
        }
        else {
            state = S2;
            attributename = c;
        }
    }

    public void state6(String c) {
    }

    public void state7(String c) {
    }

    public void state8(String c) {
        if (c.equals("<")) {
        	// checking if there is only whitespace in this ...  
        	//cdata = this.cdata.trim();
            state = S9;
        }
        else {
            state = S8;
            //add to cdata text
            cdata += c;
        }
    }

    public void state9(String c) {
        if (c.equals("/")) {
            state = S10;
        }
        else {
            child = new XMLTagParser(this);
            child.taglisteners = taglisteners;
            child.setLevel(level + 1);
            //child.addChar((new String("<")).charAt(0));
            //child.addChar(c.charAt(0));
            child.state = 10;
            child.tagname += c;
            state = S8;
        }
    }

    public void state10(String c) {
        if (c.equals(">")) {
            state = S11;
            //tag finished
            element.name = tagname;
            element.text = cdata;
            element.attributes = attributes;
            if (parent != null) {
                parent.child = null;
                parent.element.addElement(element);
            }
            //System.out.print("\nEnd of tag detected->  ");
            //System.out.print(element.toString());
            tagStop();
        }
        else {
            state = S10;
        }
    }

    public void state11(String c) {
    	System.out.println("STATE 11 ????? ");
    }

    public void tagStart() {
        element.name = tagname;
        element.attributes = attributes;
        Element e = element;
        //System.out.println("\nTAG started:" + e.name + "/" + e.toString());
        for (int i = 0; i < taglisteners.size(); i++) {
            ((TagListener)taglisteners.elementAt(i)).tagStart(e);
        }
    }

    public void tagStop() {
        Element e = element;
        //System.out.println("\nTAG ended:" + e.name + "/" + e.toString());
        for (int i = 0; i < taglisteners.size(); i++) {
            //System.out.println("Notifying tag listener");
            ((TagListener)taglisteners.elementAt(i)).tagStop(e);
        }
        if (level == 0) {
            level = 0;
            state = 0;
            element = new Element();
            tagname = "";
            attributes = new PHashtable();
            attributename = "";
            attributevalue = "";
            cdata = "";
            closetagname = "";
        }
    }

    public void attach(TagListener t) {
        taglisteners.addElement(t);
    }

    public void detach(TagListener t) {
        taglisteners.removeElement(t);
    }

    /**
     * resets the node tree. 
     *
     */
    public void reset(){
    	child = null;
    	element = new Element();
        tagname = "";
        attributes = new PHashtable();
        attributename = "";
        attributevalue = "";
        cdata = "";
        closetagname = "";
    }
    public Vector taglisteners = new Vector();
    
    public void parse(String input){
    	for(int i=0;i<input.length();i++){
    		char c = input.charAt(i);
    		this.addChar(c);
    	}
    }
}
