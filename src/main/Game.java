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
    private SPlayer dragonTileOwner;

    static Game game;

   /* private Game(String filename){
        board = Board.getBoard();
        remainingPlayers = new ArrayList<>();
        eliminatedPlayers = new ArrayList<>();
        tilePile = TilePile.getTilePile(filename);
        dragonTileOwner = null;
    }*/

   private Game(){
       this.board = Board.getBoard();
       this.remainingPlayers = new ArrayList<>();
       this.eliminatedPlayers = new ArrayList<>();
       this.tilePile = TilePile.getTilePile();
       dragonTileOwner = null;
   }

    private Game(List<SPlayer> remainingPlayers, List<SPlayer> eliminatedPlayers, TilePile tilePile){
        this.board = Board.getBoard();
        this.remainingPlayers = remainingPlayers;
        this.eliminatedPlayers = eliminatedPlayers;
        this.tilePile = tilePile;
        dragonTileOwner = null;
    }

    public static Game getGame(List<SPlayer> remainingPlayers, List<SPlayer> eliminatedPlayers, TilePile tilePile){
        if (game == null) game = new Game(remainingPlayers, eliminatedPlayers, tilePile);
        return game;
    }

    public static Game getGame(){
        if (game == null) game = new Game();
        return game;
    }

    public static void resetGame(){
        game = null;
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
    private void eliminatePlayer(SPlayer eliminatedPlayer){
        eliminatedPlayer.returnTilesToPile();
        remainingPlayers.remove(eliminatedPlayer);
        eliminatedPlayers.add(eliminatedPlayer);
    }

    private SPlayer findPlayerToDrawFirst(Set<SPlayer> failedPlayers, SPlayer currentPlayer){
        if (dragonTileOwner != null && !failedPlayers.contains(dragonTileOwner)){
            return dragonTileOwner;
        }
        else {
            int currentIndex = remainingPlayers.indexOf(currentPlayer);
            while (failedPlayers.contains(remainingPlayers.get(currentIndex))){
                currentIndex = (currentIndex + 1) % remainingPlayers.size();
            }
            return remainingPlayers.get(currentIndex);
        }
    }

    public Set<SPlayer> playTurn(Tile tile, SPlayer player){
        Set<Token> failedTokens = board.placeTile(tile, player.getToken());
        Set<SPlayer> failedPlayers = new HashSet<SPlayer>();
        for(Token failedToken : failedTokens){
            failedPlayers.add(failedToken.getPlayer());
        }

        if(failedPlayers.containsAll(remainingPlayers))
            return new HashSet<>();

        player.removeTileFromBank(tile);
        player.drawFromPile();

        if (!failedPlayers.isEmpty())
        {
            for(SPlayer failedPlayer : failedPlayers){
                failedPlayer.returnTilesToPile();
            }

            SPlayer playerToDrawFirst = findPlayerToDrawFirst(failedPlayers, player);

            for(SPlayer failedPlayer : failedPlayers){
                eliminatePlayer(failedPlayer);
            }

            drawAfterElimination(playerToDrawFirst);
        }

        return failedPlayers;
    }

    public void requestDragonTile(SPlayer player){
        if (dragonTileOwner == null && tilePile.isEmpty()) {
            dragonTileOwner = player;
        }
    }

    private void resetDragonTile(){
        if (dragonTileOwner != null){
            dragonTileOwner = null;
        }
    }

    private boolean areAllRemainingHandsFull() {
       for(SPlayer player : remainingPlayers){
           if (!player.hasFullHand())
               return false;
       }
       return true;
    }

    private void drawAfterElimination(SPlayer playerToDrawFirst){
        int playerToDrawIndex = remainingPlayers.indexOf(playerToDrawFirst);
        while(!tilePile.isEmpty() && !areAllRemainingHandsFull()){
            remainingPlayers.get(playerToDrawIndex).drawFromPile();
            playerToDrawIndex = (playerToDrawIndex + 1) % remainingPlayers.size();

            /*boolean anyDrawn = false;
            for (int i = playerToDrawIndex; i < playerToDrawIndex + remainingPlayers.size(); i++){
                SPlayer playerToDraw = remainingPlayers.get(i % remainingPlayers.size());
                if (!playerToDraw.hasFullHand()){
                    playerToDraw.drawFromPile();
                    anyDrawn = true;
                }
            }
            if (!anyDrawn)
                break;*/
        }
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
