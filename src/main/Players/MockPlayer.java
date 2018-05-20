package main.Players;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Tile;

/**
 * Useful for mocking up APlayers outside of the package.
 * Created by William on 5/20/2018.
 */

public abstract class MockPlayer extends APlayer {
    public MockPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return mockStartingLocation(board);
    }

    public abstract Pair<BoardSpace, Integer> mockStartingLocation(Board board);

    @Override
    Tile chooseTile(Board board, int remainingTiles) {
        return null;
    }

    public abstract Tile mockChooseTile(Board board, int remainingTiles);
}
