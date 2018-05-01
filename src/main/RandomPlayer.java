package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    protected Pair<BoardSpace, Integer> getStartingLocation(){
        Board board = Game.getGame().getBoard();

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

    public Tile chooseTile() {
        List<Tile> legalMoves = new ArrayList<>();
        boolean hasSafeMove = splayer.hasSafeMove();

        for(int i = 0; i < 3; i++){
            Tile tile = splayer.getTile(i);

            for(int r = 0; r < 4; r++){
                tile.rotateClockwise();
                if(!hasSafeMove || splayer.isSafeMove(tile)){
                    legalMoves.add(new Tile(tile));
                }
            }
        }

        int randomIndex = random.nextInt(legalMoves.size());
        return legalMoves.get(randomIndex);
    }

    public void endGame() {
        //does nothing now but will do something for human player most likely
    }
}
