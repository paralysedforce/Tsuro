package test;

import main.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.Set;

/**
 * Created by vyasalwar on 4/23/18.
 */
public class BoardTest {


    @After
    public void resetBoard() {
        Board.resetBoard();
    }

    @Test
    public void placeTile() throws Exception {
        Board board = Board.getBoard();

        Tile tile = new Tile(0, 2, 1, 3, 4, 5, 6, 7);
        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer("Vyas", space, 0, TilePile.getTilePile());

        board.placeTile(tile, player.getToken());
        Assert.assertTrue(board.isOccupied(0, 0));
        Assert.assertFalse(player.getToken().getBoardSpace().hasTile());
        Assert.assertEquals(player.getToken().getTokenSpace(), 7);
    }

    @Test
    public void moveFromEdgeTest() throws Exception{
        /* Setup */
        Board board = Board.getBoard();

        BoardSpace upperSpace = board.getBoardSpace(0, 0);
        SPlayer vyas = new SPlayer("Vyas", upperSpace, 0);
        Tile vyasTile = new Tile(0, 4, 1, 5, 2, 3, 6, 7);


        BoardSpace lowerSpace = board.getBoardSpace(1, 0);
        SPlayer keith = new SPlayer("Keith", lowerSpace, 7);
        Tile keithTile = new Tile(7, 0, 1, 2, 3, 4, 5, 6);


        /* Goals:
            -Have Keith be forced off the board,
            -Have Vyas move two spaces at once

            Start       End
             V              K
            +----+      +----+
            |0,0 |      |0,0 |
            |    |      |    |
            +----+  ->  +----+
           K|1,0 |      |1,0 |V
            |    |      |    |
            +----+      +----+


           Keith's tile        Vyas's Tile
             (1, 0)              (0, 0)

              |  |                |  |
             -+  +-            -+  \/   +-
                                |  /\   |
             -+  +-            -+ /  \  +-
              |  |                |  |

         */

        Assert.assertFalse(board.isOccupied(0, 0));
        Assert.assertFalse(board.isOccupied(1, 0));
        Assert.assertFalse(upperSpace.hasTile());
        Assert.assertFalse(lowerSpace.hasTile());
        Assert.assertTrue(keithTile.isValid());
        Assert.assertTrue(vyasTile.isValid());


        Set<SPlayer> removedPlayers = board.placeTile(keithTile, keith);


        Assert.assertTrue(lowerSpace.hasTile());
        Assert.assertEquals(lowerSpace.getTokensOnSpace().size(), 0);
        Assert.assertEquals(upperSpace.getTokensOnSpace().size(), 2);
        Assert.assertEquals(upperSpace.findToken(vyas.getToken()), 0);
        Assert.assertEquals(upperSpace.findToken(keith.getToken()), 5);
        Assert.assertEquals(removedPlayers.size(), 0);


        removedPlayers = board.placeTile(vyasTile, vyas);


        Assert.assertTrue(lowerSpace.hasTile());
        Assert.assertTrue(upperSpace.hasTile());
        Assert.assertEquals(lowerSpace.getTokensOnSpace().size(), 0);
        Assert.assertEquals(upperSpace.getTokensOnSpace().size(), 0);
        Assert.assertEquals(vyas.getToken().getBoardSpace().getRow(), 1);
        Assert.assertEquals(vyas.getToken().getBoardSpace().getCol(), 1);
        Assert.assertEquals(vyas.getToken().getTokenSpace(), 7);
        Assert.assertEquals(removedPlayers.size(), 1);
        Assert.assertTrue(removedPlayers.contains(keith));
    }

    @Test
    public void RotateTest() throws Exception{
        /* Setup */
        Board board = Board.getBoard();
        BoardSpace start = board.getBoardSpace(0, 0);
        SPlayer keith = new SPlayer("Keith", start, 0);
        Tile tile = new Tile(0, 7, 2, 6, 1, 3, 4, 5);

        /* Goal:
             - If Keith places the tile as is, it will kill him.
             - If Keith rotates the tile before placing it, he will survive

             Original Tile              Rotated Tile
              |    |                     |   |
             -+  .-+----            -+    \  +--
              __/   \___             |     \
                                    -+   .--+---
               +----+                   /    \
               |    |                   |    |

         */

        Assert.assertFalse(start.hasTile());
        Assert.assertTrue(tile.isValid());
        Assert.assertFalse(board.isOccupied(0, 0));
        Assert.assertEquals(tile.findMatch(0), 7);
        Assert.assertTrue(board.willKillPlayer(tile, keith.getToken()));

        tile.rotateClockwise();

        Assert.assertEquals(tile.findMatch(0), 4);
        Assert.assertFalse(board.willKillPlayer(tile, keith.getToken()));

        Set<SPlayer> killed = board.placeTile(tile, keith);
        Assert.assertTrue(killed.isEmpty());
        Assert.assertEquals(keith.getToken().getBoardSpace().getRow(), 1);
        Assert.assertEquals(keith.getToken().getBoardSpace().getCol(), 0);
        Assert.assertEquals(keith.getToken().getTokenSpace(), 1);
    }

    @Test
    public void isLegalMove() throws Exception{
        Board board = Board.getBoard();
    }
}