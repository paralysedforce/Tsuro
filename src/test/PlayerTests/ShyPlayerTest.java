package test.PlayerTests;

import main.Board;
import main.Color;
import main.Players.ShyPlayer;
import main.Tile;
import main.Token;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyasalwar on 6/15/18.
 */
public class ShyPlayerTest {

    @Test
    public void TileHeuristicTwoTokenTest() {
        // This tile will move blue closer to red
        Tile closerTile = new Tile(0, 5, 1, 4, 2, 7, 3, 6);

        // This tile will move blue further from red
        Tile furtherTile = new Tile(0, 2, 1, 3, 4, 6, 5, 7);

        Board board = new Board();

        Token blueToken = new Token(board.getBoardSpace(0, 0), 0, Color.BLUE);
        Token redToken = new Token(board.getBoardSpace(4, 0), 6, Color.RED);

        List<Color> allColors = new ArrayList<Color>();
        allColors.add(Color.BLUE);
        allColors.add(Color.RED);


        int closerScore = ShyPlayer.ShyHeuristic(closerTile, board, Color.BLUE, allColors);
        int furtherScore = ShyPlayer.ShyHeuristic(furtherTile, board, Color.BLUE, allColors);

        Assert.assertTrue(closerScore < furtherScore);
        Assert.assertEquals(closerScore, 3);
        Assert.assertEquals(furtherScore, 5);
    }

    @Test
    public void TileHeuristicThreeTokenTest() {
        // This tile will move blue closer to red
        Tile closerTile = new Tile(0, 5, 1, 4, 2, 7, 3, 6);

        // This tile will move blue further from red
        Tile furtherTile = new Tile(0, 2, 1, 3, 4, 6, 5, 7);

        Board board = new Board();

        Token blueToken = new Token(board.getBoardSpace(0, 0), 0, Color.BLUE);
        Token redToken = new Token(board.getBoardSpace(4, 0), 6, Color.RED);

        // Now add another token further away and see that it makes no difference
        Token greenToken = new Token(board.getBoardSpace(5, 5), 4, Color.GREEN);


        List<Color> allColors = new ArrayList<Color>();
        allColors.add(Color.BLUE);
        allColors.add(Color.RED);
        allColors.add(Color.GREEN);


        int closerScore = ShyPlayer.ShyHeuristic(closerTile, board, Color.BLUE, allColors);
        int furtherScore = ShyPlayer.ShyHeuristic(furtherTile, board, Color.BLUE, allColors);

        Assert.assertTrue(closerScore < furtherScore);
        Assert.assertEquals(closerScore, 3);
        Assert.assertEquals(furtherScore, 5);
    }
}
