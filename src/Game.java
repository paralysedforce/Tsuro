import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Game {
    private Board board;
    private List<Player> remainingPlayers;
    private List<Player> eliminatedPlayers;
    private TilePile tilePile;

    public Game(String filename){
        board = new Board();
        remainingPlayers = new ArrayList<>();
        eliminatedPlayers = new ArrayList<>();
        tilePile = new TilePile(filename);
    }

    public Game(Board board, List<Player> remainingPlayers, List<Player> eliminatedPlayers, TilePile tilePile){
        this.board = board;
        this.remainingPlayers = remainingPlayers;
        this.eliminatedPlayers = eliminatedPlayers;
        this.tilePile = tilePile;
    }

    public void registerPlayer(String name, BoardSpace startingLocation, int startingTokenSpace){
        Player player = new Player(name, startingLocation, startingTokenSpace);
        remainingPlayers.add(player);
    }

    public List<Player> playTurn(Tile tile, Player player){
        return board.placeTile(tile, player);
    }

    public static void PlayATurn(){

    }
}
