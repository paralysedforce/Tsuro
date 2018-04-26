package main;

import javafx.util.Pair;

import java.util.*;

/**
 *
 * Represents a Tsuro Board
 *
 * Created by vyasalwar on 4/16/18.
 */
public class Board {

    //================================================================================
    // Singleton Pattern
    //================================================================================
    private static Board board;

    private Board() {
        this.spaces = new BoardSpace[BOARD_LENGTH][BOARD_LENGTH];
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                spaces[i][j] = new BoardSpace(i, j);
            }
        }
    }

    public static Board getBoard() {
        if (board == null) board = new Board();
        return board;
    }

    public static void resetBoard() {
        board = new Board();
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
            return null;
        return spaces[row][col];
    }

    //================================================================================
    // Public methods
    //================================================================================

    // Returns true if there is a tile on the row and col
    public boolean isOccupied(int row, int col) {
        return isValidCoordinate(row, col) && spaces[row][col].hasTile();
    }

    // Returns true if placing the tile in front of the token will lead to the player's death
    public boolean willKillPlayer(Tile tile, Token token) {

        /*if (!player.hasTile(tile) || player.getToken().getBoardSpace().hasTile())
            return false;

        Token token = player.getToken();*/

        BoardSpace curSpace = token.getBoardSpace();
        int tokenSpace = token.getTokenSpace();

        // Move to the space across the tile
        int nextTokenSpace = tile.findMatch(tokenSpace);
        BoardSpace nextSpace = board.getNextSpace(curSpace, nextTokenSpace);

        // Trace out a path by moving across spaces with tiles on them
        while (nextSpace != null) {
            if (!nextSpace.hasTile())
                return false;
            nextSpace = getNextSpace(nextSpace, nextTokenSpace);
            nextTokenSpace = nextSpace.getTile().findMatch(nextTokenSpace);
        }
        return true;
    }

    // Places the tile in front of the player, regardless of whether it will kill the player
    //   Returns the Set of tokens driven off the board
    public Set<Token> placeTile(Tile tile, Token currentPlayerToken) {

        BoardSpace space = currentPlayerToken.getBoardSpace();
        space.setTile(tile);

        Deque<BoardSpace> spaces = new LinkedList<>();
        Set<Token> eliminatedPlayers = new HashSet<>();
        spaces.add(space);

        while (spaces.size() > 0) {
            BoardSpace curSpace = spaces.removeFirst();
            if (curSpace.hasTile()) {
                Set<Token> advancedTokens = curSpace.advanceTokens();

                for (Token token : advancedTokens) {
                    if (!isOnEdge(token)) {
                        transferToken(token);
                        spaces.add(token.getBoardSpace());
                    } else {
                        token.removeFromBoard();
                        eliminatedPlayers.add(token);
                    }
                }
            }
        }

        return eliminatedPlayers;
    }

    // Places a token on the board at a row, col, and token space.
    //  throws an IllegalArgumentException if a token is attempted to be placed not on an edge
    public void startPlayer(Token token, int row, int col, int tokenSpace) {
        if (!isOnEdge(row, col, tokenSpace))
            throw new IllegalArgumentException("Token must start on edge");

        spaces[row][col].addToken(token, tokenSpace);
    }

    //================================================================================
    // Private Helpers
    //================================================================================

    // Gets the adjacent space of an arbitrary board space and token space.
    //  Returns null if the
    private BoardSpace getNextSpace(BoardSpace boardSpace, int tokenSpace) {
        // TODO: Duplicated code from Token.java: fix somehow
        int row = boardSpace.getRow();
        int col = boardSpace.getCol();
        int direction = tokenSpace / 2;

        if (direction == 0) row--; // Move up
        else if (direction == 1) col++; // Move right
        else if (direction == 2) row++; // Move down
        else if (direction == 3) col--; // Move left
        else throw new IllegalArgumentException("Illegal value for tokenSpace");

        if (!isValidCoordinate(row, col))
            return null;
        else
            return spaces[row][col];
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

    // Returns true if the row and col pair are a valid address in the board
    private static boolean isValidCoordinate(int row, int col) {
        return (0 <= row && row < BOARD_LENGTH) && (0 <= col && col < BOARD_LENGTH);
    }

    private static boolean isOnEdge(int row, int col, int tokenSpace) {
        int direction = tokenSpace / 2;
        boolean topEdge = row == 0 && direction == 0;
        boolean rightEdge = col == 5 && direction == 1;
        boolean bottomEdge = row == 5 && direction == 2;
        boolean leftEdge = col == 0 && direction == 3;
        boolean onEdge = topEdge || rightEdge || bottomEdge || leftEdge;

        return isValidCoordinate(row, col) && onEdge;
    }

    private static boolean isOnEdge(Token token) {


        int row = token.getBoardSpace().getRow();
        int col = token.getBoardSpace().getCol();
        int tokenSpace = token.getTokenSpace();
        return isOnEdge(row, col, tokenSpace);
    }
}
