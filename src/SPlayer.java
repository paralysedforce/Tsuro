/**
 * Created by vyasalwar on 4/16/18.
 */
public class SPlayer {

    private final int MAX_TILES_IN_BANK = 3;

    private Token token;
    private Tile[] tileBank;
    private String name;

    public SPlayer(String name, BoardSpace startingLocation, int startingTokenSpace, TilePile tilePile){
        this.name = name;
        token = new Token(startingLocation, startingTokenSpace, this);
        tileBank = new Tile[MAX_TILES_IN_BANK];

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            tileBank[i] = tilePile.drawFromDeck();
        }
    }

    public BoardSpace getBoardSpace(){
        return token.getBoardSpace();
    }

    public int getTokenSpace(){
        return token.getTokenSpace();
    }

    public String getName(){
        return name;
    }
}
