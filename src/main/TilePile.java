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

    final private String DEFAULT_FILE_PATH = "tiles.txt";

    private Deque<Tile> tiles;

    public TilePile(){
        tiles = new LinkedList<>();
        fillTiles(DEFAULT_FILE_PATH);
    }

    public TilePile(String filename){
        tiles = new LinkedList<>();
        /*
        for line in file:
            main.Tile tile = main.Tile.generateFromFile(line);
            tiles.addLast(tile);
         */
        fillTiles(filename);

    }

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
