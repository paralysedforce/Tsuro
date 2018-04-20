package test;

import com.sun.tools.javac.util.Assert;
import main.Tile;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by vyasalwar on 4/19/18.
 */
public class TileTest {

    @Test
    public void EqualsTest(){
        Tile t1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile t2 = new Tile(1, 0, 2, 3, 4, 5, 6, 7);
        Tile t3 = new Tile(4,5,6,7,0,1,2,3);
        Tile t4 = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        Assert.check(t1.equals(t2));
        Assert.check(t2.equals(t3));
        Assert.check(!t1.equals(t4));
    }

}