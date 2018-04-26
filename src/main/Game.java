package main;

import java.util.*;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Game {

    //================================================================================
    // Singleton Model
    //================================================================================
    static Game game;

    // Constructor initializing a TilePile from file
    private Game(String filename){
        board = Board.getBoard();
        remainingPlayers = new ArrayList<>();
        eliminatedPlayers = new ArrayList<>();
        tilePile = TilePile.getTilePile(filename);
        dragonTileOwner = null;
    }

    // Default constructor
   private Game(){
       this.board = Board.getBoard();
       this.remainingPlayers = new ArrayList<>();
       this.eliminatedPlayers = new ArrayList<>();
       this.tilePile = TilePile.getTilePile();
       dragonTileOwner = null;
   }

    public static Game getGame(){
        if (game == null) game = new Game();
        return game;
    }

   /* ONLY FOR DEBUGGING!!! */
   // TODO: Remove in production
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
    public static void resetGame(){
        game = null;
    }
    /* END DEBUGGING SEGMENT */

    //================================================================================
    // Instance Variables
    //================================================================================
    private Board board;
    private List<SPlayer> remainingPlayers;
    private List<SPlayer> eliminatedPlayers;
    private TilePile tilePile;
    private SPlayer dragonTileOwner;

    //================================================================================
    // Public Methods
    //================================================================================

    // Add a player to a new game
    public void registerPlayer(String name, BoardSpace startingLocation, int startingTokenSpace){
        SPlayer player = new SPlayer(name, startingLocation, startingTokenSpace, tilePile);
        remainingPlayers.add(player);
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, SPlayer player){
        if(!player.hasTile(tile))
            return false;
        if(player.hasLegalMove() && board.willKillPlayer(tile, player.getToken()))
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
        Set<Token> failedTokens = board.placeTile(tile, player.getToken());
        Set<SPlayer> failedPlayers = new HashSet<SPlayer>();
        for(Token failedToken : failedTokens)
            failedPlayers.add(failedToken.getPlayer());

        if(failedPlayers.containsAll(remainingPlayers))
            return new HashSet<>();

        player.removeTileFromBank(tile);
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
        while (remainingPlayers.size() > 1) {
            for (SPlayer player : remainingPlayers) {
                if  (player.hasLegalMove()) {
                    Tile tile = player.chooseTile();
                    playTurn(tile, player);
                }
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
            System.out.println(player + ": Please input [row] [col] [tokenspace]");
            String line = scanner.nextLine();
            int row = Integer.decode(line.substring(0, 1));
            int col = Integer.decode(line.substring(2, 3));
            int tokenSpace = Integer.decode(line.substring(4, 5));
            game.registerPlayer(player, game.board.getBoardSpace(row, col), tokenSpace);
        }
        game.playGame();
    }
}
