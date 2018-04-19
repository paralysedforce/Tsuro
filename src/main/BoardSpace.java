package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class BoardSpace {
    private final int NUM_SPACES = 8;

    private Tile tile;
    private Token[] tokenSpaces;
    public int row;
    private int col;

    public BoardSpace(int row, int col){
        tile = null;
        tokenSpaces = new Token[NUM_SPACES];
        this.row = row;
        this.col = col;
    }

    public boolean hasTile(){
        return tile != null;
    }

    public Tile getTile(){
        return tile;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public void setTile(Tile tile){
        if (!hasTile())
            this.tile = tile;
        else
            throw new IllegalArgumentException("main.BoardSpace already occupied");
    }

    public List<Token> advanceTokens(){
        List<Token> tokensAdvanced = new ArrayList<>();
        for (int i = 0; i < NUM_SPACES; i++){
            Token token = tokenSpaces[i];
            if (token != null){
                int nextTokenLocation = tile.findMatch(i);

                tokensAdvanced.add(token);
                tokenSpaces[nextTokenLocation] = token;
                token.setTokenSpace(nextTokenLocation);
                tokenSpaces[i] = null;
            }
        }
        return tokensAdvanced;
    }

    public Token removeToken(int tokenSpace){
        Token token = tokenSpaces[tokenSpace];
        if (token != null){
            tokenSpaces[tokenSpace] = null;
        }
        return token;
    }

    public void addToken(Token token, int tokenSpace){
        tokenSpaces[tokenSpace] = token;
    }

    public List<Token> getTokensOnSpace(){
        List<Token> tokensOnSpace = new LinkedList<>();
        for (int i = 0; i < NUM_SPACES; i++){
            if (tokenSpaces[i] != null)
                tokensOnSpace.add(tokenSpaces[i]);
        }
        return tokensOnSpace;
    }

    @Override
    public int hashCode(){
        return tile.hashCode();
    }
}
