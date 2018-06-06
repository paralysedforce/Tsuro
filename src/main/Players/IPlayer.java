package main.Players;

import java.util.List;
import java.util.Set;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Tile;

/**
 * The player interface. Chosen to be an abstract class rather
 * than an interface to allow APlayer to implement them intermediate
 * from final implementations in a protected manner.
 */
abstract class IPlayer {
//    get-name       ;; -> string
//    ;; Returns the players name

    /**
     * @return the players name
     */
    abstract String getName();

//    initialize     ;; color? (listof color?) -> void?
//    ;; Called to indicate a game is starting.
//    ;; The first argument is the player's color
//    ;; and the second is all of the players'
//    ;; colors, in the order that the game will be played.
    abstract void initialize(Color color, List<Color> colors);

//    place-pawn     ;; board? -> pawn-loc?
//    ;; Called at the first step in a game; indicates where
//    ;; the player wishes to place their pawn. The pawn must
//    ;; be placed along the edge in an unoccupied space.
    protected abstract Pair<BoardSpace, Integer> getStartingLocation(Board board);

//            play-turn      ;; board? (set/c tile?) natural? -> tile?
//    ;; Called to ask the player to make a move. The tiles
//            ;; are the ones the player has, the number is the
//    ;; count of tiles that are not yet handed out to players.
//    ;; The result is the tile the player should place,
//    ;; suitably rotated.
    abstract Tile chooseTile(Board board, Set<Tile> hand, int remainingTiles);

//    end-game       ;; board? (set/c color?) -> void?
//    ;; Called to inform the player of the final board
//    ;; state and which players won the game.
    abstract void endGame(Board board, Set<Color> colors);
}
