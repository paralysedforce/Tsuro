package main;

import main.Parser.ParserUtils;
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
import javax.xml.transform.TransformerException;

import javafx.util.Pair;
import main.Parser.ParserException;
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
            Node root = NetworkMessage.nodeFromString(request);
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

        Color myColor = Color.fromXml((Element) root.getFirstChild());
        List<Color> otherColors = new LinkedList<>();
        for (Node colorNode = colorListNode.getFirstChild();
             colorNode != null;
             colorNode = colorNode.getNextSibling()) {

            if (!colorNode.getNodeName().equals("color")) {
                throw new ParserException();
            }

            otherColors.add(Color.fromXml((Element)colorNode));
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


        PlayerHand playerHand = new PlayerHand();
        playerHand.fromXML((Element)setOfTilesNode);
        aplayer.setHand(playerHand);

        int numTilesLeft = Integer.parseInt(tilesLeftNode.getTextContent());
        Tile tile = aplayer.chooseTile(numTilesLeft);

        board.placeTile(tile, aplayer);

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element returnedTileElement = tile.toXML(document);

            return ParserUtils.xmlElementToString(returnedTileElement);

        } catch (ParserConfigurationException | TransformerException e) {
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
            winners.add(Color.fromXml((Element) colorNode));
        }

        aplayer.endGame(winners);
        gameEnded = true;
        return "<void></void>";
    }


    //================================================================================
    // Entry point for running a network player
    //================================================================================

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.valueOf(args[1]);
//        APlayer mockPlayer = new RandomPlayer("randy", Color.BLUE) {
//            @Override
//            protected Tile chooseTile(Board board, Set<Tile> hand, int remainingTiles) {
//                Set<Tile> moves = getLegalMoves();
//                System.out.println("Number of legal moves: " + moves.size());
//
//                System.out.println("Choosing tile at location row: " + token.getBoardSpace().getRow() + " col: " + token.getBoardSpace().getCol());
//                if (moves.size() > 0)
//                    return moves.iterator().next();
//                else
//                    return this.hand.getTile(0);
//            }
//        };
        APlayer player = new MostSymmetricPlayer("symmetric", Color.BLUE);

        new NetworkGame(host, port, player).handleInstructions();
    }

}
