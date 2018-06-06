package test.ParserTests;

import main.BoardSpace;
import main.Parser.ParserUtils;
import main.Tile;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static test.ParserTests.ParserTestUtils.*;

/**
 * Created by vyasalwar on 5/22/18.
 */
public class TileParsingTest {


    @Test
    public void testTileToXml() {
        try {
            Document doc = ParserUtils.newDocument();
            Element element = testTile.toXML(doc);
            ParserUtils.nodesAreEquivalent(ParserUtils.nodeFromString(testTileXml),
                    element);
            assertElementIsExpected(element, testTileXml);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTileFromXml() throws IOException, SAXException, ParserConfigurationException {
        Node node = ParserUtils.nodeFromString(testTileXml);
        Tile actual = new Tile();
        actual.fromXML((Element) node);
        Assert.assertEquals(testTile, actual);
    }

    /* ****************************** TESTING TILES ON BOARD ********************************** */



    @Test
    public void testBoardSpaceToXml() {

        Document doc = ParserUtils.newDocument();
        testBoardSpace.setTile(testTile);
        Element element = testBoardSpace.toXML(doc);
        assertElementIsExpected(element, testSpaceXml);
    }

    @Test
    public void testSpaceFromXml() throws IOException, SAXException, ParserConfigurationException {
        BoardSpace actual = new BoardSpace(1, 2);
        actual.setTile(testTile);
        Node node = ParserUtils.nodeFromString(testSpaceXml);
        actual.fromXML((Element) node);
        assertTrue(actual.equals(testBoardSpace));
    }

}
