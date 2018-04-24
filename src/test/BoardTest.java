package test;

import main.*;
import org.junit.Test;
import org.junit.Assert;

/**
 * Created by vyasalwar on 4/23/18.
 */
public class BoardTest {
    @Test
    public void placeTile() throws Exception {
        Board board = Board.getBoard();
        Assert.assertFalse(board.isOccupied(0, 0));

        Tile tile = new Tile(0, 2, 1, 3, 4, 5, 6, 7);
        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer("Vyas", space, 0);

        board.placeTile(tile, player);
        Assert.assertTrue(board.isOccupied(0, 0));
        Assert.assertFalse(player.getToken().getBoardSpace().hasTile());
        Assert.assertEquals(player.getToken().getTokenSpace(), 7);
    }

    @Test
    public void isLegalMove() throws Exception{
        Board board = Board.getBoard();
    }
}