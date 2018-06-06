package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javafx.util.Pair;
import main.Parser.ParserException;
import main.Parser.ParserUtils;
import main.Players.APlayer;
import main.Players.MostSymmetricPlayer;
import main.Players.PlayerHand;

/**
 * The Player's representation of the Game Server
 * <p>
 * Created by vyasalwar on 5/21/18.
 */
public class NetworkGame {

    //================================================================================
    // Private variables
    //================================================================================
    private String host;
    private int port;

    private APlayer aplayer;
    private boolean gameEnded;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private boolean debug = false;
    //================================================================================
    // Constructors
    //================================================================================

    public NetworkGame(String host, int port, APlayer aplayer) {
        this.host = host;
        this.port = port;
        this.aplayer = aplayer;

        // Set up networking
        try {
            this.socket = new Socket(host, port);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ParserException("Failed to connect to the Game Server");
        }
    }

    //================================================================================
    // Public methods
    //================================================================================

    // Runs a loop to listen on the input stream, parse data,
    // forward requests to appropriate method on APlayer, then send back a response
    public void handleInstructions() {
        while (!gameEnded || true) {
            try {
                String request = getRequestFromInputStream();
                String response = forwardRequestToAPlayer(request);
                sendResponseToServer(response);
            } catch (IOException e) {
                System.err.println("Failed to interact with network");
                e.printStackTrace();
                break;
            }
        }
    }



    //================================================================================
    // Private methods
    //================================================================================
    private String getRequestFromInputStream() throws IOException {
        return reader.readLine();
    }

    private void sendResponseToServer(String response) throws IOException{
        writer.print(response);
        writer.print("\n");
        writer.flush();
    }

    private String forwardRequestToAPlayer(String request) throws IOException {
        try {
            Node root = ParserUtils.nodeFromString(request);
            if (debug) System.out.println(root.getNodeName() + " for " + aplayer.getColor());
            switch (root.getNodeName()) {
                case "get-name":
                    return getNameHandler(root);
                case "initialize":
                    return initializeHandler(root);
                case "place-pawn":
                    return placePawnHandler(root);
                case "play-turn":
                    return playTurnHandler(root);
                case "end-game":
                    return endGameHandler(root);
                default:
                    throw new ParserException();
            }

        } catch (SAXException | ParserConfigurationException e) {
            System.err.println("XML Document failed to parse");
            e.printStackTrace();
            throw new IOException();
        }
    }

    //================================================================================
    // Request Handlers
    //================================================================================

    private String getNameHandler(Node root) {
        return "<player-name>" + aplayer.getName() + "</player-name>";
    }

    private String initializeHandler(Node root) {
        Node myColorNode = root.getFirstChild();
        Node colorListNode = myColorNode.getNextSibling();

        if (!myColorNode.getNodeName().equals("color") || !colorListNode.getNodeName().equals("list"))
            throw new ParserException();

        Color myColor = Color.fromXML((Element) root.getFirstChild());
        List<Color> otherColors = new LinkedList<>();
        for (Node colorNode = colorListNode.getFirstChild();
             colorNode != null;
             colorNode = colorNode.getNextSibling()) {

            if (!colorNode.getNodeName().equals("color")) {
                throw new ParserException();
            }

            otherColors.add(Color.fromXML((Element)colorNode));
        }

        aplayer.setColor(myColor);
        aplayer.initialize(otherColors);
        return "<void></void>";
    }

    private String placePawnHandler(Node root){
        Node boardNode = root.getFirstChild();

        Board board = new Board();
        board.fromXML((Element) boardNode);
        aplayer.setBoard(board);
        Pair<BoardSpace, Integer> playerLocation = aplayer.placeToken();

        return Token.pawnLocFromLocation(playerLocation);
    }

    private String playTurnHandler(Node root) throws IOException{
        Node boardNode = root.getFirstChild();

        Node setOfTilesNode = boardNode.getNextSibling();
        Node tilesLeftNode = setOfTilesNode.getNextSibling();

        Board board = new Board();
        board.fromXML((Element) boardNode);

        aplayer.setBoard(board);

        if (debug) System.out.println("INTERNAL BOARD:");
        try {
            if (debug) System.out.println(ParserUtils.xmlElementToString(board.toXML(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        PlayerHand playerHand = new PlayerHand();
        playerHand.fromXML((Element)setOfTilesNode);
        aplayer.setHand(playerHand);

        int numTilesLeft = Integer.parseInt(tilesLeftNode.getTextContent());
        Tile tile = aplayer.chooseTile(numTilesLeft);

        board.placeTile(tile, aplayer);

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element returnedTileElement = tile.toXML(document);

            if (debug) System.out.println("RESPONSE TILE:");
            if (debug) System.out.println(ParserUtils.xmlElementToString(returnedTileElement));

            return ParserUtils.xmlElementToString(returnedTileElement);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    private String endGameHandler(Node root){

        Node boardNode = root.getFirstChild();

        // Parse the board
        Board board = new Board();
        board.fromXML((Element) boardNode);
        aplayer.setBoard(board);

        // Parse the set-of-colors
        Set<Color> winners = new HashSet<>();
        for (Node colorNode = boardNode.getNextSibling().getFirstChild();
             colorNode != null;
             colorNode = colorNode.getNextSibling()
        ){
            winners.add(Color.fromXML((Element) colorNode));
        }

        aplayer.endGame(winners);
        gameEnded = true;
        return "<void></void>";
    }


    //================================================================================
    // Entry point for running a network player
    //================================================================================

    public static void main(String[] args) {
        try {
            String host = args[0];
            System.out.println(host);
            int port = Integer.valueOf(args[1]);

            APlayer player = new MostSymmetricPlayer("symmetric", Color.BLUE);

            NetworkGame game = new NetworkGame(host, port, player);
            game.debug = false;
            game.handleInstructions();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Required arguments are hostname and port");
        }
    }

}
