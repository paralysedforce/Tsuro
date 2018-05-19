package main;

//import org.omg.PortableServer.THREAD_POLICY_ID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 *
 * Created by vyasalwar on 4/16/18.
 */
public class BoardSpace implements Parsable{

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

        if (!Board.isValidCoordinate(row, col)){
            throw new IllegalArgumentException("Invalid Tile Access");
        }
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
        return new HashSet<>(tokenSpaces.keySet());
    }

    //================================================================================
    // Setters
    //================================================================================

    public void setTile(Tile tile){
        if (!hasTile())
            this.tile = tile;
        else
            throw new IllegalArgumentException("BoardSpace already occupied");
    }

    //================================================================================
    // Public Methods
    //================================================================================

    public boolean hasTile(){
        return tile != null;
    }

    // Move all tokens on the tile to their opposite endpoints
    public void advanceTokens(){
        Set<Token> tokensOnSpace = getTokensOnSpace();
        for (Token token: tokensOnSpace){
            advanceToken(token);
        }
    }

    // Move
    public void advanceToken(Token token){
        if (hasTile()) {
            int endpoint = findToken(token);
            tokenSpaces.replace(token, tile.findMatch(endpoint));
        }
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
        if (0 <= tokenSpace && tokenSpace < 8)
            tokenSpaces.put(token, tokenSpace);

        else
            throw new IllegalArgumentException("Invalid token space");
    }


    @Override
    public Element toXML(Document document) {
        if (!hasTile())
            throw new ContractException("toXML called on a Boardspace without a tile");

        Element entryElement = document.createElement("ent");


        Element xyElement = document.createElement("xy");
        Element xElement = document.createElement("x");
        Element yElement = document.createElement("y");

        Element nRowElement = document.createElement("n");
        nRowElement.appendChild(document.createTextNode(Integer.toString(row)));
        xElement.appendChild(nRowElement);
        Element nColElement = document.createElement("n");
        nColElement.appendChild(document.createTextNode(Integer.toString(col)));
        yElement.appendChild(nColElement);
        xyElement.appendChild(xElement);
        xyElement.appendChild(yElement);

        Element tileElement = tile.toXML(document);

        entryElement.appendChild(xyElement);
        entryElement.appendChild(tileElement);

        return entryElement;
    }

    @Override
    public void fromXML(Element xmlElement) {

    }
}
