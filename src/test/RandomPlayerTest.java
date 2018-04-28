package test;

import main.Game;
import main.TilePile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RandomPlayerTest {

    @Mock
    TilePile tilePileMock;

    @Before
    public void gameReset(){
        Game.resetGame();
        Game.getGame().setTilePile(tilePileMock);
    }

    @Test
    public void randomPlayerCanBeInstantiatedTest(){
        new RandomPlayerTest();
    }
}
