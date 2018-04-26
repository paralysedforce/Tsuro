package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

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
    private static TilePile tilePile;

    private TilePile(){
        tiles = new LinkedList<>();
        fillTiles(DEFAULT_FILE_PATH);
    }

    private TilePile(String filename){
        tiles = new LinkedList<>();
        fillTiles(filename);
    }

    public static TilePile getTilePile(String filename){
        if (tilePile == null)
            tilePile = new TilePile(filename);
        return tilePile;
    }

    public static TilePile getTilePile(){
        if (tilePile == null){
            tilePile = new TilePile();
        }
        return tilePile;
    }

    public static void resetTilePile() {
        tilePile = new TilePile();
    }

    //================================================================================
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

    public void returnToDeck(Tile tile){
        tiles.addLast(tile);
    }

    public boolean isEmpty(){
        return tiles.isEmpty();
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
