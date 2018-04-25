package main;

import java.util.*;

/**
 * TODO: Decide what to return in advance if !hasTile()
 * Created by vyasalwar on 4/16/18.
 */
public class BoardSpace {
    private final int NUM_SPACES = 8;

    private Tile tile;
    private Map<Token, Integer> tokenSpaces;
    private int row;
    private int col;

    public BoardSpace(int row, int col){
        tile = null;
        tokenSpaces = new HashMap<>();
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

    public int findToken(Token token){
        return tokenSpaces.getOrDefault(token, -1);
    }

    public int removeToken(Token token){
        return tokenSpaces.remove(token);
    }

    public void addToken(Token token, int tokenSpace){
        tokenSpaces.put(token, tokenSpace);
    }

    //currently only using this function during testing so mayb should remove it?
    //could also keep it just for testing if it is important for that
    public boolean willCollide(Token token){
        if (!hasTile())
            return false;

        int tokenEndpoint = findToken(token);
        int matching_endpoint = tile.findMatch(tokenEndpoint);
        for (int endpoint : tokenSpaces.values()){
            if (endpoint == matching_endpoint)
                return true;
        }

        return false;
    }

    public Set<Token> getTokensOnSpace(){
        return tokenSpaces.keySet();
    }
}
