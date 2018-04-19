from board import TokenSpot
from deck import Deck
from dragon_card import DragonCard
from player import Player
from player_token import Token


def create_token():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    token = Token(player)
    return token


def test_token_occupant_arrival_adjacent():
    token = create_token()

    token_spot = TokenSpot()
    token_spot.receive_token_via_adjacent(token)
    assert token == token_spot.get_occupant()


def test_token_occupant_arrival_path():
    token = create_token()

    token_spot = TokenSpot()
    token_spot.receive_token_via_path(token)
    assert token == token_spot.get_occupant()


def test_adjacent_static_pair():
    token_spot1 = TokenSpot()
    token_spot2 = TokenSpot()
    TokenSpot.pair_adjacent(token_spot1, token_spot2)
    assert token_spot1._next_card == token_spot2
    assert token_spot2._next_card == token_spot1


def test_path_static_pair():
    token_spot1 = TokenSpot()
    token_spot2 = TokenSpot()
    TokenSpot.pair_path(token_spot1, token_spot2)
    assert token_spot1._next_spot == token_spot2
    assert token_spot2._next_spot == token_spot1


def test_token_passed_adjacent():
    token = create_token()

    token_spot1 = TokenSpot()
    token_spot2 = TokenSpot()

    TokenSpot.pair_adjacent(token_spot1, token_spot2)

    token_spot1.receive_token_via_path(token)

    assert token_spot1.get_occupant() is None
    assert token_spot2.get_occupant() == token


def test_token_passed_path():
    token = create_token()

    token_spot1 = TokenSpot()
    token_spot2 = TokenSpot()

    TokenSpot.pair_path(token_spot1, token_spot2)

    token_spot1.receive_token_via_adjacent(token)

    assert token_spot1.get_occupant() is None
    assert token_spot2.get_occupant() == token


def test_terminal_elimination():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    token = Token(player)

    token_spot = TokenSpot(True)
    token_spot.receive_token_via_path(token)

    assert not player.is_active()
