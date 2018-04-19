from admin import Administrator
from board import Board, Token
from deck import Deck
from dragon_card import DragonCard
from player import Player


def test_legal_move():
    player = Player(Deck([[(0, 5), (1, 4), (2, 7), (3, 6), ]]), DragonCard())
    token = Token(player)

    board = Board()
    board.place_token_start(0, token)

    assert Administrator.isLegalPlay(board, player._hand[0], player)


def test_illegal_move():
    player = Player(Deck([[(0, 1), (2, 3), (4, 5), (6, 7), ]]), DragonCard())
    token = Token(player)

    board = Board()
    board.place_token_start(0, token)

    assert not (Administrator.isLegalPlay(board, player._hand[0], player))
