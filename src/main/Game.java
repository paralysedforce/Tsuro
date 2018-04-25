package main;

import java.util.*;

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
        SPlayer player = new SPlayer(name, startingLocation, startingTokenSpace, TilePile.getTilePile());
        remainingPlayers.add(player);
    }

    public boolean isLegalMove(Tile tile, SPlayer player){
        if(!player.hasTile(tile))
            return false;
        if(player.hasLegalMove() && board.willKillPlayer(tile, player.getToken()))
            return false;

        return true;
    }

    //Maybe should be moved into the SPlayer
    private void eliminatePlayer(SPlayer player){
        player.getToken().removeFromBoard();
        player.returnTilesToPile();
    }

    public Set<SPlayer> playTurn(Tile tile, SPlayer player){
        Set<Token> failedTokens = board.placeTile(tile, player.getToken());
        Set<SPlayer> failedPlayers = new HashSet<SPlayer>();
        for(Token failedToken : failedTokens){
            failedPlayers.add(failedToken.getPlayer());
        }

        player.removeTileFromBank(tile);
        player.drawFromPile();

        for(SPlayer failedPlayer : failedPlayers){
            eliminatePlayer(failedPlayer);
        }

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
