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
       this.tilePile = new TilePile();
       dragonTileOwner = null;
       this.isOver = false;
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
    private TilePile tilePile;
    private APlayer dragonTileOwner;
    private boolean isOver;

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

        remainingPlayers.add(aplayer);
    }


    // For testing purposes only
    public void registerPlayer(APlayer player){
        remainingPlayers.add(player);
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, APlayer player){
        if(!player.getHand().holdsTile(tile))
            return false;

        if(player.hasSafeMove() && board.willKillPlayer(tile, player.getToken()))
            return false;

        return true;
    }

    // Let a player with an empty hand request the TilePile
    public void requestDragonTile(APlayer player){
        if (dragonTileOwner == null && tilePile.isEmpty()) {
            dragonTileOwner = player;
        }
    }


    // Deal with when the player place the tile on the board
    //   Returns a set of players who have lost after the tile is placed
    public Set<APlayer> playTurn(Tile tile, APlayer player) throws ContractException{
            if (!isLegalMove(tile, player)) {
                throw new ContractException(ContractViolation.PRECONDITION, "Player made an illegal move");
            }

            Set<Token> failedTokens = board.placeTile(tile, player.getToken());
            Set<APlayer> failedPlayers = new HashSet<>();

            for (APlayer possiblyFailedPlayer: remainingPlayers){
                if (failedTokens.contains(possiblyFailedPlayer.getToken()))
                    failedPlayers.add(possiblyFailedPlayer);
            }

            if (failedPlayers.containsAll(remainingPlayers))
                return failedPlayers;

            player.getHand().removeTile(tile);
            player.getHand().drawFromDeck();

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
    public Set<APlayer> playGame(){
        initializePlayers();

        for (APlayer player: remainingPlayers) {
            player.placeToken();
        }

        while (true) {
            APlayer player = remainingPlayers.get(0);
            Tile tile = player.chooseTile(tilePile.getCount());
            try {
                Set<APlayer> losingPlayers = playTurn(tile, player);
                if(losingPlayers.containsAll(remainingPlayers) || remainingPlayers.size() <= 1){
                    break;
                }
                if(tilePile.isEmpty() && areAllRemainingHandsEmpty()){
                    break;
                }
            }
            catch (ContractException e) {
                remainingPlayers.remove(player);
                player = blamePlayer(player);
                remainingPlayers.add(0, player);
                continue;
            }

            if(!eliminatedPlayers.contains(player)){
                remainingPlayers.remove(player);
                remainingPlayers.add(player);
            }
        }

        Set<Color> winningPlayers = new HashSet<>();
        for (APlayer player : remainingPlayers) {
            winningPlayers.add(player.getColor());
        }

        for (APlayer player : remainingPlayers){
            player.endGame(winningPlayers);
        }
        for (APlayer player : eliminatedPlayers){
            player.endGame(winningPlayers);
        }

        return new HashSet<>(remainingPlayers);
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

    private APlayer blamePlayer(APlayer splayer){
        return new RandomPlayer(splayer);
    }


    //================================================================================
    // Main
    //================================================================================

    /* Runs a simple command line UI to play a game */
    public static void main(String[] args){
        Game.resetGame();
        Game game = getGame();
        Scanner scanner = new Scanner(System.in);
        System.err.println("Welcome to Tsuro!");

        /* One move */
        try {
            /* Get input from stdin */
            Element tilePileListElement      = (Element) ParserUtils.nodeFromString(scanner.nextLine());
            Element remainingPlayersElement  = (Element) ParserUtils.nodeFromString(scanner.nextLine());
            Element eliminatedPlayersElement = (Element) ParserUtils.nodeFromString(scanner.nextLine());
            Element boardElement             = (Element) ParserUtils.nodeFromString(scanner.nextLine());
            Element tileToBePlacedElement    = (Element) ParserUtils.nodeFromString(scanner.nextLine());


            /* Convert input into Game representations */
            TilePile tilePile = new TilePile();
            tilePile.fromXML(tilePileListElement);

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

            game.dragonTileOwner = null;
            for (APlayer player: remainingPlayers){
                if (player.getColor() == dragonTileOwnerColor) {
                    game.dragonTileOwner = player;
                    break;
                }
            }

            /* Make a move */
            game.playTurn(tileToBePlaced, remainingPlayers.get(0));

            /* Send output to stdout */
            Document document = ParserUtils.newDocument();
            System.out.println(ParserUtils.xmlElementToString(tilePile.toXML(document)));
            System.out.println(ParserUtils.APlayerListToString(remainingPlayers, game.dragonTileOwner));
            System.out.println(ParserUtils.APlayerListToString(eliminatedPlayers, game.dragonTileOwner));
            System.out.println(ParserUtils.xmlElementToString(board.toXML(document)));
            System.out.println(game.isOver ?
                    ParserUtils.APlayerListToString(remainingPlayers, game.dragonTileOwner) :
                    "<false></false>");


        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
            return;
        }




    }
}
