package test;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import main.Board;
import main.NetworkMessage;
import main.Tile;
import main.Token;

/**
 * Created by William on 5/22/2018.
 */

public class BoardParsingTest {

    @Test
    public void testBoardFromXml() throws IOException, SAXException, ParserConfigurationException {
        String boardXmlInputStr =
                "<board>" +
                        "<map>" + // Tiles
                        "<ent>" +
                        "<xy>" +
                        "<x>" +
                        "2" +
                        "</x>" +
                        "<y>" +
                        "3" +
                        "</y>" +
                        "</xy>" +
                        "<tile>" +
                        "<connect>" +
                        "<n>" +
                        "0" +
                        "</n>" +
                        "<n>" +
                        "1" +
                        "</n>" +
                        "</connect>" +
                        "<connect>" +
                        "<n>" +
                        "2" +
                        "</n>" +
                        "<n>" +
                        "3" +
                        "</n>" +
                        "</connect>" +
                        "<connect>" +
                        "<n>" +
                        "4" +
                        "</n>" +
                        "<n>" +
                        "5" +
                        "</n>" +
                        "</connect>" +
                        "<connect>" +
                        "<n>" +
                        "6" +
                        "</n>" +
                        "<n>" +
                        "7" +
                        "</n>" +
                        "</connect>" +
                        "</tile>" +
                        "</ent>" +
                        "</map>" +

                        "<map>" + // pawns
                        "<ent>" +
                        "<color>" +
                        "blue" +
                        "</color>" +

                        "<pawn-loc>" +
                        "<h></h>" +
                        "<n>" +
                        "3" +
                        "</n>" +
                        "<n>" +
                        "5" +
                        "</n>" +
                        "</pawn-loc>" +
                        "</ent>" +
                        "</map>" +

                        "</board>";

        Element inputXmlElement = (Element) NetworkMessage.nodeFromString(boardXmlInputStr);

        Board board = new Board();
        board.fromXML(inputXmlElement);

        // Tile was placed
        Assert.assertEquals(
                board.getBoardSpace(3, 2).getTile(),
                new Tile(0, 1, 2, 3, 4, 5, 6, 7)
        );


        Iterator<Token> tokenIterator = board.getBoardSpace(2,2).getTokensOnSpace().iterator();
        Assert.assertTrue(tokenIterator.hasNext());

        Token token = tokenIterator.next();
        Assert.assertEquals(token.getBoardSpace(),
                board.getBoardSpace(2,2)
        );
        Assert.assertEquals(token.getTokenSpace(),
                4
        );
    }
}
