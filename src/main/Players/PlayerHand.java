package main.Players;

import main.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the current tiles held by an APlayer at any time
 *
 * Created by vyasalwar on 5/15/18.
 */
public class PlayerHand implements Iterable<Tile>, Parsable{

    private final int MAX_TILES_IN_HAND = 3;
    private TilePile deck;
    protected List<Tile> hand;

    //================================================================================
    // Constructors
    //================================================================================

    public PlayerHand(){
        this.deck = Game.getGame().getTilePile();
        this.hand = new ArrayList<>();
        for (int i = 0; i < MAX_TILES_IN_HAND; i++){
            drawFromDeck();
        }
    }

    public PlayerHand(Element xmlElement){
        fromXML(xmlElement);
    }

    //================================================================================
    // Public methods
    //================================================================================

    public Tile getTile(int i){

        // Keep the appearance of a 3-element array
        if (0 <= i && i < 3) {
            if (i > hand.size() - 1)
                return null;

            return hand.get(i);
        }

        else
            throw new IndexOutOfBoundsException("Illegal Hand Access");
    }

    public void drawFromDeck(){
        if (isFull() || deck.isEmpty())
            return;


        // The reason the code has this check is for the benefit of testing
        // TODO: Rework it somehow
        Tile tile = deck.drawFromDeck();
        if (tile != null)
            hand.add(tile);

        /*if (!isValid())
            throw new ContractException(ContractViolation.POSTCONDITION,
                    "Invalid hand after drawing");*/
    }

    public void returnTilesToDeck(){
        for (Tile tile: hand) {
            deck.returnToDeck(tile);
        }

        hand = new ArrayList<>();
    }

    public void removeTile(Tile tile){
        if (!holdsTile(tile))
            throw new IllegalArgumentException("Tile not found in hand");

        hand.remove(tile);
    }

    public boolean isFull(){
        return hand.size() == MAX_TILES_IN_HAND;
    }

    public boolean isEmpty(){
        return hand.size() == 0;
    }

    public boolean holdsTile(Tile tile){
        return hand.contains(tile);
    }


    // Enforces correctness on a tile
    public boolean isValid(){

        // Hand is not too large
        if (hand.size() > 3)
            return false;

        // No tile in the hand is on the board
        for (Tile tile: hand){
            if (Game.getGame().getBoard().findLocationOfTile(tile) != null)
                return false;
        }

        // All tiles are distinct
        for (int i = 0; i < hand.size(); i++){
            for (int j = i + 1; j < hand.size(); j++ ){
                if (getTile(i).equals(getTile(j)))
                    return false;
            }
        }

        return true;
    }

    public void setDeck(TilePile deck){
        this.deck = deck;
    }

    @Override
    public Iterator<Tile> iterator() {
        return hand.iterator();
    }

    //================================================================================
    // XML Parsing
    //================================================================================
    @Override
    public Element toXML(Document document) {
        Element setElement = document.createElement("set");
        for (Tile tile: hand){
            setElement.appendChild(tile.toXML(document));
        }
        return setElement;
    }

    @Override
    public void fromXML(Element xmlElement) throws IllegalArgumentException {

        this.hand = new ArrayList<>();

        for (Node tileXml = xmlElement.getFirstChild();
             tileXml != null;
             tileXml = tileXml.getNextSibling()) {

            Tile t = new Tile();
            t.fromXML((Element) tileXml);
            this.hand.add(t);
        }
    }
}
