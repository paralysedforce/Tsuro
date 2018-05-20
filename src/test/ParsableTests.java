package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Players.APlayer;
import main.Players.MockPlayer;
import main.Players.RandomPlayer;
import main.Tile;
import main.Token;

import static org.junit.Assert.assertTrue;

/**
 * Created by William on 5/16/2018.
 * <p>
 * This class tests XML representations of the Parsable objects in our project.
 */

public class ParsableTests {

    @Before
    public void setUp() throws Exception {

        this.setupTestSpace();

        this.setUpTestBoard();
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
    private String xmlElementToString(Element element) {
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

    /**
     * Document generator for the purpose of these tests.
     * <p>
     * The test will fail if the document cannot be generated.
     *
     * @return Document for use in tests.
     */
    @SuppressWarnings("ConstantConditions")
    private Document setUpDocument() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Assert.assertTrue(false); // When in doubt, fail the enclosing test.
            return null; // To make compiler happy.
        }
    }

    /**
     * Checks that two nodes have the same name and the same children. Does not check attriutes.
     *
     * @param node1 Node for comparison
     * @param node2 Node for comparison
     * @return true if the nodes are functionally equivalent (i.e. if they are leaf nodes, their
     * texts are equivalent or if they are internal nodes, their children are equivalent) and thier
     * tags are equivalent.
     */
    private boolean nodesAreEquivalent(Node node1, Node node2, boolean debug) {
        // Tag equivalence
        if (!Objects.equals(node1.getNodeName(), node2.getNodeName())) {
            if (debug)
                System.err.println("\nNode names are not equivalent: " +
                        node1.getNodeName() + " differs from " + node2.getNodeName());
            return false;
        }

        // Found a leaf. Compare contents
        if (node1.getNodeType() == Node.TEXT_NODE) {
            if (node1.getTextContent().equals(node2.getTextContent()))
                return true;
            else {
                if (debug)
                    System.err.println("Contents of text nodes not the same: " +
                            node1.getTextContent() + " differs from " + node2.getTextContent());
                return false;
            }
        }

        // Internal node, compare children
        // Create arraylists out of the nodelists
        NodeList children1 = node1.getChildNodes();
        NodeList children2 = node2.getChildNodes();
        if (children1.getLength() != children2.getLength()) {
            if (debug)
                System.err.println("Children lengths are not equivalent for " + node1.getNodeName() +
                        ": " + Integer.toString(children1.getLength()) + " vs " + Integer.toString(children2.getLength()));
            return false;
        }
        for (int i = 0; i < children1.getLength(); i++) {
            Node child = children1.item(i);
            boolean childWasFound = false;
            for (int j = 0; j < children2.getLength(); j++) {
                // Look for child in children2
                if (nodesAreEquivalent(child, children2.item(j), false)) {
                    // Found child in children2
                    childWasFound = true;
                    break;
                }
            }
            if (!childWasFound) {
                if (debug)
                    System.err.println("Child was not found: " + xmlElementToString((Element) child));
                return false;
            }
        }
        // Length the same and all children in list 1 found in list 2
        return true;
    }

