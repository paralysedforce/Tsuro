package main;
import java.util.Random;
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
    private Tile[] tileBank;
    private String name;
    private TilePile tilePile;


    //================================================================================
    // Constructors
    //================================================================================

    public SPlayer(String name, BoardSpace startingLocation, int startingTokenSpace, TilePile tilepile){
        this.name = name;
        token = new Token(startingLocation, startingTokenSpace, this);
        tileBank = new Tile[MAX_TILES_IN_BANK];
        this.tilePile = tilepile;

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            tileBank[i] = tilePile.drawFromDeck();
        }
    }

    public SPlayer(String name, BoardSpace startingLocation, int startingTokenSpace){
        this.name = name;
        token = new Token(startingLocation, startingTokenSpace, this);
        tileBank = new Tile[MAX_TILES_IN_BANK];
        this.tilePile = TilePile.getTilePile();

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            tileBank[i] = tilePile.drawFromDeck();
        }

    }


    //================================================================================
    // Getters
    //================================================================================
    public Token getToken(){
        return token;
    }

    public Tile getTile(int i){
        return tileBank[i];
    }

    public String getName(){
        return name;
    }

    //================================================================================
    // Public Methods
    //================================================================================
    public boolean hasTile(Tile tile){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (tileBank[i] != null && tileBank[i].equals(tile)) {
                return true;
            }
        }
        return false;
    }

    public void drawFromPile() {
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (tileBank[i] == null) {
                tileBank[i] = tilePile.drawFromDeck();
                if (tileBank[i] == null)
                    requestDragonTile();
                break;
            }
        }
    }

    public boolean hasFullHand() {
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (tileBank[i] == null)
                return false;
        }
        return true;
    }

    private void requestDragonTile(){
        Game game = Game.getGame();
        game.requestDragonTile(this);
    }

    public void removeTileFromBank(Tile tile){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (tileBank[i].equals(tile)) {
                tileBank[i] = null;
                break;
            }
        }
    }

    public void returnTilesToPile(){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if(tileBank[i] != null) {
                tilePile.returnToDeck(tileBank[i]);
                tileBank[i] = null;
            }
        }
    }

    public boolean hasLegalMove(){
        Board board = Board.getBoard();

        for (Tile tile: tileBank){
            if(tile == null)
                continue;

            Tile copy = new Tile(tile);
            for (int i = 0; i < 4; i++){
                copy.rotateClockwise();
                if (!board.willKillPlayer(copy, token))
                    return true;
            }
        }
        return false;
    }

    /* A command line UI for a player to play their tiles
    *  EXPERIMENTAL */
    public Tile chooseTile(){
        System.out.println("It is " + name + "'s turn.");
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
                if      (command.endsWith("1")) tile = tileBank[0];
                else if (command.endsWith("2")) tile = tileBank[1];
                else if (command.endsWith("3")) tile = tileBank[2];

                if (tile == null || !Game.getGame().isLegalMove(tile, this))
                    System.err.println("Error: choose a valid tile");
                else
                    return tile;
            }

            else if (command.startsWith("rotate")){
                try {
                    if      (command.endsWith("1")) tileBank[0].rotateClockwise();
                    else if (command.endsWith("2")) tileBank[1].rotateClockwise();
                    else if (command.endsWith("3")) tileBank[2].rotateClockwise();
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
                    if (tileBank[i] != null)
                        line += tileBank[i].toString();
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

}
