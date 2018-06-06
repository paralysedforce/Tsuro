package main.Parser;

import main.*;
import main.Players.APlayer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by vyasalwar on 5/22/18.
 */
public class ParserUtils {

    public static boolean debug = false;

    public static void setDebug(boolean debugVal){
        debug = debugVal;
    }

    public static Document newDocument() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Transforms a dom element to string for printing, comparison, or sending over the wire.\
     * <p>
     * The method below was inspired by the answer to
     * <a href="https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java>this</a>
     * stackoverflow question.
     *
     * @param element org.w3c.dom.Element to be transformed into a string.
     * @return String representation of element.
     */
    public static String xmlElementToString(Element element) throws TransformerException {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "html"); // This prevents collapsing empty tags

        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(element);
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    private static final String[] UnorderedTags = {"set", "tile"};

    /**
     * Checks that two nodes have the same name and the same children. Does not check attributes.
     *
     * @param node1 Node for comparison
     * @param node2 Node for comparison
     * @return true if the nodes are functionally equivalent (i.e. if they are leaf nodes, their
     * texts are equivalent or if they are internal nodes, their children are equivalent) and thier
     * tags are equivalent.
     */
    public static boolean nodesAreEquivalent(Node node1, Node node2)  {

        // Tag equivalence
        if (!node1.getNodeName().equals(node2.getNodeName())) {
            if (debug)
                System.err.println(String.format("Node names are not equivalent: %s differs from %s",
                        node1.getNodeName(), node2.getNodeName()));
            return false;
        }

        // Found a leaf. Compare contents
        if (node1.getNodeType() == Node.TEXT_NODE) {
            if (node1.getTextContent().equals(node2.getTextContent()))
                return true;
            else {
                if (debug)
                    System.err.println(String.format("Contents of text nodes not the same: %s differs from %s",
                            node1.getTextContent(), node2.getTextContent()));
                return false;
            }
        }

        // Internal node, compare children
        NodeList children1 = node1.getChildNodes();
        NodeList children2 = node2.getChildNodes();
        if (children1.getLength() != children2.getLength()) {
            if (debug)
                System.err.println(String.format("Children lengths are not equivalent for %s: %d vs. %d",
                        node1.getNodeName(), children1.getLength(), children2.getLength()));
            return false;
        }

        String name = node1.getNodeName();
        if (name.equals("set") || name.equals("tile") || name.equals("connect"))
            return compareUnorderedChildren(node1, node2);
        else
            return compareOrderedChildren(node1, node2);
    }


    public static Node nodeFromString(String xml) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new ByteArrayInputStream(xml.getBytes("UTF-8"))
        );
        return doc.getFirstChild();
    }

    public static List<Tile> tileListFromNode(Node node){

        if (!node.getTextContent().equals("list")){
            throw new IllegalArgumentException();
        }

        List<Tile> parsedTileList = new ArrayList<Tile>();
        for (Node child = node.getFirstChild();
             child != null;
             child = child.getNextSibling()){

            // Parse the child
            Tile parsedChild = new Tile();
            parsedChild.fromXML((Element) child);
            parsedTileList.add(parsedChild);
        }

        return parsedTileList;
    }

    public static Element convertTileListToElement(Document document, List<Tile> tiles){
        Element parent = document.createElement("list");

        for (Tile tile: tiles){
            parent.appendChild(tile.toXML(document));
        }

        return parent;
    }


    //================================================================================
    // Private Helpers
    //================================================================================
    private static boolean compareOrderedChildren(Node node1, Node node2){
        for (Node child1 = node1.getFirstChild(), child2 = node2.getFirstChild();
             child1 != null || child2 != null;
             child1 = child1.getNextSibling(), child2 = child2.getNextSibling()){

            if (!nodesAreEquivalent(child1, child2)){
                if (debug)
                    System.err.println(String.format("Children do not match." +
                                    "\n  child1: %s" +
                                    "\n  child2: %s",
                            child1.getNodeName(), child2.getNodeName()));
                return false;
            }
        }
        return true;
    }

    private static boolean compareUnorderedChildren(Node node1, Node node2) {
        for (Node child1 = node1.getFirstChild(); child1 != null; child1 = child1.getNextSibling()){
            boolean found = false;
            for (Node child2 = node2.getFirstChild(); child2 != null; child2 = child2.getNextSibling()) {
                if (nodesAreEquivalent(child1, child2)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                if (debug)
                    System.err.println(String.format("Child could not be found in node2's children. \n" +
                                    "\tchild1: %s", child1.getNodeName()));
                return false;
            }
        }

        // All nodes have been found
        return true;
    }


    public static Color findDragonTilePlayerColor(Element remainingPlayersElement) {
        for (Node child = remainingPlayersElement.getFirstChild();
                child != null;
                child = child.getNextSibling()){
            if (child.getTextContent().equals("splayer-dragon")){
                return APlayer.fromXML((Element) child).getColor();
            }
        }

        return null;
    }

    public static String APlayerListToString(List<APlayer> aPlayerList, APlayer dragonTileOwner) {
        try {

            Document document = newDocument();
            Element listElement = document.createElement("list");
            for (APlayer player: aPlayerList){

                Element childElement;
                childElement = APlayer.toXML(document, player, player == dragonTileOwner);
                listElement.appendChild(childElement);
            }

            return xmlElementToString(listElement);

        } catch (TransformerException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public static List<APlayer> APlayerListFromNode(Element aPlayerListElement) {
        if (aPlayerListElement.getTextContent().equals("list"))
            throw new IllegalArgumentException();

        List<APlayer> players = new ArrayList<>();
        for (Node child = aPlayerListElement.getFirstChild();
             child != null;
             child = child.getNextSibling()){

            players.add(APlayer.fromXML((Element) child));
        }

        return players;
    }
}
