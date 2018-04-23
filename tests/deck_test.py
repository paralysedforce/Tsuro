from deck import Deck
from board import PathTile


def Tile(i, j):
    """Create a PathTile with a single connection."""
    # Just using this to make the tests read a bit easier.
    return PathTile([(i, j)])


def test_deck_equality():
    assert Deck([]) == Deck([])
    assert not Deck([Tile(0, 1)]) == Deck([])

    assert Deck([Tile(0, 1)]) == Deck([Tile(0, 1)])
    assert not Deck([Tile(0, 1), Tile(2, 3)]) == Deck([Tile(0, 1)]), 'different lengths'

    assert Deck([Tile(0, 1), Tile(2, 3)]) == Deck([Tile(0, 1), Tile(2, 3)])
    assert not Deck([Tile(2, 3), Tile(0, 1)]) == Deck([Tile(0, 1), Tile(2, 3)]), 'ordering matters'


def test_deck_len():
    assert len(Deck([])) == 0
    assert len(Deck([Tile(0, 1)])) == 1
    assert len(Deck([Tile(0, 1), Tile(2, 3)])) == 2


def test_deck_contains():
    assert Tile(0, 1) in Deck([Tile(0, 1)])
    assert Tile(0, 2) not in Deck([Tile(0, 1)])


def test_deck_factories():
    assert Deck([PathTile([(0, 1)])]) == Deck.from_connections([[(0, 1)]])


def test_deck_draw():
    deck = Deck([])
    assert deck.draw() is None

    deck = Deck([Tile(0, 1)])
    assert deck.draw() == Tile(0, 1)
    assert deck.draw() is None

    deck = Deck([Tile(0, 1), Tile(2, 3)])
    assert deck.draw() == Tile(0, 1), 'draws from top of the deck'
    assert deck.draw() == Tile(2, 3)
    assert deck.draw() is None


def test_deck_replace_tiles():
    # from empty deck
    deck = Deck([])
    assert deck.draw() is None
    deck.replace_tiles([Tile(0, 1)])
    assert deck.draw() == Tile(0, 1)
    assert deck.draw() is None

    # from non-empty deck
    deck = Deck([Tile(0, 1)])
    deck.replace_tiles([Tile(2, 3)])
    assert deck.draw() == Tile(0, 1), 'existing cards are drawn first'
    assert deck.draw() == Tile(2, 3)
    assert deck.draw() is None
