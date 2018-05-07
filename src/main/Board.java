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
            throw new IllegalArgumentException("Invalid Tile Access");
        return spaces[row][col];
    }

    //================================================================================
    // Public methods
    //================================================================================

    // Returns true if there is a tile on the row and col
    public boolean isOccupied(int row, int col) {
        if (isValidCoordinate(row, col))
            return spaces[row][col].hasTile();

        throw new IllegalArgumentException("Invalid Tile Access");
    }

    // Returns true if placing the tile in front of the token will lead to the player's death
    // Does not actually place the tile
    public boolean willKillPlayer(Tile tile, SPlayer player) {
        Token token = player.getToken();
        BoardSpace curSpace = token.getBoardSpace();
        int tokenSpace = token.getTokenSpace();

        try {
            // Move to the space across the tile
            int nextTokenSpace = tile.findMatch(tokenSpace);
            BoardSpace nextSpace = getNextSpace(curSpace, nextTokenSpace);

            // Trace out a path by moving across spaces with tiles on them
            while (nextSpace.hasTile()){
                nextSpace = getNextSpace(nextSpace, nextTokenSpace);
                nextTokenSpace = nextSpace.getTile().findMatch(nextTokenSpace);
            }
            // We've walked to a place on the board without a tile
            return false;
        }

        catch (IllegalArgumentException e){
            // We've walked to a place off the board since we're trying to
            //   access a space with an invalid coordinate
            return true;
        }
    }

    // Places the tile in front of the player, regardless of whether it will kill the player
    //   Returns the Set of tokens driven off the board
    public Set<Token> placeTile(Tile tile, SPlayer player) {

        BoardSpace space = player.getToken().getBoardSpace();
        space.setTile(tile);

        Set<Token> tokensToMove = space.getTokensOnSpace();
        Set<Token> eliminatedPlayers = new HashSet<>();

        for (Token token: tokensToMove){
            advanceToEnd(token);
            if (isOnEdge(token)) {
                eliminatedPlayers.add(token);
                token.removeFromBoard();
            }
        }

        return eliminatedPlayers;
    }


    //================================================================================
    // Private Helpers
    //================================================================================

    // Gets the adjacent space of an arbitrary board space and token space.
    //  Returns null if the
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
            curSpace.advanceToken(token);
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
}
