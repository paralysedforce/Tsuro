package main.Players;

import main.Parser.ParserException;
import main.Parser.ParserUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.NetworkMessage;
import main.Tile;
import main.Token;

/**
 * Created by William on 5/20/2018.
 */

public class NetworkPlayer extends APlayer {
    private BufferedReader fromClient;
    private PrintWriter toClient;

    /**
     * Listens on portNumber and communicates over a network after calling that.
     */
    public NetworkPlayer(String name, Color color, int portNumber) throws IOException {
        super(name, color);

        Socket socket = new ServerSocket(portNumber).accept();
        fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toClient = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Constructs streams from Reader/Writer pair for use in testing.
     *
     * @param name
     * @param color
     * @param reader
     * @param writer
     */
    public NetworkPlayer(String name, Color color, Reader reader, Writer writer) {
        super(name, color);
        fromClient = new BufferedReader(reader);
        toClient = new PrintWriter(writer);
    }


    //================================================================================
    // Network Handlers
    //================================================================================

    @Override
    public String getName() {
        try {
            Document d = ParserUtils.newDocument();
            // Note: we can use \r\n in println because \r is whitespace and should be ignored.
            toClient.println(
                    NetworkMessage.xmlElementToString(
                            NetworkMessage.GET_NAME.getMessageRootElement(d)
                    ));

            String response = fromClient.readLine();
            Node responseNode = ParserUtils.nodeFromString(response);
            return responseNode.getTextContent();

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return ""; // TODO: something smarter on failure.
        }
    }

    @Override
    public void initialize(Color color, List<Color> colors) {
        super.initialize(color, colors);
        try {
            Document document = ParserUtils.newDocument();
            Element initializeElement = NetworkMessage.INITIALIZE.getMessageRootElement(document);
            initializeElement.appendChild(color.toXML(document));

            Element colorListElement = document.createElement("list");
            for (Color aColor : colors) {
                colorListElement.appendChild(aColor.toXML(document));
            }

            initializeElement.appendChild(colorListElement);
            toClient.println(ParserUtils.xmlElementToString(initializeElement));
            String responseXML = fromClient.readLine();
            // Check to make sure that the response is void


        } catch (IOException e) {
            e.printStackTrace();
            // Do something maybe?
            System.exit(-1);
        }

    }

    @Override
    protected Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            // Construct the request
            String openingTag = String.format("<%s>", NetworkMessage.PLACE_PAWN.getTag());
            String content = ParserUtils.xmlElementToString(board.toXML(d));
            String closingTag = String.format("</%s>", NetworkMessage.PLACE_PAWN.getTag());
            String request = openingTag + content + closingTag;

            // Send to the client and listen for a response
            toClient.println(request);
            String response = fromClient.readLine();

            // Parse the response
            Node responseXml = ParserUtils.nodeFromString(response);
            return parseStartingLocationResponse(responseXml);

        } catch (ParserConfigurationException | IOException | SAXException | ParserException e) {
            e.printStackTrace();
            // Randomly choose a starting location
            return RandomPlayer.getRandomStartingLocation(new Random(), board);
        }
    }


    @Override
    Tile chooseTile(Board board, Set<Tile> hand, int remainingTiles) {
        // Make play-turn network call
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element boardElement = board.toXML(d);
            Element handElement = d.createElement("set");
            Element remainingTilesElement = d.createElement("n");

            for (Tile tile : hand) handElement.appendChild(tile.toXML(d));
            remainingTilesElement.appendChild(d.createTextNode(Integer.toString(remainingTiles)));

            Element playTurnElement = d.createElement(NetworkMessage.PLAY_TURN.getTag());

            playTurnElement.appendChild(boardElement);
            playTurnElement.appendChild(handElement);
            playTurnElement.appendChild(remainingTilesElement);

            toClient.println(ParserUtils.xmlElementToString(playTurnElement));

            String response = fromClient.readLine();

            Node responseNode = ParserUtils.nodeFromString(response);

            if (responseNode.getNodeName().equals(NetworkMessage.TILE.getTag())) {
                Tile tile = new Tile();
                tile.fromXML((Element) responseNode);
                return tile;
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        // TODO: make a random move or have null interpreted as illegal move and replaced with random.
        return null;
    }

    @Override
    void endGame(Board board, Set<Color> winners) {
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element boardElement = board.toXML(d);
            Element winnersSet = d.createElement("set");

            for (Color color : winners) {
                winnersSet.appendChild(color.toXML(d));
            }

            Element endGameElement = d.createElement(NetworkMessage.END_GAME.getTag());
            endGameElement.appendChild(boardElement);
            endGameElement.appendChild(winnersSet);

            toClient.println(ParserUtils.xmlElementToString(endGameElement));

            String response = fromClient.readLine();
            // TODO: Maybe do something if the response is not <void></void>

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    //================================================================================
    // Private helpers
    //================================================================================

    private Pair<BoardSpace, Integer> parseStartingLocationResponse(Node responseXml) {

        if (!responseXml.getNodeName().equals(NetworkMessage.PAWN_LOC.getTag()))
            throw new ParserException();

        Node hvNode = responseXml.getFirstChild();
        Node coord1Node = hvNode.getNextSibling();
        Node coord2Node = coord1Node.getNextSibling();

        String hvNodeName = hvNode.getNodeName();

        if (!(hvNodeName.equals("h") || hvNodeName.equals("v")))
            throw new ParserException();

        if (!coord1Node.getNodeName().equals("n") || !coord2Node.getNodeName().equals("n"))
            throw new ParserException();

        boolean isHorizontal = hvNodeName.equals("h");
        int coord1 = Integer.valueOf(coord1Node.getTextContent());
        int coord2 = Integer.valueOf(coord2Node.getTextContent());

        return Token.locationFromPawnLoc(board, isHorizontal, coord1, coord2);
    }

    private boolean responseIsVoid(String response) {
        try {
            return ParserUtils.nodeFromString(response).getNodeName().equals("void");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return false;
        }

    }
}
