package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    public Element toXml(Document document) {
        Element tag = document.createElement("color");
        tag.appendChild(document.createTextNode(str));
        return tag;
    }

}
