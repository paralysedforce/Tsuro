package main.Players;

import javafx.util.Pair;
import main.*;

import java.util.Random;

/**
 * Created by vyasalwar on 6/5/18.
 */
public class ShyPlayer extends TileHeuristicPlayer {
    public ShyPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    protected int ScoreTile(Tile tile) {
        return 0;
    }

    @Override
    protected Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return RandomPlayer.getRandomStartingLocation(new Random(), board);
        /*
        if (color == turnOrder.get(0)){
            return RandomPlayer.getRandomStartingLocation(new Random(), board);
        }

        else {
        }*/
    }



    public static Pair<BoardSpace, Integer> shyHeuristic(Board board){
        return null;
    }
}
