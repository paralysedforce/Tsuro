package main;

import javafx.util.Pair;

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
        this.color = color;
        this.splayer = new SPlayer(this);
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
    abstract protected Pair<BoardSpace, Integer> getStartingLocation();

    abstract public Tile chooseTile();


}
