package main;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    // Not implemented
    public void playTile(Tile playerTile, SPlayer player){
        throw new NotImplementedException();
    }
}
