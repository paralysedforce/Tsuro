package main.Players;

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
     * @param name
     * @param color
     * @param portNumber
     * @throws IOException
     */
    public NetworkPlayer(String name, Color color, int portNumber) throws IOException {
        super(name, color);

        Socket socket = new ServerSocket(portNumber).accept();
        fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toClient = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Constructs streams from Reader/Writer pair for use in testing.
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

    @Override
    public String getName() {
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            // Note: we can use \r\n in println because \r is whitespace and should be ignored.
            toClient.println(
                    NetworkMessage.xmlElementToString(
                            NetworkMessage.GET_NAME.getMessageRootElement(d)
                    ));

            String response = fromClient.readLine();

            Node responseNode = NetworkMessage.nodeFromString(response);
            return responseNode.getTextContent();

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return ""; // TODO: something smarter on failure.
        }
    }

    @Override
    void initialize(Color color, List<Color> colors) {
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element initializeElement = NetworkMessage.INITIALIZE.getMessageRootElement(d);
            initializeElement.appendChild(this.getColor().toXml(d));

            Element colorListElement = d.createElement("list");
            for (Color aColor : colors) {
                colorListElement.appendChild(aColor.toXml(d));
            }

            initializeElement.appendChild(colorListElement);

            toClient.println(NetworkMessage.xmlElementToString(initializeElement));

            String response = fromClient.readLine();
            // TODO: Maybe add a check to make sure the response is void?

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            toClient.println(
                    "<" + NetworkMessage.PLACE_PAWN.getTag() + ">" +
                            NetworkMessage.xmlElementToString(board.toXML(d)) +
                            "</" + NetworkMessage.PLACE_PAWN.getTag() + ">"
            );
            String response = fromClient.readLine();

            Node e = NetworkMessage.nodeFromString(response);
            boolean isHorizontal;
            int coord1, coord2;

            if (e.getNodeName().equals(NetworkMessage.PAWN_LOC.getTag())) {
                Node orientationNode = e.getFirstChild();
                if (orientationNode.getNodeName().equals("h") ||
                        orientationNode.getNodeName().equals("v")) {
                    isHorizontal = orientationNode.getNodeName().equals("h");

                    Node coord1Node = orientationNode.getNextSibling();
                    if (coord1Node.getNodeName().equals("n")) {
                        coord1 = Integer.valueOf(coord1Node.getTextContent());

                        Node coord2Node = coord1Node.getNextSibling();
                        if (coord2Node.getNodeName().equals("n")) {
                            coord2 = Integer.valueOf(coord2Node.getTextContent());

                            return Token.locationFromPawnLoc(board, isHorizontal, coord1, coord2);
                        }
                    }
                }
            }

            // Default to returning a random location
            System.err.println("Couldn't parse location: " + response);
            return RandomPlayer.getRandomStartingLocation(new Random());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            // Randomly choose a starting location
            return RandomPlayer.getRandomStartingLocation(new Random());
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

            toClient.println(NetworkMessage.xmlElementToString(playTurnElement));

            String response = fromClient.readLine();

            Node responseNode = NetworkMessage.nodeFromString(response);

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
                winnersSet.appendChild(color.toXml(d));
            }

            Element endGameElement = d.createElement(NetworkMessage.END_GAME.getTag());
            endGameElement.appendChild(boardElement);
            endGameElement.appendChild(winnersSet);

            toClient.println(NetworkMessage.xmlElementToString(endGameElement));

            String response = fromClient.readLine();
            // TODO: Maybe do something if the response is not <void></void>

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }

    }
}
