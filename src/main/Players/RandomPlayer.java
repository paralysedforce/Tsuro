package main.Players;

import apple.laf.JRSUIConstants;
import javafx.util.Pair;
import main.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    }

    //For testing
    public RandomPlayer(String name, Color color, int seed){
        super(name, color);
        random = new Random(seed);
    }



    //================================================================================
    // Override methods
    //================================================================================
    public Pair<BoardSpace, Integer> getStartingLocation(){
        return getRandomStartingLocation();
    }

    public Tile chooseTile() {
        Set<Tile> legalMoves =  splayer.getLegalMoves();
        Tile[] legalMovesArr = legalMoves.toArray(new Tile[legalMoves.size()]);
        int randomIndex = random.nextInt(legalMovesArr.length);
        return legalMovesArr[randomIndex];
    }

    public void endGame() {
        //does nothing now but will do something for human player most likely
    }


    //================================================================================
    // Public Static Methods
    //================================================================================

    public static Pair<BoardSpace, Integer> getRandomStartingLocation(){
        Board board = Game.getGame().getBoard();
        Random random = new Random();

        int edgeNumber = random.nextInt(4);
        int indexOfEdge = random.nextInt(6);
        int leftOrRightTokenSpace = random.nextInt(2);

        int tokenSpace = edgeNumber * 2 + leftOrRightTokenSpace;

        if(edgeNumber == 0){
            return new Pair<>(board.getBoardSpace(0, indexOfEdge), tokenSpace);
        }
        else if (edgeNumber == 1){
            return new Pair<>(board.getBoardSpace(indexOfEdge, 5), tokenSpace);
        }
        else if (edgeNumber == 2){
            return new Pair<>(board.getBoardSpace(5, indexOfEdge), tokenSpace);
        }
        else{
            return new Pair<>(board.getBoardSpace(indexOfEdge, 0), tokenSpace);
        }
    }
}
