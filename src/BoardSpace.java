/**
 * Created by vyasalwar on 4/16/18.
 */
public class BoardSpace {
    private final int NUM_SPACES = 8;

    private Tile tile;
    private Token[] tokenSpaces;
    public int row;
    private int col;

    public BoardSpace(int row, int col){
        tile = null;
        tokenSpaces = new Token[NUM_SPACES];
        this.row = row;
        this.col = col;
    }

    public boolean hasTile(){
        return tile != null;
    }

    public Tile getTile(){
        return tile;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public void setTile(Tile tile){
        if (!hasTile())
            this.tile = tile;
        else
            throw new IllegalArgumentException("BoardSpace already occupied");
    }
}
