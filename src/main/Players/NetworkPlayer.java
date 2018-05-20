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
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.NetworkMessage;
import main.Tile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    @Override
    Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        throw new NotImplementedException();
    }

    @Override
    Tile chooseTile(Board board, int remainingTiles) {
        throw new NotImplementedException();
    }

    @Override
    void endGame(Board board, Set<Color> winners) {
        throw new NotImplementedException();
    }
}
