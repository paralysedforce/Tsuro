/**
 * Created by vyasalwar on 4/16/18.
 */
public class Token {

    private BoardSpace space;
    private int tokenSpace;
    private SPlayer player;

    public Token(BoardSpace startingLocation, int startingTokenSpace, SPlayer player){
        space = startingLocation;
        tokenSpace = startingTokenSpace;
        this.player = player;

        space.addToken(this, startingTokenSpace);
    }

    public void setBoardSpace(BoardSpace location, int tokenSpace){
        space = location;
        this.tokenSpace = tokenSpace;
    }

    public void setTokenSpace(int tokenSpace){
        this.tokenSpace = tokenSpace;
    }

    public BoardSpace getBoardSpace(){
        return space;
    }

    public int getTokenSpace(){
        return tokenSpace;
    }

    public SPlayer getPlayer(){
        return player;
    }
}
