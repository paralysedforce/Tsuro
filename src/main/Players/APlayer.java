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
    private boolean isTurn;
    public SPlayer splayer;
    private List<Token> otherPlayers;

    //================================================================================
    // Constructor
    //================================================================================
    public APlayer(String name, Color color){
        this.name = name;
        this.splayer = new SPlayer(this, color);
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
        Pair<BoardSpace, Integer> startingTokenLocation = getStartingLocation();
        splayer.placeToken(startingTokenLocation.getKey(), startingTokenLocation.getValue());
    }

    public void endGame(){}



    //================================================================================
    // Abstract methods
    //================================================================================
    abstract public Pair<BoardSpace, Integer> getStartingLocation();

    abstract public Tile chooseTile();


}
