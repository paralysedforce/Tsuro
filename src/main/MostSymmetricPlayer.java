package main;

import javafx.util.Pair;

import java.util.Random;

/**
 * Created by vyasalwar on 4/30/18.
 */
public class MostSymmetricPlayer extends APlayer {
    public MostSymmetricPlayer(String name, Color color) {
        super(name, color);
    }



    // Get random starting location
    @Override
    protected Pair<BoardSpace, Integer> getStartingLocation() {
        Board board = Game.getGame().getBoard();
        Random random = new Random();

        int edgeNumber = random.nextInt(4);
        int indexOfEdge = random.nextInt(6);
        int leftOrRightTokenSpace = random.nextInt(2);

        int tokenSpace = edgeNumber * 2 + leftOrRightTokenSpace;

        if (edgeNumber == 0) {
            return new Pair<>(board.getBoardSpace(0, indexOfEdge), tokenSpace);
        } else if (edgeNumber == 1) {
            return new Pair<>(board.getBoardSpace(indexOfEdge, 5), tokenSpace);
        } else if (edgeNumber == 2) {
            return new Pair<>(board.getBoardSpace(5, indexOfEdge), tokenSpace);
        } else {
            return new Pair<>(board.getBoardSpace(indexOfEdge, 0), tokenSpace);
        }
    }

    // Order tiles from least to most symmetric, and choose the first legal rotation among them
    @Override
    public Tile chooseTile() {
        Tile mostSymmetric = null;
        int maxSymmetries = Integer.MIN_VALUE;
        boolean hasSafeMoves = splayer.hasSafeMove();

        // Find a (possibly) safe rotation with the maximum symmetries
        for (int i = 0; i < 3; i++) {
            Tile tile = splayer.getTile(i);
            int tileSymmetries = tile.calculateSymmetries();
            for (int rotation = 0; rotation < 4; rotation++) {

                // Disregard safety if there are not safe moves.
                if ((!hasSafeMoves || splayer.isSafeMove(tile)) &&
                        tileSymmetries >= maxSymmetries) {

                    maxSymmetries = tileSymmetries;
                    mostSymmetric = tile;

                } else {
                    tile.rotateClockwise();
                }
            }
        }

        return mostSymmetric;
    }
}
