package main.Players;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.ContractException;
import main.Game;
import main.Tile;
import main.Token;

public abstract class APlayer extends IPlayer {

    //================================================================================
    // Instance Variables
    //================================================================================


    private String name;
    private Color color;
    private List<Color> otherPlayers;
    private State curState;
    private Token token;

    protected PlayerType playerType;
    protected PlayerHand hand;

    //================================================================================
    // Constructor
    //================================================================================
    public APlayer(String name, Color color){
        this.name = name;
        this.color = color;
        curState = State.UNINITIALIZED;
        token = null;
        hand = new PlayerHand();
    }

    public APlayer(APlayer other){
        name = other.name;
        color = other.color;
        otherPlayers = new ArrayList<>(other.otherPlayers);
        curState = other.curState;
        token = other.token;
        hand = new PlayerHand();
    }

    //================================================================================
    // Getters
    //================================================================================
    public Color getColor() {
        return color;
    }

    public Token getToken(){
        return token;
    }

    public PlayerHand getHand(){return hand;}


    //================================================================================
    // Public methods
    //================================================================================
    public void placeToken() {
        if (curState != State.INITIALIZED)
            throw new ContractException();

        Pair<BoardSpace, Integer> startingTokenLocation = getStartingLocation(Game.getGame().getBoard());
        token = new Token(startingTokenLocation.getKey(), startingTokenLocation.getValue(), this);
        curState = State.TURNPLAYABLE;
    }

    //Same as placeToken but with a provided location for testing
    public void placeToken(BoardSpace startingLocation, int startingTokenSpace){
        if (curState != State.INITIALIZED)
            throw new ContractException();

        token = new Token(startingLocation, startingTokenSpace, this);
        curState = State.TURNPLAYABLE;
    }

    public final void initialize(List<Color> otherPlayers){
        if (curState != State.UNINITIALIZED)
            throw new ContractException();

        // Allow subclasses to further implement this method
        this.initialize(this.color, otherPlayers);

        this.otherPlayers = new ArrayList<>(otherPlayers);
        this.curState = State.INITIALIZED;
    }

    public void endGame(Set<Color> colors){
        if (curState != State.TURNPLAYABLE)
            throw new ContractException();

        // Do something if subclass deems appropriate
        this.endGame(Game.getGame().getBoard(), colors);

        curState = State.GAMEENDED;
    }

    // Enforces Sequential contract but delegates picking the tile to chooseTileHelper
    public Tile chooseTile(){
        if (curState != State.TURNPLAYABLE || !hand.isValid())
            throw new ContractException();

        return this.chooseTile(Game.getGame().getBoard(), Game.getGame().getTilePile().getCount());
    }


    public void drawFromDeck() {
        hand.drawFromDeck();

        // Indicates an unsuccessful draw
        if (!hand.isFull())
            requestDragonTile();
    }

    public boolean isSafeMove(Tile tile){
        return !Game.getGame().getBoard().willKillPlayer(tile, this);
    }

    public boolean hasSafeMove(){
        for (Tile tile: hand){

            Tile copy = new Tile(tile);
            for (int i = 0; i < 4; i++){
                copy.rotateClockwise();
                if (isSafeMove(copy))
                    return true;
            }
        }
        return false;
    }

    public Set<Tile> getLegalMoves(){
        Set<Tile> legalMoves = new HashSet<>();
        boolean hasSafeMoves = hasSafeMove();

        for (Tile tile: hand){
            for (int rotation = 0; rotation < 4; rotation++){
                if (!hasSafeMoves || isSafeMove(tile))
                    legalMoves.add(new Tile (tile));
                tile.rotateClockwise();
            }
        }

        return legalMoves;
    }


    //================================================================================
    // Private methods
    //================================================================================
    private void requestDragonTile(){
        Game game = Game.getGame();
        game.requestDragonTile(this);
    }

    //================================================================================
    // IPlayer methods that can be overwritten by subclasses
    //================================================================================
    public String getName(){
        return name;
    }

    void initialize(Color color, List<Color> colors) { }

    void endGame(Board board, Set<Color> colors) { }


    //================================================================================
    // Sequential Contract
    //================================================================================
    private enum State {UNINITIALIZED, INITIALIZED, TURNPLAYABLE, GAMEENDED};
}
