package main.Players;

import java.util.Random;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Tile;

/**
 * Created by vyasalwar on 4/30/18.
 */
public class MostSymmetricPlayer extends TileHeuristicPlayer {
    public MostSymmetricPlayer(String name, Color color) {
        super(name, color);
        playerType = PlayerType.MOSTSYMMETRIC;
    }

    // Get random starting location
    public Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return RandomPlayer.getRandomStartingLocation(new Random());
    }

    // Order tiles from least to most symmetric, and choose the first legal rotation among them
    protected int ScoreTile(Tile tile){
        return tile.calculateSymmetries();
    }
}
