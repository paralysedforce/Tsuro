package main;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Turn {
    public static enum TurnAction {PLAYABLE, RESIGN, TILE};

    private Tile tile;
    private TurnAction action;

    public TurnAction getAction() {
        return action;
    }

    public Turn(){
        action = TurnAction.PLAYABLE;
        tile = null;
    }

    public void resign(){
        if (action == TurnAction.PLAYABLE)
            action = TurnAction.RESIGN;
    }

    public void playTile(Tile playerTile, SPlayer player){
        if (action == TurnAction.PLAYABLE){
            Board board = Board.getBoard();
            if (board.isLegalMove(playerTile, player)) {
                action = TurnAction.TILE;
                tile = playerTile;
            }
        }
    }
}
