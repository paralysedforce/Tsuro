//package main;
//import javafx.util.Pair;
//import main.Players.APlayer;
//import main.Players.RandomPlayer;
//
//import java.util.*;
//
///**
// * Created by vyasalwar on 4/16/18.
// */
//public class SPlayer {
//
//
//    //================================================================================
//    // Instance Variables
//    //================================================================================
//    private final int MAX_TILES_IN_BANK = 3;
//    private Token token;
//    private List<Tile> hand;
//    private TilePile tilePile;
//    private APlayer aplayer;
//
//    //================================================================================
//    // Constructors
//    //================================================================================
//
//    public SPlayer(BoardSpace startingLocation, int startingTokenSpace, APlayer aplayer){
//        this.aplayer = aplayer;
//        token = new Token(startingLocation, startingTokenSpace, this);
//        hand = new ArrayList<>();
//        this.tilePile = Game.getGame().getTilePile();
//
//        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
//            drawFromPile();
//        }
//    }
//
//    public SPlayer(APlayer aplayer, Color color){
//        this.aplayer = aplayer;
//        token = null;
//        hand = new ArrayList<>();
//        this.tilePile = Game.getGame().getTilePile();
//
//        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
//            drawFromPile();
//        }
//    }
//
//    //================================================================================
//    // Getters
//    //================================================================================
//    public Token getToken(){
//        return token;
//    }
//
//    public Tile getTile(int i){
//        if (0 <= i && i < 3) {
//            if (i > hand.size() - 1)
//                return null;
//
//            return hand.get(i);
//        }
//        else
//            throw new IndexOutOfBoundsException("Illegal Hand Access");
//    }
//
//    //================================================================================
//    // Public Methods
//    //================================================================================
//    public boolean holdsTile(Tile tile){
//        return hand.contains(tile);
//    }
//
//    public void drawFromPile() {
//
//        if (!hasFullHand() && !tilePile.isEmpty()) {
//            Tile drawnTile = tilePile.drawFromDeck();
//
//           // TODO: This check exists only for the benefit of our tests. Refactor tests to render it unneccesary
//            if (drawnTile != null)
//                hand.add(drawnTile);
//        }
//        else
//            requestDragonTile();
//    }
//
//    public boolean hasFullHand() {
//
//        return hand.size() == MAX_TILES_IN_BANK;
//    }
//
//    public void removeTileFromHand(Tile tile){
//        hand.remove(tile);
//    }
//
//    public void returnTilesToPile(){
//        for (Tile tile: hand) {
//            tilePile.returnToDeck(tile);
//        }
//
//        hand = new ArrayList<>();
//    }
//
//    public void placeToken(BoardSpace startingLocation, int startingTokenSpace){
//        token = new Token(startingLocation, startingTokenSpace, this);
//    }
//
//    public boolean isSafeMove(Tile tile){
//        return !Game.getGame().getBoard().willKillPlayer(tile, this);
//    }
//
//    public boolean hasSafeMove(){
//        for (Tile tile: hand){
//
//            Tile copy = new Tile(tile);
//            for (int i = 0; i < 4; i++){
//                copy.rotateClockwise();
//                if (isSafeMove(copy))
//                    return true;
//            }
//        }
//        return false;
//    }
//
//    public Set<Tile> getLegalMoves(){
//        Set<Tile> legalMoves = new HashSet<>();
//        boolean hasSafeMoves = hasSafeMove();
//
//        for (Tile tile: hand){
//            for (int rotation = 0; rotation < 4; rotation++){
//                if (!hasSafeMoves || isSafeMove(tile))
//                    legalMoves.add(new Tile (tile));
//                tile.rotateClockwise();
//            }
//        }
//
//        return legalMoves;
//    }
//
//    public Tile chooseTile(){
//        return aplayer.chooseTile();
//    }
//
//    public Pair<BoardSpace, Integer> getStartingLocation(){
//        return aplayer.getStartingLocation();
//    }
//
//    public void convertToRandom(){
//        this.aplayer = new RandomPlayer(aplayer.getName(), aplayer.getColor());
//    }
//
//    public boolean isValidHand(){
//        if (hand.size() <= 3)
//            return false;
//
//        for (Tile tile: hand){
//            if (Game.getGame().getBoard().findLocationOfTile(tile) != null)
//                return false;
//        }
//
//        for (int i = 0; i < hand.size(); i++){
//            for (int j = i + 1; j < hand.size(); j++ ){
//                if (hand.get(i).equals(hand.get(j)))
//                    return false;
//            }
//        }
//
//        return true;
//    }
//
//    //================================================================================
//    // Private Helpers
//    //================================================================================
//    private void requestDragonTile(){
//        Game game = Game.getGame();
//        game.requestDragonTile(this);
//    }
//
//    //================================================================================
//    // Private Class
//    //================================================================================
//
//}
