package main.Players;

import javafx.util.Pair;
import main.*;

import java.util.List;

public abstract class APlayer {

    //================================================================================
    // Instance Variables
    //================================================================================
    private String name;
    private Color color;
    public SPlayer splayer;
    private List<Token> otherPlayers;
    private State curState;

    //================================================================================
    // Constructor
    //================================================================================
    public APlayer(String name, Color color){
        this.name = name;
        this.splayer = new SPlayer(this, color);
        curState = State.UNINITIALIZED;
    }

    //================================================================================
    // Getters
    //================================================================================
    public Color getColor() {
        return color;
    }

    public String getName(){
        return name;
    }

    public SPlayer getSplayer(){ return splayer; }

    //================================================================================
    // Public methods
    //================================================================================
    public void placeToken() {
        if (curState != State.INITIALIZED)
            throw new ContractException();

        Pair<BoardSpace, Integer> startingTokenLocation = getStartingLocation();
        splayer.placeToken(startingTokenLocation.getKey(), startingTokenLocation.getValue());
        curState = State.TURNPLAYABLE;
    }

    public void initialize(List<Token> otherPlayers){
        if (curState != State.UNINITIALIZED)
            throw new ContractException();

        this.otherPlayers = otherPlayers;
        this.curState = State.INITIALIZED;
    }

    public void endGame(){
        if (curState != State.TURNPLAYABLE)
            throw new ContractException();
        // Do something?

        curState = State.GAMEENDED;
    }

    public Tile chooseTile(){
        if (curState != State.TURNPLAYABLE || !splayer.isValidHand())
            throw new ContractException();

        return chooseTileHelper();
    }

    //================================================================================
    // Abstract methods
    //================================================================================
    abstract public Pair<BoardSpace, Integer> getStartingLocation();

    // TODO: Think of a better name for this method
    abstract protected Tile chooseTileHelper();


    //================================================================================
    // Sequential Contract
    //================================================================================
    private enum State {UNINITIALIZED, INITIALIZED, TURNPLAYABLE, GAMEENDED};
}
