package test;

import org.junit.Assert;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

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
        Reader r = new StringReader("<player-name>name!</player-name>\n");
        Writer w = new StringWriter();
        APlayer player = new NetworkPlayer("Name", Color.BLUE, r, w);

        Assert.assertEquals(player.getName(), "name!");

        Assert.assertEquals(
                w.toString(),
                "<" +NetworkMessage.GET_NAME.getTag() + ">" +
                        "</" + NetworkMessage.GET_NAME.getTag() + ">\r\n"
        );
    }

    @Test
    public void testInitialize() {
        List<Color> colors = Arrays.asList(Color.BLUE, Color.DARKGREEN);
        String xmlResponse = "<void></void>\n";

        Reader r = new StringReader(xmlResponse);
        Writer w = new StringWriter();
        APlayer player = new NetworkPlayer("Name", Color.BLUE, r, w);

        player.initialize(colors);

        Assert.assertEquals(
                w.toString(),
                "<" + NetworkMessage.INITIALIZE.getTag() + ">" +
                        "<color>" + Color.BLUE.str() + "</color>" +
                        "<list>" +
                        "<color>" + Color.BLUE.str() + "</color>" +
                        "<color>" + Color.DARKGREEN.str() + "</color>" +
                        "</list>" +
                        "</" + NetworkMessage.INITIALIZE.getTag() + ">"
                );
    }
}
