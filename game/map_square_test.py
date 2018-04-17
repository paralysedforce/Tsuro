from .map_square import MapSquare
from .map_card import MapCard
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

paths = [
    (0,1),
    (2,3),
    (4,5),
    (6,7),
]

def test_place_card():
    card = MapCard(paths)

    tsp0 = TokenSpot()
    tsp1 = TokenSpot()

    spots = [tsp0,tsp1]
    for _ in range(6):
        spots.append(TokenSpot())

    square = MapSquare(spots)

    square.place_card(card)

    token = create_token()
    tsp0.arrive_via_adjacent(token)

    assert tsp1.get_occupant() == token

