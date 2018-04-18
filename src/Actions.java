import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyasalwar on 4/16/18.
 */


public class Actions {


    static private class PlayReturn {
        private TilePile tilePile;
        private List<SPlayer> remainingPlayers;
        private List<SPlayer> eliminatedPlayers;
        private Board board;
        private List<SPlayer> winningPlayers;

        public PlayReturn(TilePile tilePile, List<SPlayer> remainingPlayers, List<SPlayer> eliminatedPlayers,
                          Board board, List<SPlayer> winningPlayers){
            this.tilePile = tilePile;
            this.remainingPlayers = remainingPlayers;
            this.eliminatedPlayers = eliminatedPlayers;
            this.board = board;
            this.winningPlayers = winningPlayers;
        }
    }

    public static PlayReturn PlayATurn(TilePile tilePile, List<SPlayer> remainingPlayers, List<SPlayer> eliminatedPlayers,
                                       Board board, Tile tile) {
        Game game = new Game(board, remainingPlayers, eliminatedPlayers, tilePile);

        SPlayer player = remainingPlayers.remove(0);
        List<SPlayer> playersLosingOnTurn = game.playTurn(tile, player);

        remainingPlayers.removeAll(playersLosingOnTurn);
        eliminatedPlayers.addAll(playersLosingOnTurn);

        if(remainingPlayers.isEmpty()){
            return new PlayReturn(tilePile, remainingPlayers, eliminatedPlayers, board, playersLosingOnTurn);
        }
        else{
            return new PlayReturn(tilePile, remainingPlayers, eliminatedPlayers, board, null);
        }
    }

    public static boolean isLegalMove(SPlayer player, Board board, Tile tile){
        return board.isLegalMove(tile, player);
    }

    public static void Main(String[] args){

        Board board = new Board();
        int numberOfPlayers = 3;
        List<SPlayer> listOfPlayers = new ArrayList<>();
        TilePile tilePile = new TilePile(); //need to add tile stuff here

        for(int i = 0; i < numberOfPlayers; i++){
            Pair<BoardSpace, Integer> position = board.getRandomStartingLocation();
            listOfPlayers.add(new SPlayer("john" + i, position.getKey(), position.getValue(), tilePile));
        }

        Actions.PlayATurn(tilePile, listOfPlayers, new ArrayList<SPlayer>(), board, new Tile()); //need to change how we get the tile

        //Actions.PlayATurn();
    }

}
