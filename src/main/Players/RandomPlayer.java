package main.Players;

//import apple.laf.JRSUIConstants;
import java.util.Random;
import java.util.Set;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Tile;

public class RandomPlayer extends APlayer {

    //================================================================================
    // Instance variables
    //================================================================================
    private Random random;

    //================================================================================
    // Constructor
    //================================================================================
    public RandomPlayer(String name, Color color){
        super(name, color);
        random = new Random();
        playerType = PlayerType.RANDOM;
    }

    public RandomPlayer(APlayer other){
        super(other);
    }

    //For testing
    public RandomPlayer(String name, Color color, int seed){
        super(name, color);
        random = new Random(seed);
    }



    //================================================================================
    // Override methods
    //================================================================================
    public Pair<BoardSpace, Integer> getStartingLocation(Board board){
        return getRandomStartingLocation(random, board);
    }

    protected Tile chooseTile(Board board, Set<Tile> hand, int remainingTiles) {
        Set<Tile> legalMoves =  getLegalMoves();
        Tile[] legalMovesArr = legalMoves.toArray(new Tile[legalMoves.size()]);
        int randomIndex = random.nextInt(legalMovesArr.length);
        return legalMovesArr[randomIndex];
    }


    //================================================================================
    // Public Static Methods
    //================================================================================

    public static Pair<BoardSpace, Integer> getRandomStartingLocation(Random random, Board board){
//        Board board = this.board;//Game.getGame().getBoard();

        while (true) {
            int edgeNumber = random.nextInt(4);
            int indexOfEdge = random.nextInt(6);
            int leftOrRightTokenSpace = random.nextInt(2);
            int row, col;

            int tokenSpace = edgeNumber * 2 + leftOrRightTokenSpace;
            if (edgeNumber == 0) {
                row = 0;
                col = indexOfEdge;
            } else if (edgeNumber == 1) {
                row = indexOfEdge;
                col = 5;
            } else if (edgeNumber == 2) {
                row = 5;
                col = indexOfEdge;
            } else {
                row = indexOfEdge;
                col = 0;
            }
            boolean isOccupied = board.getBoardSpace(row, col).getOccupiedSpaces().contains(tokenSpace);
            if (!isOccupied)
                return new Pair<>(board.getBoardSpace(row, col), tokenSpace);
        }


    }
}
