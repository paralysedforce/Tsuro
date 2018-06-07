package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import main.Parser.ParserUtils;
import main.Players.APlayer;
import main.Players.LeastSymmetricPlayer;
import main.Players.MostSymmetricPlayer;
import main.Players.PlayerType;
import main.Players.RandomPlayer;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Game {

    //================================================================================
    // Singleton Model
    //================================================================================
    private static Game game;

    // Default constructor
    private Game(){
       this.board = new Board();
       this.remainingPlayers = new ArrayList<>();
       this.eliminatedPlayers = new ArrayList<>();
       this.winningPlayers = new ArrayList<>();
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
    private List<APlayer> remainingPlayers;
    private List<APlayer> eliminatedPlayers;
    private List<APlayer> winningPlayers;
    private TilePile tilePile;
    private APlayer dragonTileOwner;

    //================================================================================
    // Getters
    //================================================================================
    public Board getBoard() {
        return board;
    }

    public TilePile getTilePile() {
        return tilePile;
    }

    public boolean isOver(){ return !winningPlayers.isEmpty();}

    //================================================================================
    // Setters
    //================================================================================
    public void setTilePile(TilePile tilePile) {
        this.tilePile = tilePile;
    }

    //================================================================================
    // Public Methods
    //================================================================================

    // For testing purposes only
    public void registerPlayer(APlayer player){
        remainingPlayers.add(player);
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, APlayer player){
        /*if(!player.getHand().holdsTile(tile))
            return false;*/

        if(player.hasSafeMove() && board.willKillPlayer(tile, player))
            return false;

        return true;
    }

    // Let a player with an empty hand request the TilePile
    public void requestDragonTile(APlayer player){
        if (dragonTileOwner == null && tilePile.isEmpty()) {
            dragonTileOwner = player;
        }
    }

    /* TODO: Combine with playATurn */
    // Deal with when the player place the tile on the board
    //   Returns a set of players who have lost after the tile is placed
    public Set<APlayer> playTurn(Tile tile, APlayer player) throws ContractException{
            if (!isLegalMove(tile, player)) {
                throw new ContractException(ContractViolation.PRECONDITION, "Player made an illegal move");
            }

            // Place tile and move players
            Set<Token> failedTokens = board.placeTile(tile, player.getToken());
            Set<APlayer> failedPlayers = new HashSet<>();
            for (APlayer possiblyFailedPlayer: remainingPlayers){
                if (failedTokens.contains(possiblyFailedPlayer.getToken()))
                    failedPlayers.add(possiblyFailedPlayer);
            }

            if (failedPlayers.containsAll(remainingPlayers)) {
                for (APlayer failedPlayer: failedPlayers){
                    failedPlayer.getHand().returnTilesToDeck();
                    eliminatePlayer(failedPlayer);
                }

                winningPlayers = new ArrayList<>(failedPlayers);
                return failedPlayers;
            }

            // There are still some living players on the board
            else {
                // Update hands
                // player.getHand().removeTile(tile);
                player.getHand().drawFromDeck();

                // Update deck with eliminated players
                if (!failedPlayers.isEmpty()) {
                    for (APlayer failedPlayer : failedPlayers)
                        failedPlayer.getHand().returnTilesToDeck();

                    APlayer playerToDrawFirst = findPlayerToDrawFirst(failedPlayers, player);

                    for (APlayer failedPlayer : failedPlayers)
                        eliminatePlayer(failedPlayer);

                    drawAfterElimination(playerToDrawFirst);
                }

                return failedPlayers;
            }
    }

    /**
     * Matches the return specs of the assignment, calls playTurn as appropriate
     * @return null if the game is not over, list of winning players if the game is over.
     */
    public void handleWinners(Tile tile, APlayer player) throws ContractException {
        // Cheating occurs here and throws ContractException
        Set<APlayer> playersEliminatedThisTurn = playTurn(tile, player);

        if (isOver())
            return;

        // Check for end game conditions that don't result in one winner in addition to one winner.
        if (playersEliminatedThisTurn.containsAll(remainingPlayers)){
            winningPlayers = new ArrayList<>(remainingPlayers);
            return;
        }

        if(tilePile.isEmpty() && areAllRemainingHandsEmpty()){
            winningPlayers = new ArrayList<>(remainingPlayers);
            return;
        }

        if (remainingPlayers.size() == 1){
            winningPlayers = new ArrayList<>(remainingPlayers);
        }
    }

    public void initializePlayers(){
        List<Color> startingTokenList = new ArrayList<>();
        for(APlayer player : remainingPlayers){
            startingTokenList.add(player.getColor());
        }
        for(APlayer player : remainingPlayers){
            player.initialize(startingTokenList);
        }
    }


    // Main game loop
    public List<APlayer> playGame(){
        initializePlayers();

        for (APlayer player: remainingPlayers) {
            player.placeToken();
        }

        while (!isOver()) {
            // Get the chosen move
            APlayer playingPlayer = remainingPlayers.get(0);
            Tile tile = playingPlayer.chooseTile(tilePile.getCount());

            try {
                // Update winning players
                handleWinners(tile, playingPlayer);
            } catch (ContractException e) {
                // Detect cheating
                remainingPlayers.remove(playingPlayer);
                playingPlayer = blamePlayer(playingPlayer);
                remainingPlayers.add(0, playingPlayer);
                continue;
            }
        }


        /* Inform every player the colors of the winning players */
        Set<Color> winningPlayerColors = new HashSet<>();
        for (APlayer player : winningPlayers) {
            winningPlayerColors.add(player.getColor());
        }

        for (APlayer player : remainingPlayers){
            player.endGame(winningPlayerColors);
        }
        for (APlayer player : eliminatedPlayers){
            player.endGame(winningPlayerColors);
        }

        return winningPlayers;

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
       for(APlayer player : remainingPlayers){
           if (!player.getHand().isFull())
               return false;
       }
       return true;
    }

    private boolean areAllRemainingHandsEmpty() {
        for(APlayer player : remainingPlayers){
            if (!player.getHand().isEmpty())
                return false;
        }
        return true;
    }

    // After a player has been eliminated, go around in a clockwise direction and have
    //   all players draw tiles if necessary
    private void drawAfterElimination(APlayer playerToDrawFirst){
        int playerToDrawIndex = remainingPlayers.indexOf(playerToDrawFirst);
        while(!tilePile.isEmpty() && !areAllRemainingHandsFull()){
            remainingPlayers.get(playerToDrawIndex).getHand().drawFromDeck();
            playerToDrawIndex = (playerToDrawIndex + 1) % remainingPlayers.size();
            resetDragonTile();
        }
    }

    // Determine which player should draw first after a player has been eliminated
    private APlayer findPlayerToDrawFirst(Set<APlayer> failedPlayers, APlayer currentPlayer){
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
    private void eliminatePlayer(APlayer eliminatedPlayer){
        if (dragonTileOwner == eliminatedPlayer){
            resetDragonTile();
        }
        eliminatedPlayer.getHand().returnTilesToDeck();
        remainingPlayers.remove(eliminatedPlayer);
        eliminatedPlayers.add(eliminatedPlayer);
    }

    private APlayer blamePlayer(APlayer aplayer){
        return new RandomPlayer(aplayer);
    }


    //================================================================================
    // Main
    //================================================================================

    public static void main(String[] args){

        Game.resetGame();
        game = getGame();
        Scanner scanner = new Scanner(System.in);
        //System.err.println("Welcome to Tsuro!");

        while (scanner.hasNextLine()) {


        /* One move */
            try {
                /* Get input from stdin */
                Element tilePileListElement      = (Element) ParserUtils.nodeFromString(scanner.nextLine());
                Element remainingPlayersElement  = (Element) ParserUtils.nodeFromString(scanner.nextLine());
                Element eliminatedPlayersElement = (Element) ParserUtils.nodeFromString(scanner.nextLine());
                Element boardElement             = (Element) ParserUtils.nodeFromString(scanner.nextLine());
                Element tileToBePlacedElement    = (Element) ParserUtils.nodeFromString(scanner.nextLine());


            /* Convert input into Game representations */
                TilePile tilePile = new TilePile(tilePileListElement);
                Color dragonTileOwnerColor = ParserUtils.findDragonTilePlayerColor(remainingPlayersElement);
                List<APlayer> remainingPlayers = ParserUtils.APlayerListFromNode(remainingPlayersElement);
                List<APlayer> eliminatedPlayers = ParserUtils.APlayerListFromNode(eliminatedPlayersElement);
                Board board = new Board(boardElement);
                Tile tileToBePlaced = new Tile(tileToBePlacedElement);

            /* Update Game fields */
                game.board = board;
                game.tilePile = tilePile;
                game.remainingPlayers = remainingPlayers;
                game.eliminatedPlayers = eliminatedPlayers;
                game.winningPlayers = new ArrayList<>();

            /* Connect all the components to each other */
                game.dragonTileOwner = null;
                for (APlayer player : remainingPlayers) {
                    if (player.getColor() == dragonTileOwnerColor) {
                        game.dragonTileOwner = player;
                        break;
                    }
                }

                for (APlayer player : game.remainingPlayers) {
                    player.setBoard(board);
                    player.getHand().setDeck(tilePile);
                }


                for (APlayer player : game.eliminatedPlayers) {
                    player.setBoard(board);
                    player.getHand().setDeck(tilePile);
                }


            /* Make a move */
                game.handleWinners(tileToBePlaced, remainingPlayers.get(0));

            /* Send output to stdout */
                Document document = ParserUtils.newDocument();
                System.out.println(ParserUtils.xmlElementToString(tilePile.toXML(document)));
                System.out.println(ParserUtils.APlayerListToString(remainingPlayers, game.dragonTileOwner));
                System.out.println(ParserUtils.APlayerListToString(eliminatedPlayers, game.dragonTileOwner));
                System.out.println(ParserUtils.xmlElementToString(board.toXML(document)));
                System.out.println(game.isOver() ?
                        ParserUtils.APlayerListToString(game.winningPlayers, game.dragonTileOwner) :
                        "<false></false>"
                );

            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
