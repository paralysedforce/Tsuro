package main;

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

    public Board getBoard(){
        if (board == null) board = new Board();
        return board;
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

    public boolean isOccupied(int row, int col){
        return isValidCoordinate(row, col) && spaces[row][col].hasTile();
    }

    // TODO: Fix isLegalMove without modifying the state of the board
    public static boolean isLegalMove(Tile tile, SPlayer player){
        Token token = player.getToken();

        BoardSpace curSpace = token.getBoardSpace();
        int tokenSpace = token.getTokenSpace();

        if (!player.hasTile(tile) || curSpace.hasTile())
            return false;

        int nextTokenSpace = tile.findMatch(tokenSpace);
        BoardSpace nextBoardSpace = getNextSpace(curSpace, nextTokenSpace);
        nextTokenSpace = findNextTokenSpace(nextTokenSpace);
        while (nextBoardSpace != null){

            if (!nextBoardSpace.hasTile())
                return true;

            Tile curTile = nextBoardSpace.getTile();
            nextTokenSpace = curTile.findMatch(nextTokenSpace);
            nextBoardSpace = getNextSpace(nextBoardSpace, nextTokenSpace);
            nextTokenSpace = findNextTokenSpace(nextTokenSpace);
        }
        return false;
    }

    public Set<SPlayer> placeTile(Tile tile, SPlayer player){

        BoardSpace space = player.getToken().getBoardSpace();
        space.setTile(tile);

        Deque<BoardSpace> spaces = new LinkedList<>();
        Set<SPlayer> eliminatedPlayers = new HashSet<>();
        spaces.add(space);

        while (spaces.size() > 0){
            BoardSpace curSpace = spaces.removeFirst();
            if (curSpace.hasTile()){
                Set<Token> advancedTokens = curSpace.advanceTokens();

                for (Token token : advancedTokens){
                    if (!token.isOnEdge()) {
                        transferToken(token);
                        spaces.add(token.getBoardSpace());
                    }
                    else {
                        curSpace.removeToken(token);
                        eliminatedPlayers.add(token.getPlayer());
                    }
                }
            }
        }

        player.removeTileFromBank(tile);
        return eliminatedPlayers;
    }


    private BoardSpace getNextSpace(Token token){
        if (token.isOnEdge())
            return null;

        Pair<Integer, Integer> newCoordinates = token.nextCoordinate();
        int row = newCoordinates.getKey();
        int col = newCoordinates.getValue();

        return spaces[row][col];
    }

    private void transferToken(Token token) {

        int nextTokenSpace = token.findNextTokenSpace();
        BoardSpace nextSpace = getNextSpace(token);
        BoardSpace oldSpace = token.getBoardSpace();

        // TODO: Refactor to remove this redundancy
        nextSpace.addToken(token, nextTokenSpace);
        oldSpace.removeToken(token);
        token.setBoardSpace(nextSpace, nextTokenSpace);
    }


    private static boolean isValidCoordinate(int row, int col){
        return (0 <= row && row < BOARD_LENGTH) && (0 <= col && col < BOARD_LENGTH);
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
