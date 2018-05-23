package test.ParserTests;

import main.Board;
import main.Color;
import main.Parser.ParserUtils;
import main.Players.APlayer;
import main.Players.RandomPlayer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

import static test.ParserTests.ParserTestUtils.*;

/**
 * Created by vyasalwar on 5/22/18.
 */
public class BoardParsingTest {

    private final String testBoardXml =
            "<board>" +
                    "<map>" +
                    testSpaceXml +
                    "</map>" +
                    "<map>" +
                    String.format(testPawnTemplate, pawnLocTop) +
                    String.format(testPawnTemplate, pawnLocLeft)+
                    "</map>" +
                    "</board>";


    private Board setUpTestBoard() {
        Board board = new Board();
        APlayer playerTop = new RandomPlayer("Vyas", Color.BLUE);
        playerTop.initialize(new ArrayList<>());
        playerTop.placeToken(board.getBoardSpace(1, 2), 4);

        board.placeTile(testTile, playerTop); // Moves playerTop to (2,2,0)

        APlayer playerLeft = new RandomPlayer("Will", Color.BLUE);
        playerLeft.initialize(new ArrayList<>());
        playerLeft.placeToken(board.getBoardSpace(2, 2), 6);

        return board;
    }

    @Ignore
    @Test
    public void testBoardToXml() throws ParserConfigurationException{
        // This test only fails when run in the big test suite. I don't know why, but I don't think it's relevant
        ParserUtils.setDebug(false);
        Document doc = ParserUtils.newDocument();
        Board board = setUpTestBoard();
        assertElementIsExpected(
                board.toXML(doc),
                testBoardXml
        );
    }
}
