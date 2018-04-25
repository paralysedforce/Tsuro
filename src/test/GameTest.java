package test;

import main.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameTest {

    @Mock
    TilePile tilePileMock;

    @Before
    public void reset() {
        Board.resetBoard();
        Game.resetGame();
    }


    @Test
    public void isLegalMoveIsTrueWithLegalMove(){
        Board board = Board.getBoard();

        Tile testTile = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer("Vyas", space, 0, tilePileMock);
        List<SPlayer> remainingPlayers = new ArrayList<SPlayer>();
        remainingPlayers.add(player);

        Game game = Game.getGame(remainingPlayers, new ArrayList<SPlayer>(), tilePileMock);
        Assert.assertTrue(game.isLegalMove(testTile, player));
    }

    @Test
    public void isLegalMoveIsTrueWithNoMoves() {
        Board board = Board.getBoard();

        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer("Vyas", space, 0, tilePileMock);
        List<SPlayer> remainingPlayers = new ArrayList<SPlayer>();
        remainingPlayers.add(player);

        Game game = Game.getGame(remainingPlayers, new ArrayList<SPlayer>(), tilePileMock);
        Assert.assertTrue(game.isLegalMove(testTile, player));
    }

    @Test
    public void isLegalMoveFalseWithOtherMove() {
        Board board = Board.getBoard();

        Tile testTileCantMove = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile testTileCanMove = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTileCantMove)
                .thenReturn(testTileCanMove)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer("Vyas", space, 0, tilePileMock);
        List<SPlayer> remainingPlayers = new ArrayList<SPlayer>();
        remainingPlayers.add(player);

        Game game = Game.getGame(remainingPlayers, new ArrayList<SPlayer>(), tilePileMock);
        Assert.assertTrue(game.isLegalMove(testTileCanMove, player));
        Assert.assertFalse(game.isLegalMove(testTileCantMove, player));
    }

    @Test
    public void isLegalMoveIsFalseWithRotationMove() {
        Board board = Board.getBoard();

        Tile testTile = new Tile(0, 1, 2, 3, 4, 6, 5, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer("Vyas", space, 0, tilePileMock);
        List<SPlayer> remainingPlayers = new ArrayList<SPlayer>();
        remainingPlayers.add(player);

        Game game = Game.getGame(remainingPlayers, new ArrayList<SPlayer>(), tilePileMock);
        Assert.assertFalse(game.isLegalMove(testTile, player));
    }

    @Test
    public void playMoveEliminatesPlayersThatLose() {
        Board board = Board.getBoard();

        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null);
        when(tilePileMock.isEmpty())
                .thenReturn(true);

        BoardSpace spaceOne = board.getBoardSpace(0, 0);
        BoardSpace spaceTwo = board.getBoardSpace(3, 5);
        SPlayer vyas = new SPlayer("Vyas", spaceOne, 0, tilePileMock);
        SPlayer keith = new SPlayer("Keith", spaceTwo, 2, tilePileMock);
        List<SPlayer> remainingPlayers = new ArrayList<SPlayer>();
        remainingPlayers.add(vyas);
        remainingPlayers.add(keith);

        Game game = Game.getGame(remainingPlayers, new ArrayList<SPlayer>(), tilePileMock);
        game.playTurn(testTile, vyas);
        Assert.assertNull(vyas.getTile(0));
        Assert.assertNull(vyas.getTile(1));
        Assert.assertNull(vyas.getTile(2));
        Assert.assertEquals(spaceOne.findToken(vyas.getToken()), -1);
        Assert.assertNull(vyas.getToken().getBoardSpace());
    }

    @Test
    public void dragonTileWithNoneDrawnTest() {
        Board board = Board.getBoard();

        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile, testTile, null)
                .thenReturn(testTile, testTile, null)
                .thenReturn(testTile, testTile, null)
                .thenReturn(testTile, testTile, null)
                .thenReturn(null)
                .thenReturn(testTile);
        when(tilePileMock.isEmpty())
                .thenReturn(true)
                .thenReturn(false);

        BoardSpace spaceOne = board.getBoardSpace(1, 0);
        BoardSpace spaceTwo = board.getBoardSpace(5, 5);
        SPlayer vyas = new SPlayer("Vyas", spaceOne, 7, tilePileMock);
        SPlayer keith =  new SPlayer("Keith", spaceOne, 6, tilePileMock);
        SPlayer robby =  new SPlayer("Robby", spaceTwo, 2, tilePileMock);
        SPlayer christos =  new SPlayer("Christos", spaceTwo, 5, tilePileMock);

        List<SPlayer> remainingPlayers = new ArrayList<SPlayer>();
        remainingPlayers.add(vyas);
        remainingPlayers.add(keith);
        remainingPlayers.add(robby);
        remainingPlayers.add(christos);

        Game game = Game.getGame(remainingPlayers, new ArrayList<SPlayer>(), tilePileMock);
        Assert.assertEquals(game.playTurn(testTile, vyas).size(), 2);
        Assert.assertTrue(robby.hasFullHand());
        Assert.assertTrue(christos.hasFullHand());

        verify(tilePileMock, times(15)).drawFromDeck();

    }
}
