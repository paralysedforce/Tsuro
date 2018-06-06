package test.GameTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javafx.util.Pair;
import main.Board;
import main.BoardSpace;
import main.Color;
import main.Game;
import main.NetworkMessage;
import main.Players.APlayer;
import main.Players.PlayerHand;
import main.Players.RandomPlayer;
import main.Tile;
import main.TilePile;
import main.Token;
import test.ParserTests.TokenParsingTest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameTest {

    Game game;
    Board board;

    @Mock
    TilePile tilePileMock;

    @Before
    public void reset() {
        Game.resetGame();
        game = Game.getGame();
        board = game.getBoard();
        game.setTilePile(tilePileMock);
    }


    @Test
    public void isLegalMoveIsTrueWithLegalMove(){
        Tile testTile = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        APlayer player = new RandomPlayer("Keith", Color.SIENNA);
        player.initialize(new ArrayList<>());
        player.placeToken(space, 0);
        game.registerPlayer(player);

        Assert.assertTrue(game.isLegalMove(testTile, player));
    }

    @Test
    public void isLegalMoveIsTrueWithNoMoves() {
        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        APlayer player = new RandomPlayer("Keith", Color.SIENNA);
        player.initialize(new ArrayList<>());
        player.placeToken(space, 0);
        game.registerPlayer(player);

        Assert.assertNotNull(player.getHand());
        Assert.assertTrue(game.isLegalMove(testTile, player));
    }

    @Test
    public void isLegalMoveFalseWithOtherMove() {


        Tile testTileCantMove = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile testTileCanMove = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTileCantMove)
                .thenReturn(testTileCanMove)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        APlayer player = new RandomPlayer("Keith", Color.SIENNA);
        player.initialize(new ArrayList<>());
        player.placeToken(space, 0);
        game.registerPlayer(player);

        Assert.assertTrue(game.isLegalMove(testTileCanMove, player));
        Assert.assertFalse(game.isLegalMove(testTileCantMove, player));
    }

    @Test
    public void isLegalMoveIsFalseWithRotationMove() {


        Tile testTile = new Tile(0, 1, 2, 3, 4, 6, 5, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        APlayer player = new RandomPlayer("Keith", Color.SIENNA);
        player.initialize(new ArrayList<>());
        player.placeToken(space, 0);
        game.registerPlayer(player);

        Assert.assertFalse(game.isLegalMove(testTile, player));
    }

    @Test
    public void playMoveEliminatesPlayersThatLose() {
        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.isEmpty())
                .thenReturn(false)
                .thenReturn(true);
        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null);

        BoardSpace spaceOne = board.getBoardSpace(0, 0);
        BoardSpace spaceTwo = board.getBoardSpace(3, 5);
        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE);
        vyas.initialize(new ArrayList<>());
        vyas.placeToken(spaceOne, 0);
        APlayer keith = new RandomPlayer("Keith", Color.SIENNA);
        keith.initialize(new ArrayList<>());
        keith.placeToken(spaceTwo, 2);

        game.registerPlayer(vyas);
        game.registerPlayer(keith);
        game.playTurn(testTile, vyas);

        Assert.assertNull(vyas.getHand().getTile(0));
        Assert.assertNull(vyas.getHand().getTile(1));
        Assert.assertNull(vyas.getHand().getTile(2));
        // Removed because tournament expects to still know eliminated player positions
