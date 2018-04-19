from board import Token
from deck import Deck
from dragon_card import DragonCard
from player import Player


def test_eliminate_player():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    token = Token(player)

    token.eliminate()
    assert not player.is_active()
