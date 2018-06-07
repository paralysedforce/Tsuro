package main;

//import org.omg.PortableServer.THREAD_POLICY_ID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class BoardSpace implements Parsable {

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
    public BoardSpace(int row, int col) {
        tile = null;
        tokenSpaces = new HashMap<>();
        this.row = row;
        this.col = col;
    }

    public BoardSpace(Element xmlElement){
        fromXML(xmlElement);
    }


    //================================================================================
    // Getters
    //================================================================================

    public Tile getTile() {
        return tile;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Set<Token> getTokensOnSpace() {
        return new HashSet<>(tokenSpaces.keySet());
    }

    public Set<Integer> getOccupiedSpaces() {
        return new HashSet<>(this.tokenSpaces.values());
    }

    //================================================================================
    // Setters
    //================================================================================

    public void setTile(Tile tile) {
        if (!hasTile())
            this.tile = tile;
        else
            throw new IllegalArgumentException("BoardSpace already occupied");
    }

    //================================================================================
    // Public Methods
    //================================================================================

    public boolean hasTile() {
        return tile != null;
    }

    // Move all living tokens on the tile to their opposite endpoints
    public void advanceTokens() {
        Set<Token> tokensOnSpace = getTokensOnSpace();
        for (Token token : tokensOnSpace) {
            advanceToken(token);
        }
    }

    public void advanceToken(Token token) {
        if (token.isAlive() && hasTile()) {
            int endpoint = findToken(token);
            tokenSpaces.replace(token, tile.findMatch(endpoint));
        }
    }

    // Returns the token space the the token is on.
    //   If the token is not on the space, return -1
    public int findToken(Token token) {
        return tokenSpaces.getOrDefault(token, -1);
    }

    public int removeToken(Token token) {
        return tokenSpaces.remove(token);
    }

    public void addToken(Token token, int tokenSpace) {
        if (0 <= tokenSpace && tokenSpace < 8)
            tokenSpaces.put(token, tokenSpace);

        else
            throw new IllegalArgumentException("Invalid token space");
    }

    @Override
    public boolean equals(Object object) {
        return
                object instanceof BoardSpace
                        && this.row == ((BoardSpace) object).row
                        && this.col == ((BoardSpace) object).col
                        && this.tokenSpaces.equals(((BoardSpace) object).tokenSpaces)
                        && (this.tile == null && ((BoardSpace) object).tile == null
                            || this.tile.equals(((BoardSpace) object).tile));

    }

    //================================================================================
    // XML Parsing
    //================================================================================

    @Override
    public Element toXML(Document document) {
        if (!hasTile())
            throw new ContractException(ContractViolation.PRECONDITION,
                    "toXML called on a Boardspace without a tile");

        Element entryElement = document.createElement("ent");

        Element xyElement = document.createElement("xy");
        Element xElement = document.createElement("x");
        Element yElement = document.createElement("y");

        yElement.appendChild(document.createTextNode(Integer.toString(row)));
        xElement.appendChild(document.createTextNode(Integer.toString(col)));

        xyElement.appendChild(xElement);
        xyElement.appendChild(yElement);

        Element tileElement = tile.toXML(document);

        entryElement.appendChild(xyElement);
        entryElement.appendChild(tileElement);

        return entryElement;
    }

    // TODO
    @Override
    public void fromXML(Element xmlElement) {

    }
}
