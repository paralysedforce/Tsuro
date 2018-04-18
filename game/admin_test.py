from .admin import Administrator
from .player import Player
from .dragon_card import DragonCard
from .deck import Deck
from .token import Token
from .board import Board

def test_legal_move():

    player = Player(Deck([[(0,5),(1,4),(2,7),(3,6),]]), DragonCard())
    token = Token(player)

    board = Board()
 
    board.place_token_start(0, token)

    assert Administrator.isLegalPlay(board, player._hand[0], player)

