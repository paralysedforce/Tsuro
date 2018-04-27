package test;

import main.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SPlayerTest {

    @Mock TilePile tilePileMock;

    Tile tileOne;
    Tile tileTwo;
    Tile tileThree;

    @Before
    public void tileInitialization(){
        tileOne = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        tileTwo = new Tile(0, 2, 1, 3, 4, 6, 5, 7);
        tileThree = new Tile (0, 5, 1, 4, 2, 7, 3, 6);

        Game.resetGame();
        Game.getGame().setTilePile(tilePileMock);
    }


    @Test
    public void drawFromPileDoesntDrawWithFullHandTest() {
        when(tilePileMock.drawFromDeck())
                .thenReturn(tileOne, tileTwo, tileThree);

        SPlayer testSPlayer = new SPlayer("Keith", new BoardSpace(0, 0), 0);
        testSPlayer.drawFromPile();

        verify(tilePileMock, times(3)).drawFromDeck();
    }

    @Test
    public void drawFromPileDrawsWithOpenSpaceTest() {
        when(tilePileMock.drawFromDeck()).thenReturn(null)
                .thenReturn(null, null, null)
                .thenReturn(tileOne);

        SPlayer testSPlayer = new SPlayer("Keith", new BoardSpace(0, 0), 0);
        testSPlayer.drawFromPile();

        verify(tilePileMock, times(4)).drawFromDeck();
    }

    @Test
    public void hasTileReturnsTrueWithEqualTile() {

        when(tilePileMock.drawFromDeck())
                .thenReturn(tileOne, tileTwo, tileThree);

        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        SPlayer testSPlayer = new SPlayer("Keith", new BoardSpace(0, 0), 0);

        Assert.assertTrue(testSPlayer.holdsTile(testTile));
        testTile.rotateClockwise();
        Assert.assertTrue(testSPlayer.holdsTile(testTile));
    }

    @Test
    public void hasTileReturnsFalseWithoutTile() {

        when(tilePileMock.drawFromDeck())
                .thenReturn(tileOne, tileTwo, tileThree);

        Tile testTile = new Tile(0, 4, 1, 5, 2, 6, 3, 7);
        SPlayer testSPlayer = new SPlayer("Keith", new BoardSpace(0, 0), 0);

        Assert.assertFalse(testSPlayer.holdsTile(testTile));
    }
}
