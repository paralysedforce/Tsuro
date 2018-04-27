package main;

/**
 * Created by vyasalwar on 4/27/18.
 */
public interface IPlayer {
    public String getColor(); // Change to enum
    public String getName();
    public boolean placeToken(); // rename to startToken?
    public Tile chooseTile();
    public boolean endGame();
}
