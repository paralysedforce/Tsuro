package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Set;

import javafx.util.Pair;
import main.Parser.ParserException;
import main.Players.APlayer;

/**
 *
 * Represents a Tsuro Board
 *
 * Created by vyasalwar on 4/16/18.
 */
public class Board implements Parsable{

    //================================================================================
    // Constructor
    //================================================================================

    public Board() {
        this.spaces = new BoardSpace[BOARD_LENGTH][BOARD_LENGTH];
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                spaces[i][j] = new BoardSpace(i, j);
            }
        }
    }

    public Board(Element xmlElement){
        fromXML(xmlElement);
    }

    //================================================================================
    // Instance Variables
    //================================================================================
    private BoardSpace[][] spaces;
    final static int BOARD_LENGTH = 6;

    //================================================================================
    // Getters
    //================================================================================
    public BoardSpace getBoardSpace(int row, int col) {
        if (!isValidCoordinate(row, col))
            throw new IllegalArgumentException("Invalid Tile Access row: " + row + " col: " + col);

        return spaces[row][col];
    }

    //================================================================================
    // Public methods
    //================================================================================

    /**
     * Returns true if there is a tile on the row and col
     */
    public boolean hasTile(int row, int col) {
        return getBoardSpace(row, col).hasTile();
    }

    // Returns true if placing the tile in front of the token will lead to the player's death
    // Does not actually place the tile
    public boolean willKillPlayer(Tile tile, Token token) {

        BoardSpace originalSpace = token.getBoardSpace();
        int originalRow = originalSpace.getRow();
        int originalCol = originalSpace.getCol();

        BoardSpace curSpace = originalSpace;
        int curTokenSpace = token.getTokenSpace();

        try {
            // Trace out a path on the board
            while (true){

                // Each time we cross the original space,
                //  we need to use the information from the input tile
                if (curSpace.getRow() == originalRow && curSpace.getCol() == originalCol) {
                    // Move to the space across the tile
                    curTokenSpace = tile.findMatch(curTokenSpace);
                    curSpace = getNextSpace(curSpace, curTokenSpace);
                    curTokenSpace = Token.getMirroredTokenSpace(curTokenSpace);
                }

                // If we pass a space with a tile on it
                //   Use the information from that tile
                else if (curSpace.hasTile()) {
                    curTokenSpace = curSpace.getTile().findMatch(curTokenSpace);
                    curSpace = getNextSpace(curSpace, curTokenSpace);
                    curTokenSpace = Token.getMirroredTokenSpace(curTokenSpace);
                }
                else {
                    // We're on a valid space on the board without a tile
                    //   that we aren't trying to place a tile onto
                    return false;
                }
            }

        }

        catch (IllegalArgumentException e){
            // We've walked to a place off the board since we're trying to
            //   access a space with an invalid coordinate
            return true;
        }
    }

    // Places the tile in front of the player, regardless of whether it will kill the player
    //   Returns the Set of tokens driven off the board
    public Set<Token> placeTile(Tile tile, Token playerToken) {
        if (playerToken.getBoardSpace().hasTile()){
            throw new ContractException(ContractViolation.PRECONDITION,
                    "Token should be on empty Boardspace");
        }

        // Place the tile on the space
        BoardSpace space = playerToken.getBoardSpace();
        space.setTile(tile);

        // Gather every token currently on the space
        Set<Token> tokensToMove = space.getTokensOnSpace();
        Set<Token> eliminatedPlayers = new HashSet<>();

        // Advance each token to the end of their path
        for (Token token: tokensToMove){
            advanceToEnd(token);

            // Eliminate the token if necessary
            if (isOnEdge(token)) {
                eliminatedPlayers.add(token);
                token.removeFromPlay();
            }
        }

        if (playerToken.isAlive() && playerToken.getBoardSpace().hasTile()){
            throw new ContractException(ContractViolation.POSTCONDITION,
                    "Token should be eliminated or on empty Boardspace");
        }

        return eliminatedPlayers;
    }

    public BoardSpace findLocationOfTile(Tile tile){
        for (int row = 0; row < BOARD_LENGTH; row++) {
            for (int col = 0; col < BOARD_LENGTH; col++) {
                BoardSpace space = getBoardSpace(row, col);
                if (space.hasTile() && space.getTile().equals(tile))
                    return space;
            }
        }

        // Tile is not on board
        return null;
    }

    // TODO: These should eventually should be refactored out, but keep it here for now
    public Set<Token> placeTile(Tile tile, APlayer player){
        return placeTile(tile, player.getToken());
    }
    public boolean willKillPlayer(Tile tile, APlayer player) {
        return willKillPlayer(tile, player.getToken());
    }


    public Token findToken(Color color) {
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {

                for (Token token : getBoardSpace(i, j).getTokensOnSpace()) {
                    if (token.getColor() == color) {
                        return token;
                    }
                }
            }
        }

        // Token not on board
        return null;
    }

    //================================================================================
    // Private Helpers
    //================================================================================

    // Gets the adjacent space of an arbitrary board space and token space.
    private BoardSpace getNextSpace(BoardSpace boardSpace, int tokenSpace) {
        int row = boardSpace.getRow();
        int col = boardSpace.getCol();
        int direction = tokenSpace / 2;

        if      (direction == 0) row--; // Move up
        else if (direction == 1) col++; // Move right
        else if (direction == 2) row++; // Move down
        else if (direction == 3) col--; // Move left
        else throw new IllegalArgumentException("Illegal value for tokenSpace");

        return getBoardSpace(row, col);
    }

    // Gets the adjacent space that the token is on.
    //  Returns null if token is on the edge
    private BoardSpace getNextSpace(Token token) {
        return getNextSpace(token.getBoardSpace(), token.getTokenSpace());
    }

    // Moves a token from a board space to its adjacent board space.
    //  Assumes the token is not on the edge
    private void transferToken(Token token) {

        int nextTokenSpace = token.findNextTokenSpace();
        BoardSpace nextSpace = getNextSpace(token);
        token.moveToken(nextSpace, nextTokenSpace);
    }

    private void advanceToEnd(Token token){
        BoardSpace curSpace = token.getBoardSpace();

        while (curSpace.hasTile()){
            // Move the token to the other side of the tile
            curSpace.advanceToken(token);

            // Check before we transfer
            if (isOnEdge(token)) {
                break;
            }
            transferToken(token);
            curSpace = token.getBoardSpace();
        }
    }

    // Returns true if the row and col pair are a valid address in the board
    public static boolean isValidCoordinate(int row, int col) {
        return (0 <= row && row < BOARD_LENGTH) && (0 <= col && col < BOARD_LENGTH);
    }

    private static boolean isOnEdge(int row, int col, int tokenSpace) {
        if (!isValidCoordinate(row, col))
            throw new IllegalArgumentException("Invalid Tile Access");

        int direction = tokenSpace / 2;
        boolean topEdge    = row == 0 && direction == 0;
        boolean rightEdge  = col == 5 && direction == 1;
        boolean bottomEdge = row == 5 && direction == 2;
        boolean leftEdge   = col == 0 && direction == 3;

        return topEdge || rightEdge || bottomEdge || leftEdge;
    }

    private static boolean isOnEdge(Token token) {

        int row = token.getBoardSpace().getRow();
        int col = token.getBoardSpace().getCol();
        int tokenSpace = token.getTokenSpace();
        return isOnEdge(row, col, tokenSpace);
    }


    //================================================================================
    // XML Parsing
    //================================================================================

    @Override
    public Element toXML(Document document) {
        Element boardElement = document.createElement("board");
        Element tileMap = document.createElement("map");
        Element pawnMap = document.createElement("map");

        /* List of tiles */
        for (int i = 0; i < BOARD_LENGTH; i++){
            for (int j = 0; j < BOARD_LENGTH; j++){
                if (hasTile(i, j)){
                    tileMap.appendChild(getBoardSpace(i, j).toXML(document));
                }

                for (Token token : getBoardSpace(i,j).getTokensOnSpace()) {
                    pawnMap.appendChild(token.toXML(document));
                }
            }
        }

        boardElement.appendChild(tileMap);
        boardElement.appendChild(pawnMap);
        return boardElement;
    }

    @Override
    public void fromXML(Element xmlElement) {
        // Input checking
        if (!xmlElement.getTagName().equals("board"))
            throw new ParserException("Board.fromXml() called on non-board element: " + xmlElement.getTagName());

        Node tilesElement = xmlElement.getFirstChild();
        Node pawnsElement = tilesElement.getNextSibling();

        // Update the tiles
        for (Node tileEntElement = tilesElement.getFirstChild();
                tileEntElement != null;
                tileEntElement = tileEntElement.getNextSibling()) {
            Node xyNode = tileEntElement.getFirstChild();
            Node xNode = xyNode.getFirstChild();
            Node yNode = xNode.getNextSibling();
            int x = Integer.valueOf(xNode.getTextContent());
            int y = Integer.valueOf(yNode.getTextContent());

            Tile toPlace = new Tile();
            toPlace.fromXML((Element) xyNode.getNextSibling());

            this.getBoardSpace(y, x).setTile(toPlace);
        }

        // Update the pawns
        for (Node pawnEntNode = pawnsElement.getFirstChild();
                pawnEntNode != null;
                pawnEntNode = pawnEntNode.getNextSibling()) {
            Node colorNode = pawnEntNode.getFirstChild();
            Node pawnLocNode = colorNode.getNextSibling();

            Color color = Color.fromXML((Element) colorNode);

            Node hvNode = pawnLocNode.getFirstChild();
            boolean isHorizontal = hvNode.getNodeName().equals("h");

            Node coord1Node = hvNode.getNextSibling();
            Node coord2Node = coord1Node.getNextSibling();
            int coord1 = Integer.valueOf(coord1Node.getTextContent());
            int coord2 = Integer.valueOf(coord2Node.getTextContent());

            Pair<BoardSpace, Integer> location =
                    Token.locationFromPawnLoc(this, isHorizontal, coord1, coord2);

            // Creates a token that places itself on the board that
            new Token(location.getKey(), location.getValue(), color);
        }
    }
}
