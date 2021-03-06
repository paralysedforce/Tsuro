package test.PlayerTests;

import main.*;
import main.Players.*;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeastSymmetricPlayerTest {

    @Mock
    TilePile tilePileMock;

    @Before
    public void gameReset(){
        Game.resetGame();
        Game.getGame().setTilePile(tilePileMock);
    }

    @Test
    public void LeastSymmetricPlayerCanBeInstantiatedTest() {
        APlayer leastSymmetricPlayer = new LeastSymmetricPlayer("Vyas", Color.SIENNA);
    }

    @Test
    public void LeastSymmetricPlayerMakesUnsafeMovesOnlyWhenNecessaryTest(){
        when(tilePileMock.drawFromDeck())
                .thenReturn(new Tile(0, 1, 2, 3, 4, 5, 6, 7))
                .thenReturn(null);

        /*
        This tile will always kill a player on an edge.

              |  |
          -+  +--+  +-
           |        |
          -+  +--+  +-
              |  |

        Test whether or not a leastSymmetricPlayer plays it when it's the only choice available

         */

        APlayer leastSymmetricPlayer = new LeastSymmetricPlayer("Vyas", Color.SIENNA);
        leastSymmetricPlayer.initialize(new ArrayList<>());
        leastSymmetricPlayer.placeToken();
        Tile tile = leastSymmetricPlayer.chooseTile(420);
        Assert.assertTrue(Game.getGame().getBoard().willKillPlayer(tile, leastSymmetricPlayer));
    }

    @Test
    public void LeastSymmetricPlayerMakesSafeMoves() {
        Tile tile1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile tile2 = new Tile(0, 5, 1, 4, 2, 7, 3, 6);

        when(tilePileMock.drawFromDeck())
                .thenReturn(tile2)
                .thenReturn(tile1)
                .thenReturn(null);

        /*
            Tile 1 will kill all player on any edge, while Tile 2 never will.

             Tile 1                Tile 2

              |  |                |    |
          -+  +--+  +-          --+----+--
           |        |             |    |
          -+  +--+  +-          --+----+--
              |  |                |    |

            Test to make sure that even when tile 1 is in a LeastSymmetricPlayer's hand, it will always choose Tile 2

         */

        APlayer leastSymmetricPlayer = new LeastSymmetricPlayer("Vyas", Color.SIENNA);
        leastSymmetricPlayer.initialize(new ArrayList<>());
        leastSymmetricPlayer.placeToken();
        Tile tile = leastSymmetricPlayer.chooseTile(420420);
        Assert.assertFalse(Game.getGame().getBoard().willKillPlayer(tile, leastSymmetricPlayer));
        Assert.assertTrue(tile.equals(new Tile(0, 5, 1, 4, 2, 7, 3, 6)));
    }

    @Test
    public void LeastSymmetricPlayerMakesLeastSymmetricSafeMove(){
        Tile tile1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile tile2 = new Tile(0, 5, 1, 4, 2, 7, 3, 6);
        Tile tile3 = new Tile(0, 4, 1, 2, 3, 5, 6, 7);


        when(tilePileMock.drawFromDeck())
                .thenReturn(tile3)
                .thenReturn(tile2)
                .thenReturn(tile1);

        /*
            Tile 1 will kill all player on any edge, while Tile 2 never will.
            Tile 3 will always have a rotation that does not kill the player

             Tile 1                Tile 2           Tile 3
                                                     |   |
              |  |                |    |        -+    \  +--
          -+  +--+  +-          --+----+--       |     \
           |        |             |    |        -+   .--+---
          -+  +--+  +-          --+----+--          /    \
              |  |                |    |            |    |

            Test to make sure that even when tile 1 and tile 3 are in a LeastSymmetricPlayer's hand, it will always choose Tile 2

         */

        APlayer leastSymmetricPlayer = new LeastSymmetricPlayer("Vyas", Color.SIENNA);
        leastSymmetricPlayer.initialize(new ArrayList<>());
        leastSymmetricPlayer.placeToken();
        Tile tile = leastSymmetricPlayer.chooseTile(420);
        Assert.assertFalse(Game.getGame().getBoard().willKillPlayer(tile, leastSymmetricPlayer));
        Assert.assertTrue(tile.equals(new Tile(0, 4, 1, 2, 3, 5, 6, 7)));
        Assert.assertEquals(tile.calculateSymmetries(), 1);
    }

}
