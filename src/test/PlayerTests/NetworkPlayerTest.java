package test.PlayerTests;

import main.Parser.ParserUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.w3c.dom.Document;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import main.Color;
import main.ContractException;
import main.Game;
import main.NetworkMessage;
import main.Players.APlayer;
import main.Players.NetworkPlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
                "<" + NetworkMessage.GET_NAME.getTag() + ">" +
                        "</" + NetworkMessage.GET_NAME.getTag() + ">\n"
        );
    }

    @Test
    public void testInitialize() {
        String xmlResponse = getInitializationResponse();

        Reader r = new StringReader(xmlResponse);
        Writer w = new StringWriter();
        APlayer player = initializeNetworkPlayer(r, w);

        Assert.assertEquals(player.getColor(), Color.BLUE);
        Assert.assertEquals(
                w.toString(),
                getInitializationRequest());
    }

    private List<Color> getInitializationColors() {
        return Arrays.asList(Color.BLUE, Color.SIENNA);
    }

    private String getInitializationRequest() {
        return "<" + NetworkMessage.INITIALIZE.getTag() + ">" +
                "<color>" + Color.BLUE.toString() + "</color>" +
                "<list>" +
                "<color>" + Color.BLUE.toString() + "</color>" +
                "<color>" + Color.SIENNA.toString() + "</color>" +
                "</list>" +
                "</" + NetworkMessage.INITIALIZE.getTag() + ">\n";
    }

    private String getInitializationResponse() {
        return "<void></void>\n";
    }

    private APlayer initializeNetworkPlayer(Reader r, Writer w) {
        APlayer player = new NetworkPlayer("Testname", Color.BLUE, r, w);
        player.initialize(getInitializationColors());
        return player;
    }

    @Test
    public void testPlacePawn() throws ParserConfigurationException {
        Reader r = new StringReader(
                getInitializationResponse() +
                        "<pawn-loc>" +
                        "<h></h>" +
                        "<n>0</n>" +
                        "<n>2</n>" +
                        "</pawn-loc>\r\n"
        );
        Writer w = new StringWriter();
        APlayer player = initializeNetworkPlayer(r, w);

        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        String expectedPlacementRequestBoard =
                ParserUtils.xmlElementToString(Game.getGame().getBoard().toXML(d));

        player.placeToken();

        // The token should now be at (0,1,0)
        Assert.assertEquals(player.getToken().getBoardSpace().getCol(), 1);
        Assert.assertEquals(player.getToken().getBoardSpace().getRow(), 0);
        Assert.assertEquals(player.getToken().getTokenSpace(), 0);

        // The request should be formed as expected.
        Assert.assertEquals(
                w.toString(),
                getInitializationRequest() +
                        "<" + NetworkMessage.PLACE_PAWN.getTag() + ">" +
                        expectedPlacementRequestBoard +
                        "</" + NetworkMessage.PLACE_PAWN.getTag() + ">\n"

        );
    }


    @Test
    public void testEndGame() {
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Reader r = new StringReader("<void></void>");
            Writer w = new StringWriter();
            APlayer player = initializeNetworkPlayer(r, w);
            String expectedRequestBoard =
                    ParserUtils.xmlElementToString(Game.getGame().getBoard().toXML(d));
            String expectedColors =
                    "<set>" +
                            ParserUtils.xmlElementToString(Color.BLUE.toXML(d)) +
                            "</set>";

            player.endGame(Sets.newSet(Color.BLUE));

            Assert.assertEquals(
                    w.toString(),
                    "<" + NetworkMessage.END_GAME.getTag() + ">" +
                            expectedRequestBoard + expectedColors +
                            "</" + NetworkMessage.END_GAME.getTag() + ">\n"
            );

        } catch (ContractException e) {
            // We know that calling endGame will break the contract, so catch in anticipation.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
