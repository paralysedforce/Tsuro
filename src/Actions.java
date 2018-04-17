import java.util.List;

/**
 * Created by vyasalwar on 4/16/18.
 */


public class Actions {


    static private class PlayReturn {
        private TilePile tilePile;
        private List<Player> remainingPlayers;
        private List<Player> eliminatedPlayers;
        private Board board;
        private List<Player> winningPlayers;

        public PlayReturn(TilePile tilePile, List<Player> remainingPlayers, List<Player> eliminatedPlayers,
                          Board board, List<Player> winningPlayers){
            this.tilePile = tilePile;
            this.remainingPlayers = remainingPlayers;
            this.eliminatedPlayers = eliminatedPlayers;
            this.board = board;
            this.winningPlayers = winningPlayers;
        }
    }

    public static PlayReturn PlayATurn(TilePile tilePile, List<Player> remainingPlayers, List<Player> eliminatedPlayers,
                                       Board board, Tile tile) {
        Game game = new Game(board, remainingPlayers, eliminatedPlayers, tilePile);

        Player player = remainingPlayers.remove(0);
        List<Player> playersLosingOnTurn = game.playTurn(tile, player);

        remainingPlayers.removeAll(playersLosingOnTurn);
        eliminatedPlayers.addAll(playersLosingOnTurn);

        if(remainingPlayers.isEmpty()){
            return new PlayReturn(tilePile, remainingPlayers, eliminatedPlayers, board, playersLosingOnTurn);
        }
        else{
            return new PlayReturn(tilePile, remainingPlayers, eliminatedPlayers, board, null);
        }
    }

    public static void Main(String[] args){
        //Actions.PlayATurn();
    }

}
