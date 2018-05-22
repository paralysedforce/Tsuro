package main.Players;

import java.util.Set;

import main.Board;
import main.Color;
import main.Tile;

/**
 * Created by vyasalwar on 5/3/18.
 */
public abstract class TileHeuristicPlayer extends APlayer {

    public TileHeuristicPlayer(String name, Color color){
        super(name, color);
    }

    protected Tile chooseTile(Board board, Set<Tile> hand, int remainingTiles) {
        Set<Tile> legalMoves = getLegalMoves();
        Tile bestTile = null;
        int bestScore = Integer.MIN_VALUE;

        for (Tile tile: legalMoves){
            int curScore = ScoreTile(tile);
            if (curScore > bestScore){
                bestScore = curScore;
                bestTile = tile;
            }
        }

        return bestTile;
    }

    abstract protected int ScoreTile(Tile tile);
}
