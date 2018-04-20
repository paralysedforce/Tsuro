<<<<<<< HEAD:src/main/TilePile.java
package main;

=======
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
>>>>>>> f9e2c1ce2c1050aa3dc7ab635ada47de5b696689:src/TilePile.java
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
<<<<<<< HEAD:src/main/TilePile.java
        /*
        for line in file:
            main.Tile tile = main.Tile.generateFromFile(line);
            tiles.addLast(tile);
         */
=======
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
>>>>>>> f9e2c1ce2c1050aa3dc7ab635ada47de5b696689:src/TilePile.java
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
