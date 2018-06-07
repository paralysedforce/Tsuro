package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import main.Parser.ParserException;

/**
 * Created by vyasalwar on 4/27/18.
 */
public enum Color {
    GREEN("green"), RED("red"), ORANGE("orange"), BLUE("blue"), SIENNA("sienna"),
    HOTPINK("hotpink"), DARKGREEN("darkgreen"), PURPLE("purple");

    String str;

    Color(String s) {
        str = s;
    }

    @Override
    public String toString() {
        return str;
    }

    //================================================================================
    // XML Parsing
    //================================================================================

    public Element toXML(Document document) {
        Element tag = document.createElement("color");
        tag.appendChild(document.createTextNode(str));
        return tag;
    }


    public static Color fromXML(Element colorElement) {
        switch (colorElement.getTextContent()) {
            case "green":     return GREEN;
            case "red":       return RED;
            case "orange":    return ORANGE;
            case "blue":      return BLUE;
            case "sienna":    return SIENNA;
            case "hotpink":   return HOTPINK;
            case "darkgreen": return DARKGREEN;
            case "purple":    return PURPLE;
            default:
                throw new ParserException("Unknown color in element: " + colorElement.getTextContent());
        }
    }
}
