package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Game {
    private Board board;
    private List<SPlayer> remainingPlayers;
    private List<SPlayer> eliminatedPlayers;
    private TilePile tilePile;

    public Game(String filename){
        board = Board.getBoard();
        remainingPlayers = new ArrayList<>();
        eliminatedPlayers = new ArrayList<>();
        tilePile = TilePile.getTilePile(filename);
    }

    public Game(List<SPlayer> remainingPlayers, List<SPlayer> eliminatedPlayers){
        this.board = Board.getBoard();
        this.remainingPlayers = remainingPlayers;
        this.eliminatedPlayers = eliminatedPlayers;
        this.tilePile = TilePile.getTilePile();
    }

    //to be used later in some way
    public void registerPlayer(String name, BoardSpace startingLocation, int startingTokenSpace){
        SPlayer player = new SPlayer(name, startingLocation, startingTokenSpace);
        remainingPlayers.add(player);
    }

    public boolean isLegalMove(Tile tile, SPlayer player){
        if(!player.hasTile(tile))
            return false;
        if(player.hasLegalMove() && board.willKillPlayer(tile, player.getToken()))
            return false;

        return true;
    }

    public Set<SPlayer> playTurn(Tile tile, SPlayer player){
        Set<SPlayer> failedPlayers = board.placeTile(tile, player);
        player.drawFromPile();
        return failedPlayers;
    }

    public void playGame(){
        while (remainingPlayers.size() > 1) {
            for (SPlayer player : remainingPlayers) {
                Turn turn = player.generateTurn();
                if (turn.getAction() == Turn.TurnAction.RESIGN) {
                    remainingPlayers.remove(player);
                    eliminatedPlayers.add(player);
                }
            }
        }
    }
}
