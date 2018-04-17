import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Game {
    private Board board;
    private List<SPlayer> remainingPlayers;
    private List<SPlayer> eliminatedPlayers;
    private TilePile tilePile;

    public Game(String filename){
        board = new Board();
        remainingPlayers = new ArrayList<>();
        eliminatedPlayers = new ArrayList<>();
        tilePile = new TilePile(filename);
    }

    public Game(Board board, List<SPlayer> remainingPlayers, List<SPlayer> eliminatedPlayers, TilePile tilePile){
        this.board = board;
        this.remainingPlayers = remainingPlayers;
        this.eliminatedPlayers = eliminatedPlayers;
        this.tilePile = tilePile;
    }

    public void registerPlayer(String name, BoardSpace startingLocation, int startingTokenSpace){
        SPlayer player = new SPlayer(name, startingLocation, startingTokenSpace);
        remainingPlayers.add(player);
    }

    public List<SPlayer> playTurn(Tile tile, SPlayer player){
        return board.placeTile(tile, player);
    }
}
