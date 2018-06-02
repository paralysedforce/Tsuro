package main.Players;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;
import main.*;

public abstract class APlayer extends IPlayer {

    //================================================================================
    // Instance Variables
    //================================================================================


    private String name;
    private Color color;
    private List<Color> otherPlayers;
    private State curState;
    protected Token token;

    protected PlayerType playerType;
    protected PlayerHand hand;
    protected Board board;

    //================================================================================
    // Constructor
    //================================================================================
    public APlayer(String name, Color color){
        this.name = name;
        this.color = color;
        curState = State.UNINITIALIZED;
        token = null;
        hand = new PlayerHand();
        board = Game.getGame().getBoard();
    }

    public APlayer(APlayer other){
        name = other.name;
        color = other.color;
        otherPlayers = new ArrayList<>(other.otherPlayers);
        curState = other.curState;
        token = other.token;
        board = other.board;
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
    // Setters
    //================================================================================

    public void setColor(Color color) {this.color = color;}

    public void setBoard(Board board) {
        this.board = board;
        // Player should be on board too if it was not already
        if (token != null)
            board.updateToken(token);
    }


    //================================================================================
    // Public methods
    //================================================================================
    public Pair<BoardSpace, Integer> placeToken() {
        if (curState != State.INITIALIZED)
            throw new ContractException();

        Pair<BoardSpace, Integer> startingTokenLocation = getStartingLocation(board);
        token = new Token(startingTokenLocation.getKey(), startingTokenLocation.getValue(), this);
        curState = State.TURNPLAYABLE;

        return startingTokenLocation;
    }

    //Same as placeToken but with a provided location for testing
    public Pair<BoardSpace, Integer> placeToken(BoardSpace startingLocation, int startingTokenSpace){
        if (curState != State.INITIALIZED)
            throw new ContractException();

        token = new Token(startingLocation, startingTokenSpace, this);
        curState = State.TURNPLAYABLE;

        return new Pair<>(startingLocation, startingTokenSpace);
    }

    public final void initialize(List<Color> otherPlayers){
        if (!(curState == State.UNINITIALIZED || curState == State.GAMEENDED))
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
        this.endGame(board, colors);

        curState = State.GAMEENDED;
    }

    // Enforces Sequential contract but delegates picking the tile to chooseTileHelper
    public Tile chooseTile(int numTilesLeft){
        if (curState != State.TURNPLAYABLE)// || !hand.isValid())
            throw new ContractException("State is " + curState + " and hand is valid=" + hand.isValid());

        Set<Tile> hand = new HashSet<>();
        for (Tile aTile : this.hand) {
            hand.add(aTile);
        }
        return this.chooseTile(board, hand, numTilesLeft);
    }


    public void drawFromDeck() {
        hand.drawFromDeck();

        // Indicates an unsuccessful draw
        if (!hand.isFull())
            requestDragonTile();
    }

    public boolean isSafeMove(Tile tile){
        return !board.willKillPlayer(tile, this);
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

    public void setHand(PlayerHand hand) {
        this.hand = hand;
    }


    //================================================================================
    // Sequential Contract
    //================================================================================
    private enum State {UNINITIALIZED, INITIALIZED, TURNPLAYABLE, GAMEENDED};
}
