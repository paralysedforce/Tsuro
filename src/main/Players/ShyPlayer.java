package main.Players;

import javafx.util.Pair;
import main.*;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by vyasalwar on 6/5/18.
 */
public class ShyPlayer extends TileHeuristicPlayer {

    public ShyPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    protected int ScoreTile(Tile tile) {
        return ShyHeuristic(tile, board, color, turnOrder);
    }

    @Override
    protected Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return RandomPlayer.getRandomStartingLocation(new Random(), board);
    }


    // Place the tile onto a copy of the board. The Manhattan distance away to the nearest player's boardspace is the
    // score. As a result, tiles that result in the player being further away from other players gives a higher score.
    public static int ShyHeuristic(Tile tile, Board board, Color color, List<Color> turnOrder){
        Board testBoard = new Board(board);
        Set<Token> failedPlayers = testBoard.placeTile(tile, testBoard.findToken(color));

        int row = testBoard.findToken(color).getBoardSpace().getRow();
        int col = testBoard.findToken(color).getBoardSpace().getCol();
        int score = Integer.MAX_VALUE;

        for (Color otherColor: turnOrder){

            // Don't consider the current token in the calculation
            if (otherColor == color)
                continue;

            // If the other player has been eliminated, disregard them
            if (testBoard.findToken(otherColor) == null)
                continue;

            BoardSpace otherBoardspace = testBoard.findToken(otherColor).getBoardSpace();

            // Calculate Manhattan distance to the boardspace the other token is on
            int otherScore = Math.abs(otherBoardspace.getCol() - col) + Math.abs(otherBoardspace.getRow() - row);

            // If the other token is closer than the closest previously seen, update score
            score = Math.min(score, otherScore);
        }

        return score;
    }
}
