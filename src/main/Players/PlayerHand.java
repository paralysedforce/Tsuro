package main.Players;

import main.Game;
import main.Parsable;
import main.Tile;
import main.TilePile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Represents the current tiles held by an APlayer at any time
 *
 * Created by vyasalwar on 5/15/18.
 */
public class PlayerHand implements Iterable<Tile>, Parsable{

    private final int MAX_TILES_IN_HAND = 3;
    private TilePile deck;
    private List<Tile> hand;

    public PlayerHand(){
        this.deck = Game.getGame().getTilePile();
        this.hand = new ArrayList<>();

        for (int i = 0; i < MAX_TILES_IN_HAND; i++){
            drawFromDeck();
        }
    }

    public Tile getTile(int i){
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
        // Rework it somehow
        Tile tile = deck.drawFromDeck();
        if (tile != null)
            hand.add(tile);
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

    public boolean isValid(){
        if (hand.size() > 3)
            return false;

        for (Tile tile: hand){
            if (Game.getGame().getBoard().findLocationOfTile(tile) != null)
                return false;
        }

        for (int i = 0; i < hand.size(); i++){
            for (int j = i + 1; j < hand.size(); j++ ){
                if (getTile(i).equals(getTile(j)))
                    return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<Tile> iterator() {
        return hand.iterator();
    }

    @Override
    public Element toXML(Document document) {
        return null;
    }

    @Override
    public void fromXML(Element xmlElement) throws IllegalArgumentException {

    }
}
