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
    final int BOARD_LENGTH = 6;
    private BoardSpace[][] spaces;

    public Board() {
        this.spaces = new BoardSpace[BOARD_LENGTH][BOARD_LENGTH];
        for (int i = 0; i < BOARD_LENGTH; i++){
            for (int j = 0; j < BOARD_LENGTH; j++){
                spaces[i][j] = new BoardSpace(i, j);
            }
        }
    }

    public boolean isOccupied(int row, int col){
        return  (0 <= row && row < BOARD_LENGTH) &&
                (0 <= col && col < BOARD_LENGTH) &&
                spaces[row][col].hasTile();
    }

    public boolean isLegalMove(Tile tile, SPlayer player){
        BoardSpace curSpace = player.getBoardSpace();
        int tokenSpace = player.getTokenSpace();

        if (!player.hasTile(tile))
            return false;
        if (curSpace.hasTile())
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

    public List<SPlayer> placeTile(Tile tile, SPlayer player){
        BoardSpace space = player.getBoardSpace();
        space.setTile(tile);

        Deque<BoardSpace> spaces = new LinkedList<>();
        List<SPlayer> eliminatedPlayers = new ArrayList<>();
        spaces.add(space);

        while (spaces.size() > 0){
            BoardSpace curSpace = spaces.removeFirst();
            if (curSpace.hasTile()){
                // transfer tokens if possible
                // if not possible, determine if token will be transferred off edge
                //      if so, this is a failed player
                // if it is possible, add boardspaces that all tokens have been transferred onto to the queue

                curSpace.advanceTokens();

                for(int i = 0; i < 8; i++) {
                    Token token = curSpace.removeToken(i);
                    if (token != null) {
                        BoardSpace nextSpace = getNextSpace(token.getBoardSpace(), i);
                        if (nextSpace != null) {
                            transferToken(nextSpace, i, token);
                            spaces.add(nextSpace);
                        } else {
                            eliminatedPlayers.add(token.getPlayer());
                        }
                    }
                }

            }
        }
        player.removeTileFromBank(tile);
        return eliminatedPlayers;
    }

    private BoardSpace getNextSpace(BoardSpace boardSpace, int tokenSpace){
        int row = boardSpace.getRow();
        int col = boardSpace.getCol();

        Pair<Integer, Integer> newCoordinates = nextSpace(row, col, tokenSpace);
        row = newCoordinates.getKey();
        col = newCoordinates.getValue();

        try {
            return spaces[row][col];
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    private void transferToken(BoardSpace nextSpace, int tokenSpace, Token token){
        int nextTokenSpace = findNextTokenSpace(tokenSpace);
        nextSpace.addToken(token, nextTokenSpace);
        token.setBoardSpace(nextSpace, nextTokenSpace);
    }

    private static boolean willTransferOffBoard(BoardSpace boardSpace, int tokenSpace){
        Pair<Integer, Integer> nextLocationPair = nextSpace(boardSpace.getRow(), boardSpace.getCol(), tokenSpace);

        int nextRow = nextLocationPair.getKey();
        int nextCol = nextLocationPair.getValue();

        return (nextRow > 5 || nextCol > 5 || nextRow < 0 || nextCol < 0);
    }

    private static Pair<Integer, Integer> nextSpace(int row, int col, int tokenSpace){
        int nextRow = row;
        int nextCol = col;

        switch (tokenSpace){
            case 0:
            case 1:
                nextRow--;
                break;
            case 2:
            case 3:
                nextCol++;
                break;
            case 4:
            case 5:
                nextRow++;
                break;
            case 6:
            case 7:
                nextCol--;
                break;
        }

        return new Pair(nextRow, nextCol);
    }

    private static int findNextTokenSpace(int tokenSpace){
        int nextTokenSpace = 0;

        switch (tokenSpace){
            case 0:
            case 1:
            case 4:
            case 5:
                nextTokenSpace = 5 - tokenSpace;
                break;
            case 2:
            case 3:
            case 6:
            case 7:
                nextTokenSpace = 9 - tokenSpace;
                break;
        }
        return nextTokenSpace;
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
        //intellij cant make up its mind about whether we need the types on this pair
        return new Pair<BoardSpace, Integer>(spaces[row][col], tokenLocation);

    }



}
