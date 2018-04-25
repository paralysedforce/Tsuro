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

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameTest {

    @Mock
    TilePile tilePileMock;

    @Before
    public void resetBoard() {
        Board.resetBoard();
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

        Game game = new Game(remainingPlayers, new ArrayList<SPlayer>());
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

        Game game = new Game(remainingPlayers, new ArrayList<SPlayer>());
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

        Game game = new Game(remainingPlayers, new ArrayList<SPlayer>());
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

        Game game = new Game(remainingPlayers, new ArrayList<SPlayer>());
        Assert.assertFalse(game.isLegalMove(testTile, player));
    }

    @Test
    public void playMoveEliminatesPlayersThatLose() {
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

        Game game = new Game(remainingPlayers, new ArrayList<SPlayer>());
        game.playTurn(testTile, player);
        Assert.assertNull(player.getTile(0));
        Assert.assertNull(player.getTile(1));
        Assert.assertNull(player.getTile(2));
        Assert.assertEquals(space.findToken(player.getToken()), -1);
        Assert.assertNull(player.getToken().getBoardSpace());
    }
}
