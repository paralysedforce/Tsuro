package main;

import javafx.util.Pair;

public abstract class APlayer {

    //================================================================================
    // Instance Variables
    //================================================================================
    private String name;
    private Color color;
    protected SPlayer splayer;

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


    //================================================================================
    // Abstract methods
    //================================================================================
    abstract protected Pair<BoardSpace, Integer> getStartingLocation();

    abstract public Tile chooseTile();

    abstract public void endGame();
}
