from .token_spot import TokenSpot
from .player import Player
from .deck import Deck
from .dragon_card import DragonCard
from .token import Token

def create_token():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    token = Token(player)
    return token

def test_token_occupant_arrival_adjacent():
    token = create_token()

    token_spot = TokenSpot()
    token_spot.arrive_via_adjacent(token)
    assert token == token_spot.get_occupant()


def test_token_occupant_arrival_path():
    token = create_token()

    token_spot = TokenSpot()
    token_spot.arrive_via_path(token)
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

    token_spot1.arrive_via_path(token)

    assert token_spot1.get_occupant() is None
    assert token_spot2.get_occupant() == token

def test_token_passed_path():
    token = create_token()

    token_spot1 = TokenSpot()
    token_spot2 = TokenSpot()

    TokenSpot.pair_path(token_spot1, token_spot2)

    token_spot1.arrive_via_adjacent(token)

    assert token_spot1.get_occupant() is None
    assert token_spot2.get_occupant() == token

def test_terminal_elimination():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    token = Token(player)

    token_spot = TokenSpot(True)

    assert token_spot._did_eliminate(token)

def test_occupant_elimination():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    player2 = Player(deck, dragon)
    token = Token(player)
    token2 = Token(player2)

    token_spot = TokenSpot()
    token_spot.arrive_via_adjacent(token)

    assert token_spot._did_eliminate(token2)

def test_both_elimination():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    player2 = Player(deck, dragon)
    token = Token(player)
    token2 = Token(player2)

    token_spot = TokenSpot()
    token_spot.arrive_via_adjacent(token)
    token_spot.arrive_via_path(token2)
    
    assert (not player.is_active()) and (not player2.is_active())