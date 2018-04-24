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

    public void updateBoardSpace(BoardSpace location){
        if (space != null)
            space.removeToken(this);
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

    public void moveToken(BoardSpace boardSpace, int tokenSpace){
        space.removeToken(this);
        boardSpace.addToken(this, tokenSpace);
        space = boardSpace;
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

}
