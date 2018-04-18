import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyasalwar on 4/16/18.
 */


public class Actions {


    static private class PlayReturn {
        public  TilePile tilePile;
        public  List<SPlayer> remainingPlayers;
        public  List<SPlayer> eliminatedPlayers;
        public  Board board;
        public  List<SPlayer> winningPlayers;

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
        remainingPlayers.add(player);

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

    public static void main(String[] args){

        Board board = new Board();
        int numberOfPlayers = 3;
        List<SPlayer> listOfPlayers = new ArrayList<>();
        TilePile tilePile = new TilePile(); //need to add tile stuff here

        for(int i = 0; i < numberOfPlayers; i++){
            Pair<BoardSpace, Integer> position = board.getRandomStartingLocation();
            listOfPlayers.add(new SPlayer("john" + i, position.getKey(), position.getValue(), tilePile));
        }

        PlayReturn ret = Actions.PlayATurn(tilePile, listOfPlayers, new ArrayList<SPlayer>(), board, listOfPlayers.get(0).getRandomTileFromBank()); //need to change how we get the tile
        if (ret.tilePile != tilePile)
            throw new AssertionError();
        if (ret.winningPlayers != null)
            throw new AssertionError();
        if (ret.board != board)
            throw new AssertionError();
        if (!ret.eliminatedPlayers.isEmpty())
            throw new AssertionError();
        if (!ret.remainingPlayers.equals(listOfPlayers))
            throw new AssertionError();


        Tile trueTile  = listOfPlayers.get(0).getRandomTileFromBank();
        Tile falseTile = listOfPlayers.get(1).getRandomTileFromBank();

        if (Actions.isLegalMove(listOfPlayers.get(0), board, trueTile) != true)
            throw new AssertionError();
        if (Actions.isLegalMove(listOfPlayers.get(0), board, falseTile) != false)
            throw new AssertionError();


        //Actions.PlayATurn();
    }

}
