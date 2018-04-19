package main;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A collection of all Tiles in Tsuro
 *
 * Created by vyasalwar on 4/16/18.
 */
public class TilePile {

    private Deque<Tile> tiles;

    public TilePile(){
        tiles = new LinkedList<>();
    }

    public TilePile(String filename){
        tiles = new LinkedList<>();
        /*
        for line in file:
            main.Tile tile = main.Tile.generateFromFile(line);
            tiles.addLast(tile);
         */
    }

    public Tile drawFromDeck(){
        try {
            return tiles.removeFirst();
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("main.TilePile is empty");
        }
    }

    public void returnToDeck(Tile tile){
        tiles.addLast(tile);
    }
}
