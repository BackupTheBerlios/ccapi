package ulsjabberd.xml;
import java.util.Hashtable;

public class Element implements TagListener{

	public PHashtable attributes;
	public Vector elements;

	public String text="";

	public String name="";
	public boolean closed=false;
	public Element parent=null;
	
	/**
	 * required for storage ID stuff. 
	 */
	public long storageID = -1;

	Element(){
		attributes=new PHashtable();
		elements=new Vector();
	}

        public boolean isEmpty(){
	    if(elements.isEmpty())return true;
	    return false;
	}

	Element(String name){
		this.name=name;
		attributes=new PHashtable();
		elements=new Vector();
	}

	public void addAttr(String name, String value){
		attributes.put(name, value);
	}

	public String getAttr(String name){
		return (String)attributes.get(name);
	}

	public void setText(String t){
		text=t;
	}

	public String getText(){
		return text;
	}

	public void addElement(Element e){
		elements.addElement(e);
	}

	public Element getElement(String n){
		for(int i=0;i<elements.size();i++){
			Element e=(Element)elements.elementAt(i);
			if(e.name.equals(n))return e;
		}
		return null;
	}

	public Vector elements(){
		return elements;
	}

	public String toString(){
		String str="";

		str+="<"+name;
		for(int i=0;i<attributes.size();i++){
			String k=(String)attributes.getKey(i);
			String v=(String)attributes.get(k);
			str+=" "+k+"='"+v+"'";
		}

		str+=">"+text;

		for(int i=0;i<elements.size();i++){
			Element e=(Element)elements.elementAt(i);
			str+=e.toString();
		}
		str+="</"+name+">";

		return str;
	}

	
	
	public void buildFromString(String input){
		XMLTagParser xtp = new XMLTagParser();
		xtp.attach(this);
		xtp.parse(input);
	}
	
	public void tagStart(Element e){
		
	}
	
	public void tagStop(Element e){
		this.attributes=e.attributes;
		this.elements=e.elements();
		this.name=e.name;
		this.parent = e.parent;
		this.closed = e.closed;
		this.text=e.text;
		this.storageID = e.storageID;
	}
	
}
