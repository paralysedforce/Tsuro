package main.Parser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by vyasalwar on 5/16/18.
 */
public interface Parsable {
    /**
     * Creates an XML string representing the object.
     * @param document Document to be used in creating elements.
     * @return an Element representing the DOM node for this element.
     */
    public Element toXML(Document document);

    /**
     * Updates the host object fields to reflect the data in the xmlElement.
     * @param xmlElement Element of the DOM node representing what this object should be.
     */
    public void fromXML(Element xmlElement);
}
