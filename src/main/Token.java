package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.util.Pair;
import main.Players.APlayer;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Token implements Parsable {

    //================================================================================
    // Instance Variables
    //================================================================================
    private BoardSpace space;
    private APlayer player;

    //================================================================================
    // Constructor
    //================================================================================
    public Token(BoardSpace startingLocation, int startingTokenSpace, APlayer player){
        space = startingLocation;
        this.player = player;
        space.addToken(this, startingTokenSpace);
    }

    //================================================================================
    // Getters
    //================================================================================

    public BoardSpace getBoardSpace(){
        return space;
    }

    public int getTokenSpace(){
        return space.findToken(this);
    }

    public APlayer getPlayer(){
        return player;
    }



    //================================================================================
    // Public Methods
    //================================================================================

    // Removes the token from the board altogether
    //   Should only be called when a player loses
    public void removeFromBoard(){
        space.removeToken(this);
        space = null;
    }

    // Places the token at the given location
    public void moveToken(BoardSpace boardSpace, int tokenSpace){
        space.removeToken(this);
        boardSpace.addToken(this, tokenSpace);
        space = boardSpace;
    }

    // Gets the tokenSpace on the adjacent tile bordering
    public int findNextTokenSpace(){
        int tokenSpace = getTokenSpace();
        return getMirroredTokenSpace(tokenSpace);
    }

    public static int getMirroredTokenSpace(int tokenSpace){
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


    @Override
    public Element toXML(Document document) {
        int tick = getTokenSpace();
        int row = getBoardSpace().getRow();
        int col = getBoardSpace().getCol();

        boolean isVertical = (tick / 2) % 2 == 1;
        Element hvElement;
        int coord1, coord2;
        if (isVertical) {
            hvElement = document.createElement("v");

            coord1 = col;
            boolean isLeft = tick > 5;
            if (!isLeft) coord1 += 1;

            coord2 = 2*row;
            if (tick == 3 || tick == 6) coord2 += 1;
        } else {
            hvElement = document.createElement("h");

            coord1 = row;
            boolean isTop = tick < 2;
            if (!isTop) coord1 += 1;

            coord2 = 2*col;
            if (tick == 1 || tick == 4) coord2 += 1;
        }

        Element coord1Element = document.createElement("n");
        Element coord2Element = document.createElement("n");
        coord1Element.appendChild(document.createTextNode(Integer.toString(coord1)));
        coord2Element.appendChild(document.createTextNode(Integer.toString(coord2)));

        Element pawnLoc = document.createElement("pawn-loc");
        pawnLoc.appendChild(hvElement);
        pawnLoc.appendChild(coord1Element);
        pawnLoc.appendChild(coord2Element);

        Element entryElement = document.createElement("ent");

        entryElement.appendChild(this.player.getColor().toXml(document));
        entryElement.appendChild(pawnLoc);

        return entryElement;
    }

    @Override
    public void fromXML(Element xmlElement) {
        // TODO
    }
}
