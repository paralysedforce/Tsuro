package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public enum NetworkMessage {
    GET_NAME("get-name"), PLAYER_NAME("player-name"), INITIALIZE("initialize"),
    VOID("void"), PLACE_PAWN("place-pawn"), PAWN_LOC("pawn-loc"), PLAY_TURN("play-turn"),
    TILE("tile"), END_GAME("end-game");

    final String str;

    NetworkMessage(String s) {
        str = s;
    }

    public String getTag() {
        return str;
    }

    public Element getMessageRootElement(Document document) {
        return document.createElement(str);
    }

    /**
     * Transforms a dom element to string for printing, comparison, or sending over the wire.\
     * <p>
     * The method below was inspired to the answer to
     * <a href="https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java>this</a>
     * stackoverflow question.
     *
     * @param element org.w3c.dom.Element to be transformed into a string.
     * @return String representation of element.
     */
    public static String xmlElementToString(Element element) {
        // This should probably be moved to where it can be useful to more than tests.
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html"); // This prevents collapsing empty tags

            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(element);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException e) {
            // For testing purposes, we just return an empty string. This should be updated if this
            // code is moved to functional sections.
            e.printStackTrace();
            return "";
        }
    }


    public static Node nodeFromString(String xml) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new ByteArrayInputStream(xml.getBytes("UTF-8"))
        );
        return doc.getFirstChild();
    }
}
