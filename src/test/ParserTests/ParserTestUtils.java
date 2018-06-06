package test.ParserTests;

import org.junit.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import main.BoardSpace;
import main.Parser.ParserUtils;
import main.Tile;

/**
 * Created by vyasalwar on 5/22/18.
 */
class ParserTestUtils {
    /**
     * Asserts that the element matches the parsed Dom version of the expected text.
     *
     * @param element  Element to be tested.
     * @param expected String xml representation of what was expected.
     */
    static void assertElementIsExpected(Element element, String expected) {
        try {
            Node expectedNode = ParserUtils.nodeFromString(expected);
            expectedNode.normalize();
            element.normalize();

            if (ParserUtils.debug) {
                System.out.println(ParserUtils.xmlElementToString(element));
                System.out.println(ParserUtils.xmlElementToString((Element) expectedNode));
            }
            boolean eq = ParserUtils.nodesAreEquivalent(expectedNode, element);
            Assert.assertTrue(eq); // Provides useful debugging info.
            // assertTrue(element.isEqualNode(expectedNode)); // Should also be true, but doesn't rely on untested code.
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /* Objects used for testing */
    static final Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
    static final BoardSpace testBoardSpace = new BoardSpace(1, 2);



    /* XML strings used for testing */
    static final String testTileXml =
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

    static final String testSpaceXml =
            "<ent>" +
                "<xy>" +
                    "<x>" +
                        "2" +
                    "</x>" +
                    "<y>" +
                        "1" +
                    "</y>" +
                "</xy>" +
                testTileXml +
            "</ent>";

    static final String pawnLocTop = "<pawn-loc><h></h><n>2</n><n>4</n></pawn-loc>";   // Corresponds to (2,2,0)
    static final String pawnLocRight = "<pawn-loc><v></v><n>3</n><n>4</n></pawn-loc>"; // Corresponds to (2,2,2)
    static final String pawnLocBottom = "<pawn-loc><h></h><n>3</n><n>4</n></pawn-loc>";// Corresponds to (2,2,5)
    static final String pawnLocLeft = "<pawn-loc><v></v><n>2</n><n>5</n></pawn-loc>";  // Corresponds to (2,2,6)
    static final String testPawnTemplate = "<ent><color>blue</color>%s</ent>"; // To be used with String.format
}
