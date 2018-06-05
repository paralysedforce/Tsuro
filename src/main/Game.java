package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.Players.APlayer;
import main.Players.LeastSymmetricPlayer;
import main.Players.MostSymmetricPlayer;
import main.Players.PlayerType;
import main.Players.RandomPlayer;

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
    // TODO: Remove in production
    public void registerPlayer(APlayer player){
        remainingPlayers.add(player);
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, APlayer player){
        if(!player.getHand().holdsTile(tile))
            return false;

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

    /* TODO: MAKE PRIVATE WHEN NOT DEBUGGING */
    // Deal with when the player place the tile on the board
    //   Returns a set of players who have lost after the tile is placed
    public Set<APlayer> playTurn(Tile tile, APlayer player) throws ContractException{
            if (!isLegalMove(tile, player)) {
                throw new ContractException("Player made an illegal move");
            }

            // Place tile and move players
            Set<Token> failedTokens = board.placeTile(tile, player);
            Set<APlayer> failedPlayers = new HashSet<>();
            for (Token failedToken : failedTokens)
                failedPlayers.add(failedToken.getPlayer());

            if (failedPlayers.containsAll(remainingPlayers))
                return failedPlayers;

            // Update hands
            player.getHand().removeTile(tile);
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

    /**
     * Matches the return specs of the assignment, calls playTurn as appropriate
     * @return null if the game is not over, list of winning players if the game is over.
     */
    public HashSet<APlayer> playATurn(Tile tile, APlayer player) throws ContractException {

        // Cheating occurs here and throws ContractException
        Set<APlayer> playersEliminatedThisTurn = playTurn(tile, player);

        // Check for end game conditions that don't result in one winner in addition to one winner.
        if(playersEliminatedThisTurn.containsAll(remainingPlayers) || remainingPlayers.size() <= 1){
            return new HashSet<APlayer>(remainingPlayers);
        }
        if(tilePile.isEmpty() && areAllRemainingHandsEmpty()){
            return new HashSet<APlayer>(remainingPlayers);
        }


        // Game not ended. Update player lists.
        if(!eliminatedPlayers.contains(player)){
            remainingPlayers.remove(player);
            remainingPlayers.add(player);
        }
        this.eliminatedPlayers.addAll(playersEliminatedThisTurn);
        this.remainingPlayers.removeAll(playersEliminatedThisTurn);

        return null;
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
            // Get the chosen move
            APlayer playingPlayer = remainingPlayers.get(0);
            Tile tile = playingPlayer.chooseTile(tilePile.getCount());

            Set<APlayer> winningPlayers;
            try {
                // Update winning players
                winningPlayers = playATurn(tile, playingPlayer);
            }
            catch (ContractException e) {
                // Detect cheating
                remainingPlayers.remove(playingPlayer);
                playingPlayer = blamePlayer(playingPlayer);
                remainingPlayers.add(0, playingPlayer);
                continue;
            }

            if (winningPlayers != null) {
                Set<Color> winningPlayerColors = new HashSet<>();
                for (APlayer player : remainingPlayers) {
                    winningPlayerColors.add(player.getColor());
                }

                for (APlayer player : remainingPlayers){
                    player.endGame(winningPlayerColors);
                }

                return winningPlayers;

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

            Color dragonTileOwnerColor = ParserUtils.findDragonTilePlayer(remainingPlayersElement).getColor();
            List<APlayer> remainingPlayers = ParserUtils.APlayerListFromNode(remainingPlayersElement);
            List<APlayer> eliminatedPlayers = ParserUtils.APlayerListFromNode(eliminatedPlayersElement);


            Board board = new Board();
            board.fromXML(boardElement);

            Tile tileToBePlaced = new Tile();
            tileToBePlaced.fromXML(tileToBePlacedElement);

            /* Update Game fields */
            game.board = board;
            game.tilePile = tilePile;
            game.remainingPlayers = remainingPlayers;
            game.eliminatedPlayers = eliminatedPlayers;
            game.dragonTileOwner = board.findToken(dragonTileOwnerColor).getPlayer();

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
