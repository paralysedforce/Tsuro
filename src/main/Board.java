package main;

import javafx.util.Pair;

import java.util.*;

/**
 *
 * TODO: Handle two tokens running into each other
 *
 * Represents a Tsuro main.Board
 *
 * Created by vyasalwar on 4/16/18.
 */
public class Board {

    /* Singleton pattern */
    private static Board board;
    final static int BOARD_LENGTH = 6;

    public static Board getBoard(){
        if (board == null) board = new Board();
        return board;
    }

    public static void resetBoard(){
        board = new Board();
    }


    /* Instance variables */
    private BoardSpace[][] spaces;

    /* Constructor */
    private Board() {
        this.spaces = new BoardSpace[BOARD_LENGTH][BOARD_LENGTH];
        for (int i = 0; i < BOARD_LENGTH; i++){
            for (int j = 0; j < BOARD_LENGTH; j++){
                spaces[i][j] = new BoardSpace(i, j);
            }
        }
    }

    /* Methods */
    public BoardSpace getBoardSpace(int row, int col){
        if (!isValidCoordinate(row, col))
            return null;
        return spaces[row][col];
    }

    // Returns true if there is a tile on the row and col
    public boolean isOccupied(int row, int col){
        return isValidCoordinate(row, col) && spaces[row][col].hasTile();
    }


    public boolean willKillPlayer(Tile tile, Token token){

        /*if (!player.hasTile(tile) || player.getToken().getBoardSpace().hasTile())
            return false;

        Token token = player.getToken();*/

        BoardSpace nextSpace = board.getNextSpace(token);
        int nextTokenSpace = token.findNextTokenSpace();

        // Move to the space across the tile
        nextTokenSpace = tile.findMatch(nextTokenSpace);

        // Trace out a path by moving across spaces with tiles on them
        while (nextSpace != null){
            nextSpace = getNextSpace(nextSpace, nextTokenSpace);
            if (!nextSpace.hasTile())
                return true;
            nextTokenSpace = nextSpace.getTile().findMatch(nextTokenSpace);
        }
        return false;
    }

    //
    public Set<Token> placeTile(Tile tile, Token currentPlayerToken){

        BoardSpace space = currentPlayerToken.getBoardSpace();
        space.setTile(tile);

        Deque<BoardSpace> spaces = new LinkedList<>();
        Set<Token> eliminatedPlayers = new HashSet<>();
        spaces.add(space);

        while (spaces.size() > 0){
            BoardSpace curSpace = spaces.removeFirst();
            if (curSpace.hasTile()){
                Set<Token> advancedTokens = curSpace.advanceTokens();

                for (Token token : advancedTokens){
                    if (!isOnEdge(token)) {
                        transferToken(token);
                        spaces.add(token.getBoardSpace());
                    }
                    else {
                        curSpace.removeToken(token);
                        eliminatedPlayers.add(token);
                    }
                }
            }
        }

        return eliminatedPlayers;
    }

    // Places a token on the board at a row, col, and token space.
    //  throws an IllegalArgumentException if a token is attempted to be placed not on an edge
    public void startPlayer(Token token, int row, int col, int tokenSpace){
        if (!isOnEdge(row, col, tokenSpace))
            throw new IllegalArgumentException("Token must start on edge");

        spaces[row][col].addToken(token, tokenSpace);
    }


    // Gets the adjacent space of an arbitrary board space and token space.
    //  Returns null if the
    private BoardSpace getNextSpace(BoardSpace boardSpace, int tokenSpace){
        // TODO: Duplicated code from Token.java: fix somehow
        int row = boardSpace.getRow();
        int col = boardSpace.getCol();
        int direction = tokenSpace / 2;

        if      (direction == 0)    row--; // Move up
        else if (direction == 1)    col++; // Move right
        else if (direction == 2)    row++; // Move down
        else if (direction == 3)    col--; // Move left
        else throw new IllegalArgumentException("Illegal value for tokenSpace");

        if (!isValidCoordinate(row, col))
            return null;
        else
            return spaces[row][col];
    }

    // Gets the adjacent space that the token is on.
    //  Returns null if token is on the edge
    private BoardSpace getNextSpace(Token token){
        return getNextSpace(token.getBoardSpace(), token.getTokenSpace());
    }

    // Moves a token from a board space to its adjacent board space.
    //  Assumes the token is not on the edge
    private void transferToken(Token token) {

        int nextTokenSpace = token.findNextTokenSpace();
        BoardSpace nextSpace = getNextSpace(token);
        BoardSpace oldSpace = token.getBoardSpace();

        // TODO: Refactor Data structures to remove this redundancy
        token.moveToken(nextSpace, nextTokenSpace);
    }

    /* Helpers */

    // Returns true if the row and col pair are a valid address in the board
    private static boolean isValidCoordinate(int row, int col){
        return (0 <= row && row < BOARD_LENGTH) && (0 <= col && col < BOARD_LENGTH);
    }

    private static boolean isOnEdge(int row, int col, int tokenSpace){
        int direction = tokenSpace / 2;
        boolean topEdge    = row == 0 && direction == 0;
        boolean rightEdge  = col == 5 && direction == 1;
        boolean bottomEdge = row == 5 && direction == 2;
        boolean leftEdge   = col == 0 && direction == 3;
        boolean onEdge = topEdge || rightEdge || bottomEdge || leftEdge;

        return isValidCoordinate(row, col) && onEdge;
    }

    private static boolean isOnEdge(Token token){


        int row = token.getBoardSpace().getRow();
        int col = token.getBoardSpace().getCol();
        int tokenSpace = token.getTokenSpace();
        return isOnEdge(row, col, tokenSpace);
    }

    public Pair<BoardSpace, Integer> getRandomStartingLocation(){
        Random random = new Random();
        int row = random.nextInt(6);
        int col = random.nextInt(6);

        while(row != 0 && row != 5 && col != 0 && col != 5){
            row = random.nextInt(6);
            col = random.nextInt(6);
        }

        List<Integer> possibleTokenLocations  = new ArrayList<>();
        if (row == 0 ){
            possibleTokenLocations.add(0);
            possibleTokenLocations.add(1);
        }
        else if (row == 5){
            possibleTokenLocations.add(4);
            possibleTokenLocations.add(5);
        }

        if (col == 0){
            possibleTokenLocations.add(6);
            possibleTokenLocations.add(7);
        }
        else if (col == 5){
            possibleTokenLocations.add(2);
            possibleTokenLocations.add(3);
        }

        int tokenLocation = possibleTokenLocations.get(random.nextInt(possibleTokenLocations.size()));
        return new Pair(spaces[row][col], tokenLocation);

    }
}
