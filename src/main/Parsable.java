package main;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by vyasalwar on 5/16/18.
 */
public interface Parsable {
    public Element toXML(Document document);
    public void fromXML(Element xmlElement);
}
