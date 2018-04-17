from .player import Player
from .deck import Deck
from .dragon_card import DragonCard

def test_player_inactive_inital():
    assert Player(Deck(), DragonCard()).is_active() 

def test_player_has_dragon_after_draw():
    dragon = DragonCard()
    deck = Deck([])

    player = Player(deck, dragon)

    assert dragon.get_holder() == player

def test_player_cannot_steal_dragon():
    dragon = DragonCard()
    deck = Deck([])

    player = Player(deck, dragon)
    player2 = Player(deck, dragon)

    assert dragon.get_holder() == player and dragon.get_holder != player2
