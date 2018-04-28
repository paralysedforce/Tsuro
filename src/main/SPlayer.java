package main;
import java.util.Scanner;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class SPlayer {


    //================================================================================
    // Instance Variables
    //================================================================================
    private final int MAX_TILES_IN_BANK = 3;
    private Token token;
    private Tile[] hand;
    private TilePile tilePile;
    private APlayer aplayer;

    //================================================================================
    // Constructors
    //================================================================================

    public SPlayer(BoardSpace startingLocation, int startingTokenSpace, APlayer aplayer){
        this.aplayer = aplayer;
        token = new Token(startingLocation, startingTokenSpace, this);
        hand = new Tile[MAX_TILES_IN_BANK];
        this.tilePile = Game.getGame().getTilePile();

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            hand[i] = tilePile.drawFromDeck();
        }
    }

    public SPlayer(APlayer aplayer){
        this.aplayer = aplayer;
        token = null;
        hand = new Tile[MAX_TILES_IN_BANK];
        this.tilePile = Game.getGame().getTilePile();

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            hand[i] = tilePile.drawFromDeck();
        }
    }

    //================================================================================
    // Getters
    //================================================================================
    public Token getToken(){
        return token;
    }

    public Tile getTile(int i){
        return hand[i];
    }

    //================================================================================
    // Public Methods
    //================================================================================
    public boolean holdsTile(Tile tile){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (hand[i] != null && hand[i].equals(tile)) {
                return true;
            }
        }
        return false;
    }

    public void drawFromPile() {
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (hand[i] == null) {
                hand[i] = tilePile.drawFromDeck();
                if (hand[i] == null)
                    requestDragonTile();
                break;
            }
        }
    }

    public boolean hasFullHand() {
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (hand[i] == null)
                return false;
        }
        return true;
    }

    public void removeTileFromHand(Tile tile){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (hand[i].equals(tile)) {
                hand[i] = null;
                break;
            }
        }
    }

    public void returnTilesToPile(){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if(hand[i] != null) {
                tilePile.returnToDeck(hand[i]);
                hand[i] = null;
            }
        }
    }

    public void placeToken(BoardSpace startingLocation, int startingTokenSpace){
        token = new Token(startingLocation, startingTokenSpace, this);
    }


    public boolean isSafeMove(Tile tile){
        return !Game.getGame().getBoard().willKillPlayer(tile, this);
    }

    public boolean hasSafeMove(){
        Board board = Game.getGame().getBoard();

        for (Tile tile: hand){
            if(tile == null)
                continue;

            Tile copy = new Tile(tile);
            for (int i = 0; i < 4; i++){
                copy.rotateClockwise();
                if (!board.willKillPlayer(copy, this))
                    return true;
            }
        }
        return false;
    }

    /* A command line UI for a player to play their tiles
    *  EXPERIMENTAL */
    public Tile chooseTile(){
//        System.out.println("It is " + name + "'s turn.");
        System.out.println("Type help to see commands");
        /* For input */
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine();
            if (command.startsWith("help")){
                System.out.println("Available Commands are:");
                System.out.println("\tchoose [tile] - Try to place the chosen tile");
                System.out.println("\trotate [tile] - Rotate tile");
                System.out.println("\tdisplay       - Display tiles currently in hand");
            }

            else if (command.startsWith("choose")){
                Tile tile = null;
                if      (command.endsWith("1")) tile = hand[0];
                else if (command.endsWith("2")) tile = hand[1];
                else if (command.endsWith("3")) tile = hand[2];

                if (tile == null || !Game.getGame().isLegalMove(tile, this))
                    System.err.println("Error: choose a valid tile");
                else
                    return tile;
            }

            else if (command.startsWith("rotate")){
                try {
                    if      (command.endsWith("1")) hand[0].rotateClockwise();
                    else if (command.endsWith("2")) hand[1].rotateClockwise();
                    else if (command.endsWith("3")) hand[2].rotateClockwise();
                    else                         throw new NullPointerException();

                    System.out.println("Tile Rotated");
                }
                catch (NullPointerException e){
                    System.err.println("Error: No tile found");
                }
            }

            else if (command.startsWith("display")){
                System.out.println("Displaying tiles in hand...");
                for (int i = 0; i < MAX_TILES_IN_BANK; i++){
                    String line = "\t" + (i+1) + ": ";
                    if (hand[i] != null)
                        line += hand[i].toString();
                    else
                        line += "No tile present";

                    System.out.println(line);
                }
            }
            else {
                System.out.println("Command not understood");
            }
        }

    }

    //================================================================================
    // Private Helpers
    //================================================================================
    private void requestDragonTile(){
        Game game = Game.getGame();
        game.requestDragonTile(this);
    }

}
