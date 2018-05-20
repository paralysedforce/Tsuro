package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A collection of all Tiles in Tsuro
 *
 * Created by vyasalwar on 4/16/18.
 */
public class TilePile {

    //================================================================================
    // Singleton Design Pattern
    //================================================================================

    final private String DEFAULT_FILE_PATH = "tiles.txt";

    public TilePile(){
        tiles = new LinkedList<>();
        fillTiles(DEFAULT_FILE_PATH);
    }

    public TilePile(String filename){
        tiles = new LinkedList<>();
        fillTiles(filename);
    }

    //=====b===========================================================================
    // Instance Variables
    //================================================================================
    private Deque<Tile> tiles;


    //================================================================================
    // Public methods
    //================================================================================
    public Tile drawFromDeck(){
        try {
            return tiles.removeFirst();
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public void shuffleDeck(){
        List<Tile> tileList = new ArrayList<Tile>(tiles);
        Collections.shuffle(tileList);
        tiles = new LinkedList<>(tileList);
    }

    public void shuffleDeck(int seed){
        List<Tile> tileList = new ArrayList<Tile>(tiles);
        Collections.shuffle(tileList, new Random(seed));
        tiles = new LinkedList<>(tileList);
    }
    public void returnToDeck(Tile tile){
        tiles.addLast(tile);
    }

    public boolean isEmpty(){
        return tiles.isEmpty();
    }

    public int getCount() {
        return tiles.size();
    }

    //================================================================================
    // Private helpers
    //================================================================================

    // Initialize the TilePile using the tiles specified in the given file
    private void fillTiles(String filename) {
        try {
            File file = new File(filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while((line = bufferedReader.readLine()) != null){
                Tile newTile = new Tile(line);
                tiles.addLast(newTile);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
