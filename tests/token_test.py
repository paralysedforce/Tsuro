from player_token import Token
from player import Player
from dragon_card import DragonCard
from deck import Deck


def test_eliminate_player():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    token = Token(player)

    token.eliminate()
    assert not player.is_active()
