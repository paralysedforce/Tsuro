package main.Players;

import java.util.Set;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Tile;

/**
 * Created by William on 6/2/2018.
 */

public class PlaceholderPlayer extends APlayer {
    public PlaceholderPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    protected Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return null;
    }

    @Override
    Tile chooseTile(Board board, Set<Tile> hand, int remainingTiles) {
        return null;
    }
}
