package main;
import java.util.Random;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class SPlayer {

    private final int MAX_TILES_IN_BANK = 3;

    private Token token;
    private Tile[] tileBank;
    private String name;
    private TilePile tilePile;

    public SPlayer(String name, BoardSpace startingLocation, int startingTokenSpace){
        this.name = name;
        token = new Token(startingLocation, startingTokenSpace, this);
        tileBank = new Tile[MAX_TILES_IN_BANK];
        this.tilePile = TilePile.getTilePile();

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            tileBank[i] = tilePile.drawFromDeck();
        }
    }

    public Token getToken(){
        return token;
    }

    public boolean hasTile(Tile tile){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (tileBank[i].equals(tile)) {
                return true;
            }
        }
        return false;
    }

    public String getName(){
        return name;
    }

    public Tile getRandomTileFromBank(){
        Random random = new Random();
        return tileBank[random.nextInt(MAX_TILES_IN_BANK)];
    }

    public void drawFromPile() {
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (tileBank[i] == null) {
                tileBank[i] = tilePile.drawFromDeck();
                break;
            }
        }
    }

    public void removeTileFromBank(Tile tile){
        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            if (tileBank[i] == tile) {
                tileBank[i] = null;
                break;
            }
        }
    }

    public Turn generateTurn(){
        Turn turn = new Turn();
        return turn;
    }

    public boolean hasLegalMove(){
        Board board = Board.getBoard();

        for (Tile tile: tileBank){
            Tile copy = new Tile(tile);
            for (int i = 0; i < 4; i++){
                copy.rotateClockwise();
                if (!board.willKillPlayer(copy, token))
                    return true;
            }
        }
        return false;
    }
}
