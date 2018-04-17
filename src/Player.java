/**
 * Created by vyasalwar on 4/16/18.
 */
public class Player {

    private Token token;
    private Tile[] tileBank;
    private String name;

    public Player(String name, BoardSpace startingLocation, int startingTokenSpace){
        this.name = name;
        token = new Token(startingLocation, startingTokenSpace, this);
        tileBank = new Tile[3];
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
