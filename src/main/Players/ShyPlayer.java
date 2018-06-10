package main.Players;

import javafx.util.Pair;
import main.*;

import java.util.Random;

/**
 * Created by vyasalwar on 6/5/18.
 */
public class ShyPlayer extends TileHeuristicPlayer {

    private Random random;

    public ShyPlayer(String name, Color color) {
        super(name, color);
        random = new Random();
    }

    @Override
    protected int ScoreTile(Tile tile) {
        return random.nextInt();
    }

    @Override
    protected Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return RandomPlayer.getRandomStartingLocation(new Random(), board);
    }
}