    private Node nodeFromString(String xml) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new ByteArrayInputStream(xml.getBytes("UTF-8"))
        );
        return doc.getFirstChild();
    }

    // A couple of quick tests to show that the above testing code works
    @Test
    public void compareXmlSame() throws IOException, SAXException, ParserConfigurationException {
        Assert.assertTrue(nodesAreEquivalent(
                nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                true
        ));
    }
    @Test
    public void compareXmlChildrenDifferentOrder() throws IOException, SAXException, ParserConfigurationException {
        Assert.assertTrue(nodesAreEquivalent(
                nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                nodeFromString("<div><span2></span2><span1></span1></div>").getParentNode(),
                true
        ));
    }
    @Test
    public void compareXmlChildMissing() throws IOException, SAXException, ParserConfigurationException {
        Assert.assertFalse(nodesAreEquivalent(
                nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                nodeFromString("<div><span1></span1></div>").getParentNode(),
                false
        ));
        Assert.assertFalse(nodesAreEquivalent(
                nodeFromString("<div><span1></span1></div>").getParentNode(),
                nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                false
        ));
    }
    @Test
    public void compareXmlChildText() throws IOException, SAXException, ParserConfigurationException {
        Assert.assertFalse(nodesAreEquivalent(
                nodeFromString("<div><span1>text</span1><span2></span2></div>").getParentNode(),
                nodeFromString("<div><span1></span1><span2></span2></div>").getParentNode(),
                false
        ));
        Assert.assertTrue(nodesAreEquivalent(
                nodeFromString("<div><span1>text</span1><span2></span2></div>").getParentNode(),
                nodeFromString("<div><span1>text</span1><span2></span2></div>").getParentNode(),
                true
        ));
    }

    /**
     * Asserts that the element matches the parsed Dom version of the expected text.
     *
     * @param element  Element to be tested.
     * @param expected String xml representation of what was expected.
     */
    @SuppressWarnings("ConstantConditions")
    private void assertElementIsExpected(Element element, String expected, boolean debug) {
        try {
            Node expectedNode = nodeFromString(expected);
            expectedNode.normalize();
            element.normalize();
            if (debug) {
                System.out.println(xmlElementToString(element));
                System.out.println(xmlElementToString((Element) expectedNode));
            }
            Assert.assertTrue(nodesAreEquivalent(expectedNode, element, debug)); // Provides useful debugging info.
            //Note: The line below was commented out because it takes order of children into account.
//            assertTrue(element.isEqualNode(expectedNode)); // Should also be true, but doesn't rely on untested code.
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false); // When in doubt, fail the enclosing test.
        }
    }


    /* ****************************** TESTING TILES *********************************** */
    // Helpful objects
    private final Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
    private final String testTileXml =
            "<tile>" +
                    "<connect>" +
                    "<n>0</n>" +
                    "<n>1</n>" +
                    "</connect>" +
                    "<connect>" +
                    "<n>2</n>" +
                    "<n>3</n>" +
                    "</connect>" +
                    "<connect>" +
                    "<n>4</n>" +
                    "<n>5</n>" +
                    "</connect>" +
                    "<connect>" +
                    "<n>6</n>" +
                    "<n>7</n>" +
                    "</connect>" +
                    "</tile>";

    @Test
    public void testTileToXml() {
        Document doc = setUpDocument();
        Element element = testTile.toXML(doc);
        assertElementIsExpected(element, testTileXml, false);
    }

    @Test
    public void testTileFromXml() throws IOException, SAXException, ParserConfigurationException {
        Node node = nodeFromString(testTileXml);
        Tile actual = new Tile();
        actual.fromXML((Element) node);
        Assert.assertEquals(testTile, actual);
    }

    /* ****************************** TESTING TILES ON BOARD ********************************** */
    private BoardSpace testSpace = new BoardSpace(1, 2);
    private final String testSpaceXml =
            "<ent>" +
                    "<xy>" +
                    "<x>" +
                    "<n>2</n>" +
                    "</x>" +
                    "<y>" +
                    "<n>1</n>" +
                    "</y>" +
                    "</xy>" +
                    testTileXml +
                    "</ent>";

    public void setupTestSpace() {
        testSpace.setTile(testTile);
    }

    @Test
    public void testBoardSpaceToXml() {
        Document doc = setUpDocument();
        Element element = testSpace.toXML(doc);
        assertElementIsExpected(element, testSpaceXml, false);
    }

    @Test
    public void testSpaceFromXml() throws IOException, SAXException, ParserConfigurationException {
        BoardSpace actual = new BoardSpace(1, 2);
        actual.setTile(testTile);
        Node node = nodeFromString(testSpaceXml);
        actual.fromXML((Element) node);
        assertTrue(actual.equals(testSpace));
    }

    /* ****************************** TESTING PAWNS **************************************** */
    private final String pawnLocTop = "<pawn-loc><h></h><n>2</n><n>4</n></pawn-loc>";   // Corresponds to (2,2,0)
    private final String pawnLocRight = "<pawn-loc><v></v><n>3</n><n>4</n></pawn-loc>"; // Corresponds to (2,2,2)
    private final String pawnLocBottom = "<pawn-loc><h></h><n>3</n><n>4</n></pawn-loc>";// Corresponds to (2,2,5)
    private final String pawnLocLeft = "<pawn-loc><v></v><n>2</n><n>5</n></pawn-loc>";  // Corresponds to (2,2,6)
    private final String testPawnStart =
            "<ent>" +
                    "<color>blue</color>";
    private final String testPawnEnd = "</ent>";

    @Test
    public void testPawnLocations() {
        Document doc = setUpDocument();
        RandomPlayer bluePlayer = new RandomPlayer("", Color.BLUE);
        BoardSpace space = new BoardSpace(2,2);
        boolean debug = false;
        // Top
        Token topToken = new Token(space, 0, bluePlayer);
        assertElementIsExpected(
                topToken.toXML(doc),
                testPawnStart + pawnLocTop + testPawnEnd,
                debug
        );
        // Right
        Token rightToken = new Token(space, 2, bluePlayer);
        assertElementIsExpected(
                rightToken.toXML(doc),
                testPawnStart + pawnLocRight + testPawnEnd,
                debug
        );
        // Bottom
        Token bottomToken = new Token(space, 5, bluePlayer);
        assertElementIsExpected(
                bottomToken.toXML(doc),
                testPawnStart + pawnLocBottom + testPawnEnd,
                debug
        );
        // Left
        Token leftToken = new Token(space, 6, bluePlayer);
        assertElementIsExpected(
                leftToken.toXML(doc),
                testPawnStart + pawnLocLeft + testPawnEnd,
                debug
        );
    }

    /* ****************************** TESTING BOARD **************************************** */
    private final String testBoardXml =
            "<board>" +
                    "<map>" +
                    testSpaceXml +
                    "</map>" +
                    "<map>" +
                    testPawnStart +
                    pawnLocTop +
                    testPawnEnd +
                    testPawnStart +
                    pawnLocLeft +
                    testPawnEnd +
                    "</map>" +
                    "</board>";
    private Board testBoard;
    private void setUpTestBoard() {
        testBoard = new Board();
        APlayer playerTop = new MockPlayer("", Color.BLUE) {
            @Override
            public Pair<BoardSpace, Integer> mockStartingLocation(Board board) {
                return new Pair<>(testBoard.getBoardSpace(1,2), 4);
            }

            @Override
            public Tile mockChooseTile(Board board, int remainingTiles) {
                return null;
            }
        };
        playerTop.initialize(new ArrayList<>());
        playerTop.placeToken();
        testBoard.placeTile(testTile, playerTop); // Moves playerTop to (2,2,0)

        APlayer playerLeft = new MockPlayer("", Color.BLUE) {
            @Override
            public Pair<BoardSpace, Integer> mockStartingLocation(Board board) {
                return new Pair<>(testBoard.getBoardSpace(2,2), 6);
            }

            @Override
            public Tile mockChooseTile(Board board, int remainingTiles) {
                return null;
            }
        };
        playerLeft.initialize(new ArrayList<>());
        playerLeft.placeToken();
    }

    @Test
    public void testBoardToXml() {
        Document doc = setUpDocument();
        assertElementIsExpected(
                testBoard.toXML(doc),
                testBoardXml,
                false
        );
    }



}
