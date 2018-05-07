package main.Players;

import main.Color;
import main.Tile;

import java.util.Set;

/**
 * Created by vyasalwar on 5/3/18.
 */
public abstract class ScorePlayer extends APlayer {

    public ScorePlayer(String name, Color color){
        super(name, color);
    }

    @Override
    public Tile chooseTile(){
        Set<Tile> legalMoves = splayer.getLegalMoves();
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
