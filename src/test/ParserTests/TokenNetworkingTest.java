package test.ParserTests;

import org.junit.Assert;
import org.junit.Test;

import javafx.util.Pair;
import main.BoardSpace;
import main.Token;

/**
 * Created by William on 5/22/2018.
 */

public class TokenNetworkingTest {

    @Test
    public void pawnLocFromLocationTest() {
        BoardSpace space = new BoardSpace(3, 5);
        Pair<BoardSpace, Integer> location = new Pair<>(space, 3);

        Assert.assertEquals(
                Token.pawnLocFromLocation(location),
                "<pawn-loc>" +
                        "<v></v>" +
                        "<n>" +
                        "6" +
                        "</n>" +
                        "<n>" +
                        "7" +
                        "</n>" +
                        "</pawn-loc>"
        );
    }
}