//        Assert.assertEquals(spaceOne.findToken(vyas.getToken()), -1);
//        Assert.assertNull(vyas.getToken().getBoardSpace());
        Assert.assertEquals(spaceOne.findToken(vyas.getToken()), 1);
    }

    @Test
    public void dragonTileWithNoneDrawnTest() {


        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile, testTile, null)
                .thenReturn(testTile, testTile, null)
                .thenReturn(testTile, testTile, null)
                .thenReturn(testTile, testTile, null)
                .thenReturn(null)
                .thenReturn(testTile);
        when(tilePileMock.isEmpty())
                .thenReturn(false, false, true)
                .thenReturn(false, false, true)
                .thenReturn(false, false, true)
                .thenReturn(false, false, true)
                .thenReturn(false);

        BoardSpace spaceOne = board.getBoardSpace(1, 0);
        BoardSpace spaceTwo = board.getBoardSpace(5, 5);

        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE);
        vyas.initialize(new ArrayList<>());
        vyas.placeToken(spaceOne, 7);
        APlayer keith =  new RandomPlayer("Keith", Color.SIENNA);
        keith.initialize(new ArrayList<>());
        keith.placeToken(spaceOne, 6);
        APlayer robby =  new RandomPlayer("Robby", Color.HOTPINK);
        robby.initialize(new ArrayList<>());
        robby.placeToken(spaceTwo, 2);
        APlayer christos =  new RandomPlayer("Christos", Color.GREEN);
        christos.initialize(new ArrayList<>());
        christos.placeToken(spaceTwo, 5);

        game.registerPlayer(vyas);
        game.registerPlayer(keith);
        game.registerPlayer(robby);
        game.registerPlayer(christos);

        Assert.assertEquals(game.playTurn(testTile, vyas).size(), 2);
        Assert.assertTrue(robby.getHand().isFull());
        Assert.assertTrue(christos.getHand().isFull());

        verify(tilePileMock, times(14)).drawFromDeck();
    }

    @Test
    public void testLegalFromDebug() {

        APlayer player = new RandomPlayer("p", Color.RED) {
            @Override
            public Pair<BoardSpace, Integer> placeToken(BoardSpace startingLocation, int startingTokenSpace){

                this.token = new Token(startingLocation, startingTokenSpace, getColor());

                return new Pair<>(startingLocation, startingTokenSpace);
            }
        };

        TokenParsingTest.MockBoard board = new TokenParsingTest.MockBoard() {
            @Override
            public void setup() {
                Tile t = new Tile(0,1, 2, 7, 3, 4, 5, 6);
                this.getBoardSpace(2, 5).setTile(t);
            }
        };
        board.setup();
        player.placeToken(board.getBoardSpace(3, 5), 0);

        PlayerHand hand = new PlayerHand() {
            boolean alreadyDrew = false;
            @Override
            public void drawFromDeck() {
                if (!alreadyDrew) {
                    Tile t = new Tile(0,1,2,7,3,6,4,5);
                    this.hand.add(t);
                    alreadyDrew = true;
                }
            }
        };

        player.setHand(hand);
        player.setBoard(board);

        Set<Tile> legalMoves = player.getLegalMoves();
        for (Tile t : legalMoves) {
            System.out.println(t);
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                System.out.println(NetworkMessage.xmlElementToString(t.toXML(doc)));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

        }
        Assert.assertEquals(1, legalMoves.size());
    }

    @Test
    public void testLegalFromDebug2() throws IOException, SAXException, ParserConfigurationException {
        Board b = new Board();

        b.fromXML(
                (Element) NetworkMessage.nodeFromString("<board><map><ent><xy><x>3</x><y>3</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>7</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>2</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>5</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>2</x><y>1</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>4</x><y>4</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>6</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>5</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>4</n></connect></tile></ent><ent><xy><x>3</x><y>1</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>3</n></connect><connect><n>4</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>3</x><y>5</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>3</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>1</x><y>4</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>3</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>3</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>5</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>5</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>4</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>6</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>4</x><y>3</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>4</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>7</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>3</x><y>4</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>4</n></connect><connect><n>3</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>1</x><y>3</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>2</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>3</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>4</x><y>5</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>6</n></connect><connect><n>3</n><n>5</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>5</x><y>2</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>6</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>3</x><y>2</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>4</n></connect><connect><n>6</n><n>7</n></connect></tile></ent></map><map><ent><color>red</color><pawn-loc><v></v><n>4</n><n>4</n></pawn-loc></ent><ent><color>blue</color><pawn-loc><h></h><n>1</n><n>5</n></pawn-loc></ent></map></board>\n")
        );
        PlayerHand hand = new PlayerHand();
        hand.fromXML(
                (Element) NetworkMessage.nodeFromString("<set><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>2</n></connect><connect><n>4</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>6</n></connect></tile><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>4</n></connect></tile></set>\n")
        );

        APlayer player = new RandomPlayer("randy", Color.RED) {
            @Override
            public Pair<BoardSpace, Integer> placeToken(BoardSpace startingLocation, int startingTokenSpace){
                token = new Token(startingLocation, startingTokenSpace, getColor());

                return new Pair<>(startingLocation, startingTokenSpace);
            }
        };
        player.setBoard(b);
        player.setHand(hand);
        player.placeToken(board.getBoardSpace(2, 4), 7);
//        System.out.println(NetworkMessage.xmlElementToString(b.toXML(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())));
        Tile badTile = new Tile(0, 3, 1, 2, 4, 6, 5, 7);
        Assert.assertFalse(player.isSafeMove(badTile));

        Assert.assertEquals(3, player.getLegalMoves().size());
        Assert.assertFalse(player.getLegalMoves().contains(badTile));
    }

    @Test
    public void testLegalFromDebug3() throws IOException, SAXException, ParserConfigurationException {
        Board b = new Board();

        b.fromXML(
                (Element) NetworkMessage.nodeFromString("<board><map><ent><xy><x>3</x><y>3</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>2</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>3</n></connect><connect><n>4</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile></ent><ent><xy><x>2</x><y>1</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>7</n></connect><connect><n>2</n><n>3</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>4</x><y>0</y></xy><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>5</n></connect><connect><n>6</n><n>7</n></connect></tile></ent><ent><xy><x>1</x><y>4</y></xy><tile><connect><n>0</n><n>5</n></connect><connect><n>1</n><n>4</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>3</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>7</n></connect></tile></ent><ent><xy><x>4</x><y>2</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>0</x><y>5</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>7</n></connect><connect><n>4</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>0</y></xy><tile><connect><n>0</n><n>7</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>4</n></connect><connect><n>3</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>5</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>2</x><y>4</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>7</n></connect><connect><n>3</n><n>4</n></connect><connect><n>5</n><n>6</n></connect></tile></ent><ent><xy><x>1</x><y>1</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>3</n></connect><connect><n>2</n><n>5</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>4</x><y>3</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>5</n></connect><connect><n>3</n><n>6</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>3</x><y>0</y></xy><tile><connect><n>0</n><n>2</n></connect><connect><n>1</n><n>6</n></connect><connect><n>3</n><n>7</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>2</x><y>2</y></xy><tile><connect><n>0</n><n>6</n></connect><connect><n>1</n><n>2</n></connect><connect><n>3</n><n>7</n></connect><connect><n>4</n><n>5</n></connect></tile></ent><ent><xy><x>0</x><y>4</y></xy><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>6</n></connect><connect><n>2</n><n>5</n></connect><connect><n>4</n><n>7</n></connect></tile></ent><ent><xy><x>3</x><y>2</y></xy><tile><connect><n>0</n><n>1</n></connect><connect><n>2</n><n>7</n></connect><connect><n>3</n><n>6</n></connect><connect><n>4</n><n>5</n></connect></tile></ent></map><map><ent><color>red</color><pawn-loc><v></v><n>5</n><n>4</n></pawn-loc></ent><ent><color>blue</color><pawn-loc><h></h><n>4</n><n>9</n></pawn-loc></ent></map></board>\n")
        );
        PlayerHand hand = new PlayerHand();
        hand.fromXML(
                (Element) NetworkMessage.nodeFromString("<set><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>2</n></connect><connect><n>4</n><n>6</n></connect><connect><n>5</n><n>7</n></connect></tile><tile><connect><n>0</n><n>3</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>4</n><n>7</n></connect></tile><tile><connect><n>0</n><n>4</n></connect><connect><n>1</n><n>5</n></connect><connect><n>2</n><n>6</n></connect><connect><n>3</n><n>7</n></connect></tile></set>\n")
        );

        APlayer player = new RandomPlayer("randy", Color.RED) {
            @Override
            public Pair<BoardSpace, Integer> placeToken(BoardSpace startingLocation, int startingTokenSpace){
                token = new Token(startingLocation, startingTokenSpace, getColor());

                return new Pair<>(startingLocation, startingTokenSpace);
            }
        };
        player.setBoard(b);
        player.setHand(hand);
        player.placeToken(board.getBoardSpace(2, 5), 7);
        System.out.println(NetworkMessage.xmlElementToString(b.toXML(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())));
        Tile badTile = new Tile(0, 4, 1, 5, 2, 6, 3, 7);
        Assert.assertFalse(player.isSafeMove(badTile));

//        Assert.assertEquals(3, player.getLegalMoves().size());
        for (Tile t : player.getLegalMoves()) {
            System.out.println(NetworkMessage.xmlElementToString((Element) t.toXML(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())));
        }
        Assert.assertFalse(player.getLegalMoves().contains(badTile));
    }


}
