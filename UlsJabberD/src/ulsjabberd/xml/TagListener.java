package ulsjabberd.xml;

/*
 * 
 * @author us
 *
 * a very plain tag listener interface. 
 */
public interface TagListener{
    public void tagStart(Element e);
    public void tagStop(Element e);
}
