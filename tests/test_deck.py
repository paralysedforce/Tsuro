from board import PathTile
from deck import Deck

from _helpers import TileAlias as T


def test_deck_equality():
    assert Deck([]) == Deck([])
    assert not Deck([T(0, 1)]) == Deck([])

    assert Deck([T(0, 1)]) == Deck([T(0, 1)])
    assert not Deck([T(0, 1), T(2, 3)]) == Deck([T(0, 1)]), 'different lengths'

    assert Deck([T(0, 1), T(2, 3)]) == Deck([T(0, 1), T(2, 3)])
    assert not Deck([T(2, 3), T(0, 1)]) == Deck([T(0, 1), T(2, 3)]), 'ordering matters'


def test_deck_len():
    assert len(Deck([])) == 0
    assert len(Deck([T(0, 1)])) == 1
    assert len(Deck([T(0, 1), T(2, 3)])) == 2


def test_deck_contains():
    assert T(0, 1) in Deck([T(0, 1)])
    assert T(0, 2) not in Deck([T(0, 1)])


def test_deck_factories():
    assert Deck([PathTile([(0, 1)])]) == Deck.from_connections([[(0, 1)]])


def test_deck_draw():
    deck = Deck([])
    assert deck.draw() is None, 'draw from an empty deck returns None'

    deck = Deck([T(0, 1)])
    assert deck.draw() == T(0, 1), 'draw from a deck with a single card'
    assert deck.draw() is None

    deck = Deck([T(0, 1), T(2, 3)])
    assert deck.draw() == T(0, 1), 'draws from top of the deck'
    assert deck.draw() == T(2, 3)
    assert deck.draw() is None


def test_deck_replace_tiles():
    # from empty deck
    deck = Deck([T(0, 1)])
    deck.replace_tiles([T(2, 3)])
    assert deck.draw() == T(0, 1), 'existing cards are drawn first'
    assert deck.draw() == T(2, 3), 'replaced card is drawn second'
    assert deck.draw() is None


def test_deck_state():
    deck = Deck([])
    assert deck.state() == [], 'empty deck'

    deck = Deck([T(0, 1)])
    assert deck.state() == [T(0, 1)], 'deck with one tile'
    deck.draw()
    assert deck.state() == [], 'state updates when card is drawn'

    deck = Deck([T(0, 1), T(2, 3), T(4, 5)])
    assert deck.state() == [T(0, 1), T(2, 3), T(4, 5)], 'deck with many tiles'
    deck.draw()
    assert deck.state() == [T(2, 3), T(4, 5)], 'drawing the top card updates state'


def test_deck_from_state():
    state = [T(0, 1), T(2, 3), T(4, 5)]
    assert Deck.from_state(state) == Deck(state)
