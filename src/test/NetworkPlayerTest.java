package test;

import org.junit.Assert;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import main.Color;
import main.NetworkMessage;
import main.Players.APlayer;
import main.Players.NetworkPlayer;

/**
 * Created by William on 5/20/2018.
 */

public class NetworkPlayerTest {

    @Test
    public void testGetName() {
        Reader r = new StringReader("");
        Writer w = new StringWriter();
        APlayer player = new NetworkPlayer("Name", Color.BLUE, r, w);
        player.getName();

        Assert.assertEquals(
                w.toString(),
                "<" +NetworkMessage.GET_NAME.getTag() + ">" +
                        "</" + NetworkMessage.GET_NAME.getTag() + ">\r\n"
        );
    }
}
