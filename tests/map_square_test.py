from deck import Deck
from dragon_card import DragonCard
from map_card import MapCard
from map_square import MapSquare, Side
from player import Player
from player_token import Token
from token_spot import TokenSpot


def create_token():
    deck = Deck()
    dragon = DragonCard()

    player = Player(deck, dragon)
    token = Token(player)
    return token


paths = [
    (0, 1),
    (2, 3),
    (4, 5),
    (6, 7),
]


def test_place_card():
    card = MapCard(paths)

    tsp0 = TokenSpot()
    tsp1 = TokenSpot()

    spots = [tsp0, tsp1]
    for _ in range(6):
        spots.append(TokenSpot())

    square = MapSquare(spots)

    square.place_card(card)

    token = create_token()
    tsp0.receive_token_via_adjacent(token)

    assert tsp1.get_occupant() == token


def test_place_card_token_present():
    card = MapCard(paths)

    tsp0 = TokenSpot()
    tsp1 = TokenSpot()

    token = create_token()
    tsp0.receive_token_via_adjacent(token)

    spots = [tsp0, tsp1]
    for _ in range(6):
        spots.append(TokenSpot())

    square = MapSquare(spots)

    square.place_card(card)

    assert tsp1.get_occupant() == token


def test_bind_top():
    tsp00 = TokenSpot()
    tsp01 = TokenSpot()
    spots0 = [tsp00, tsp01]
    for _ in range(6):
        spots0.append(TokenSpot())

    square0 = MapSquare(spots0)

    tsp15 = TokenSpot()
    tsp14 = TokenSpot()
    spots1 = []
    for _ in range(4):
        spots1.append(TokenSpot())
    spots1.append(tsp14)
    spots1.append(tsp15)
    for _ in range(2):
        spots1.append(TokenSpot())

    square1 = MapSquare(spots1)

    square0.set_adjacent(square1, Side.TOP)

    assert tsp00._next_card == tsp15
    assert tsp01._next_card == tsp14
    assert tsp14._next_card == tsp01
    assert tsp15._next_card == tsp00


def test_bind_right():
    tsp02 = TokenSpot()
    tsp03 = TokenSpot()
    spots0 = []
    for _ in range(2):
        spots0.append(TokenSpot())
    spots0.append(tsp02)
    spots0.append(tsp03)
    for _ in range(4):
        spots0.append(TokenSpot())

    square0 = MapSquare(spots0)

    tsp16 = TokenSpot()
    tsp17 = TokenSpot()
    spots1 = []
    for _ in range(6):
        spots1.append(TokenSpot())
    spots1.append(tsp16)
    spots1.append(tsp17)

    square1 = MapSquare(spots1)

    square0.set_adjacent(square1, Side.RIGHT)

    assert tsp02._next_card == tsp17
    assert tsp03._next_card == tsp16
    assert tsp16._next_card == tsp03
    assert tsp17._next_card == tsp02


def test_bind_bottom():
    tsp04 = TokenSpot()
    tsp05 = TokenSpot()
    spots0 = []
    for _ in range(4):
        spots0.append(TokenSpot())
    spots0.append(tsp04)
    spots0.append(tsp05)
    for _ in range(2):
        spots0.append(TokenSpot())

    square0 = MapSquare(spots0)

    tsp10 = TokenSpot()
    tsp11 = TokenSpot()
    spots1 = []
    spots1.append(tsp10)
    spots1.append(tsp11)
    for _ in range(6):
        spots1.append(TokenSpot())

    square1 = MapSquare(spots1)

    square0.set_adjacent(square1, Side.BOTTOM)

    assert tsp04._next_card == tsp11
    assert tsp05._next_card == tsp10
    assert tsp10._next_card == tsp05
    assert tsp11._next_card == tsp04


def test_bind_left():
    tsp06 = TokenSpot()
    tsp07 = TokenSpot()
    spots0 = []
    for _ in range(6):
        spots0.append(TokenSpot())
    spots0.append(tsp06)
    spots0.append(tsp07)

    square0 = MapSquare(spots0)

    tsp12 = TokenSpot()
    tsp13 = TokenSpot()
    spots1 = []
    for _ in range(2):
        spots1.append(TokenSpot())
    spots1.append(tsp12)
    spots1.append(tsp13)
    for _ in range(4):
        spots1.append(TokenSpot())

    square1 = MapSquare(spots1)

    square0.set_adjacent(square1, Side.LEFT)

    assert tsp06._next_card == tsp13
    assert tsp07._next_card == tsp12
    assert tsp12._next_card == tsp07
    assert tsp13._next_card == tsp06


def test_terminate_top():

    tsp00 = TokenSpot()
    tsp01 = TokenSpot()
    spots0 = [tsp00, tsp01]
    for _ in range(6):
        spots0.append(TokenSpot())

    square0 = MapSquare(spots0)

    square0.set_terminal(Side.TOP)

    assert tsp00._is_terminal_spot
    assert tsp01._is_terminal_spot


def test_terminate_right():

    tsp02 = TokenSpot()
    tsp03 = TokenSpot()
    spots0 = []
    for _ in range(2):
        spots0.append(TokenSpot())
    spots0.append(tsp02)
    spots0.append(tsp03)
    for _ in range(4):
        spots0.append(TokenSpot())

    square0 = MapSquare(spots0)

    square0.set_terminal(Side.RIGHT)

    assert tsp02._is_terminal_spot
    assert tsp03._is_terminal_spot


def test_terminate_bottom():
    tsp04 = TokenSpot()
    tsp05 = TokenSpot()

    spots0 = []
    for _ in range(4):
        spots0.append(TokenSpot())

    spots0.append(tsp04)
    spots0.append(tsp05)

    for _ in range(2):
        spots0.append(TokenSpot())

    square0 = MapSquare(spots0)
    square0.set_terminal(Side.BOTTOM)

    assert tsp04._is_terminal_spot
    assert tsp05._is_terminal_spot


def test_terminate_left():
    tsp06 = TokenSpot()
    tsp07 = TokenSpot()

    spots0 = []
    for _ in range(6):
        spots0.append(TokenSpot())

    spots0.append(tsp06)
    spots0.append(tsp07)
    square0 = MapSquare(spots0)
    square0.set_terminal(Side.LEFT)

    assert tsp06._is_terminal_spot
    assert tsp07._is_terminal_spot


def test_different_spots():
    square = MapSquare()
    equal_count = 0
    for spot in square._spots:
        equal_count += len([x for x in square._spots if x == spot])
    assert equal_count == 8
