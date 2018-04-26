package main;

import java.util.*;

/**
 *
 * Created by vyasalwar on 4/16/18.
 */
public class BoardSpace {

    private final int NUM_SPACES = 8;

    //================================================================================
    // Instance variables
    //================================================================================
    private Tile tile;
    private Map<Token, Integer> tokenSpaces;
    private int row;
    private int col;

    //================================================================================
    // Constructors
    //================================================================================
    public BoardSpace(int row, int col){
        tile = null;
        tokenSpaces = new HashMap<>();
        this.row = row;
        this.col = col;
    }


    //================================================================================
    // Getters
    //================================================================================

    public Tile getTile(){
        return tile;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public Set<Token> getTokensOnSpace(){
        return tokenSpaces.keySet();
    }

    //================================================================================
    // Setters
    //================================================================================

    public void setTile(Tile tile){
        if (!hasTile())
            this.tile = tile;
        else
            throw new IllegalArgumentException("main.BoardSpace already occupied");
    }

    //================================================================================
    // Public Methods
    //================================================================================

    public boolean hasTile(){
        return tile != null;
    }

    // Move all tokens on the tile to their opposite endpoints
    public Set<Token> advanceTokens(){
        Set<Token> tokensAdvanced = new HashSet<>();

        Map <Token, Integer> advancedTokenSpaces = new HashMap<>();
        for (Map.Entry<Token, Integer> pair: tokenSpaces.entrySet()){
            Token token = pair.getKey();
            int newTokenSpace = tile.findMatch(pair.getValue());

            tokensAdvanced.add(token);
            advancedTokenSpaces.put(token, newTokenSpace);
        }
        this.tokenSpaces = advancedTokenSpaces;
        return tokensAdvanced;
    }

    // Returns the token space the the token is on.
    //   If the token is not on the space, return -1
    public int findToken(Token token){
        return tokenSpaces.getOrDefault(token, -1);
    }

    public int removeToken(Token token){
        return tokenSpaces.remove(token);
    }

    public void addToken(Token token, int tokenSpace){
        tokenSpaces.put(token, tokenSpace);
    }


}
