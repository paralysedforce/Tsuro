package main;

import javafx.util.Pair;
import main.Players.APlayer;
import main.Players.LeastSymmetricPlayer;
import main.Players.MostSymmetricPlayer;
import main.Players.RandomPlayer;

import java.util.*;

public class Game {

    //================================================================================
    // Singleton Model
    //================================================================================
    static Game game;

    // Default constructor
    private Game(){
       this.board = new Board();
       this.remainingPlayers = new ArrayList<>();
       this.eliminatedPlayers = new ArrayList<>();
       this.tilePile = new TilePile();
       dragonTileOwner = null;
   }

    public static Game getGame(){
        if (game == null) game = new Game();
        return game;
    }

    public static void resetGame(){
        game = null;
    }

    //================================================================================
    // Instance Variables
    //================================================================================
    private Board board;
    private List<SPlayer> remainingPlayers;
    private List<SPlayer> eliminatedPlayers;
    private TilePile tilePile;
    private SPlayer dragonTileOwner;

    //================================================================================
    // Getters
    //================================================================================
    public Board getBoard() {
        return board;
    }

    public TilePile getTilePile() {
        return tilePile;
    }

    //================================================================================
    // Setters
    //================================================================================
    public void setTilePile(TilePile tilePile) {
        this.tilePile = tilePile;
    }

    //================================================================================
    // Public Methods
    //================================================================================

    // Adds a player to a new game
    public void registerPlayer(String name, Color color, PlayerType type){
        APlayer aplayer;
        switch(type) {
            case RANDOM:
                aplayer = new RandomPlayer(name, color);
                break;
            case MOSTSYMMETRIC:
                aplayer = new MostSymmetricPlayer(name, color);
                break;
            case LEASTSYMMETRIC:
                aplayer = new LeastSymmetricPlayer(name, color);
                break;
            default:
                throw new IllegalArgumentException("player type given was not valid");
        }

        remainingPlayers.add(aplayer.splayer);
    }


    // For testing purposes only
    // TODO: Remove in production
    public void registerPlayer(SPlayer player){
        remainingPlayers.add(player);
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, SPlayer player){
        if(!player.holdsTile(tile))
            return false;

        if(player.hasSafeMove() && board.willKillPlayer(tile, player))
            return false;

        return true;
    }

    // Let a player with an empty hand request the TilePile
    public void requestDragonTile(SPlayer player){
        if (dragonTileOwner == null && tilePile.isEmpty()) {
            dragonTileOwner = player;
        }
    }

    /* TODO: MAKE PRIVATE WHEN NOT DEBUGGING */
    // Deal with when the player place the tile on the board
    //   Returns a set of players who have lost after the tile is placed
    public Set<SPlayer> playTurn(Tile tile, SPlayer player){
        Set<Token> failedTokens = board.placeTile(tile, player);
        Set<SPlayer> failedPlayers = new HashSet<>();
        for(Token failedToken : failedTokens)
            failedPlayers.add(failedToken.getPlayer());

        if(failedPlayers.containsAll(remainingPlayers))
            return new HashSet<>();

        player.removeTileFromHand(tile);
        player.drawFromPile();

        if (!failedPlayers.isEmpty())
        {
            for(SPlayer failedPlayer : failedPlayers)
                failedPlayer.returnTilesToPile();

            SPlayer playerToDrawFirst = findPlayerToDrawFirst(failedPlayers, player);

            for(SPlayer failedPlayer : failedPlayers)
                eliminatePlayer(failedPlayer);


            drawAfterElimination(playerToDrawFirst);
        }

        return failedPlayers;
    }

    // Main game loop: EXPERIMENTAL!!!
    public void playGame(){
        for (SPlayer player: remainingPlayers) {
            Pair<BoardSpace, Integer> startingLocation = player.getStartingLocation();
            BoardSpace boardSpace = startingLocation.getKey();
            int tokenSpace = startingLocation.getValue();

            player.placeToken(boardSpace, tokenSpace);
        }

        while (remainingPlayers.size() > 1) {
            for (SPlayer player : remainingPlayers) {
                Tile tile = player.chooseTile();
                playTurn(tile, player);
            }
        }
    }


    //================================================================================
    // Private Helpers
    //================================================================================

    // Remove the dragon tile from whatever player that has it
    private void resetDragonTile(){
        if (dragonTileOwner != null){
            dragonTileOwner = null;
        }
    }

    // Checks to see if all players still in the game have full hands
    private boolean areAllRemainingHandsFull() {
       for(SPlayer player : remainingPlayers){
           if (!player.hasFullHand())
               return false;
       }
       return true;
    }

    // After a player has been eliminated, go around in a clockwise direction and have
    //   all players draw tiles if necessary
    private void drawAfterElimination(SPlayer playerToDrawFirst){
        int playerToDrawIndex = remainingPlayers.indexOf(playerToDrawFirst);
        while(!tilePile.isEmpty() && !areAllRemainingHandsFull()){
            remainingPlayers.get(playerToDrawIndex).drawFromPile();
            playerToDrawIndex = (playerToDrawIndex + 1) % remainingPlayers.size();
            resetDragonTile();
        }
    }

    // Determine which player should draw first after a player has been eliminated
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

    // Eliminates a player. To be called when a player token is forced off the edge
    private void eliminatePlayer(SPlayer eliminatedPlayer){
        eliminatedPlayer.returnTilesToPile();
        remainingPlayers.remove(eliminatedPlayer);
        eliminatedPlayers.add(eliminatedPlayer);
    }


    //================================================================================
    // Main
    //================================================================================

    /* Runs a simple command line UI to play a game
        EXPERIMENTAL
     */
    public static void main(String[] args){
        Game game = getGame();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Tsuro!");

        for (String player: args) {
            game.registerPlayer(player, Color.BLACK, PlayerType.RANDOM);
        }
        game.playGame();
    }
}
