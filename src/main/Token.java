package main;

import javafx.util.Pair;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Token {

    private BoardSpace space;
    private SPlayer player;

    public Token(BoardSpace startingLocation, int startingTokenSpace, SPlayer player){
        space = startingLocation;
        this.player = player;

        space.addToken(this, startingTokenSpace);
    }

    public void setBoardSpace(BoardSpace location, int tokenSpace){
        space = location;
    }

    public BoardSpace getBoardSpace(){
        return space;
    }

    public int getTokenSpace(){
        return space.findToken(this);
    }

    public SPlayer getPlayer(){
        return player;
    }

    public Pair<Integer, Integer> nextCoordinate(){
        int nextRow = space.getRow();
        int nextCol = space.getCol();
        int direction = getTokenSpace() / 2;

        switch (direction){
            case 0: // Top of space
                nextRow--;
                break;
            case 1: // Right of space
                nextCol++;
                break;
            case 2: // Bottom of space
                nextRow++;
                break;
            case 3: // Left of space
                nextCol--;
                break;
        }

        return new Pair(nextRow, nextCol);
    }

    public int findNextTokenSpace(){
        int tokenSpace = getTokenSpace();

        switch (tokenSpace){
            case 0: return 5;
            case 1: return 4;
            case 2: return 7;
            case 3: return 6;
            case 4: return 1;
            case 5: return 0;
            case 6: return 3;
            case 7: return 2;
        }
        throw new IllegalArgumentException("Invalid tokenSpace");
    }

    public boolean isOnEdge(){
        Pair<Integer, Integer> pair = nextCoordinate();
        return pair.getKey() < 0 || pair.getKey() > 5 || pair.getValue() < 0 || pair.getValue() > 5;
    }
}
