/**
 * Represents a Tsuro Board
 *
 * Created by vyasalwar on 4/16/18.
 */
public class Board {
    final int BOARD_LENGTH = 6;

    private BoardSpace[][] spaces;

    public Board() {
        this.spaces = new BoardSpace[BOARD_LENGTH][BOARD_LENGTH];

        for (int i = 0; i < BOARD_LENGTH; i++){
            for (int j = 0; j < BOARD_LENGTH; j++){
                spaces[i][j] = new BoardSpace(i, j);
            }
        }
    }

    public boolean isOccupied(int row, int col){
        return  (0 <= row && row < BOARD_LENGTH) &&
                (0 <= col && col < BOARD_LENGTH) &&
                spaces[row][col].hasTile();
    }

    public boolean placeTile(Tile tile, Player player){
        BoardSpace space = player.getNextSpace();
        spaces[space.getRow()][space.getCol()] = space;
        // this.advance();
    }

    public boolean isLegalMove(Tile tile, Player player){

    }

}
