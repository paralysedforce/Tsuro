package main.Players;

import javafx.util.Pair;
import main.*;

import java.util.Random;
import java.util.Set;

/**
 * Created by vyasalwar on 6/5/18.
 */
public class AggressivePlayer extends TileHeuristicPlayer {
    public AggressivePlayer(String name, Color color) {
        super(name, color);
        playerType = PlayerType.AGGRESSIVE;
    }

    @Override
    protected int ScoreTile(Tile tile) {
        return AggressiveHeuristic(tile, token, board);
    }


    @Override
    protected Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return RandomPlayer.getRandomStartingLocation(new Random(), board);
    }

    public static int AggressiveHeuristic(Tile tile, Token token, Board board){
        // Try to kill the most people possible.
        //   Note that if the playing the tile kills yourself (i.e. for all moves, willKillPlayer is true),
        //   then try to take down as many of these motherfuckers down with you
        Set<Token> tokensOnSpace = token.getBoardSpace().getTokensOnSpace();
        int numTokensKilled = 0;

        for (Token otherToken: tokensOnSpace){
            if (board.willKillPlayer(tile, otherToken))
                numTokensKilled++;
        }

        return numTokensKilled;
    }
}
