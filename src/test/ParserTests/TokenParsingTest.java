package test.ParserTests;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Parser.ParserUtils;
import main.Players.RandomPlayer;
import main.Token;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertEquals;
import static test.ParserTests.ParserTestUtils.*;

/**
 * Created by vyasalwar on 5/22/18.
 */
public class TokenParsingTest {
    @Test
    public void testTokenLocations() {
        try {
            Document doc = ParserUtils.newDocument();
            RandomPlayer bluePlayer = new RandomPlayer("Will", Color.BLUE);
            BoardSpace space = new BoardSpace(2, 2);

            // Top
            Token topToken = new Token(space, 0, bluePlayer);
            assertElementIsExpected(
                    topToken.toXML(doc),
                    String.format(testPawnTemplate, pawnLocTop));

            // Right
            Token rightToken = new Token(space, 2, bluePlayer);
            assertElementIsExpected(
                    rightToken.toXML(doc),
                    String.format(testPawnTemplate, pawnLocRight));

            // Bottom
            Token bottomToken = new Token(space, 5, bluePlayer);
            assertElementIsExpected(
                    bottomToken.toXML(doc),
                    String.format(testPawnTemplate, pawnLocBottom));

            // Left
            Token leftToken = new Token(space, 6, bluePlayer);
            assertElementIsExpected(
                    leftToken.toXML(doc),
                    String.format(testPawnTemplate, pawnLocLeft));

        } catch (ParserConfigurationException e){
            e.printStackTrace();
            Assert.fail();
        }
    }


    private static void assertLocationSame(Pair<BoardSpace, Integer> location, int row, int col, int tick) {
        assertEquals(location.getKey().getRow(), row);
        assertEquals(location.getKey().getCol(), col);
        assertEquals(location.getValue().intValue(), tick);
    }

    @Test
    public void testLocationToInternalRepresentation() {
        Board board = new Board();
        Pair<BoardSpace, Integer> result;

        // Top edge
        assertLocationSame(
                Token.locationFromPawnLoc(board, true, 0, 0),
                0, 0, 0);

        // Right edge
        assertLocationSame(
                Token.locationFromPawnLoc(board, false, 6, 0),
                0, 5, 2);

        // Bottom edge
        assertLocationSame(
                Token.locationFromPawnLoc(board, true, 6, 0),
                5,0,5);

        // Left edge,
        assertLocationSame(
                Token.locationFromPawnLoc(board, false, 0, 0),
                0,0,7);

        // TODO: internal tests
        // Internal with tile above
        // Internal with tile to the right
        // Internal with tile below
        // Internal with tile to the left

    }
}
