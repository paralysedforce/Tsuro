from .deck import Deck
from .deck import DEFAULT_CARDS
from .map_card import MapCard

def test_init():
    card_desc = [(0,1)]
    deck = Deck([card_desc])
    card = MapCard(card_desc)

    assert card in deck._cards

def test_default_init():
    deck1 = Deck()
    deck2 = Deck(DEFAULT_CARDS)
    assert deck1 == deck2

def test_equal():
    deck1 = Deck(DEFAULT_CARDS)
    deck2 = Deck(DEFAULT_CARDS)
    assert deck1 == deck2

def test_equal_after_shuffle():
    deck1 = Deck(DEFAULT_CARDS)
    deck2 = Deck(DEFAULT_CARDS)
    deck1.shuffle()
    assert deck1 == deck2

def test_not_equal():
    deck1 = Deck([[(0,1)]])
    # Leave out first card
    deck2 = Deck(DEFAULT_CARDS)
    assert not deck1 == deck2

def test_not_equal_lengths():
    deck1 = Deck(DEFAULT_CARDS)
    # Leave out first card
    deck2 = Deck(DEFAULT_CARDS[1:])
    assert not deck1 == deck2


